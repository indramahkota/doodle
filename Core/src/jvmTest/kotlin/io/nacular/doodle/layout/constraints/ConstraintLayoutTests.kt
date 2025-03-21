package io.nacular.doodle.layout.constraints

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.nacular.doodle.accessibility.AccessibilityManager
import io.nacular.doodle.core.ChildObserver
import io.nacular.doodle.core.Display
import io.nacular.doodle.core.InternalDisplay
import io.nacular.doodle.core.Layout
import io.nacular.doodle.core.Layout.Companion.simpleLayout
import io.nacular.doodle.core.View
import io.nacular.doodle.core.container
import io.nacular.doodle.core.forceHeight
import io.nacular.doodle.core.forceSize
import io.nacular.doodle.core.view
import io.nacular.doodle.drawing.GraphicsDevice
import io.nacular.doodle.drawing.impl.RenderManagerImpl
import io.nacular.doodle.geometry.Point
import io.nacular.doodle.geometry.Rectangle
import io.nacular.doodle.geometry.Size
import io.nacular.doodle.geometry.times
import io.nacular.doodle.layout.Insets
import io.nacular.doodle.layout.constraints.Strength.Companion.Medium
import io.nacular.doodle.layout.constraints.Strength.Companion.Strong
import io.nacular.doodle.layout.constraints.impl.Solver
import io.nacular.doodle.scheduler.AnimationScheduler
import io.nacular.doodle.scheduler.Task
import io.nacular.doodle.theme.InternalThemeManager
import io.nacular.doodle.utils.ObservableList
import io.nacular.doodle.utils.SetPool
import io.nacular.measured.units.Measure
import io.nacular.measured.units.Time
import io.nacular.measured.units.times
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.expect


/**
 * Created by Nicholas Eddy on 3/8/22.
 */
class ConstraintLayoutTests {
    @Test fun foo() {
        val child       = NamedView("child").apply { suggestWidth(10.0) }
        val parent      = container { + child }
        val grandParent = container {
            + parent

            layout = simpleLayout { items,_,_,_,_ ->
                items.forEach {
                    it.updateBounds(0.0, 0.0, Size(10), Size.Infinite)
                }

                Size(100)
            }
        }

        parent.layout = constrain(child) {
            this.parent.width greaterEq it.right.readOnly + 10
        }

        grandParent.doLayout_()

        expect(20.0) { parent.width }
    }

    @Test fun basic() {
        val child1    = NamedView("child1").apply { forceHeight(10.0) }
        val child2    = NamedView("child2")
        val container = container {
            + listOf(child1, child2)
        }

        container.layout = constrain(child1, child2) { a, b ->
            a.width   eq parent.width  - 10
            a.height  eq parent.height - 10
            b.width   eq 50
            b.height  eq 50
            b.centerX eq parent.centerX
        }

        container.forceSize(Size(200))

        container.doLayout_()

        expect(Size ( 50   )) { child2.size     }
        expect(Point( 75, 0)) { child2.position }

        expect(Size(200 - 10)) { child1.size }
    }

    @Test fun `with updates`() {
        val child1 = NamedView("child1").apply { suggestWidth(100.0) }
        val child2 = NamedView("child2")

        val container = container {
            + listOf(child1, child2)
        }

        container.layout = constrain(child1, child2) { a, b ->
            a.top     eq     0
            a.left    eq     0
            a.width   lessEq parent.width / 3
            a.height  eq     parent.height

            b.top     eq     a.top
            b.left    eq     a.right
            b.height  eq     a.height

            a.width + b.width eq parent.width
        }

        container.forceSize(Size(1200))
        container.doLayout_()

        expect(Rectangle(100, 0, 1100, 1200)) { child2.bounds }
        expect(Rectangle(         100, 1200)) { child1.bounds }

        child1.suggestWidth(200.0)
        container.doLayout_()

        expect(Rectangle(200, 0, 1000, 1200)) { child2.bounds }
        expect(Rectangle(         200, 1200)) { child1.bounds }
    }

    @Test fun `widths sum`() {
        val child1 = NamedView("child1").apply { suggestWidth(400.0) }
        val child2 = NamedView("child2")
        val child3 = NamedView("child3")
        val child4 = NamedView("child4")

        val container = container {
            + listOf(child1, child2, child3, child4)
        }

        container.layout = constrain(child1, child2, child3) { a, b, c ->
            a.top    eq 0
            a.left   eq 0
            a.width  lessEq parent.width / 3
            a.height eq     parent.height

            b.top    eq a.top
            b.left   eq a.right
            b.height eq a.height

            c.top    eq a.top
            c.left   eq b.right
            c.height eq a.height

            a.width + b.width + c.width eq parent.width
        }

        container.forceSize(Size(1200))
        container.doLayout_()

        expect(400.0, 0.0, 800.0) { listOf(child1, child2, child3).map { it.width } }

        child1.suggestWidth(200.0)
        container.doLayout_()

        expect(200.0, 200.0, 800.0) { listOf(child1, child2, child3).map { it.width } }

        child3.suggestX(500.0)
        container.doLayout_()

        expect(200.0, 200.0, 800.0) { listOf(child1, child2, child3).map { it.width } }
    }

    @Test fun `display constraints work`() {
        val view = view {}

        var c = emptyList<Constraint>()

        val layout = constrain(view) {
            c = listOf(
                it.top    greaterEq 10,
                it.left   greaterEq 10,
                it.right  lessEq    parent.right  - 10,
                it.bottom lessEq    parent.bottom - 10,

                it.width  eq 500 strength  Medium,
                it.height eq 500 strength  Medium
            ).takeLast(1).map { it.getOrThrow() }
        }

        layout.layout(sequenceOf(view).map { it.positionable }, Size.Empty, Size(100), Size.Infinite)

        val solver = Solver()

        repeat(1) {
            c.forEach { solver.addConstraint   (it) }
            c.forEach { solver.removeConstraint(it) }
        }

        expect(80.0) { view.height }
    }

    @Test fun `layout using render manager`() {
        val child1 = NamedView("child1").apply { suggestWidth(400.0) }
        val child2 = NamedView("child2").apply { suggestWidth(400.0) }

        val parent = container {
            + listOf(child1, child2)
            layout = constrain(child1, child2) { a, b ->
                a.top    eq     0
                a.left   eq     0
                a.width  lessEq parent.width / 3
                a.height eq     parent.height

                b.center eq parent.center
                b.height eq 400           strength Medium
            }
        }

        val grandParent = container {
            + listOf(parent)
            layout = constrain(parent) {
                it.top    eq     0
                it.bottom lessEq this.parent.bottom

                it.width  eq 500 strength Medium
                it.height eq 500 strength Medium
            }

            forceSize(Size(1000))
        }

        val scheduler = ManualAnimationScheduler()

        renderManager(display = display(grandParent), scheduler = scheduler)

        scheduler.runJobs()

        expect(1000.0) { grandParent.height }
        expect( 500.0) { parent.height      }
        expect( 500.0) { child1.height      }

        grandParent.forceHeight(400.0)

        scheduler.runJobs()

        expect(400.0) { grandParent.height }
        expect(400.0) { parent.height      }
        expect(400.0) { child1.height      }
    }

    @Test fun `adding constraint works`() {
        val view1 = NamedView("view1")
        val view2 = NamedView("view2")
        val view3 = NamedView("view3")

        val container = container {
            + listOf(view1, view2, view3)
        }

        container.layout = constrain(view1, view2) { v1, v2 ->
            v1.left   eq 23
            v1.height eq 100

            v2.top eq v1.bottom
        }

        view1.suggestX     (67.0)
        view1.forceHeight(67.0)
        view2.suggestY     (67.0)
        view3.suggestWidth (67.0)

        container.doLayout_()

        expect( 23.0) { view1.x      }
        expect(100.0) { view1.height }
        expect(100.0) { view2.y      }

        (container.layout as ConstraintLayout).constrain(view3) {
            it.width eq parent.width
        }

        container.suggestWidth(211.0)

        container.doLayout_()

        expect( 23.0) { view1.x      }
        expect(100.0) { view1.height }
        expect(100.0) { view2.y      }
        expect(211.0) { view3.width  }
    }

    @Test fun `un-constrain works`() {
        val view1 = NamedView("view1")
        val view2 = NamedView("view2")
        val view3 = NamedView("view3")

        val container = container {
            + listOf(view1, view2, view3)
        }

        val constraints: ConstraintDslContext.(Bounds, Bounds, Bounds) -> Unit = { v1, v2, v3 ->
            v1.left   eq 23
            v1.height eq 100

            v2.top eq v1.bottom

            v3.width eq 100
        }

        container.layout = constrain(view1, view2, view3, constraints)

        view1.suggestX     (67.0)
        view1.forceHeight(67.0)
        view2.suggestY     (67.0)
        view3.suggestWidth (67.0)

        container.doLayout_()

        expect( 23.0) { view1.x      }
        expect(100.0) { view1.height }
        expect(100.0) { view2.y      }
        expect(100.0) { view3.width  }

        (container.layout as ConstraintLayout).unconstrain(view1, view2, view3, constraints)

        container.forceSize(container.size * 2)

        container.doLayout_()

        expect( 23.0) { view1.x      }
        expect(100.0) { view1.height }
        expect(100.0) { view2.y      }
        expect(100.0) { view3.width  }
    }

    @Test fun `edges with inset`() {
        val view = view {}

        val align: ConstraintDslContext.(Bounds) -> Unit = {
            it.edges eq parent.edges + Insets(10.0)
        }

        val container = container {
            this   += view
            forceSize(Size(100))
            layout = constrain(view) {
                align(it)
            }
        }

        container.doLayout_()

        expect(Rectangle(10, 10, 80, 80)) { view.bounds }
    }

    @Test fun `constrain iterable within bounds`() {
        val view1 = view {}
        val view2 = view {}
        val view3 = view {}
        val view4 = view {}
        val constraint: ConstraintDslContext.(Bounds) -> Unit = {
            it.edges eq parent.edges
        }

        listOf(view1, view2, view3, view4).map { it.positionable }.constrain(using = constraint) { index,_ ->
            Rectangle(index, index, 100, 100)
        }

        expect(listOf(
            Rectangle(0, 0, 100, 100),
            Rectangle(1, 1, 100, 100),
            Rectangle(2, 2, 100, 100)
        )) { listOf(view1, view2, view3).map { it.bounds } }
    }

    @Test fun `non-required const updates`() {
        val headerStartHeight = 117.0
        val footerStartHeight =  56.0

        val header       = NamedView("header").apply { forceHeight(117.0) }
        val main         = NamedView("main"  ).apply { forceHeight(186.0) }
        val footer       = NamedView("footer").apply { forceHeight( 56.0) }
        val minHeight    = 106.0
        var targetHeight = 174

        val container = container {
            + listOf(header, main, footer)

            @Suppress("LocalVariableName")
            layout = constrain(header, main, footer) { header_, main_, footer_ ->
                listOf(header_, main_, footer_).forEach { it.centerX eq parent.centerX }
                header_.top    eq       9
                header_.height.preserve

                main_.top      eq        header_.bottom + 5
                main_.height   greaterEq minHeight
                main_.height   eq        minHeight + targetHeight strength Medium

                footer_.top    eq        main_.bottom + 65
                footer_.bottom lessEq    parent.bottom.readOnly   strength Strong
                footer_.height.preserve
            }

            forceHeight(9 + headerStartHeight + 5 + minHeight + targetHeight + 65 + footerStartHeight)
        }

        container.doLayout_()

        expect(minHeight + targetHeight) { main.height }

        targetHeight = 0

        container.doLayout_()

        expect(minHeight) { main.height }

        targetHeight = 174
        container.forceHeight(container.height - 1)
        container.doLayout_()

        expect(container.height - (main.y + footer.height + 65)) { main.height }

        container.forceHeight(100.0)
        container.doLayout_()

        expect(minHeight) { main.height }
    }

    @Test fun `center works`() {
        val childSize = Size(100)
        val child     = view {}.apply { forceSize(childSize) }

        val layout = constrain(child) {
            it.center eq parent.center
        }

        val parentSize          = Size(200)
        val expectedChildBounds = Rectangle(
            (parentSize.width  - childSize.width ) / 2,
            (parentSize.height - childSize.height) / 2,
            childSize.width,
            childSize.height
        )

        val doLayout = {
            layout.layout(sequenceOf(child).map { it.positionable }, parentSize, parentSize, parentSize)
        }

        doLayout()
        expect(expectedChildBounds) { child.bounds }

        child.suggestX(child.x - 10)

        doLayout()
        expect(expectedChildBounds) { child.bounds }
    }

//    @Test fun `min works`() {
//        val child1    = object: View() { override fun toString() = "child1" }
//        val child2    = object: View() { override fun toString() = "child2" }
//        val child3    = object: View() { override fun toString() = "child3" }
//        val container = container {
//            + listOf(child1, child2, child3)
//        }
//
//        val w1 = 100.1
//        val w2 = 1000.0
//
//        container.layout = constrain(child1, child2, child3) { a, b, c ->
//            a.width eq w1
//            b.width eq w2
//
//            c.width eq min(a.width, b.width)
//        }
//
//        container.doLayout_()
//
//        expect(w1         ) { child1.width }
//        expect(w2         ) { child2.width }
//        expect(min(w1, w2)) { child3.width }
//    }
//
//    @Test fun `max works`() {
//        val child1    = object: View() { override fun toString() = "child1" }
//        val child2    = object: View() { override fun toString() = "child2" }
//        val child3    = object: View() { override fun toString() = "child3" }
//        val container = container {
//            + listOf(child1, child2, child3)
//        }
//
//        val w1 = 1000.0
//        val w2 = 1000.1
//
//        container.layout = constrain(child1, child2, child3) { a, b, c ->
//            a.width eq w1
//            b.width eq w2
//
//            c.width eq max(a.width, b.width)
//        }
//
//        container.doLayout_()
//
//        expect(w1         ) { child1.width }
//        expect(w2         ) { child2.width }
//        expect(max(w1, w2)) { child3.width }
//    }

    private inline fun <T> expect(first: T, second: T, vararg expected: T, block: () -> List<T>) {
        assertEquals(listOf(first, second) + expected.toList(), block())
    }

    private fun display(vararg children: View, layout: Layout? = null): InternalDisplay = mockk<InternalDisplay>().apply {
        val displayChildren = ObservableList<View>()

        displayChildren.addAll(children)

        val observers = SetPool<ChildObserver<Display>>()

        displayChildren.changed += { _, differences ->
            observers.forEach { it(this, differences) }
        }

        val view = slot<View>()

        every { this@apply.size                      } returns Size(100, 100)
        every { this@apply.children                  } returns displayChildren
        every { this@apply.iterator()                } answers { displayChildren.iterator() }
        every { this@apply.childrenChanged           } returns observers
        every { sizeChanged                          } returns mockk()
        every { this@apply.ancestorOf(capture(view)) } answers {
            var result = false

            if (this@apply.children.isNotEmpty()) {
                var parent: View? = view.captured

                do {
                    if (parent in this@apply.children) {
                        result = true
                        break
                    }

                    parent = parent?.parent
                } while (parent != null)
            }

            result
        }
        every { this@apply.layout                    } returns layout
    }

    private fun renderManager(
        display             : InternalDisplay      = mockk(),
        themeManager        : InternalThemeManager = mockk(),
        scheduler           : AnimationScheduler   = mockk(),
        accessibilityManager: AccessibilityManager = mockk(),
        graphicsDevice      : GraphicsDevice<*>    = mockk()): Pair<RenderManagerImpl, AccessibilityManager> = RenderManagerImpl(display, scheduler, themeManager, accessibilityManager, graphicsDevice) to accessibilityManager

    private class NamedView(private val name: String): View() {
        override fun toString() = name
    }

    private class SimpleTask(override var completed: Boolean = false) : Task {
        override fun cancel() {
            completed = true
        }
    }

    private class ManualAnimationScheduler: AnimationScheduler {
        val tasks = mutableListOf<Pair<SimpleTask, (Measure<Time>) -> Unit>>()

        fun runJobs() {
            while (tasks.isNotEmpty()) {
                val running = ArrayList(tasks)
                tasks.clear()

                running.forEach {
                    it.first.completed = true
                    it.second(0 * Time.milliseconds)
                }
            }
        }

        override fun onNextFrame(job: (Measure<Time>) -> Unit): Task {
            val task = SimpleTask()

            tasks += task to job

            return task
        }
    }
}