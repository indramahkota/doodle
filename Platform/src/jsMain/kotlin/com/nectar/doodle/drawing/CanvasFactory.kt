package com.nectar.doodle.drawing

import org.w3c.dom.HTMLElement

/**
 * Created by Nicholas Eddy on 10/23/17.
 */
interface CanvasFactory {
    operator fun invoke(region: HTMLElement): Canvas
}