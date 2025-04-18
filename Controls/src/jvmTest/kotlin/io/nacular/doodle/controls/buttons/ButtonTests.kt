package io.nacular.doodle.controls.buttons

import JsName
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.nacular.doodle.core.Behavior
import io.nacular.doodle.core.Icon
import io.nacular.doodle.core.View
import io.nacular.doodle.core.forceSize
import io.nacular.doodle.drawing.Canvas
import io.nacular.doodle.geometry.Point
import io.nacular.doodle.geometry.Size
import io.nacular.doodle.utils.Anchor.Left
import io.nacular.doodle.utils.ChangeObserver
import io.nacular.doodle.utils.HorizontalAlignment.Center
import io.nacular.doodle.utils.HorizontalAlignment.Right
import io.nacular.doodle.utils.PropertyObserver
import io.nacular.doodle.utils.PropertyObservers
import io.nacular.doodle.utils.VerticalAlignment.Bottom
import io.nacular.doodle.utils.VerticalAlignment.Middle
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.test.Test
import kotlin.test.expect

/**
 * Created by Nicholas Eddy on 3/15/20.
 */
class ButtonTests {
    private class TestButton(text: String = "", icon: Icon<Button>? = null, model: ButtonModel): Button(text, icon, model) {
        override fun click() {}
    }

    @Test @JsName("defaults")
    fun `defaults valid`() {
        mapOf(
                Button::text                    to "",
                Button::icon                    to null,
                Button::behavior                to null,
                Button::selected                to false,
                Button::iconAnchor              to Left,
                Button::pressedIcon             to null,
                Button::disabledIcon            to null,
                Button::selectedIcon            to null,
                Button::pointerOverIcon         to null,
                Button::iconTextSpacing         to 4.0,
                Button::verticalAlignment       to Middle,
                Button::horizontalAlignment     to Center,
                Button::disabledSelectedIcon    to null,
                Button::pointerOverSelectedIcon to null
        ).forEach { validateDefault(it.key, it.value) }
    }

    @Test @JsName("settersWork")
    fun `setters work`() {
        validateSetter(Button::text,                    "foo"                )
        validateSetter(Button::icon,                    mockk(relaxed = true))
        validateSetter(Button::behavior,                null                 )
        validateSetter(Button::iconAnchor,              Left                 )
        validateSetter(Button::pressedIcon,             mockk(relaxed = true))
        validateSetter(Button::disabledIcon,            mockk(relaxed = true))
        validateSetter(Button::selectedIcon,            mockk(relaxed = true))
        validateSetter(Button::pointerOverIcon,         mockk(relaxed = true))
        validateSetter(Button::iconTextSpacing,         5.6                  )
        validateSetter(Button::verticalAlignment,       Bottom               )
        validateSetter(Button::horizontalAlignment,     Right                )
        validateSetter(Button::disabledSelectedIcon,    mockk(relaxed = true))
        validateSetter(Button::pointerOverSelectedIcon, mockk(relaxed = true))
    }

    @Test @JsName("iconsFallback")
    fun `icons fallback`() {
        TestButton(icon = mockk(relaxed = true), model = mockk(relaxed = true)).apply {
            expect(pressedIcon            ) { icon         }
            expect(disabledIcon           ) { icon         }
            expect(selectedIcon           ) { icon         }
            expect(pointerOverIcon        ) { icon         }
            expect(disabledSelectedIcon   ) { disabledIcon }
            expect(pointerOverSelectedIcon) { selectedIcon }
        }
    }

    @Test @JsName("selectionNotifiesModel")
    fun `selection notifies model`() {
        val model = mockk<ButtonModel>(relaxed = true)

        TestButton(model = model).apply {
            selected = true

            verify(exactly = 1) { model.selected = true }
        }
    }

    @Test @JsName("cannotSelectDisabled")
    fun `cannot select disabled`() {
        val model = mockk<ButtonModel>(relaxed = true)

        TestButton(model = model).apply {
            enabled  = false
            selected = true

            verify (exactly = 0) { model.selected }
        }
    }

    @Test @JsName("installsUninstallsBehaviors")
    fun `installs and uninstalls behaviors`() {
        val button    = TestButton(model = mockk(relaxed = true))
        val behavior1 = mockk<Behavior<Button>>(relaxed = true)
        val behavior2 = mockk<Behavior<Button>>(relaxed = true)

        button.behavior = behavior1

        verify(exactly = 1) { behavior1.install  (button) }
        verify(exactly = 0) { behavior1.uninstall(any() ) }

        button.behavior = behavior2

        verify(exactly = 1) { behavior1.uninstall(button) }
        verify(exactly = 1) { behavior2.install  (button) }
    }

    @Test @JsName("modelChangeWorks")
    fun `model change works`() {
        val button = TestButton(model = mockk(relaxed = true))
        val model1 = mockk<ButtonModel>(relaxed = true)
        val model2 = mockk<ButtonModel>(relaxed = true)

        button.model = model1
        button.model = model2

        verify(atLeast = 1) { model1.fired += any() }
        verify(atLeast = 1) { model1.fired -= any() }
    }

    @Test @JsName("notifiesOfTextChange")
    fun `notifies of text change`() {
        val button    = TestButton(model = mockk(relaxed = true))
        val listener = mockk<PropertyObserver<Button, String>>(relaxed = true)

        button.textChanged += listener

        button.text = "foo"

        verify(exactly = 1) { listener(button, "", "foo") }
    }

    @Test @JsName("notifiesWhenModelFires")
    fun `notifies when model fires`() {
        val listener = slot<ChangeObserver<ButtonModel>>()

        val model = mockk<ButtonModel>(relaxed = true).apply {
            every { fired += capture(listener) } just Runs
        }

        val myListener = mockk<ChangeObserver<Button>>(relaxed = true)

        val button = TestButton(model = model).apply {
            fired += myListener
        }

        listener.captured.invoke(model)

        verify(exactly = 1) { myListener.invoke(button) }
    }

    @Test @JsName("renderWithoutBehaviorNoOp")
    fun `render without behavior no-op`() {
        val button = TestButton(model = mockk(relaxed = true))

        button.render(mockk(relaxed = true))
    }

    @Test @JsName("delegatesRenderToBehavior")
    fun `delegates render to behavior`() {
        val button   = TestButton(model = mockk(relaxed = true))
        val canvas   = mockk<Canvas>(relaxed = true)
        val behavior = mockk<Behavior<Button>>(relaxed = true)

        button.behavior = behavior

        button.render(canvas)

        verify(exactly = 1) { behavior.render(button, canvas) }
    }

    @Test @JsName("styleChangeEventsWork")
    fun `style change events work`() {
        validateStyleChanged(Button::icon,                    mockk(relaxed = true))
        validateStyleChanged(Button::iconAnchor,              mockk(relaxed = true))
        validateStyleChanged(Button::pressedIcon,             mockk(relaxed = true))
        validateStyleChanged(Button::disabledIcon,            mockk(relaxed = true))
        validateStyleChanged(Button::selectedIcon,            mockk(relaxed = true))
        validateStyleChanged(Button::pointerOverIcon,         mockk(relaxed = true))
        validateStyleChanged(Button::iconTextSpacing,         5.6                  )
        validateStyleChanged(Button::verticalAlignment,       mockk(relaxed = true))
        validateStyleChanged(Button::horizontalAlignment,     mockk(relaxed = true))
        validateStyleChanged(Button::disabledSelectedIcon,    mockk(relaxed = true))
        validateStyleChanged(Button::pointerOverSelectedIcon, mockk(relaxed = true))
    }

    @Test @JsName("delegatesContainsPointToBehavior")
    fun `delegates contains point to behavior`() {
        val button   = TestButton(model = mockk(relaxed = true)).apply { forceSize(Size(100)) }
        val behavior = mockk<Behavior<Button>>(relaxed = true)
        val point    = Point(4, 5)

        button.behavior = behavior

        point in button

        verify(exactly = 1) { behavior.contains(button, point) }
    }

    private fun <T> validateDefault(p: KProperty1<Button, T>, default: T?) {
        expect(default, "$p defaults to $default") { p.get(object: Button() { override fun click() {} }) }
    }

    private fun validateChanged(property: KMutableProperty1<Button, Boolean>, changed: KProperty1<Button, PropertyObservers<Button, Boolean>>) {
        val view     = object: Button() { override fun click() {} }
        val old      = property.get(view)
        val observer = mockk<PropertyObserver<Button, Boolean>>(relaxed = true)

        changed.get(view).plusAssign(observer)

        property.set(view, !property.get(view))

        verify(exactly = 1) { observer(view, old, property.get(view)) }
    }

    private fun <T> validateSetter(p: KMutableProperty1<Button, T>, value: T) {
        TestButton(model = mockk(relaxed = true)).also {
            p.set(it, value)

            expect(value, "$p set to $value") { p.get(it) }
        }
    }

    private fun <T: Any?> validateStyleChanged(property: KMutableProperty1<Button, T>, value: T) {
        val button   = TestButton(model = mockk(relaxed = true))
        val observer = mockk<ChangeObserver<View>>(relaxed = true)

        button.styleChanged += observer

        property.set(button, value)

        verify(exactly = 1) { observer(button) }
    }
}