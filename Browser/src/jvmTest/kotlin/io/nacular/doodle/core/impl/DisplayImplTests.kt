package io.nacular.doodle.core.impl

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
import io.nacular.doodle.core.ChildObserver
import io.nacular.doodle.core.Container
import io.nacular.doodle.core.Display
import io.nacular.doodle.core.Layout
import io.nacular.doodle.core.View
import io.nacular.doodle.core.container
import io.nacular.doodle.core.fill
import io.nacular.doodle.core.height
import io.nacular.doodle.core.forceBounds
import io.nacular.doodle.core.forcePosition
import io.nacular.doodle.core.view
import io.nacular.doodle.core.width
import io.nacular.doodle.dom.CSSStyleDeclaration
import io.nacular.doodle.dom.Event
import io.nacular.doodle.dom.HTMLElement
import io.nacular.doodle.dom.HtmlFactory
import io.nacular.doodle.dom.addIfNotPresent
import io.nacular.doodle.dom.clear
import io.nacular.doodle.dom.setBackgroundColor
import io.nacular.doodle.dom.setBackgroundImage
import io.nacular.doodle.dom.setBackgroundSize
import io.nacular.doodle.dom.setOpacity
import io.nacular.doodle.drawing.AffineTransform.Companion.Identity
import io.nacular.doodle.drawing.Canvas
import io.nacular.doodle.drawing.CanvasFactory
import io.nacular.doodle.drawing.Color.Companion.Red
import io.nacular.doodle.drawing.ImagePaint
import io.nacular.doodle.drawing.Paint
import io.nacular.doodle.drawing.opacity
import io.nacular.doodle.geometry.Point
import io.nacular.doodle.geometry.Rectangle
import io.nacular.doodle.geometry.Size
import io.nacular.doodle.geometry.Size.Companion.Empty
import io.nacular.doodle.layout.Insets.Companion.None
import io.nacular.doodle.system.Cursor
import io.nacular.doodle.utils.PropertyObserver
import io.nacular.doodle.utils.diff.Delete
import io.nacular.doodle.utils.diff.Differences
import io.nacular.doodle.utils.diff.Equal
import io.nacular.doodle.utils.diff.Insert
import kotlin.reflect.KProperty1
import kotlin.test.Test
import kotlin.test.expect


/**
 * Created by Nicholas Eddy on 8/10/19.
 */
class DisplayImplTests {
    @Test fun `defaults valid`() {
        expect(true, "DisplayImpl::children.isEmpty()") { display().children.isEmpty() }

        mapOf(
            DisplayImpl::size               to Empty,
            DisplayImpl::width              to 0.0,
            DisplayImpl::height             to 0.0,
            DisplayImpl::cursor             to null,
            DisplayImpl::insets             to None,
            DisplayImpl::layout             to null,
            DisplayImpl::transform          to Identity
        ).forEach { validateDefault(it.key, it.value) }
    }

    @Test fun `registers onresize`() {
        val rootElement = mockk<HTMLElement>()

        display(rootElement = rootElement)

        verify(exactly = 1) { rootElement.onresize = any() }
    }

    @Test fun `has initial window size`() {
        val rootElement = mockk<HTMLElement>().apply {
            every { offsetWidth  } returns 100
            every { offsetHeight } returns 150
        }

        expect(Size(100, 150)) { display(rootElement = rootElement).size }
    }

    @Test fun `handles window resize`() {
        var slot = slot<(Event) -> Unit>()

        val rootElement = mockk<HTMLElement>().apply {
            every { onresize = captureLambda() } answers {
                slot = lambda()
            }
        }

        val sizeObserver = mockk<PropertyObserver<Display, Size>>()

        val display = display(rootElement = rootElement).apply {
            sizeChanged += sizeObserver
        }

        val newSize = Size(100, 150)

        rootElement.apply {
            every { offsetWidth  } returns newSize.width.toInt ()
            every { offsetHeight } returns newSize.height.toInt()
        }

        slot.captured(mockk())

        verify { sizeObserver(display, Empty, newSize) }

        expect(newSize) { display.size }
    }

    @Test fun `notifies cursor change`() {
        val cursorObserver = mockk<PropertyObserver<Display, Cursor?>>()

        val display = display().apply {
            cursorChanged += cursorObserver
        }

        display.cursor = Cursor.Grab

        verify { cursorObserver(display, null, Cursor.Grab) }

        expect(Cursor.Grab) { display.cursor!! }
    }

    @Test fun `notifies child added`() {
        val observer = mockk<ChildObserver<Display>>()

        val display = display().apply { childrenChanged += observer }

        val view = view().apply { suggestPosition(x + 10.0, y + 12.0) }
        display += view

        verify (exactly = 1) {
            observer(display, Differences(listOf(Insert(view))))
        }
    }

    @Test fun `notifies child removed`() {
        val observer = mockk<ChildObserver<Display>>()

        val display = display()

        val view = view().apply { suggestPosition(x + 10.0, y + 12.0) }
        display += view

        display.childrenChanged += observer

        display -= view

        verifyOrder {
            observer(display, Differences(listOf(Delete(view))))
        }
    }

    @Test fun `notifies child moved`() {
        val observer = mockk<ChildObserver<Display>>()

        val display = display()

        val view1 = view().apply { suggestPosition(x + 10.0, y + 12.0) }
        val view2 = view().apply { suggestPosition(x + 10.0, y + 12.0) }
        val view3 = view().apply { suggestPosition(x + 10.0, y + 12.0) }

        display += listOf(view1, view2, view3)

        display.childrenChanged += observer

        display.children.move(view2, 0)

        verifyOrder {
            observer(display, Differences(listOf(Insert(view2), Equal(view1), Delete(view2), Equal(view3))))
        }
    }

    @Test fun `child at (no layout) works`() {
        val display = display()
        val child0  = view().apply { forcePosition(10.0, 12.0) }
        val child1  = view().apply { forcePosition(10.0, 12.0) }
        val child2  = view().apply { forcePosition(20.0, 12.0) }
        val child3  = view().apply { forceBounds(Rectangle(10.0, 23.0, width = 0.0, height = height)) }

        display += child0
        display += child1
        display += child2
        display += child3

        expect(child1) { display.child(at = Point(11.0, 13.0)) }
        expect(child2) { display.child(at = Point(20.0, 12.0)) }
        expect(null  ) { display.child(at = child3.position  ) }

        child1.visible = false

        expect(child0) { display.child(at = Point(11.0, 13.0)) }
    }

    @Test fun `is-ancestor works`() {
        val display = display()
        val parent  = container {}
        val child   = view {}

        expect(false) { display ancestorOf mockk() }
        expect(false) { display ancestorOf child   }

        display.children += parent
        parent.children  += child

        expect(true) { display ancestorOf parent }
        expect(true) { display ancestorOf child  }
    }

    @Test fun `layout works`() {
        val layout = mockk<Layout>()

        display().apply {
            relayout() // should no-op

            this.layout = layout

            verify (exactly= 1) { layout.layout(any(), any(), any(), any()) }

            relayout()

            verify (exactly= 2) { layout.layout(any(), any(), any(), any()) }
        }
    }

    @Test fun `plus equal child multiple times works`() {
        val display = display()
        val child0  = view()
        val child1  = view()
        val child2  = view()

        display += child0
        display += child1
        display += child2
        display += child0

        expect(listOf(child1, child2, child0)) { display.children }
    }

    @Test fun `repeated child add works`() {
        val display = display()
        val child0  = view()
        val child1  = view()
        val child2  = view()

        display.children.addAll(listOf(
            child0,
            child1,
            child2,
            child0
        ))

        expect(listOf(child1, child2, child0)) { display.children }
    }

    @Test fun `+= view works`() {
        val child   = mockk<Container>()
        val display = display()

        display += child

        expect(1    ) { display.children.size    }
        expect(child) { display.children.first() }
    }

    @Test fun `-= view works`() {
        val child   = mockk<Container>()
        val display = display()

        display += child
        display -= child

        expect(true) { display.children.isEmpty() }
    }

    @Test fun `+= collection works`() {
        val child1  = mockk<Container>()
        val child2  = mockk<View>     ()
        val display = display()

        display += listOf(child1, child2)

        expect(2     ) { display.children.size }
        expect(child1) { display.children[0]   }
        expect(child2) { display.children[1]   }
    }

    @Test fun `-= collection works`() {
        val child1  = mockk<Container>()
        val child2  = mockk<View>     ()
        val child3  = mockk<View>     ()
        val display = display()

        display += listOf(child1, child2, child3)

        display -= listOf(child1, child3)

        expect(1     ) { display.children.size    }
        expect(child2) { display.children.first() }
    }

    @Test fun `fill color works`() {
        val style = mockk<CSSStyleDeclaration>()
        val root  = mockk<HTMLElement>().apply {
            every { this@apply.style } returns style
        }

        val display = display(rootElement = root)

        display.fill(Red)

        verify(exactly = 1) { style.setBackgroundColor(Red) }
    }

    @Test fun `fill invisible color works`() {
        val style = mockk<CSSStyleDeclaration>()
        val root  = mockk<HTMLElement>().apply {
            every { this@apply.style } returns style
        }

        val display = display(rootElement = root)

        display.fill(Red opacity 0f)

        verify(exactly = 1) { style.setBackgroundColor(null) }
    }

    @Test fun `fill opaque image works`() {
        val canvasParent = mockk<HTMLElement>()
        val canvasElement = mockk<HTMLElement>().apply { every { parentNode } returns canvasParent }
        val factory = mockk<HtmlFactory>().apply {
            every { create<HTMLElement>() } returns canvasElement
        }

        val style = mockk<CSSStyleDeclaration>()
        val root  = mockk<HTMLElement>().apply {
            every { this@apply.style } returns style
        }

        val display = display(factory, rootElement = root)

        val paint = mockk<ImagePaint>().apply { every { opacity } returns 1.0f }

        display.fill(paint)

        verify(exactly = 1) {
            canvasParent.removeChild(canvasElement)
            style.setBackgroundSize (paint.size )
            style.setBackgroundImage(paint.image)
        }
    }

    @Test fun `fill translucent image works`() {
        val canvasParent  = mockk<HTMLElement>()
        val canvasStyle   = mockk<CSSStyleDeclaration>()
        val canvasElement = mockk<HTMLElement>().apply {
            every { parentNode } returns canvasParent
            every { this@apply.style } returns canvasStyle
        }

        val factory = mockk<HtmlFactory>().apply {
            every { create<HTMLElement>() } returns canvasElement
        }

        val rootStyle = mockk<CSSStyleDeclaration>()
        val root      = mockk<HTMLElement>().apply {
            every { this@apply.style } returns rootStyle
        }

        val display = display(factory, rootElement = root)

        val paint = mockk<ImagePaint>().apply { every { opacity } returns 0.5f }

        val paintSize    = paint.size
        val paintImage   = paint.image
        val paintOpacity = paint.opacity

        display.fill(paint)

        verify {
            root.addIfNotPresent(canvasElement, 0)
        }

        verify(exactly = 1) {
            rootStyle.setBackgroundColor(null)

            canvasElement.clear()
            canvasStyle.setOpacity        (paintOpacity)
            canvasStyle.setBackgroundSize (paintSize   )
            canvasStyle.setBackgroundImage(paintImage  )
        }
    }

    @Test fun `fill arbitrary paint works`() {
        val canvasParent  = mockk<HTMLElement>()
        val canvasStyle   = mockk<CSSStyleDeclaration>()
        val canvasElement = mockk<HTMLElement>().apply {
            every { parentNode } returns canvasParent
            every { this@apply.style } returns canvasStyle
        }

        val htmlFactory = mockk<HtmlFactory>().apply {
            every { create<HTMLElement>() } returns canvasElement
        }

        val canvas = mockk<Canvas>()

        val canvasFactory = mockk<CanvasFactory>().apply {
            every { this@apply(canvasElement) } returns canvas
        }

        val rootStyle = mockk<CSSStyleDeclaration>()
        val root      = mockk<HTMLElement>().apply {
            every { this@apply.style } returns rootStyle
        }

        val display = display(htmlFactory, canvasFactory, rootElement = root)

        val paint = mockk<Paint>()

        display.fill(paint)

        verify {
            root.addIfNotPresent(canvasElement, 0)
        }

        verify(exactly = 1) {
            rootStyle.setBackgroundColor(null)

            canvasElement.clear()
            canvas.clear()
            canvas.rect(Rectangle(size = display.size), paint)
        }
    }

    private fun view(): View = view { suggestSize(Size(10)) }

    private fun display(htmlFactory  : HtmlFactory   = mockk(),
                        canvasFactory: CanvasFactory = mockk(),
                        rootElement  : HTMLElement = mockk()) = DisplayImpl(htmlFactory, canvasFactory, rootElement)

    private fun <T> validateDefault(p: KProperty1<DisplayImpl, T>, default: T?) {
        expect(default, "$p defaults to $default") { p.get(display()) }
    }
}