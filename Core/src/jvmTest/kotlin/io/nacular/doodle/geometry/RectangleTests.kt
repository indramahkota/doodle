package io.nacular.doodle.geometry

import io.nacular.doodle.layout.Insets
import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.test.expect

/**
 * Created by Nicholas Eddy on 2/20/18.
 */
class RectangleTests {
    @Test
    fun `default == empty`() = expect(Rectangle.Empty) { Rectangle() }

    @Test
    fun `defaults to origin`() = expect(Point.Origin) { Rectangle(width = 1.0, height = 2.0).position }

    @Test
    fun at() = listOf(
            (10  by  3) to Point(1.0, 4.5),
            (1   by  1) to Point(-4.0, 4.5),
            (100 by 37) to Point(1.0, -4.5)
    ).forEach {
        expect(Rectangle(it.second, size = it.first.size)) { it.first.at(it.second) }
        expect(Rectangle(it.second, size = it.first.size)) { it.first.at(it.second.x, it.second.y) }
    }

    @Test
    fun `at origin`() = listOf(
            (10  by  3).at( 1.0,  4.5),
            (1   by  1).at(-4.0,  4.5),
            (100 by 37).at( 1.0, -4.5)
    ).forEach {
        expect(Rectangle(Point.Origin, size = it.size)) { it.atOrigin }
    }

    @Test
    fun `negative side fails`() {
        expect(Rectangle(0,1)) { Rectangle(width = -20.0, height =   1.0) }
        expect(Rectangle(1,0)) { Rectangle(width =   1.0, height = -20.0) }
    }

    @Test
    fun `empty has area 0`() = expect(0.0) { Rectangle.Empty.area }

    @Test
    fun `zero side has area 0`() = expect(0.0) { Rectangle(width = 0.0, height = 100.0).area }

    @Test
    fun `area works`() = listOf(10 by 3 to 30, 0 by 56 to 0, 5 by 5 to 25).forEach {
        expect(it.second.toDouble()) { it.first.area }
    }

    @Test
    fun `width height work`() = listOf(10 to 3, 0 to 56, 5 to 5).forEach {
        val rect = it.first by it.second

        expect(it.first.toDouble()) { rect.width }
        expect(it.second.toDouble()) { rect.height }
    }

    @Test
    fun `inset works`() = listOf(10 by 3, 0 by 56, 5 by 5).forEach { rect ->
        listOf(Insets.None, Insets(1.0), Insets(top = 1.0), Insets(left = 1.0)).forEach {
            expect(
                Rectangle(
                    rect.x + it.left,
                    rect.y + it.top,
                    max(0.0, rect.width - (it.left + it.right)),
                    max(0.0, rect.height - (it.top + it.bottom))
                )
            ) {
                rect.inset(it)
            }
        }

        val i = 5.6

        expect(
            Rectangle(
                rect.x + i,
                rect.y + i,
                max(0.0, rect.width - 2 * i),
                max(0.0, rect.height - 2 * i)
            )
        ) { rect.inset(i) }
    }

    @Test
    fun `contains point`() {
        listOf(
            Rectangle.Empty to Rectangle.Empty.position  to false,
            10 by 5 to Point(x = 10.0) to true
        ).forEach {
            validateContains(it.first.first, it.first.second, it.second)
        }
    }

    @Test
    fun `point in`() {
        listOf(
                Rectangle.Empty to Rectangle.Empty.position  to false,
                10 by 5 to Point(x = 10.0) to true
        ).forEach {
            validateIn(it.first.first, it.first.second, it.second)
        }
    }

    @Test
    fun `contains rect`() {
        listOf(
                Rectangle.Empty to Rectangle.Empty to false,
                10 by 5                    to (  1 by   1)                 to true,
                (10 by 10).at( 10.0, 10.0) to (100 by 100)                 to false,
                (10 by 10).at( 10.0, 10.0) to ( 10 by  11).at( 10.0, 10.0) to false,
                (10 by 10).at( 10.0, 10.0) to ( 10 by  10).at( 10.0, 10.0) to true,
                (10 by 10).at(-10.0, 10.0) to ( 10 by  10).at(-10.0, 10.0) to true,
                (10 by 10).at(-10.0, 10.0) to ( 10 by  11).at(-10.0, 10.0) to false
        ).forEach {
            validateContains(it.first.first, it.first.second, it.second)
        }
    }

    @Test
    fun `rect in`() {
        listOf(
                Rectangle.Empty to Rectangle.Empty to false,
                10 by 5                    to (  1 by   1)                 to true,
                (10 by 10).at( 10.0, 10.0) to (100 by 100)                 to false,
                (10 by 10).at( 10.0, 10.0) to ( 10 by  11).at( 10.0, 10.0) to false,
                (10 by 10).at( 10.0, 10.0) to ( 10 by  10).at( 10.0, 10.0) to true,
                (10 by 10).at(-10.0, 10.0) to ( 10 by  10).at(-10.0, 10.0) to true,
                (10 by 10).at(-10.0, 10.0) to ( 10 by  11).at(-10.0, 10.0) to false
        ).forEach {
            validateIn(it.first.first, it.first.second, it.second)
        }
    }

    @Test
    fun intersects() {
        listOf(
                Rectangle.Empty to Rectangle.Empty to false,
                10 by 5                    to (  1 by   1)                 to true,
                (10 by 10).at( 10.0, 10.0) to (100 by 100)                 to true,
                (10 by 10).at( 10.0, 10.0) to ( 10 by  11).at( 10.0, 10.0) to true,
                (10 by 10).at( 10.0, 10.0) to ( 10 by  10).at( 10.0, 10.0) to true,
                (10 by 10).at(-10.0, 10.0) to ( 10 by  10).at(-10.0, 10.0) to true,
                (10 by 10).at(-10.0, 10.0) to ( 10 by  11).at(-10.0, 10.0) to true,
                (10 by 10)                 to ( 10 by  10).at(x = -10.0  ) to false,
                (10 by 10)                 to ( 10 by  10).at(x = - 0.001) to true
        ).forEach {
            validateIntersects(it.first.first, it.first.second, it.second)
        }
    }

    @Test
    fun intersect() {
        listOf(
                Rectangle.Empty to Rectangle.Empty to Rectangle.Empty,
                (10 by 10).at( 11.0, 11.0) to ( 10 by  10)                 to Rectangle.Empty,
                10 by 5                    to (  1 by   1)                 to ( 1     by  1),
                (10 by 10).at( 10.0, 10.0) to (100 by 100)                 to (10     by 10).at( 10.0, 10.0),
                (10 by 10).at( 10.0, 10.0) to ( 10 by  11).at( 10.0, 10.0) to (10     by 10).at( 10.0, 10.0),
                (10 by 10).at( 10.0, 10.0) to ( 10 by  10).at( 10.0, 10.0) to (10     by 10).at( 10.0, 10.0),
                (10 by 10).at(-10.0, 10.0) to ( 10 by  10).at(-10.0, 10.0) to (10     by 10).at(-10.0, 10.0),
                (10 by 10).at(-10.0, 10.0) to ( 10 by  11).at(-10.0, 10.0) to (10     by 10).at(-10.0, 10.0),
                (10 by 10)                 to ( 10 by  10).at(x = -10.0  ) to ( 0     by 10),
                (10 by 10)                 to ( 10 by  10).at(x = -0.001 ) to ( 9.999 by 10)
        ).forEach {
            validateIntersect(it.first.first, it.first.second, it.second)
        }
    }

    private fun validateContains(rect: Rectangle, point: Point, expected: Boolean) =
        expect(expected, "$rect contains $point") { point in rect }

    private fun validateIn(rect: Rectangle, point: Point, expected: Boolean) =
        expect(expected, "$point in $rect") { point in rect }

    private fun validateContains(rect: Rectangle, other: Rectangle, expected: Boolean) =
        expect(expected, "$rect contains $other") { other in rect }

    private fun validateIn(rect: Rectangle, other: Rectangle, expected: Boolean) =
        expect(expected, "$other in $rect") { other in rect }

    private fun validateIntersects(rect: Rectangle, other: Rectangle, expected: Boolean) {
        expect(expected, "$rect intersects $other") { rect intersects other }
        expect(expected, "$other intersects $rect") { other intersects rect }
    }

    private fun validateIntersect(rect: Rectangle, other: Rectangle, expected: Rectangle) {
        expect(expected, "$rect intersect $other") { rect intersect other }
        expect(expected, "$other intersect $rect") { other intersect rect }
    }

    private infix fun Number.by(that: Number) = Rectangle(width = this.toDouble(), height = that.toDouble())
}