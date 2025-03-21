package io.nacular.doodle.theme.basic.table

import io.nacular.doodle.controls.IndexedItem
import io.nacular.doodle.controls.ItemVisualizer
import io.nacular.doodle.controls.list.ListLike
import io.nacular.doodle.controls.table.AbstractTableBehavior.FooterCellGenerator
import io.nacular.doodle.controls.table.AbstractTableBehavior.HeaderCellGenerator
import io.nacular.doodle.controls.table.AbstractTableBehavior.MetaRowPositioner
import io.nacular.doodle.controls.table.AbstractTableBehavior.OverflowColumnConfig
import io.nacular.doodle.controls.table.Column
import io.nacular.doodle.controls.table.ExpansionObserver
import io.nacular.doodle.controls.table.MetaRowGeometry
import io.nacular.doodle.controls.table.TreeTable
import io.nacular.doodle.controls.table.TreeTableBehavior
import io.nacular.doodle.controls.tree.TreeLike
import io.nacular.doodle.core.View
import io.nacular.doodle.drawing.Canvas
import io.nacular.doodle.drawing.Color
import io.nacular.doodle.drawing.Color.Companion.Blue
import io.nacular.doodle.drawing.Color.Companion.Lightgray
import io.nacular.doodle.drawing.Color.Companion.White
import io.nacular.doodle.drawing.ColorPaint
import io.nacular.doodle.drawing.horizontalStripedPaint
import io.nacular.doodle.drawing.lighter
import io.nacular.doodle.drawing.opacity
import io.nacular.doodle.drawing.width
import io.nacular.doodle.event.KeyEvent
import io.nacular.doodle.event.KeyListener
import io.nacular.doodle.event.PointerEvent
import io.nacular.doodle.event.PointerListener
import io.nacular.doodle.focus.FocusManager
import io.nacular.doodle.geometry.Point
import io.nacular.doodle.geometry.Rectangle
import io.nacular.doodle.geometry.with
import io.nacular.doodle.layout.Insets
import io.nacular.doodle.system.SystemInputEvent.Modifier.Ctrl
import io.nacular.doodle.system.SystemInputEvent.Modifier.Meta
import io.nacular.doodle.system.SystemInputEvent.Modifier.Shift
import io.nacular.doodle.theme.basic.ListItem
import io.nacular.doodle.theme.basic.SelectableTreeKeyHandler
import io.nacular.doodle.theme.basic.SimpleTreeRowIcon
import io.nacular.doodle.theme.basic.TreeRow
import io.nacular.doodle.theme.basic.TreeRowIcon
import io.nacular.doodle.theme.basic.VerticalListPositioner
import io.nacular.doodle.utils.Path
import io.nacular.doodle.utils.PropertyObserver
import io.nacular.doodle.utils.SetObserver

/**
 * Created by Nicholas Eddy on 4/8/19.
 */
public fun TreeLike.map(mapper: (Int) -> Path<Int>, unmapper: (Path<Int>) -> Int): ListLike = object: ListLike {
    override val hasFocus     get() = this@map.hasFocus
    override val focusChanged get() = this@map.focusChanged
    override val numItems      get() = this@map.numRows

    override fun selectAll     () = this@map.selectAll     ()
    override fun clearSelection() = this@map.clearSelection()

    override fun selected       (item : Int     ) = this@map.selected       (mapper(item))
    override fun setSelection   (items: Set<Int>) = this@map.setSelection   (items.map(mapper).toSet())
    override fun addSelection   (items: Set<Int>) = this@map.addSelection   (items.map(mapper).toSet())
    override fun removeSelection(items: Set<Int>) = this@map.removeSelection(items.map(mapper).toSet())
    override fun toggleSelection(items: Set<Int>) = this@map.toggleSelection(items.map(mapper).toSet())

    override fun next    (after : Int): Int? = this@map.next    (mapper(after ))?.let(unmapper)
    override fun previous(before: Int): Int? = this@map.previous(mapper(before))?.let(unmapper)

    override val selection      : Set<Int> get() = this@map.selection.map(unmapper).toSet()
    override val lastSelection  : Int?     get() = this@map.lastSelection?.let  (unmapper)
    override val firstSelection : Int?     get() = this@map.firstSelection?.let (unmapper)
    override val selectionAnchor: Int?     get() = this@map.selectionAnchor?.let(unmapper)
    override val firstSelectable: Int?     get() = this@map.firstSelectable?.let(unmapper)
    override val lastSelectable : Int?     get() = this@map.lastSelectable?.let (unmapper)
}

public open class BasicTreeTableBehavior<T>(
        private val focusManager         : FocusManager?,
        private val rowHeight            : Double = 20.0,
        private val headerColor          : Color? = Lightgray,
        private val footerColor          : Color? = Lightgray,
                    evenRowColor         : Color? = White,
                    oddRowColor          : Color? = Lightgray.lighter().lighter(),
                    iconFactory          : () -> TreeRowIcon = { SimpleTreeRowIcon() },
        private val selectionColor       : Color? = Blue,
        private val selectionBlurredColor: Color? = Lightgray): TreeTableBehavior<T>(), PointerListener, KeyListener, SelectableTreeKeyHandler {

    private val selectionChanged: SetObserver<TreeTable<T, *>, Path<Int>> = { treeTable ,_,_ ->
        treeTable.bodyDirty()
        treeTable.scrollToSelection()
    }

    @Suppress("UNCHECKED_CAST")
    private val focusChanged: PropertyObserver<View, Boolean> = { table ,_,_ ->
        (table as? TreeTable<T, *>)?.bodyDirty()
    }

    private val expansionChanged: ExpansionObserver<T> = { table,_ ->
        if (table.selection.firstOrNull() != null) {
            table.bodyDirty()
        }
    }

    private val canvasFill = horizontalStripedPaint(rowHeight, evenRowColor, oddRowColor)

    private val movingColumns = mutableSetOf<Column<*>>()

    @Suppress("UNCHECKED_CAST")
    override val treeCellGenerator: TreeCellGenerator<T> = object: TreeCellGenerator<T> {
        override fun <A> invoke(table: TreeTable<T, *>, column: Column<A>, cell: A, path: Path<Int>, row: Int, itemGenerator: ItemVisualizer<A, IndexedItem>, current: View?): View = when (current) {
            is TreeRow<*> -> (current as TreeRow<A>).apply { update(table, cell, path, table.rowFromPath(path)!!) }
            else          -> TreeRow(table, cell, path, table.rowFromPath(path)!!, selectionColor = null, itemVisualizer = object: ItemVisualizer<A, IndexedItem> {
                override fun invoke(item: A, previous: View?, context: IndexedItem) = itemGenerator.invoke(item, previous, context)
            }, iconFactory = iconFactory)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override val cellGenerator: CellGenerator<T> = object: CellGenerator<T> {
        override fun <A> invoke(table: TreeTable<T, *>, column: Column<A>, cell: A, path: Path<Int>, row: Int, itemGenerator: ItemVisualizer<A, IndexedItem>, current: View?): View = when (current) {
            is ListItem<*> -> (current as ListItem<A>).apply { update(table.map({ table.pathFromRow(it)!! }, { table.rowFromPath(it)!! }), cell, row) }
            else           -> ListItem(table.map({ table.pathFromRow(it)!! }, { table.rowFromPath(it)!! }), cell, row, itemGenerator, backgroundSelectionColor = null)
        }.apply { column.cellAlignment?.let { positioner = it } }
    }

    override val headerPositioner: MetaRowPositioner<TreeTable<T, *>> = object: MetaRowPositioner<TreeTable<T, *>> {
        override fun invoke(table: TreeTable<T, *>) = MetaRowGeometry(0.0, 0.0, 1.1 * rowHeight)
    }

    override val footerPositioner: MetaRowPositioner<TreeTable<T, *>> = object: MetaRowPositioner<TreeTable<T, *>> {
        override fun invoke(table: TreeTable<T, *>) = MetaRowGeometry(0.0, 0.0, 1.1 * rowHeight)
    }

    override val rowPositioner: RowPositioner<T> = object: RowPositioner<T>() {
        private val delegate = VerticalListPositioner(rowHeight)

        override fun rowBounds(of: TreeTable<T, *>, path: Path<Int>, row: T, index: Int) = delegate.itemBounds (of.prospectiveBounds.size, of.insets, index)
        override fun row      (of: TreeTable<T, *>, at: Point)                           = delegate.itemFor    (of.prospectiveBounds.size, of.insets,    at)
        override fun size     (of: TreeTable<T, *>, below: Path<Int>)                    = delegate.minimumSize(of.rowsBelow(below),       of.insets       )
    }

    override val headerCellGenerator: HeaderCellGenerator<TreeTable<T, *>> = object: HeaderCellGenerator<TreeTable<T, *>> {
        override fun <A> invoke(table: TreeTable<T, *>, column: Column<A>) = TableHeaderCell(column, headerColor)
    }

    override val footerCellGenerator: FooterCellGenerator<TreeTable<T, *>> = object: FooterCellGenerator<TreeTable<T, *>> {
        override fun <A> invoke(table: TreeTable<T, *>, column: Column<A>) = TableFooterCell(column, headerColor)
    }

    override val overflowColumnConfig: OverflowColumnConfig<TreeTable<T, *>> = object: OverflowColumnConfig<TreeTable<T, *>> {
        override fun body(table: TreeTable<T, *>): View = object: View() {
            init {
                pointerChanged += object: PointerListener {
                    private var pointerOver    = false
                    private var pointerPressed = false

                    override fun entered(event: PointerEvent) {
                        pointerOver = true
                    }

                    override fun exited(event: PointerEvent) {
                        pointerOver = false
                    }

                    override fun pressed(event: PointerEvent) {
                        pointerPressed = true
                    }

                    override fun released(event: PointerEvent) {
                        if (pointerOver && pointerPressed) {
                            val index = rowPositioner.row(table, event.location)

                            if (index >= table.numRows) {
                                return
                            }

                            when {
                                Ctrl  in event.modifiers || Meta in event.modifiers     -> table.toggleSelection(setOf(index))
                                Shift in event.modifiers && table.lastSelection != null -> {
                                    table.selectionAnchor?.let { table.rowFromPath(it) }?.let { anchor ->
                                        when {
                                            index  < anchor -> table.setSelection((index.. anchor ).reversed().toSet())
                                            anchor < index  -> table.setSelection((anchor  ..index).           toSet())
                                        }
                                    }
                                }
                                else                                                    -> table.setSelection(setOf(index))
                            }
                        }

                        pointerPressed = false
                    }
                }
            }
        }
    }

    override fun renderHeader(table: TreeTable<T, *>, canvas: Canvas) {
        headerColor?.let { canvas.rect(Rectangle(size = canvas.size), ColorPaint(it)) }
    }

    override fun renderFooter(table: TreeTable<T, *>, canvas: Canvas) {
        footerColor?.let { canvas.rect(Rectangle(size = canvas.size), ColorPaint(it)) }
    }

    override fun renderBody(table: TreeTable<T, *>, canvas: Canvas) {
        canvas.rect(Rectangle(size = canvas.size), canvasFill)

        val color = if (table.hasFocus) selectionColor else selectionBlurredColor

        if (color != null) {
            // FIXME: Performance can be bad for large lists
            table.selection.map { it to table[it] }.forEach { (path, row) ->
                row.onSuccess {
                    canvas.rect(rowPositioner.rowBounds(table, path, it, table.rowFromPath(path)!!).inset(Insets(top = 1.0)).with(width = canvas.width), ColorPaint(color))
                }
            }
        }
    }

    override fun <A> renderColumnBody(table: TreeTable<T, *>, column: Column<A>, canvas: Canvas) {
        if (column in movingColumns && headerColor != null) {
            canvas.rect(Rectangle(size = canvas.size), ColorPaint(headerColor.opacity(0.2f)))
        }
    }

    // FIXME: Centralize
    override fun install(view: TreeTable<T, *>) {
        view.expanded         += expansionChanged
        view.collapsed        += expansionChanged
        view.keyChanged       += this
        view.focusChanged     += focusChanged
        view.pointerChanged   += this
        view.selectionChanged += selectionChanged

        view.bodyDirty  ()
        view.headerDirty()
    }

    override fun uninstall(view: TreeTable<T, *>) {
        view.expanded         -= expansionChanged
        view.collapsed        -= expansionChanged
        view.keyChanged       -= this
        view.focusChanged     -= focusChanged
        view.pointerChanged   -= this
        view.selectionChanged -= selectionChanged
    }

    override fun pressed(event: PointerEvent) {
        focusManager?.requestFocus(event.source)
    }

    override fun pressed(event: KeyEvent) {
        super<SelectableTreeKeyHandler>.pressed(event)
    }

    override fun <A> columnMoveStart(table: TreeTable<T, *>, column: Column<A>) {
        if (headerColor == null) {
            return
        }

        movingColumns += column

        table.columnDirty(column)
    }

    override fun <A> columnMoveEnd(table: TreeTable<T, *>, column: Column<A>) {
        if (headerColor == null) {
            return
        }

        movingColumns -= column

        table.columnDirty(column)
    }
}