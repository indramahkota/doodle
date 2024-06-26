@file:Suppress("EXPECTED_EXTERNAL_DECLARATION", "WRONG_MODIFIER_TARGET")

package io.nacular.doodle.dom

/** @suppress */
public expect external interface JsAny

/** @suppress */
public expect external class JsString: JsAny

/** @suppress */
public expect external class JsNumber: JsAny

internal expect fun JsNumber.toDouble(): Double

internal expect fun String.toJsString(): JsString

/** @suppress */
public expect external class JsArray<T: JsAny?>: JsAny {
    internal val length: Int
}

internal expect operator fun <T: JsAny?> JsArray<T>.get(index: Int          ): T?
internal expect operator fun <T: JsAny?> JsArray<T>.set(index: Int, value: T)
internal expect fun <T: JsAny?> JsArray<T>.push(value: T)

internal expect operator fun JsArray<out JsString>.contains(value: JsString): Boolean

internal expect external fun <T : JsAny> jsArrayOf(vararg values: T): JsArray<T>