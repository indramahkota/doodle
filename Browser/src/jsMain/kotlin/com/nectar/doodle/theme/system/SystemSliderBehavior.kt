package com.nectar.doodle.theme.system

import com.nectar.doodle.controls.Slider
import com.nectar.doodle.drawing.Canvas
import com.nectar.doodle.drawing.impl.NativeSliderFactory
import com.nectar.doodle.event.MouseEvent
import com.nectar.doodle.event.MouseListener
import com.nectar.doodle.event.MouseMotionListener
import com.nectar.doodle.system.Cursor.Companion.Default
import com.nectar.doodle.theme.Behavior

/**
 * Created by Nicholas Eddy on 11/20/18.
 */
internal class SystemSliderBehavior(nativeSliderFactory: NativeSliderFactory, slider: Slider): Behavior<Slider>, MouseListener, MouseMotionListener {
    private val nativePeer by lazy { nativeSliderFactory(slider) }

    override fun render(view: Slider, canvas: Canvas) {
        nativePeer.render(canvas)
    }

    override fun install(view: Slider) {
        super.install(view)

        view.cursor              = Default
        view.mouseChanged       += this
        view.mouseMotionChanged += this

        view.rerender()
    }

    override fun uninstall(view: Slider) {
        super.uninstall(view)

        nativePeer.discard()

        view.cursor              = null
        view.mouseChanged       -= this
        view.mouseMotionChanged -= this
    }

    override fun mousePressed(event: MouseEvent) {
        event.consume()
    }

    override fun mouseDragged(event: MouseEvent) {
        event.consume()
    }

}