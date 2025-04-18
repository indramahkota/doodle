package io.nacular.doodle.layout.constraints

import io.nacular.doodle.core.Internal
import io.nacular.doodle.core.Layout
import io.nacular.doodle.core.Positionable
import io.nacular.doodle.core.Positionable.BoundsUpdateContext
import io.nacular.doodle.core.View
import io.nacular.doodle.geometry.Point
import io.nacular.doodle.geometry.Rectangle
import io.nacular.doodle.geometry.Size
import io.nacular.doodle.layout.Insets
import io.nacular.doodle.layout.Insets.Companion.None
import io.nacular.doodle.layout.constraints.Operator.EQ
import io.nacular.doodle.layout.constraints.Operator.GE
import io.nacular.doodle.layout.constraints.Operator.LE
import io.nacular.doodle.layout.constraints.Strength.Companion.Required
import io.nacular.doodle.layout.constraints.impl.BoundsImpl
import io.nacular.doodle.layout.constraints.impl.ConstraintLayoutImpl
import io.nacular.doodle.layout.constraints.impl.ConstraintLayoutImpl.Companion.setupSolver
import io.nacular.doodle.layout.constraints.impl.ConstraintLayoutImpl.Companion.solve
import io.nacular.doodle.layout.constraints.impl.ParentBoundsImpl
import io.nacular.doodle.layout.constraints.impl.ReflectionVariable
import io.nacular.doodle.layout.constraints.impl.Solver
import io.nacular.doodle.utils.Pool


public val fill  : (ConstraintDslContext.(Bounds) -> Unit) = { it.edges  eq parent.edges  }
public val center: (ConstraintDslContext.(Bounds) -> Unit) = { it.center eq parent.center }

public fun fill  (                strength: Strength           ): (ConstraintDslContext.(Bounds) -> Unit) = { (it.edges  eq parent.edges         ) strength strength }
public fun fill  (insets: Insets, strength: Strength = Required): (ConstraintDslContext.(Bounds) -> Unit) = { (it.edges  eq parent.edges + insets) strength strength }
public fun center(                strength: Strength           ): (ConstraintDslContext.(Bounds) -> Unit) = { (it.center eq parent.center        ) strength strength }

/**
 * A [Layout] that positions Views using a set of constraints. These layouts are created using
 * the [constrain] functions and follow the form:
 *
 * ```
 *
 * val layout = constrain(view1, view2) { v1, v2 ->
 *     v1.left  eq     10
 *     v1.width lessEq parent.width / 2
 *
 *     v2.edges eq     parent.edges - 10
 * }
 * ```
 */
public abstract class ConstraintLayout internal constructor(): Layout {
    /**
     * Add constraints for [view].
     *
     * @param view being constrained
     * @param constraints being applied
     * @return Layout with additional constraints
     */
    public abstract fun constrain(view: View, constraints: ConstraintDslContext.(Bounds) -> Unit): ConstraintLayout

    /**
     * Remove all constraints for [view] that were created by [constraints].
     *
     * @param view being unconstrained
     * @param constraints being removed
     * @return Layout with constraints removed
     */
    public abstract fun unconstrain(view: View, constraints: ConstraintDslContext.(Bounds) -> Unit): ConstraintLayout

    /**
     * Add constraints for 2 Views.
     *
     * @param first being constrained
     * @param second being constrained
     * @param constraints being applied
     * @return Layout with additional constraints
     */
    public abstract fun constrain(first: View, second: View, constraints: ConstraintDslContext.(Bounds, Bounds) -> Unit): ConstraintLayout

    /**
     * Remove all constraints for the given Views that were created by [constraints].
     *
     * @param first View being unconstrained
     * @param second View being unconstrained
     * @param constraints being removed
     * @return Layout with constraints removed
     */
    public abstract fun unconstrain(first: View, second: View, constraints: ConstraintDslContext.(Bounds, Bounds) -> Unit): ConstraintLayout

    /**
     * Add constraints for 3 Views.
     *
     * @param first View being constrained
     * @param second View being constrained
     * @param third View being constrained
     * @param constraints being applied
     * @return Layout with additional constraints
     */
    public abstract fun constrain(first: View, second: View, third: View, constraints: ConstraintDslContext.(Bounds, Bounds, Bounds) -> Unit): ConstraintLayout

    /**
     * Remove all constraints for the given Views that were created by [constraints].
     *
     * @param first View being unconstrained
     * @param second View being unconstrained
     * @param third View being unconstrained
     * @param constraints being removed
     * @return Layout with constraints removed
     */
    public abstract fun unconstrain(first: View, second: View, third: View, constraints: ConstraintDslContext.(Bounds, Bounds, Bounds) -> Unit): ConstraintLayout

    /**
     * Add constraints for 4 Views.
     *
     * @param first first View being constrained
     * @param second second View being constrained
     * @param third third View being constrained
     * @param fourth fourth View being constrained
     * @param constraints being applied
     * @return Layout with additional constraints
     */
    public abstract fun constrain(first: View, second: View, third: View, fourth: View, constraints: ConstraintDslContext.(Bounds, Bounds, Bounds, Bounds) -> Unit): ConstraintLayout

    /**
     * Remove all constraints for the given Views that were created by [constraints].
     *
     * @param first View being unconstrained
     * @param second View being unconstrained
     * @param third View being unconstrained
     * @param fourth View being unconstrained
     * @param constraints being removed
     * @return Layout with constraints removed
     */
    public abstract fun unconstrain(first: View, second: View, third: View, fourth: View, constraints: ConstraintDslContext.(Bounds, Bounds, Bounds, Bounds) -> Unit): ConstraintLayout

    /**
     * Add constraints for 5 Views.
     *
     * @param first View being constrained
     * @param second View being constrained
     * @param third View being constrained
     * @param fourth View being constrained
     * @param fifth View being constrained
     * @param constraints being applied
     * @return Layout with additional constraints
     */
    public abstract fun constrain(first: View, second: View, third: View, fourth: View, fifth: View, constraints: ConstraintDslContext.(Bounds, Bounds, Bounds, Bounds, Bounds) -> Unit): ConstraintLayout

    /**
     * Remove all constraints for the given Views that were created by [constraints].
     *
     * @param first View being unconstrained
     * @param second View being unconstrained
     * @param third View being unconstrained
     * @param fourth View being unconstrained
     * @param fifth View being unconstrained
     * @param constraints being removed
     * @return Layout with constraints removed
     */
    public abstract fun unconstrain(first: View, second: View, third: View, fourth: View, fifth: View, constraints: ConstraintDslContext.(Bounds, Bounds, Bounds, Bounds, Bounds) -> Unit): ConstraintLayout

    /**
     * Add constraints for several Views.
     *
     * @param first View being constrained
     * @param second View being constrained
     * @param others Views being constrained
     * @param constraints being applied
     * @return Layout with additional constraints
     */
    public abstract fun constrain(first: View, second: View, vararg others: View, constraints: ConstraintDslContext.(List<Bounds>) -> Unit): ConstraintLayout

    /**
     * Remove all constraints for the given Views that were created by [constraints].
     *
     * @param first View being unconstrained
     * @param second View being unconstrained
     * @param others being unconstrained
     * @param constraints being removed
     * @return Layout with constraints removed
     */
    public abstract fun unconstrain(first: View, second: View, vararg others: View, constraints: ConstraintDslContext.(List<Bounds>) -> Unit): ConstraintLayout

    /**
     * Notified whenever an unhandled [ConstraintException] is thrown during layout.
     */
    public abstract val exceptionThrown: Pool<(ConstraintLayout, ConstraintException) -> Unit>
}

/**
 * Simple value within a [Bounds] set.
 */
public abstract class Property internal constructor() {
    /**
     * Provides the Property's value directly, and does not treat it as
     * a variable when used in a [Constraint]. This means the Property won't be
     * altered to try and satisfy the constraint.
     */
    public abstract val readOnly: Double

    internal abstract fun toTerm(): Term
}

/**
 * Simple value within a [Bounds] set that has an ideal value.
 */
public abstract class IdealSizedProperty internal constructor(): Property() {
    /**
     * Provides the Property's ideal value directly, and does not treat it as
     * a variable when used in a [Constraint]. This means the Property won't be
     * altered to try and satisfy the constraint.
     */
    public abstract val idealValue: Double
}

/**
 * 2-Dimensional value within a [Bounds] set.
 */
public class Position internal constructor(internal val left: Expression, internal val top: Expression) {
    /**
     * Provides the Position's value directly, and does not treat it as
     * a variable when used in a [Constraint]. This means the Position won't be
     * altered to try and satisfy the constraint.
     */
    public val readOnly: Point get() = Point(x = left.value, y = top.value)
}

/**
 * 2-Dimensional value within a [Bounds] set.
 */
public class Area internal constructor(internal val width: Expression, internal val height: Expression) {
    /**
     * Provides the Area's value directly, and does not treat it as
     * a variable when used in a [Constraint]. This means the Area won't be
     * altered to try and satisfy the constraint.
     */
    public val readOnly: Size get() = Size(width = width.value, height = height.value)
}

/**
 * External boundaries of a constrained item.
 */
public class Edges internal constructor(
    internal val top   : Expression? = null,
    internal val left  : Expression? = null,
    internal val right : Expression,
    internal val bottom: Expression
) {
    /**
     * Provides the Edges' value directly, and does not treat it as
     * a variable when used in a [Constraint]. This means the Edges won't be
     * altered to try and satisfy the constraint.
     */
    public val readOnly: Rectangle get() {
        val x = left?.value ?: 0.0
        val y = top?.value  ?: 0.0
        return Rectangle(x = x, y = y, width = right.value - x, height = bottom.value - y)
    }
}

public interface AllowsForcedMutation<T, V> {
    public val writable: T
    public val readOnly: V
}

/**
 * The rectangular bounds for a View that is being constrained.
 */
public interface Bounds {
    /** The rectangle's top edge */
    public val top: Expression

    /** The rectangle's left edge */
    public val left: Expression

    /** The rectangle's vertical extent */
    public val height: Property

    /** The rectangle's bottom edge */
    public val bottom: Expression

    /** The rectangle's vertical center */
    public val centerY: Expression

    /** The rectangle's horizontal extent */
    public val width: Property

    /** The rectangle's right side */
    public val right: Expression

    /** The rectangle's horizontal center */
    public val centerX: Expression

    /** The rectangle's width/height */
    public val size: Area

    /** The rectangle's 4 sides */
    public val edges: Edges

    /** The rectangle's center */
    public val center: Position

    /** The ideal width */
    public val idealWidth: Double get() = idealSize.width

    /** The ideal height */
    public val idealHeight: Double get() = idealSize.height

    /** The ideal size */
    public val idealSize: Size

    /**
     * The preferred size within [min] - [max]
     *
     * @param min size allowed
     * @param max size allowed
     */
    public fun preferredSize(min: Size, max: Size): Size
}

/**
 * [Bounds] the refer to the external, bounding rectangle for the parent of a View that is being constrained.
 */
public interface ParentBounds {
    /** The rectangle's vertical extent */
    public val height: Expression

    /** The rectangle's bottom edge */
    public val bottom: Expression

    /** The rectangle's vertical center */
    public val centerY: Expression

    /** The rectangle's horizontal extent */
    public val width: Expression

    /** The rectangle's right side */
    public val right: Expression

    /** The rectangle's horizontal center */
    public val centerX: Expression

    /** The rectangle's width/height */
    public val size: Area

    /** The rectangle's 4 sides */
    public val edges: Edges

    /** The rectangle's center */
    public val center: Position

    /** The insets if any */
    public val insets: Insets
}

/**
 * Any error that can be raised when applying constrains.
 */
public open class ConstraintException internal constructor(message: String?): Exception(message)

/**
 * Thrown when a duplicate [Constraint] is added.
 */
public class DuplicateConstraintException(public val constraint: Constraint): ConstraintException(constraint.toString())

/**
 * Indicates a [Constraint] is unsatisfiable.
 */
public class UnsatisfiableConstraintException(
    public val constraint: Constraint,
    public val existingConstraints: Collection<Constraint>
): ConstraintException("""
    Failed Constraint: $constraint
    
    Existing constraints:
    
    ${existingConstraints.joinToString("\n")}
""".trimIndent())

/**
 * Block within which constraints can be defined and captured.
 */
@Suppress("MemberVisibilityCanBePrivate")
public class ConstraintDslContext internal constructor() {
    internal var constraints = mutableListOf<Constraint>()

    public var parent: ParentBounds = ParentBoundsImpl(this)
        internal set

    public fun updateParent(size: Size, min: Size, max: Size, insets: Insets) {
        (parent as ParentBoundsImpl).update(size, min, max, insets)
    }

    private fun add(constraint: Constraint) = when {
        constraint.expression.isConstant -> Result.failure(UnsatisfiableConstraintException(constraint, constraints))
        else                             -> Result.success(constraint).also { constraints += constraint }
    }

    public operator fun Property.plus(expression: Expression): Expression = expression + this
    public operator fun Property.plus(term      : Term      ): Expression = term + this
    public operator fun Property.plus(other     : Property  ): Expression = this.toTerm() + other
    public operator fun Property.plus(constant  : Number    ): Expression = this.toTerm() + constant.toDouble()

    public operator fun Property.minus(expression: Expression): Expression = this + -expression
    public operator fun Property.minus(term      : Term      ): Expression = this + -term
    public operator fun Property.minus(other     : Property  ): Expression = this + -other
    public operator fun Property.minus(constant  : Number    ): Expression = this + -constant.toDouble()

    public operator fun Property.times     (coefficient: Number): Term = this.toTerm() * coefficient.toDouble()
    public operator fun Property.div       (denominator: Number): Term = this * 1.0 / denominator
    public operator fun Property.unaryMinus(                   ): Term = this * -1.0

    /**
     * Creates a [Constraint] that keeps the Property's current value. This is equivalent to:
     *
     * ```
     * this eq this.readOnly
     * ```
     */
    public val Property.preserve: Result<Constraint> get() = this eq this.readOnly

//    public fun min(a: Property, b: Term      ): Expression = min(1 * a, b)
//    public fun min(a: Property, b: Number    ): Expression = min(1 * a, Expression(constant = b.toDouble()))
//    public fun min(a: Property, b: Property  ): Expression = min(a.toTerm(), b.toTerm())
//    public fun min(a: Property, b: Expression): Expression = min(1 * a, b)
//
//    public fun max(a: Property, b: Term      ): Expression = max(1 * a, b)
//    public fun max(a: Property, b: Number    ): Expression = max(1 * a, Expression(constant = b.toDouble()))
//    public fun max(a: Property, b: Property  ): Expression = max(a.toTerm(), b.toTerm())
//    public fun max(a: Property, b: Expression): Expression = max(1 * a, b)

    public operator fun Term.plus(term      : Term      ): Expression = Expression(this, term)
    public operator fun Term.plus(value     : Number    ): Expression = Expression(this, constant = value.toDouble())
    public operator fun Term.plus(property  : Property  ): Expression = this + property.toTerm()
    public operator fun Term.plus(expression: Expression): Expression = expression + this

    public operator fun Term.minus(term      : Term      ): Expression = this + -term
    public operator fun Term.minus(constant  : Number    ): Expression = this + -constant.toDouble()
    public operator fun Term.minus(property  : Property  ): Expression = this + -property
    public operator fun Term.minus(expression: Expression): Expression = -expression + this

    public operator fun Term.div       (denominator: Number): Term = this * (1.0 / denominator.toDouble())
    public operator fun Term.unaryMinus(                   ): Term = this * -1.0

//    public fun min(a: Term, b: Number    ): Expression = min(a, Expression(constant = b.toDouble()))
//    public fun min(a: Term, b: Property  ): Expression = min(a, 1 * b    )
//    public fun min(a: Term, b: Expression): Expression = min(a + 0, b    )
//    public fun min(a: Term, b: Term      ): Expression = min(a + 0, b + 0)
//
//    public fun max(a: Term, b: Number    ): Expression = max(a, Expression(constant = b.toDouble()))
//    public fun max(a: Term, b: Property  ): Expression = max(a, 1 * b    )
//    public fun max(a: Term, b: Expression): Expression = max(a + 0, b    )
//    public fun max(a: Term, b: Term      ): Expression = max(a + 0, b + 0)

    public operator fun Expression.plus(term    : Term      ): Expression = Expression(*terms, term,         constant = constant                 )
    public operator fun Expression.plus(constant: Number    ): Expression = Expression(terms, constant = this.constant + constant.toDouble())
    public operator fun Expression.plus(property: Property  ): Expression = this + property.toTerm()
    public operator fun Expression.plus(other   : Expression): Expression = Expression(*terms, *other.terms, constant = constant + other.constant)

    public operator fun Expression.minus(term    : Term      ): Expression = this + -term
    public operator fun Expression.minus(value   : Number    ): Expression = this + -value.toDouble()
    public operator fun Expression.minus(property: Property  ): Expression = this + -property
    public operator fun Expression.minus(other   : Expression): Expression = this + -other

    public operator fun Expression.times(coefficient: Number): Expression = Expression(
        *Array(terms.size) { terms[it] * coefficient.toDouble() },
        constant = constant * coefficient.toDouble()
    ) // TODO Do we need to make a copy of the term objects in the array?

    public operator fun Expression.times(other: Expression): Expression = when {
        isConstant       -> constant       * other
        other.isConstant -> other.constant * this
        else             -> throw NonlinearExpressionException()
    }

    public operator fun Expression.div(denominator: Number    ): Expression = this * (1.0 / denominator.toDouble())
    public operator fun Expression.div(other      : Expression): Expression = when {
        other.isConstant -> this / other.constant
        else             -> throw NonlinearExpressionException()
    }

    public operator fun Expression.unaryMinus(): Expression = this * -1.0

//    private class LocalVariable(override val name: String, ) : Variable {
//        private var value = 0.0
//        override var constrained = false
//        override val needsSynthetic = false
//        override val needsStability = false
//
//        override fun invoke() = value
//
//        override fun invoke(value: Double) {
//            this.value = value
//        }
//
//        override fun toString() = name
//    }
//
//    public fun min(a: Expression, b: Term      ): Expression = min(a, b + 0)
//    public fun min(a: Expression, b: Number    ): Expression = min(a, Expression(constant = b.toDouble()))
//    public fun min(a: Expression, b: Property  ): Expression = min(a, 1 * b)
//    public fun min(a: Expression, b: Expression): Expression {
//        // see: https://or.stackexchange.com/questions/1160/how-to-linearize-min-function-as-a-constraint
//
//        val x = VariableTerm(LocalVariable("x"))
//        val y = VariableTerm(LocalVariable("y"))
//        val m = 1_000_000_000
//
//        x lessEq    a
//        x lessEq    b
//        x greaterEq a - m * y
//        x greaterEq b - m * (1 - y)
//
//        return x + 0
//    }
//
//    public fun max(a: Expression, b: Term      ): Expression = max(a, b + 0)
//    public fun max(a: Expression, b: Number    ): Expression = max(a, Expression(constant = b.toDouble()))
//    public fun max(a: Expression, b: Property  ): Expression = max(a, 1 * b)
//    public fun max(a: Expression, b: Expression): Expression {
//        // see: https://or.stackexchange.com/questions/711/how-to-formulate-linearize-a-maximum-function-in-a-constraint
//
//        val x = VariableTerm(LocalVariable("x"))
//        val y = VariableTerm(LocalVariable("y"))
//        val m = 1_000_000_000
//
//        x greaterEq a
//        x greaterEq b
//        x lessEq    a + m * (1 - y)
//        x lessEq    b + m * y
//
//        return x + 0
//    }

    public operator fun Number.plus(term      : Term      ): Expression = term       + this.toDouble()
    public operator fun Number.plus(property  : Property  ): Expression = property   + this.toDouble()
    public operator fun Number.plus(expression: Expression): Expression = expression + this.toDouble()

    public operator fun Number.minus(term      : Term      ): Expression = -term       + this.toDouble()
    public operator fun Number.minus(property  : Property  ): Expression = -property   + this.toDouble()
    public operator fun Number.minus(expression: Expression): Expression = -expression + this.toDouble()

    public operator fun Number.times(term      : Term      ): Term       = term       * this.toDouble()
    public operator fun Number.times(property  : Property  ): Term       = property   * this.toDouble()
    public operator fun Number.times(expression: Expression): Expression = expression * this.toDouble()

//    public fun min(a: Number, b: Term      ): Expression = min(b, a)
//    public fun min(a: Number, b: Property  ): Expression = min(b, a)
//    public fun min(a: Number, b: Expression): Expression = min(b, a)
//
//    public fun max(a: Number, b: Term      ): Expression = max(b, a)
//    public fun max(a: Number, b: Property  ): Expression = max(b, a)
//    public fun max(a: Number, b: Expression): Expression = max(b, a)

    public class NonlinearExpressionException : Exception()

    public infix fun Expression.eq(term    : Term      ): Result<Constraint> = this eq Expression(term)
    public infix fun Expression.eq(constant: Number    ): Result<Constraint> = add(Constraint(Expression(terms, constant = this.constant - constant.toDouble()), EQ, Required))//this eq Expression(constant = constant.toDouble())
    public infix fun Expression.eq(property: Property  ): Result<Constraint> = this eq property.toTerm()
    public infix fun Expression.eq(other   : Expression): Result<Constraint> = add(Constraint(this - other, EQ, Required))

    public infix fun Expression.lessEq(term    : Term      ): Result<Constraint> = this lessEq Expression(term)
    public infix fun Expression.lessEq(constant: Number    ): Result<Constraint> = this lessEq Expression(constant = constant.toDouble())
    public infix fun Expression.lessEq(property: Property  ): Result<Constraint> = this lessEq property.toTerm()
    public infix fun Expression.lessEq(other   : Expression): Result<Constraint> = add(Constraint(this - other, LE, Required))

    public infix fun Expression.greaterEq(term    : Term      ): Result<Constraint> = this greaterEq Expression(term)
    public infix fun Expression.greaterEq(constant: Number    ): Result<Constraint> = this greaterEq Expression(constant = constant.toDouble())
    public infix fun Expression.greaterEq(property: Property  ): Result<Constraint> = this greaterEq property.toTerm()
    public infix fun Expression.greaterEq(second  : Expression): Result<Constraint> = add(Constraint(this - second, GE, Required))

    public infix fun Term.eq(term      : Term      ): Result<Constraint> = Expression(this) eq term
    public infix fun Term.eq(constant  : Number    ): Result<Constraint> = Expression(this) eq constant.toDouble()
    public infix fun Term.eq(property  : Property  ): Result<Constraint> = Expression(this) eq property
    public infix fun Term.eq(expression: Expression): Result<Constraint> = expression       eq this

    public infix fun Term.lessEq(term      : Term      ): Result<Constraint> = Expression(this) lessEq term
    public infix fun Term.lessEq(constant  : Number    ): Result<Constraint> = Expression(this) lessEq constant.toDouble()
    public infix fun Term.lessEq(property  : Property  ): Result<Constraint> = Expression(this) lessEq property
    public infix fun Term.lessEq(expression: Expression): Result<Constraint> = Expression(this) lessEq expression

    public infix fun Term.greaterEq(second    : Term      ): Result<Constraint> = Expression(this) greaterEq second
    public infix fun Term.greaterEq(constant  : Number    ): Result<Constraint> = Expression(this) greaterEq constant.toDouble()
    public infix fun Term.greaterEq(property  : Property  ): Result<Constraint> = Expression(this) greaterEq property
    public infix fun Term.greaterEq(expression: Expression): Result<Constraint> = Expression(this) greaterEq expression

    public infix fun Property.eq(term      : Term      ): Result<Constraint> = term          eq this
    public infix fun Property.eq(constant  : Number    ): Result<Constraint> = this.toTerm() eq constant.toDouble()
    public infix fun Property.eq(property  : Property  ): Result<Constraint> = this.toTerm() eq property
    public infix fun Property.eq(expression: Expression): Result<Constraint> = expression    eq this

    public infix fun Property.lessEq(term      : Term      ): Result<Constraint> = this.toTerm() lessEq term
    public infix fun Property.lessEq(constant  : Number    ): Result<Constraint> = this.toTerm() lessEq constant.toDouble()
    public infix fun Property.lessEq(second    : Property  ): Result<Constraint> = this.toTerm() lessEq second
    public infix fun Property.lessEq(expression: Expression): Result<Constraint> = this.toTerm() lessEq expression

    public infix fun Property.greaterEq(term      : Term      ): Result<Constraint> = this.toTerm() greaterEq term
    public infix fun Property.greaterEq(constant  : Number    ): Result<Constraint> = this.toTerm() greaterEq constant.toDouble()
    public infix fun Property.greaterEq(second    : Property  ): Result<Constraint> = this.toTerm() greaterEq second
    public infix fun Property.greaterEq(expression: Expression): Result<Constraint> = this.toTerm() greaterEq expression

    public infix fun Position.eq(other: Position): List<Result<Constraint>> = listOf(
        top  eq other.top,
        left eq other.left
    )

    public infix fun Position.eq(point: Point): List<Result<Constraint>> = listOf(
        top  eq point.y,
        left eq point.x
    )

    public val Position.preserve: List<Result<Constraint>> get() = listOf(
        top  eq top.readOnly,
        left eq left.readOnly
    )

    public operator fun Position.plus(point: Point): Position = Position(
        top  = top  + point.y,
        left = left + point.x
    )

    public operator fun Position.minus(point: Point): Position = Position(
        top  = top  - point.y,
        left = left - point.x
    )

    public operator fun Point.plus(position: Position): Position = Position(
        top  = y + position.top,
        left = x + position.left
    )

    public operator fun Point.minus(position: Position): Position = Position(
        top  = y - position.top,
        left = x - position.left
    )

    public infix fun Area.eq(other: Area): List<Result<Constraint>> = listOf(
        width  eq other.width,
        height eq other.height
    )

    public infix fun Area.eq(size: Size): List<Result<Constraint>> = listOf(
        width  eq size.width,
        height eq size.height
    )

    public val Area.preserve: List<Result<Constraint>>
        get() = listOf(
            width eq width.readOnly,
            height eq height.readOnly
        )

    public operator fun Edges.plus(value: Number): Edges = this + Insets(-(value.toDouble()))
    public operator fun Edges.minus(value: Number): Edges = this + Insets(value.toDouble())

    public operator fun Edges.plus (insets: Insets): Edges = Edges(
        top?.plus (insets.top ) ?: Expression(constant = insets.top ),
        left?.plus(insets.left) ?: Expression(constant = insets.left),
        right  - insets.right,
        bottom - insets.bottom
    )

    public infix fun Edges.eq(other: Edges): List<Result<Constraint>> {
        val result = mutableListOf<Result<Constraint>>()

        if (top != null) {
            when (other.top) {
                null -> (top eq 0        ).also { result += it }
                else -> (top eq other.top).also { result += it }
            }
        }
        if (left != null) {
            when (other.left) {
                null -> (left eq 0         ).also { result += it }
                else -> (left eq other.left).also { result += it }
            }
        }

        (right  eq other.right ).also { result += it }
        (bottom eq other.bottom).also { result += it }

        return result
    }

    public infix fun Edges.eq(rectangle: Rectangle): List<Result<Constraint>> {
        val result = mutableListOf<Result<Constraint>>()

        if (top != null) {
            (top eq rectangle.y).also { result += it }
        }
        if (left != null) {
            (left eq rectangle.x).also { result += it }
        }

        (right  eq rectangle.right ).also { result += it }
        (bottom eq rectangle.bottom).also { result += it }

        return result
    }

    public val Edges.preserve: List<Result<Constraint>> get() = this eq this.readOnly

    public infix fun Number.eq(term      : Term      ): Result<Constraint> = term       eq this
    public infix fun Number.eq(variable  : Property  ): Result<Constraint> = variable   eq this
    public infix fun Number.eq(expression: Expression): Result<Constraint> = expression eq this

    public infix fun Number.lessEq(term      : Term      ): Result<Constraint> = this                                   lessEq Expression(term)
    public infix fun Number.lessEq(property  : Property  ): Result<Constraint> = this                                   lessEq property.toTerm()
    public infix fun Number.lessEq(expression: Expression): Result<Constraint> = Expression(constant = this.toDouble()) lessEq expression

    public infix fun Number.greaterEq(term      : Term      ): Result<Constraint> = Expression(constant = this.toDouble()) greaterEq term
    public infix fun Number.greaterEq(property  : Property  ): Result<Constraint> = this                                   greaterEq property.toTerm()
    public infix fun Number.greaterEq(expression: Expression): Result<Constraint> = expression                             lessEq    this

    public infix fun Result<Constraint>.strength(strength: Strength): Result<Constraint> {
        this.getOrNull()?.strength = strength
        return this
    }

    public infix fun Result<Constraint>.strength(strength: Int): Result<Constraint> {
        this.getOrNull()?.strength = Strength(strength)
        return this
    }

    public infix fun List<Result<Constraint>>.strength(strength: Strength): List<Result<Constraint>> {
        this.forEach { it.getOrNull()?.strength = strength }
        return this
    }

    @Deprecated(message = "use strength infix instead", replaceWith = ReplaceWith("strength(strength)"), level = DeprecationLevel.ERROR)
    public operator fun Result<Constraint>.rangeTo(strength: Strength): Result<Constraint> {
        this.getOrNull()?.strength = strength
        return this
    }

    @Deprecated(message = "use strength infix instead", replaceWith = ReplaceWith("strength(strength)"), level = DeprecationLevel.ERROR)
    public operator fun List<Result<Constraint>>.rangeTo(strength: Strength): List<Result<Constraint>> {
        this.forEach { it.getOrNull()?.strength = strength }
        return this
    }

    public fun Bounds.withOffset(top: Double = 0.0, left: Double = 0.0): Bounds = when {
        top == 0.0 && left == 0.0 -> this
        else                      -> withOffset(top = { top }, left = { left })
    }

    public fun Bounds.withOffset(top: () -> Double = { 0.0 }, left: () -> Double = { 0.0 }): Bounds = object: Bounds by this {
        override val top  get() = this@withOffset.top  - top ()
        override val left get() = this@withOffset.left - left()

        private val t = this.top
        private val l = this.left

        override val right   by lazy { l + width      }
        override val centerX by lazy { l + width  / 2 }
        override val bottom  by lazy { t + height     }
        override val centerY by lazy { t + height / 2 }

        override val center by lazy { Position(left = centerX, top = centerY) }
        override val edges  by lazy { Edges   (top = t, left = l, right = right, bottom = bottom) }
    }
}

public fun ConstraintDslContext.withSizeInsets(
    width : Double = 0.0,
    height: Double = 0.0,
    block : ConstraintDslContext.() -> Unit
): Unit = when {
    width == 0.0 && height == 0.0 -> block(this)
    else                          -> this@withSizeInsets.withSizeInsets(
        width  = { width  },
        height = { height },
        block  = block
    )
}

public fun ConstraintDslContext.withSizeInsets(
    width : () -> Double = { 0.0 },
    height: () -> Double = { 0.0 },
    block : ConstraintDslContext.() -> Unit
) {
    val oldParent = parent

    parent = object: ParentBounds by oldParent {
        override val width  get() = oldParent.width  - width ()
        override val height get() = oldParent.height - height()

        private val w = this.width
        private val h = this.height

        override val right   by lazy { w     }
        override val centerX by lazy { w / 2 }
        override val bottom  by lazy { h     }
        override val centerY by lazy { h / 2 }

        override val center  by lazy { Position(left  = centerX, top    = centerY) }
        override val edges   by lazy { Edges   (right = right,   bottom = bottom ) }
    }

    block(this)

    parent = oldParent
}

/**
 * Creates a [ConstraintLayout] that constrains a single View.
 *
 * @param a View being constrained
 * @param constraints with constraint details
 * @return Layout that constrains the given view
 */
public fun constrain(a: View, constraints: ConstraintDslContext.(Bounds) -> Unit): ConstraintLayout = ConstraintLayoutImpl(a, originalLambda = constraints) { (a) -> constraints(a) }

/**
 * Creates a [ConstraintLayout] that constrains 2 Views.
 *
 * @param a first View being constrained
 * @param b second View being constrained
 * @param constraints with constraint details
 * @return Layout that constrains the given views
 */
public fun constrain(a: View, b: View, constraints: ConstraintDslContext.(Bounds, Bounds) -> Unit): ConstraintLayout = ConstraintLayoutImpl(a, b, originalLambda = constraints) { (a, b) -> constraints(a, b) }

/**
 * Creates a [ConstraintLayout] that constrains 3 Views.
 *
 * @param a first View being constrained
 * @param b second View being constrained
 * @param c third View being constrained
 * @param constraints with constraint details
 * @return Layout that constrains the given views
 */
public fun constrain(a: View, b: View, c: View, constraints: ConstraintDslContext.(Bounds, Bounds, Bounds) -> Unit): ConstraintLayout = ConstraintLayoutImpl(a, b, c, originalLambda = constraints) { (a, b, c) -> constraints(a, b, c) }

/**
 * Creates a [ConstraintLayout] that constrains 4 Views.
 *
 * @param a first View being constrained
 * @param b second View being constrained
 * @param c third View being constrained
 * @param d fourth View being constrained
 * @param constraints with constraint details
 * @return Layout that constrains the given views
 */
public fun constrain(a: View, b: View, c: View, d: View, constraints: ConstraintDslContext.(Bounds, Bounds, Bounds, Bounds) -> Unit): ConstraintLayout = ConstraintLayoutImpl(a, b, c, d, originalLambda = constraints) { (a, b, c, d) -> constraints(a, b, c, d) }

/**
 * Creates a [ConstraintLayout] that constrains 5 Views.
 *
 * @param a first View being constrained
 * @param b second View being constrained
 * @param c third View being constrained
 * @param d fourth View being constrained
 * @param e fifth View being constrained
 * @param constraints with constraint details
 * @return Layout that constrains the given views
 */
public fun constrain(a: View, b: View, c: View, d: View, e: View, constraints: ConstraintDslContext.(Bounds, Bounds, Bounds, Bounds, Bounds) -> Unit): ConstraintLayout = ConstraintLayoutImpl(a, b, c, d, e, originalLambda = constraints) { (a, b, c, d, e) -> constraints(a, b, c, d, e) }

/**
 * Creates a [ConstraintLayout] that constrains several Views.
 *
 * @param a first View being constrained
 * @param b remaining Views being constrained
 * @param constraints with constraint details
 * @return Layout that constrains the given views
 */
public fun constrain(a: View, b: View, vararg others: View, constraints: ConstraintDslContext.(List<Bounds>) -> Unit): ConstraintLayout = ConstraintLayoutImpl(a, *listOf(b, *others).toTypedArray(), originalLambda = constraints, block = constraints)

/**
 * Applies the given constraints to the Positionables as though they were each within the Rectangle provided by [within].
 *
 * @param using this constraint for each View
 * @param within this rectangle
 * @throws ConstraintException
 * @suppress
 */
@Internal
public fun <T: Positionable> Iterable<T>.constrain(
    using : ConstraintDslContext.(Bounds) -> Unit,
    within: (Int, T) -> Rectangle
): Unit = with(Constrainer()) {
    forEachIndexed { index, view ->
        this(view.bounds, within(index, view), forceSetup = false, probe = view, using).apply {
            view.updatePosition(x, y)
        }
    }
}

/**
 * Utility for applying constraints to a single [Rectangle] relative to another.
 *
 * @suppress
 */
@Internal
public class Constrainer {
    private val activeBounds  = mutableMapOf<ReflectionVariable, Double>()
    private val updatedBounds = mutableMapOf<ReflectionVariable, Double>()

    private var using: ConstraintDslContext.(Bounds) -> Unit = {}

    private var parentSize = Size.Empty
    private val solver     = Solver()
    private val context    = ConstraintDslContext()

    private class FakePositionable: Positionable, BoundsUpdateContext {
        var probe = null as Positionable?

        override var bounds          = Rectangle.Empty
        override val visible         = true
        override val position  get() = bounds.position
        override val idealSize get() = probe?.idealSize ?: Size.Empty

        private var x        : Double  = 0.0
        private var y        : Double  = 0.0
        private var minWidth : Double? = null
        private var maxWidth : Double? = null
        private var minHeight: Double? = null
        private var maxHeight: Double? = null

        override fun setX        (value: Double) { x         = value }
        override fun setY        (value: Double) { y         = value }
        override fun setMinWidth (value: Double) { minWidth  = value }
        override fun setMaxWidth (value: Double) { maxWidth  = value }
        override fun setMinHeight(value: Double) { minHeight = value }
        override fun setMaxHeight(value: Double) { maxHeight = value }

        override fun contains(point: Point) = true

        override fun preferredSize(min: Size, max: Size) = probe?.preferredSize(min, max) ?: Size.Empty

        override fun updateBounds(x: Double, y: Double, min: Size, max: Size): Size {
            bounds = Rectangle(x, y, max.width, max.height)
            probe?.updateBounds(x, y, min, max)

            return bounds.size
        }

        override fun updatePosition(x: Double, y: Double) {
            bounds = bounds.at(x, y)

            probe?.updatePosition(x, y)
        }

        override fun updateBounds(block: BoundsUpdateContext.() -> Unit) = when (val p = probe) {
            null -> {
                x         = bounds.x
                y         = bounds.y
                maxWidth  = null
                maxHeight = null

                block(this)

                Rectangle(x, y, maxWidth ?: bounds.width, maxHeight ?: bounds.height).also { bounds = it }.size
            }
            else -> p.updateBounds(block).also { bounds = p.bounds }
        }

        override fun toString() = "FakePositionable"
    }

    private val fakePositionable = FakePositionable()
    private val fakeBounds       = BoundsImpl(fakePositionable, context)
    private val blocks           = listOf(ConstraintLayoutImpl.BlockInfo(listOf(fakeBounds)) {
        (a) -> using(a)
    })

    /**
     * Applies the given constraints to [rectangle] as though they were within the Rectangle provided by [within].
     *
     * @param rectangle to constrain
     * @param within this rectangle
     * @param forceSetup forces reconfiguring of underlying solver
     * @param probe associated with this invocation
     * @param using this constraint for each View
     * @throws ConstraintException
     * @suppress
     */
    public operator fun invoke(
        rectangle : Rectangle,
        within    : Rectangle,
        forceSetup: Boolean = false,
        probe     : View,
        using     : ConstraintDslContext.(Bounds) -> Unit
    ): Rectangle = invoke(rectangle, within, forceSetup, probe = probe.positionable, using)

    /**
     * Applies the given constraints to [rectangle] as though they were within the Rectangle provided by [within].
     *
     * @param rectangle to constrain
     * @param within this rectangle
     * @param forceSetup forces reconfiguring of underlying solver
     * @param probe associated with this invocation
     * @param using this constraint for each View
     * @throws ConstraintException
     * @suppress
     */
    public operator fun invoke(
        rectangle : Rectangle,
        within    : Rectangle,
        forceSetup: Boolean       = false,
        probe     : Positionable? = null,
        using     : ConstraintDslContext.(Bounds) -> Unit
    ): Rectangle {
        fakePositionable.probe = probe

        if (fakePositionable.bounds != rectangle) {
            fakePositionable.bounds = rectangle
            fakeBounds.reset()

            updatedBounds[fakeBounds.top_  ] = rectangle.y
            updatedBounds[fakeBounds.left_ ] = rectangle.x
            updatedBounds[fakeBounds.width ] = rectangle.width
            updatedBounds[fakeBounds.height] = rectangle.height
        }

        if (forceSetup || within.size != parentSize || this.using != using) {
            this.using     = using
            parentSize     = within.size
            context.updateParent(size = within.size, min = within.size, max = within.size, insets = None)
            setupSolver(solver, context, blocks = blocks) { /*ignore*/ }
        }

        solve(
            solver        = solver,
            activeBounds  = activeBounds,
            updatedBounds = updatedBounds,
            bounds        = sequenceOf(fakeBounds),
        ) { throw it }

        return fakePositionable.bounds.at(fakePositionable.position + within.position)
    }
}