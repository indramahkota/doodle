package io.nacular.doodle.animation.transition

import io.nacular.doodle.animation.Velocity
import io.nacular.measured.units.Measure
import io.nacular.measured.units.Time
import io.nacular.measured.units.div
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

public typealias EasingFunction = (Float) -> Float

internal class TimedEasing(private val duration: Measure<Time>, private val function: EasingFunction) {
    @Suppress("UNUSED_PARAMETER")
    fun duration(start: Double, end: Double, initialVelocity: Velocity<Double>): Measure<Time> = duration

    fun value(start: Double, end: Double, @Suppress("UNUSED_PARAMETER") initialVelocity: Velocity<Double>, elapsedTime: Measure<Time>): Double {
        val delta = min(1f, (elapsedTime / duration).toFloat())

        val current = function(delta)

        return start + (end - start) * current
    }
}

/**
 * Ported from: https://gist.github.com/mbostock/5743979
 *
 * Equivalent to when [initialBounceFraction] is `0.25`
 * private val bounceOut: EasingFunction = {
 *     var x  = it
 *     val d1 = 2.75f
 *     val n1 = 7.5625f
 *
 *     when {
 *         it < 1f   / d1 -> n1 * x * x
 *         it < 2f   / d1 -> { x -= 1.5f   / d1; n1 * x * x + 0.75f     }
 *         it < 2.5f / d1 -> { x -= 2.25f  / d1; n1 * x * x + 0.9375f   }
 *         else           -> { x -= 2.625f / d1; n1 * x * x + 0.984375f }
 *     }
 * }
*/
private fun bounceOut(initialBounceFraction: Float = 0.25f): EasingFunction {
    val b0 = 1 - initialBounceFraction
    val b1 = b0 * (1 - b0) + b0
    val b2 = b0 * (1 - b1) + b1
    val x0 = 2 * sqrt(initialBounceFraction)
    val x1 = x0 * sqrt(initialBounceFraction)
    val x2 = x1 * sqrt(initialBounceFraction)
    val t0 = 1 / (1 + x0 + x1 + x2)
    val t1 = t0 + t0 * x0
    val t2 = t1 + t0 * x1
    val m0 = t0 + t0 * x0 / 2
    val m1 = t1 + t0 * x1 / 2
    val m2 = t2 + t0 * x2 / 2
    val a  = 1 / (t0 * t0)

    return {
        var t = it

        when {
            t >= 1 -> 1f
            t < t0 -> a * t * t
            t < t1 -> { t -= m0; a * t * t + b0 }
            t < t2 -> { t -= m1; a * t * t + b1 }
            else   -> { t -= m2; a * t * t + b2 }
        }
    }
}

public val linear          : EasingFunction = { it      }
public val easeInQuad      : EasingFunction = { it * it }
public val easeOutQuad     : EasingFunction = { 1 - (1 - it) * (1 - it) }
public val easeInOutQuad   : EasingFunction = { if (it < 0.5) 2 * it * it else 1 - (-2 * it + 2).pow(2) / 2 }
public val easeInCubic     : EasingFunction = { it * it * it }
public val easeOutCubic    : EasingFunction = { 1 - (1 - it).pow(3) }
public val easeInOutCubic  : EasingFunction = { if (it < 0.5) 4 * it * it * it else 1 - (-2 * it + 2).pow(3) / 2 }
public val easeInQuart     : EasingFunction = { it * it * it * it }
public val easeOutQuart    : EasingFunction = { 1 - (1 - it).pow(4) }
public val easeInOutQuart  : EasingFunction = { if (it < 0.5) 8 * it * it * it * it else 1 - (-2 * it + 2).pow(4) / 2 }
public val easeInQuint     : EasingFunction = { it * it * it * it * it }
public val easeOutQuint    : EasingFunction = { 1 - (1 - it).pow(5) }
public val easeInOutQuint  : EasingFunction = { if (it < 0.5) 16 * it * it * it * it * it else 1 - (-2 * it + 2).pow(5) / 2 }
public val easeInSine      : EasingFunction = { 1 - cos((it * PI) / 2).toFloat() }
public val easeOutSine     : EasingFunction = { sin((it * PI) / 2).toFloat() }
public val easeInOutSine   : EasingFunction = { -(cos(PI * it).toFloat() - 1) / 2 }
public val easeInEitpo     : EasingFunction = { if (it == 0f) 0f else 2f.pow(10 * it - 10) }
public val easeOutEitpo    : EasingFunction = { if (it == 1f) 1f else 1 - 2f.pow(-10 * it) }
public val easeInOutEitpo  : EasingFunction = {
    when {
        it == 0f  -> 0f
        it == 1f  -> 1f
        it < 0.5f -> 2f.pow(20 * it - 10) / 2
        else      -> (2 - 2f.pow(-20 * it + 10)) / 2
    }
}
public val easeInCirc      : EasingFunction = { 1 - sqrt(1 - it.pow(2)) }
public val easeOutCirc     : EasingFunction = { sqrt(1 - (it - 1).pow(2)) }
public val easeInOutCirc   : EasingFunction = {
    when {
        it < 0.5 -> (1 - sqrt(1 - (2 * it).pow(2))) / 2
        else     -> (sqrt(1 - (-2 * it + 2).pow(2)) + 1) / 2
    }
}
public val easeInBack      : EasingFunction = { c3 * it * it * it - c1 * it * it }
public val easeOutBack     : EasingFunction = { 1 + c3 * (it - 1).pow(3) + c1 * (it - 1).pow(2) }
public val easeInOutBack   : EasingFunction = {
    when {
        it < 0.5 -> ((2 * it).pow(2) * ((c2 + 1) * 2 * it - c2)) / 2
        else     -> ((2 * it - 2).pow(2) * ((c2 + 1) * (it * 2 - 2) + c2) + 2) / 2
    }
}
public val easeInElastic   : EasingFunction = {
    when (it) {
        0f   -> 0f
        1f   -> 1f
        else -> -(2f.pow(10 * it - 10)) * sin((it * 10 - 10.75) * c4).toFloat()
    }
}
public val easeOutElastic  : EasingFunction = {
    when (it) {
        0f   -> 0f
        1f   -> 1f
        else -> 2f.pow(-10 * it) * sin((it * 10 - 0.75) * c4).toFloat() + 1
    }
}
public val easeInOutElastic: EasingFunction = {
    when {
        it == 0f -> 0f
        it == 1f -> 1f
        it < 0.5 -> -(2f.pow(20 * it - 10) * sin((20 * it - 11.125) * c5).toFloat()) / 2
        else     -> (2f.pow(-20 * it + 10) * sin((20 * it - 11.125) * c5).toFloat()) / 2 + 1
    }
}
public val easeInBounce: EasingFunction = easeInBounce()

/**
 * Creates an easeInBounce where the first bounce magnitude is defined by [initialBounceFraction].
 *
 * @param initialBounceFraction determines how large the first bounce is
 */
public fun easeInBounce(initialBounceFraction: Float = 0.25f): EasingFunction = { 1 - bounceOut(initialBounceFraction)(1 - it) }

public val easeOutBounce: EasingFunction = easeOutBounce()

/**
 * Creates an easeOutBounce where the first bounce magnitude is defined by [initialBounceFraction].
 *
 * @param initialBounceFraction determines how large the first bounce is
 */
public fun easeOutBounce(initialBounceFraction: Float = 0.25f): EasingFunction = bounceOut(initialBounceFraction)

public val easeInOutBounce: EasingFunction = easeInOutBounce()

/**
 * Creates an easeInOutBounce where the first bounce magnitudes are defined by [initialBounceFraction].
 *
 * @param initialBounceFraction determines how large the first bounce is
 */
public fun easeInOutBounce(initialBounceFraction: Float = 0.25f): EasingFunction = {
    val bounceOut = bounceOut(initialBounceFraction)

    when {
        it < 0.5 -> (1 - bounceOut.invoke(1 - 2 * it)) / 2
        else     -> (1 + bounceOut.invoke(2 * it - 1)) / 2
    }
}

private const val c1 = 1.70158f
private const val c2 = c1 * 1.525f
private const val c3 = c1 + 1
private const val c4 = (2 * PI) / 3
private const val c5 = (2 * PI) / 4.5