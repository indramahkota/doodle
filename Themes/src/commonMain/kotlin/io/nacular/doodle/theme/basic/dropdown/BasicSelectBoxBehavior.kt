package io.nacular.doodle.theme.basic.dropdown

import io.nacular.doodle.controls.IndexedItem
import io.nacular.doodle.controls.ItemVisualizer
import io.nacular.doodle.controls.ListModel
import io.nacular.doodle.controls.PopupManager
import io.nacular.doodle.controls.SimpleIndexedItem
import io.nacular.doodle.controls.SingleItemSelectionModel
import io.nacular.doodle.controls.StringVisualizer
import io.nacular.doodle.controls.buttons.Button
import io.nacular.doodle.controls.buttons.PushButton
import io.nacular.doodle.controls.dropdown.SelectBox
import io.nacular.doodle.controls.dropdown.SelectBoxBehavior
import io.nacular.doodle.controls.list.List
import io.nacular.doodle.controls.list.ListBehavior
import io.nacular.doodle.controls.toString
import io.nacular.doodle.core.Container
import io.nacular.doodle.core.Display
import io.nacular.doodle.core.Icon
import io.nacular.doodle.core.View
import io.nacular.doodle.drawing.AffineTransform.Companion.Identity
import io.nacular.doodle.drawing.Canvas
import io.nacular.doodle.drawing.Color
import io.nacular.doodle.drawing.Stroke
import io.nacular.doodle.drawing.Stroke.LineCap
import io.nacular.doodle.drawing.Stroke.LineJoint
import io.nacular.doodle.drawing.TextMetrics
import io.nacular.doodle.drawing.darker
import io.nacular.doodle.drawing.lighter
import io.nacular.doodle.drawing.paint
import io.nacular.doodle.event.KeyCode.Companion.Enter
import io.nacular.doodle.event.KeyCode.Companion.Escape
import io.nacular.doodle.event.KeyEvent
import io.nacular.doodle.event.KeyListener
import io.nacular.doodle.event.KeyText.Companion.ArrowDown
import io.nacular.doodle.event.KeyText.Companion.ArrowUp
import io.nacular.doodle.event.PointerEvent
import io.nacular.doodle.event.PointerListener
import io.nacular.doodle.event.PointerListener.Companion.clicked
import io.nacular.doodle.focus.FocusManager
import io.nacular.doodle.geometry.Point
import io.nacular.doodle.geometry.Point.Companion.Origin
import io.nacular.doodle.geometry.Rectangle
import io.nacular.doodle.geometry.Size
import io.nacular.doodle.layout.Insets
import io.nacular.doodle.layout.constraints.Bounds
import io.nacular.doodle.layout.constraints.ConstraintDslContext
import io.nacular.doodle.layout.constraints.ConstraintLayout
import io.nacular.doodle.layout.constraints.center
import io.nacular.doodle.layout.constraints.constrain
import io.nacular.doodle.layout.constraints.fill
import io.nacular.doodle.layout.constraints.withSizeInsets
import io.nacular.doodle.theme.basic.BasicButtonBehavior
import io.nacular.doodle.theme.basic.ColorMapper
import io.nacular.doodle.theme.basic.ListItem
import io.nacular.doodle.theme.basic.list.BasicListBehavior
import io.nacular.doodle.theme.basic.list.BasicVerticalListPositioner
import io.nacular.doodle.utils.Anchor.Right
import io.nacular.doodle.utils.ChangeObserver
import io.nacular.doodle.utils.Dimension.Height
import io.nacular.doodle.utils.Pool
import io.nacular.doodle.utils.PropertyObserver
import io.nacular.doodle.utils.SetPool
import kotlin.math.max

/**
 * Created by Nicholas Eddy on 9/9/21.
 */
public class BasicSelectBoxBehavior<T, M: ListModel<T>>(
    private val display            : Display,
    private val textMetrics        : TextMetrics,
    private val backgroundColor    : Color,
    private val darkBackgroundColor: Color,
    private val foregroundColor    : Color,
    private val cornerRadius       : Double,
    private val buttonWidth        : Double        = 20.0,
    private val focusManager       : FocusManager? = null,
    private val popupManager       : PopupManager? = null,
    private val buttonA11yLabel    : String?       = null,
    private val inset              : Double        = 4.0,
): SelectBoxBehavior<T, M>, PointerListener, KeyListener {

    public var hoverColorMapper   : ColorMapper = { it.darker(0.1f) }
    public var disabledColorMapper: ColorMapper = { it.lighter()    }

    internal var buttonAlignment: (ConstraintDslContext.(Bounds) -> Unit) = fill

    private inner class ButtonIcon(private val colors: (Button) -> Color): Icon<Button> {
        override fun size(view: Button) = Size(buttonWidth, max(0.0, view.height - 2 * inset))

        override fun render(view: Button, canvas: Canvas, at: Point) {
            val iconSize      = size(view)

            if (iconSize.empty) return

            val arrowSize     = iconSize.run { Size(width * 0.5, height * 0.6) }
            val arrowPosition = at + Point(x = (iconSize.width - arrowSize.width) / 2, y = (iconSize.height - arrowSize.height) / 2)

            val stroke = Stroke(when {
                view.enabled -> foregroundColor
                else         -> disabledColorMapper(foregroundColor)
            }.paint, 1.5, lineJoint = LineJoint.Round, lineCap = LineCap.Round)

            val points = listOf(
                    Point(arrowPosition.x,                       arrowPosition.y + arrowSize.height * 0.3),
                    Point(arrowPosition.x + arrowSize.width / 2, arrowPosition.y                         ),
                    Point(arrowPosition.x + arrowSize.width,     arrowPosition.y + arrowSize.height * 0.3)
            )

            canvas.rect(view.bounds.atOrigin.inset(Insets(
                    left   = view.width - buttonWidth - inset,
                    top    = inset,
                    right  = inset,
                    bottom = inset)), cornerRadius, colors(view).paint)

            canvas.path(points, stroke)
            canvas.transform(Identity.flipVertically(arrowPosition.y + arrowSize.height / 2)) {
                path(points, stroke)
            }
        }
    }

    private inner class ButtonBehavior: BasicButtonBehavior(
            textMetrics         = textMetrics,
            cornerRadius        = cornerRadius,
            backgroundColor     = backgroundColor,
            foregroundColor     = foregroundColor,
            darkBackgroundColor = darkBackgroundColor
    ) {
        init {
            hoverColorMapper    = this@BasicSelectBoxBehavior.hoverColorMapper
            disabledColorMapper = { it }
        }

        override fun install(view: Button) {
            view.icon       = ButtonIcon { colors(it).fillColor }
            view.iconAnchor = Right

            super.install(view)
        }

        override fun render(view: Button, canvas: Canvas) {
            icon(view)?.let {
                it.render(view, canvas, iconPosition(view, icon = it) - Point(inset, 0.0))
            }
        }
    }

    private val itemVisualizer by lazy { toString<T, IndexedItem>(StringVisualizer()) }

    private val changeObserver: ChangeObserver<SelectBox<T, M>> = {
        it.list?.setSelection(setOf(it.selection))
    }

    @Suppress("UNCHECKED_CAST")
    private val enabledChanged: PropertyObserver<View, Boolean> = { dropdown,_,_ ->
        if (!dropdown.enabled) {
            (dropdown as? SelectBox<T, M>)?.let { hideList(it) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val boundsChanged: PropertyObserver<View, Rectangle> = { dropdown, old, new ->
        if (old.height != new.height) {
            (dropdown as? SelectBox<T, M>)?.apply {
                list?.behavior = listBehavior(dropdown)
            }
        }
    }

    private inner class CustomListRow(
        dropdown      : SelectBox<T, M>,
        list          : List<T, *>,
        row           : T,
        index         : Int,
        itemVisualizer: ItemVisualizer<T, IndexedItem>,
        private val cornerRadius  : Double,
    ): ListItem<T>(list,
            row,
            index,
            itemVisualizer,
            backgroundSelectionColor        = hoverColorMapper(this@BasicSelectBoxBehavior.backgroundColor),
            backgroundSelectionBlurredColor = null) {
        init {
            insetTop = 0.0

            pointerFilter += clicked {
                dropdown.selection = this.index
                hideList(dropdown)
            }
        }

        override fun pointerOver(value: Boolean) {
            when {
                value -> list.setSelection   (setOf(index))
                else  -> list.removeSelection(setOf(index))
            }
        }

        override fun render(canvas: Canvas) {
            backgroundColor?.let { canvas.rect(bounds.atOrigin, cornerRadius, it.paint) }
        }
    }

    private inner class ItemGenerator(private val dropdown: SelectBox<T, M>): ListBehavior.ItemGenerator<T> {
        @Suppress("UNCHECKED_CAST")
        override fun invoke(list: List<T, *>, item: T, index: Int, current: View?): View = when (current) {
            is ListItem<*> -> (current as BasicSelectBoxBehavior<T, M>.CustomListRow).apply { update(list, item, index) }
            else           -> CustomListRow(
                    dropdown       = dropdown,
                    list           = list,
                    row            = item,
                    index          = index,
                    cornerRadius   = cornerRadius,
                    itemVisualizer = list.itemVisualizer ?: toString(StringVisualizer())
            )
        }.apply {
            list.cellAlignment?.let { positioner = it }
        }
    }

    override fun render(view: SelectBox<T, M>, canvas: Canvas) {
        canvas.rect(view.bounds.atOrigin, cornerRadius, backgroundColor.paint)
    }

    private fun showList(view: SelectBox<T, M>) {
        view.list?.let {
            it.font  = view.font
            it.width = view.width

            it.setSelection(setOf(view.selection))

            focusManager?.requestFocus(it)

            when (popupManager) {
                null -> {
                    val viewAbsolute = display.fromAbsolute(view.toAbsolute(Origin))
                    it.x     = viewAbsolute.x
                    it.y     = viewAbsolute.y - view.selection * (view.height - 2 * inset)
                    display += it
                }
                else -> popupManager.show(it, view) { list, dropdown ->
                    list.top  eq dropdown.y - view.selection * (view.height - 2 * inset)
                    list.left eq dropdown.x
                }
            }
        }
    }

    private fun hideList(view: SelectBox<T, M>, focusDropdown: Boolean = false) {
        view.list?.let {
            when (popupManager) {
                null -> display -= it
                else -> popupManager.hide(it)
            }
        }

        if (focusDropdown) {
            focusManager?.requestFocus(view)
        }
    }

    private fun listBehavior(dropdown: SelectBox<T, M>) = object: BasicListBehavior<T>(
        focusManager = focusManager,
        generator    = ItemGenerator(dropdown),
        fill         = null,
        positioner   = object: BasicVerticalListPositioner<T>(0.0) {
            override val height get() = max(0.0, dropdown.height - 2 * inset)
        }
    ) {
        override fun render(view: List<T, *>, canvas: Canvas) {
            canvas.rect(view.bounds.atOrigin, cornerRadius, this@BasicSelectBoxBehavior.backgroundColor.paint)
        }
    }

    override fun install(view: SelectBox<T, M>) {
        super.install(view)

        view.list = List(
            view.model,
            selectionModel = SingleItemSelectionModel(),
            itemVisualizer = view.listItemVisualizer,
            fitContent     = setOf(Height)
        ).apply {
            insets        = Insets(inset)
            behavior      = listBehavior(view)
            acceptsThemes = false

            focusChanged += { _,_,focused ->
                if (!focused) {
                    hideList(view)
                }
            }

            keyChanged += KeyListener.released { event ->
                if (displayed) {
                    when (event.code) {
                        Enter  -> firstSelection?.let { index ->
                            view.selection = index
                            hideList(view, focusDropdown = true)
                        }
                        Escape -> hideList(view, focusDropdown = true)
                    }
                }
            }
        }

        val center = Container().apply { focusable = false }
        val button = PushButton().apply {
            enabled            = !view.isEmpty
            behavior           = ButtonBehavior()
            focusable          = false
            acceptsThemes      = false
            accessibilityLabel = buttonA11yLabel

            fired += {
                showList(view)
            }
        }

        view.children.clear()

        view.children += listOf(center, button)
        view.layout = constrain(center, button) { (center, button) ->
            center.top    eq inset
            center.left   eq inset
            center.right  eq parent.right  - (buttonWidth + inset)
            center.bottom eq parent.bottom - inset

            buttonAlignment(button)
        }

        updateCenter(view)

        view.changed        += changeObserver
        view.keyChanged     += this
        view.boundsChanged  += boundsChanged // FIXME: This is a hack b/c List doesn't behave well if row dimensions are changed dynamically. That will eventually be fixed.
        view.pointerChanged += this
        view.enabledChanged += enabledChanged
    }

    override fun uninstall(view: SelectBox<T, M>) {
        super.uninstall(view)

        hideList(view)

        view.list?.behavior  = null
        view.list            = null
        view.changed        -= changeObserver
        view.keyChanged     -= this
        view.boundsChanged  -= boundsChanged // FIXME: This is a hack b/c List doesn't behave well if row dimensions are changed dynamically. That will eventually be fixed.
        view.pointerChanged -= this
        view.enabledChanged -= enabledChanged
    }

    override fun changed(dropdown: SelectBox<T, M>) {
        updateCenter(dropdown)
    }

    override fun alignmentChanged(dropdown: SelectBox<T, M>) {
        viewContainer(dropdown)?.let { updateAlignment(dropdown, it) }
    }

    override fun pressed(event: KeyEvent) {
        @Suppress("UNCHECKED_CAST")
        (event.source as? SelectBox<T, M>)?.apply {
            when (event.key) {
                ArrowUp   -> { selection -= 1; event.consume() }
                ArrowDown -> { selection += 1; event.consume() }
            }
        }
    }

    override fun pressed(event: PointerEvent) {
        // FIXME: This caused an issue on mobile where the list would not show. Need to figure out why that happens
//        focusManager?.requestFocus(event.source)
    }

    internal val centerChanged: Pool<(SelectBox<T, M>, View?, View?) -> Unit> = SetPool()

    internal fun updateCenter(dropdown: SelectBox<T, M>, newValue: View = centerView(dropdown)) {
        viewContainer(dropdown)?.let { centerView ->
            centerView.children.clear()

            centerView += newValue

            updateAlignment(dropdown, centerView)
        }
    }

    private fun centerView(dropdown: SelectBox<T, M>) = dropdown.value.fold(
        onSuccess = { (dropdown.boxItemVisualizer ?: itemVisualizer)(it, null, SimpleIndexedItem(dropdown.selection, true)) },
        onFailure = { object: View() {} }
    )

    private  fun viewContainer  (dropdown: SelectBox<T, M>): Container? =  dropdown.children.firstOrNull { it !is PushButton } as? Container
    internal fun visualizedValue(dropdown: SelectBox<T, M>): View?      = viewContainer(dropdown)?.firstOrNull()

    private fun updateAlignment(dropdown: SelectBox<T, M>, centerView: Container) {
        val constrains: ConstraintDslContext.(Bounds) -> Unit = {
            withSizeInsets(width = 0.0) {
                (dropdown.boxCellAlignment ?: center)(it)
            }
        }

        centerView.firstOrNull()?.let { child ->
            when (val l = centerView.layout) {
                is ConstraintLayout -> { l.unconstrain(child, constrains); l.constrain(child, constrains) }
                else                -> centerView.layout = constrain(child, constrains)
            }
        }

        dropdown.list?.apply {
            val alignment = dropdown.listCellAlignment ?: dropdown.boxCellAlignment ?: center

            cellAlignment = {
                withSizeInsets(width = buttonWidth) {
                    alignment(it)
                }
            }
        }
    }

    public companion object {
        public operator fun <T, M: ListModel<T>> invoke(
                display              : Display,
                textMetrics          : TextMetrics,
                backgroundColor      : Color,
                darkBackgroundColor  : Color,
                foregroundColor      : Color,
                cornerRadius         : Double,
                buttonWidth          : Double        = 20.0,
                focusManager         : FocusManager? = null): BasicSelectBoxBehavior<T, M> = BasicSelectBoxBehavior(
                    display             = display,
                    textMetrics         = textMetrics,
                    backgroundColor     = backgroundColor,
                    darkBackgroundColor = darkBackgroundColor,
                    focusManager        = focusManager,
                    foregroundColor     = foregroundColor,
                    cornerRadius        = cornerRadius,
                    buttonWidth         = buttonWidth,
        )
    }
}