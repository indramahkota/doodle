package io.nacular.doodle.theme.basic.spinner

import io.nacular.doodle.controls.StringVisualizer
import io.nacular.doodle.controls.buttons.Button
import io.nacular.doodle.controls.buttons.PushButton
import io.nacular.doodle.controls.spinner.SpinButton
import io.nacular.doodle.controls.spinner.SpinButtonBehavior
import io.nacular.doodle.controls.spinner.SpinButtonModel
import io.nacular.doodle.controls.toString
import io.nacular.doodle.core.Container
import io.nacular.doodle.core.Icon
import io.nacular.doodle.core.View
import io.nacular.doodle.drawing.AffineTransform.Companion.Identity
import io.nacular.doodle.drawing.Canvas
import io.nacular.doodle.drawing.Color
import io.nacular.doodle.drawing.ColorPaint
import io.nacular.doodle.drawing.Stroke
import io.nacular.doodle.drawing.Stroke.LineCap
import io.nacular.doodle.drawing.Stroke.LineJoint
import io.nacular.doodle.drawing.TextMetrics
import io.nacular.doodle.drawing.darker
import io.nacular.doodle.drawing.lighter
import io.nacular.doodle.drawing.paint
import io.nacular.doodle.event.KeyEvent
import io.nacular.doodle.event.KeyListener
import io.nacular.doodle.event.KeyText.Companion.ArrowDown
import io.nacular.doodle.event.KeyText.Companion.ArrowUp
import io.nacular.doodle.event.PointerEvent
import io.nacular.doodle.event.PointerListener
import io.nacular.doodle.focus.FocusManager
import io.nacular.doodle.geometry.Point
import io.nacular.doodle.geometry.Rectangle
import io.nacular.doodle.geometry.Size
import io.nacular.doodle.layout.constraints.Bounds
import io.nacular.doodle.layout.constraints.ConstraintDslContext
import io.nacular.doodle.layout.constraints.ConstraintLayout
import io.nacular.doodle.layout.constraints.center
import io.nacular.doodle.layout.constraints.constrain
import io.nacular.doodle.theme.basic.BasicButtonBehavior
import io.nacular.doodle.theme.basic.ColorMapper
import io.nacular.doodle.utils.Anchor.Leading
import io.nacular.doodle.utils.Pool
import io.nacular.doodle.utils.SetPool

@Deprecated("Use BasicSpinButtonBehavior", replaceWith = ReplaceWith("BasicSpinButtonBehavior<T, M>"))
public typealias BasicSpinnerBehavior<T, M> = BasicSpinButtonBehavior<T, M>

public class BasicSpinButtonBehavior<T, M: SpinButtonModel<T>>(
    private val textMetrics        : TextMetrics,
    private val backgroundColor    : Color,
    private val darkBackgroundColor: Color,
    private val foregroundColor    : Color,
    private val cornerRadius       : Double,
    private val buttonWidth        : Double        = 20.0,
    private val focusManager       : FocusManager? = null,
    private val incrementA11yLabel : String?       = null,
    private val decrementA11yLabel : String?       = null,
): SpinButtonBehavior<T, M>(), KeyListener, PointerListener {

    public var hoverColorMapper   : ColorMapper = { it.darker(0.1f) }
    public var disabledColorMapper: ColorMapper = { it.lighter()    }

    private inner class ButtonIcon(private val isUp: Boolean): Icon<Button> {
        override fun size(view: Button) = Size(view.width * 0.5, view.height * 0.3)

        override fun render(view: Button, canvas: Canvas, at: Point) {
            val size = size(view)
            val transform = when {
                isUp -> Identity
                else -> Identity.flipVertically(at.y + size.height / 2)
            }

            val stroke = Stroke(when {
                view.enabled -> foregroundColor
                else         -> disabledColorMapper(foregroundColor)
            }.paint, 1.5, lineJoint = LineJoint.Round, lineCap = LineCap.Round)

            canvas.transform(transform) {
                path(listOf(
                        Point(at.x,                  at.y + size.height),
                        Point(at.x + size.width / 2, at.y              ),
                        Point(at.x + size.width,     at.y + size.height)), stroke)
            }
        }
    }

    private inner class SpinButtonButtonBehavior(private val isTop: Boolean): BasicButtonBehavior(
            textMetrics         = textMetrics,
            cornerRadius        = cornerRadius,
            backgroundColor     = backgroundColor,
            foregroundColor     = foregroundColor,
            darkBackgroundColor = darkBackgroundColor
    ) {
        init {
            hoverColorMapper    = this@BasicSpinButtonBehavior.hoverColorMapper
            disabledColorMapper = { it }
        }

        override fun install(view: Button) {
            view.icon = ButtonIcon(isTop)

            super.install(view)
        }

        override fun render(view: Button, canvas: Canvas) {
            canvas.rect(
                Rectangle(
                    0.0,
                    0.0 - if (!isTop) cornerRadius else 0.0,
                    view.width,
                    view.height + cornerRadius),
                cornerRadius, colors(view).fillColor.paint)

            icon(view)?.let {
                val adjust = it.size(view).height / 5 * if (isTop) 1 else -1
                it.render(view, canvas, iconPosition(view, icon = it) + Point(0.0, adjust))
            }
        }
    }

    private val itemVisualizer by lazy { toString<T, Any>(StringVisualizer()) }

    override fun changed(spinner: SpinButton<T, M>) {}

    override fun render(view: SpinButton<T, M>, canvas: Canvas) {
        canvas.rect(view.bounds.atOrigin, cornerRadius, ColorPaint(backgroundColor))
    }

    @Suppress("LocalVariableName")
    override fun install(view: SpinButton<T, M>) {
        super.install(view)

        val center = Container().apply { focusable = false }
        val next = PushButton().apply {
            enabled            = view.hasNext
            behavior           = SpinButtonButtonBehavior(true)
            focusable          = false
            iconAnchor         = Leading
            acceptsThemes      = false
            accessibilityLabel = incrementA11yLabel
        }

        val previous = PushButton().apply {
            enabled            = view.hasPrevious
            behavior           = SpinButtonButtonBehavior(false)
            focusable          = false
            iconAnchor         = Leading
            acceptsThemes      = false
            accessibilityLabel = decrementA11yLabel
        }

        // FIXME: need to cleanup in uninstall
        view.changed += {
            updateCenter(view)

            next.enabled     = it.hasNext
            previous.enabled = it.hasPrevious
        }

        next.fired += {
            view.next()
        }

        previous.fired += {
            view.previous()
        }

        view.children.clear()
        view.children += listOf(center, next, previous)

        view.layout = constrain(center, next, previous) { center_, next_, previous_ ->
            center_.top      eq INSET
            center_.left     eq INSET
            center_.right    eq next_.left     - INSET
            center_.bottom   eq parent.bottom - INSET

            next_.top        eq INSET
            next_.right      eq parent.right - INSET
            next_.bottom     eq parent.centerY
            next_.width      eq buttonWidth

            previous_.top    eq next_.bottom
            previous_.left   eq next_.left
            previous_.right  eq next_.right
            previous_.bottom eq parent.bottom - INSET
        }

        updateCenter(view)

        view.keyChanged     += this
        view.pointerChanged += this
    }

    override fun uninstall(view: SpinButton<T, M>) {
        super.uninstall(view)

        view.children.clear()
        view.keyChanged     -= this
        view.pointerChanged -= this
    }

    override fun pressed(event: KeyEvent) {
        (event.source as? SpinButton<*,*>)?.apply {
            when (event.key) {
                ArrowUp   -> { next    (); event.consume() }
                ArrowDown -> { previous(); event.consume() }
            }
        }
    }

    override fun pressed(event: PointerEvent) {
        focusManager?.requestFocus(event.source)
    }

    internal val centerChanged: Pool<(SpinButton<T, M>, View?, View) -> Unit> = SetPool()

    internal fun updateCenter(spinButton: SpinButton<T, M>, oldCenter: View? = visualizedValue(spinButton), newCenter: View = centerView(spinButton, oldCenter)) {
        if (newCenter != oldCenter) {
            viewContainer(spinButton)?.let { centerView ->
                centerView.children.clear()

                centerView += newCenter

                updateAlignment(spinButton, centerView)
            }

            (centerChanged as SetPool).forEach { it(spinButton, oldCenter, newCenter) }
        }
    }

    private fun centerView(spinButton: SpinButton<T, M>, oldCenter: View?) = spinButton.value.fold(
        onSuccess = { (spinButton.itemVisualizer ?: itemVisualizer)(it, oldCenter, spinButton) },
        onFailure = { object: View() {} }
    )

    private fun updateAlignment(spinButton: SpinButton<T, M>, centerView: Container) {
        val constrains: ConstraintDslContext.(Bounds) -> Unit = {
            (spinButton.cellAlignment ?: center)(it)
        }

        centerView.firstOrNull()?.let { child ->
            when (val l = centerView.layout) {
                is ConstraintLayout -> { l.unconstrain(child, constrains); l.constrain(child, constrains) }
                else                -> centerView.layout = constrain(child, constrains)
            }
        }
    }

    private  fun viewContainer  (spinButton: SpinButton<T, M>): Container? = spinButton.children.firstOrNull { it !is PushButton } as? Container
    internal fun visualizedValue(spinButton: SpinButton<T, M>): View?      = viewContainer(spinButton)?.firstOrNull()

    public companion object {
        private const val INSET = 4.0
    }
}