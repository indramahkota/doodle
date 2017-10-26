package com.nectar.doodle.geometry


class Point(val x: Double, val y: Double) {

    operator fun plus (other: Point) = Point(x + other.x, y + other.y)
    operator fun minus(other: Point) = Point(x - other.x, y - other.y)

    operator fun unaryMinus() = Point(-x, -y)

    override fun hashCode(): Int = arrayOf(x, y).contentHashCode()

    override fun toString(): String = "[$x,$y]"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Point) return false

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    companion object {
        val Origin = Point(0.0, 0.0)
    }
}