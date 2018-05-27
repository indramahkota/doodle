package com.nectar.doodle.core

import com.nectar.doodle.drawing.Canvas
import com.nectar.doodle.drawing.Color
import com.nectar.doodle.drawing.Font
import com.nectar.doodle.drawing.RenderManager
import com.nectar.doodle.event.FocusEvent
import com.nectar.doodle.event.FocusEvent.Type.Gained
import com.nectar.doodle.event.FocusEvent.Type.Lost
import com.nectar.doodle.event.KeyEvent
import com.nectar.doodle.event.KeyListener
import com.nectar.doodle.event.KeyState
import com.nectar.doodle.event.MouseEvent
import com.nectar.doodle.event.MouseListener
import com.nectar.doodle.event.MouseMotionListener
import com.nectar.doodle.event.MouseWheelEvent
import com.nectar.doodle.focus.FocusTraversalPolicy
import com.nectar.doodle.focus.FocusTraversalPolicy.TraversalType
import com.nectar.doodle.geometry.Point
import com.nectar.doodle.geometry.Point.Companion.Origin
import com.nectar.doodle.geometry.Rectangle
import com.nectar.doodle.geometry.Rectangle.Companion.Empty
import com.nectar.doodle.geometry.Size
import com.nectar.doodle.layout.Insets.Companion.None
import com.nectar.doodle.system.Cursor
import com.nectar.doodle.system.SystemMouseEvent.Type.Down
import com.nectar.doodle.system.SystemMouseEvent.Type.Drag
import com.nectar.doodle.system.SystemMouseEvent.Type.Enter
import com.nectar.doodle.system.SystemMouseEvent.Type.Exit
import com.nectar.doodle.system.SystemMouseEvent.Type.Move
import com.nectar.doodle.system.SystemMouseEvent.Type.Up
import com.nectar.doodle.utils.ChangeObserver
import com.nectar.doodle.utils.ChangeObserversImpl
import com.nectar.doodle.utils.ObservableList
import com.nectar.doodle.utils.ObservableProperty
import com.nectar.doodle.utils.OverridableProperty
import com.nectar.doodle.utils.Pool
import com.nectar.doodle.utils.PropertyObservers
import com.nectar.doodle.utils.PropertyObserversImpl
import com.nectar.doodle.utils.SetPool
import com.nectar.doodle.utils.observable
import kotlin.reflect.KProperty

private typealias BooleanObservers = PropertyObservers<Gizmo, Boolean>

/**
 * The smallest unit of displayable, interactive content within the framework.
 * Gizmos are the visual entities used to display components for an application.
 * User input events are sent to all Gizmos that are configured to receive them.
 * This allows them to response to user interaction or convey such events to
 * other parts of an application.
 *
 * @author Nicholas Eddy
 * @constructor
 */
@Suppress("FunctionName", "PropertyName")
abstract class Gizmo protected constructor() {

    /** Name to identify this Gizmo */
    var name = "" // TODO: Remove?

    /** Notifies changes to [hasFocus] */
    val focusChanged: BooleanObservers by lazy { PropertyObserversImpl<Gizmo, Boolean>(this) }

    /** Whether the Gizmo has focus or not */
    var hasFocus by ObservableProperty(false, { this }, focusChanged as PropertyObserversImpl<Gizmo, Boolean>)
        private set

    /** Notifies changes to [enabled] */
    val enabledChanged: BooleanObservers by lazy { PropertyObserversImpl<Gizmo, Boolean>(this) }

    /** Whether this Gizmo is enabled */
    var enabled by ObservableProperty(true, { this }, enabledChanged as PropertyObserversImpl<Gizmo, Boolean>)

    /** Notifies changes to [visible] */
    val visibilityChanged: BooleanObservers by lazy { PropertyObserversImpl<Gizmo, Boolean>(this) }

    /** Whether this Gizmo is visible */
    var visible by ObservableProperty(true, { this }, visibilityChanged as PropertyObserversImpl<Gizmo, Boolean>)

    /** Notifies changes to [focusable] */
    val focusabilityChanged: BooleanObservers by lazy { PropertyObserversImpl<Gizmo, Boolean>(this) }

    /** Whether this Gizmo is focusable */
    open var focusable by ObservableProperty(true, { this }, focusabilityChanged as PropertyObserversImpl<Gizmo, Boolean>)

    /** The size that would best display this Gizmo, or null if no preference */
    var idealSize: Size? = null
        get() = layout?.idealSize(this, field) ?: field

    /** The minimum size preferred by the Gizmo */
    var minimumSize: Size = Size.Empty
        get() = layout?.idealSize(this, field) ?: field

    /**
     * The current visible [Rectangle] for this Gizmo within it's coordinate space.  This accounts for clipping by ancestors,
     * but NOT cousins (siblings, anywhere in the hierarchy)
     */
    val displayRect get() = renderManager?.displayRect(this) ?: Empty

    /** The current text to display for tool-tips. */
    var toolTipText = ""

    val mouseChanged by lazy { SetPool<MouseListener>() }

    var monitorsMouse by object: OverridableProperty<Boolean>(true, { _,_,_ ->

    }) {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            return super.getValue(thisRef, property) && mouseChanged.isNotEmpty()
        }
    }

    val keyChanged by lazy { SetPool<KeyListener>() }

    var monitorsKeyboard by object: OverridableProperty<Boolean>(true, { _,_,_ ->

    }) {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            return super.getValue(thisRef, property) && keyChanged.isNotEmpty()
        }
    }

    val mouseMotionChanged by lazy { SetPool<MouseMotionListener>() }

    var monitorsMouseMotion by object: OverridableProperty<Boolean>(true, { _,_,_ ->

    }) {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            return super.getValue(thisRef, property) && mouseMotionChanged.isNotEmpty()
        }
    }

    var monitorsMouseWheel by observable(true ) { _,_,_ ->

    }

    val displayRectHandlingChanged: BooleanObservers by lazy { PropertyObserversImpl<Gizmo, Boolean>(this) }

    var monitorsDisplayRect by ObservableProperty(false, { this }, displayRectHandlingChanged as PropertyObserversImpl<Gizmo, Boolean>)

    var cursor: Cursor? = null
        get() = field ?: parent?.cursor
        set(new) {
            if (new == field) {
                return
            }

            val old = field

            field = new

            (cursorChanged as PropertyObserversImpl<Gizmo, Cursor?>)(old, new)
        }

    val cursorChanged: PropertyObservers<Gizmo, Cursor?> by lazy { PropertyObserversImpl<Gizmo, Cursor?>(this) }

    var font: Font? = null
        set(new) { field = new; styleChanged() }

    var foregroundColor: Color? = null
        set(new) { field = new; styleChanged() }

    var backgroundColor: Color? = null
        set(new) { field = new; styleChanged() }

    val styleChanged: Pool<ChangeObserver<Gizmo>> by lazy { ChangeObserversImpl(this) }

    private fun styleChanged() = (styleChanged as ChangeObserversImpl)()

    var x: Double
        get( ) = bounds.x
        set(x) = setBounds(x, y, width, height)

    var y: Double
        get( ) = bounds.y
        set(y) = setBounds(x, y, width, height)

    var position: Point
        get(        ) = bounds.position
        set(position) = setBounds(position.x, position.y, width, height)

    var width: Double
        get(     ) = bounds.width
        set(width) = setBounds(x, y, width, height)

    var height: Double
        get(      ) = bounds.height
        set(height) = setBounds(x, y, width, height)

    var size: Size
        get(    ) = bounds.size
        set(size) = setBounds(x, y, size.width, size.height)

    val boundsChanged: PropertyObservers<Gizmo, Rectangle> by lazy { PropertyObserversImpl<Gizmo, Rectangle>(this) }

    var bounds by ObservableProperty(Empty, { this }, boundsChanged as PropertyObserversImpl<Gizmo, Rectangle>)

    // ================= Container ================= //
    internal val insets_ get() = insets
    protected open var insets  = None

    internal val layout_ get() = layout
    protected open var layout: Layout? by observable<Layout?>(null) { _,_,_ ->
        // TODO: Have RenderManager manage the layout?
        if (renderManager!= null) doLayout()
    }

    internal val children_ get() = children
    protected open val children by lazy {
        ObservableList<Gizmo, Gizmo>(this).also {
            it.changed += { _, removed, added, _ ->
                removed.values.forEach { it.parent = null }
                added.values.forEach {
                    require(it !== this         ) { "cannot add to self"                 }
                    require(!it.ancestorOf(this)) { "cannot add ancestor to descendant"  }

                    it.parent = this
                }
            }
        }
    }

    /**
     * Tells whether this Gizmo is an ancestor of the Gizmo.
     *
     * @param gizmo The Gizmo
     * @return true if the Gizmo is a descendant of the Gizmo
     */
    internal infix fun ancestorOf_(gizmo: Gizmo) = ancestorOf(gizmo)
    protected open infix fun ancestorOf(gizmo: Gizmo): Boolean {
        if (children.isNotEmpty()) {
            var parent = gizmo.parent

            while (parent != null) {
                if (this === parent) {
                    return true
                }

                parent = parent.parent
            }
        }

        return false
    }

    internal open var isFocusCycleRoot_ get() = isFocusCycleRoot
        set(new) { isFocusCycleRoot = new }

    protected open var isFocusCycleRoot = false

    internal val focusCycleRoot_ get() = focusCycleRoot
    protected val focusCycleRoot: Gizmo? get() {
        var result = parent

        while (result != null && !result.isFocusCycleRoot) {
            result = result.parent
        }

        return result
    }

    internal val focusTraversalPolicy_ get() = focusTraversalPolicy
    protected open var focusTraversalPolicy = null as FocusTraversalPolicy?

    // [Performance]
    // No check to prevent setting self as parent since Gizmo is the only place where this method is called from and it already
    // prevents this by preventing a Gizmo from being added to itself.
    var parent: Gizmo? = null
        private set(new) {
            if (field === new) {
                return
            }

            field?.children?.remove(this)

            field = new

            (parentChange as PropertyObserversImpl)(field, new)
        }

    val parentChange: PropertyObservers<Gizmo, Gizmo?> by lazy { PropertyObserversImpl<Gizmo, Gizmo?>(this) }

    private var renderManager: RenderManager? = null

    private val traversalKeys: MutableMap<TraversalType, Set<KeyState>> by lazy { mutableMapOf<TraversalType, Set<KeyState>>() }

    fun shouldYieldFocus() = true

    internal fun revalidate_() = revalidate()

    protected fun revalidate() {
        doLayout()
        rerender()
    }

    /**
     * Tells whether this Gizmo contains the given child.
     *
     * @param child The Gizmo
     * @return true if the Gizmo is a child of the Gizmo
     */
    protected operator fun contains(child: Gizmo) = child.parent == this

    /**
     * Causes Gizmo to layout its children if it has a Layout installed.
     */
    internal fun doLayout_() = doLayout()
    protected fun doLayout() = layout?.layout(this)

    /**
     * Sets the z-index for the given Gizmo.
     *
     * @param of The Gizmo
     * @param to the new z-index
     *
     * @throws IndexOutOfBoundsException if `index !in 0 until this.children.size`
     */
    protected open fun setZIndex(of: Gizmo, to: Int) {
        children.move(of, to)
    }

    /**
     * Gets the Gizmo's z-index.
     *
     * @param of The Gizmo
     * @return The z-index (-1 if the Gizmo is not a child)
     */
    internal fun zIndex_(of: Gizmo) = zIndex(of)
    protected open fun zIndex(of: Gizmo) = children.size - children.indexOf(of) - 1

    /**
     * Gets the Gizmo at the given point.
     *
     * @param at The point
     * @return The child (null if no child contains the given point)
     */
    internal fun child_(at: Point) = child(at)
    protected open fun child(at: Point): Gizmo? = layout?.child(this, at) ?: children.lastOrNull { it.visible && at in it }

//    var dropHandler: DropHandler? = null
//        set(new) {
//            if (field === new) {
//                return
//            }
//
//            field?.target = null
//
//            field = new?.also {
//                it.target = this
//            }
//        }
//
//    var inputVerifier: InputVerifier<*>? = null
//
//    var dataTransporter: DataTransporter? = null
//

    /**
     * Gives the Gizmo an opportunity to render itself to the given Canvas.
     *
     * @param canvas The canvas upon which drawing will be done
     */
    open fun render(canvas: Canvas) {}

    /**
     * A way of prompting a Gizmo to redraw itself. This results
     * in a render request to the rendering subsystem that will
     * result in a call to [Gizmo.render] if needed
     * repainting.
     */
    fun rerender() = renderManager?.render(this)

    /**
     * A way of prompting a Gizmo to redraw itself immediately. This results in
     * a render request to the rendering subsystem that will result in a call to
     * Gizmo.Render with no delay. Only use this method for time-sensitive
     * drawing as is the case for animations.
     */
    fun rerenderNow() = renderManager?.renderNow(this) // TODO: Remove?


    /**
     * Gets the tool-tip text based on the given mouse event. Override this method to provide
     * multiple tool-tip text values for a single Gizmo.
     *
     * @param for The mouse event to generate a tool-tip for
     * @return The text
     */
    fun toolTipText(@Suppress("UNUSED_PARAMETER") `for`: MouseEvent): String = toolTipText

    /**
     * Checks whether a point is within the boundaries of a Gizmo. Returns true if the point is within the Gizmo's bounding rectangle.
     *
     * @param point The point to check
     * @return true if the point falls within the Gizmo
     */
    open operator fun contains(point: Point) = point in bounds

    /**
     * Gets the set of keys used to trigger this type of focus traversal.
     *
     * @return The set of keys that will trigger this type of traversal
     */
    operator fun get(traversalType: TraversalType): Set<KeyState>? {
        return traversalKeys[traversalType]
    }

    /**
     * Sets the keys used to control focus traversals of the given type.
     *
     * @param traversalType The traversal type
     * @param keyStates     The set of keys that will trigger this type of traversal
     */
    operator fun set(traversalType: TraversalType, keyStates: Set<KeyState>?) {
        if (keyStates != null) {
            traversalKeys[traversalType] = keyStates
        } else {
            traversalKeys.remove(traversalType)
        }
    }

    fun toLocal(point: Point, from: Gizmo): Point {
        val source      = from.toAbsolute(point )
        val destination = this.toAbsolute(Origin)

        return source - destination
    }

    fun toAbsolute  (point: Point) = modifyHierarchically(point) { p, gizmo -> p + gizmo.position }
    fun fromAbsolute(point: Point) = modifyHierarchically(point) { p, gizmo -> p - gizmo.position }

    internal fun handleDisplayRectEvent_(old: Rectangle, new: Rectangle) = handleDisplayRectEvent(old, new)

    /**
     * This is an event invoked on a Gizmo in response to a change in the display rectangle.
     *
     * @param event The event
     */
    @Suppress("UNUSED_PARAMETER")
    protected open fun handleDisplayRectEvent(old: Rectangle, new: Rectangle) {}

    internal fun handleKeyEvent_(event: KeyEvent) = handleKeyEvent(event)

    /**
     * This is an event invoked on a Gizmo in response to a key event triggered in the subsystem.
     *
     * @param event The event
     */
    protected open fun handleKeyEvent(event: KeyEvent) = keyChanged.forEach {
        when(event.type) {
            KeyState.Type.Up    -> it.keyReleased(event)
            KeyState.Type.Down  -> it.keyPressed (event)
            KeyState.Type.Press -> it.keyTyped   (event)
        }
    }

    internal fun handleMouseEvent_(event: MouseEvent) = handleMouseEvent(event)

    /**
     * This is an event invoked on a Gizmo in response to a mouse event triggered in the subsystem.
     *
     * @param event The event
     */
    protected open fun handleMouseEvent(event: MouseEvent) = mouseChanged.forEach {
        when(event.type) {
            Up    -> it.mouseReleased(event)
            Down  -> it.mousePressed (event)
            Exit  -> it.mouseExited  (event)
            Enter -> it.mouseEntered (event)
            else  -> return
        }
    }

    internal fun handleMouseMotionEvent_(event: MouseEvent) = handleMouseMotionEvent(event)

    /**
     * This is an event invoked on a Gizmo in response to a mouse-motion event triggered in the subsystem.
     *
     * @param event The event
     */
    protected open fun handleMouseMotionEvent(event: MouseEvent) = mouseMotionChanged.forEach {
        when(event.type) {
            Move -> it.mouseMoved  (event)
            Drag -> it.mouseDragged(event)
            else -> return
        }
    }

    internal fun handleMouseWheelEvent_(event: MouseWheelEvent) = handleMouseWheelEvent(event)

    /**
     * This is an event invoked on a Gizmo in response to a mouse wheel event triggered in the subsystem.
     *
     * @param event The event
     */
    protected open fun handleMouseWheelEvent(event: MouseWheelEvent) {}

    /**
     * This is an event invoked on a Gizmo in response to a focus event triggered in the subsystem.
     *
     * @param event The event
     */

    internal fun handleFocusEvent(event: FocusEvent) {
        hasFocus = when(event.type) {
            Gained -> true
            Lost   -> false
        }
    }

    protected open fun addedToDisplay() {}

    /**
     * This method is invoked by the Render system when the Gizmo is first added
     * to the Display hierarchy.  This happens when the Gizmo itself,
     * or one of it's ancestors is added to the Display.
     */
    internal fun addedToDisplay(renderManager: RenderManager) {
        this.renderManager = renderManager

        addedToDisplay()
    }

    protected open fun removedFromDisplay() {}

    /**
     * This method is invoked by the Render system when the Gizmo is no longer
     * included in the Display hierarchy.  This happens when the Gizmo itself,
     * or one of it's ancestors is removed from the Display.
     */
    internal fun removedFromDisplay_() = removedFromDisplay().also { renderManager = null }

    /**
     * Sets the bounding rectangle.
     *
     * @param x      The new x position
     * @param y      The new y position
     * @param width  The new width
     * @param height The new height
     */
    private fun setBounds(x: Double, y: Double, width: Double, height: Double) {
        bounds = Rectangle(x, y, width, height)
    }

    private fun modifyHierarchically(point: Point, operation: (Point, Gizmo) -> Point): Point {
        var gizmo  = this as Gizmo?
        var result = point

        while (gizmo != null) {
            result = operation(result, gizmo)
            gizmo  = gizmo.parent
        }

        return result
    }

//    operator fun plus (listener: MouseWheelListener ): Gizmo = this.also { listeners.add   (listener, MouseWheelListener::class.java ) }
//    operator fun minus(listener: MouseWheelListener ): Gizmo = this.also { listeners.remove(listener, MouseWheelListener::class.java ) }
}
