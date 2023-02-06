package io.nacular.doodle.drawing

import io.nacular.doodle.HTMLElement
import io.nacular.doodle.text.StyledText
import io.nacular.doodle.utils.HorizontalAlignment

/**
 * Created by Nicholas Eddy on 10/30/17.
 */

internal interface TextFactory {
    fun create (text: String,     font: Font? = null,                                                                                          letterSpacing: Double, possible: HTMLElement? = null): HTMLElement
    fun create (text: StyledText,                                                                                                              letterSpacing: Double, possible: HTMLElement? = null): HTMLElement
    fun wrapped(text: String,     font: Font? = null, width: Double, indent: Double = 0.0, alignment: HorizontalAlignment, lineSpacing: Float, letterSpacing: Double, possible: HTMLElement? = null): HTMLElement
    fun wrapped(text: String,     font: Font? = null,                indent: Double = 0.0, alignment: HorizontalAlignment, lineSpacing: Float, letterSpacing: Double, possible: HTMLElement? = null): HTMLElement
    fun wrapped(text: StyledText,                     width: Double, indent: Double = 0.0, alignment: HorizontalAlignment, lineSpacing: Float, letterSpacing: Double, possible: HTMLElement? = null): HTMLElement
    fun wrapped(text: StyledText,                                    indent: Double = 0.0, alignment: HorizontalAlignment, lineSpacing: Float, letterSpacing: Double, possible: HTMLElement? = null): HTMLElement
}