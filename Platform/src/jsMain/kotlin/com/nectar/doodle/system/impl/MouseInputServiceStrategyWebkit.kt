package com.nectar.doodle.system.impl

import com.nectar.doodle.HTMLElement
import com.nectar.doodle.dom.HtmlFactory
import com.nectar.doodle.geometry.Point
import com.nectar.doodle.geometry.Point.Companion.Origin
import com.nectar.doodle.system.Cursor
import com.nectar.doodle.system.SystemInputEvent.Modifier
import com.nectar.doodle.system.SystemInputEvent.Modifier.Alt
import com.nectar.doodle.system.SystemInputEvent.Modifier.Ctrl
import com.nectar.doodle.system.SystemInputEvent.Modifier.Meta
import com.nectar.doodle.system.SystemInputEvent.Modifier.Shift
import com.nectar.doodle.system.SystemMouseEvent
import com.nectar.doodle.system.SystemMouseEvent.Button.Button1
import com.nectar.doodle.system.SystemMouseEvent.Button.Button2
import com.nectar.doodle.system.SystemMouseEvent.Button.Button3
import com.nectar.doodle.system.SystemMouseEvent.Type
import com.nectar.doodle.system.SystemMouseEvent.Type.Down
import com.nectar.doodle.system.SystemMouseEvent.Type.Enter
import com.nectar.doodle.system.SystemMouseEvent.Type.Exit
import com.nectar.doodle.system.SystemMouseEvent.Type.Move
import com.nectar.doodle.system.SystemMouseEvent.Type.Up
import com.nectar.doodle.system.SystemMouseScrollEvent
import com.nectar.doodle.system.impl.MouseInputServiceStrategy.EventHandler
import com.nectar.doodle.utils.ifFalse
import com.nectar.doodle.utils.ifTrue
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent

internal class MouseInputServiceStrategyWebkit(private val htmlFactory: HtmlFactory): MouseInputServiceStrategy {

    override var toolTipText: String = ""
        set(new) {
            field              = new
            inputDevice?.title = new
        }

    override var cursor: Cursor = Cursor.Default
        set(new) {
            if (new != field) {
                inputDevice?.style?.cursor = new.toString()

                field = new
            }
    }

    override var mouseLocation = Origin
        private set

    private var inputDevice  = null as HTMLElement?
    private var eventHandler = null as EventHandler?

    override fun startUp(handler: EventHandler) {
        eventHandler = handler

        if (inputDevice == null) {
            inputDevice = htmlFactory.root.also {
                registerCallbacks(it)
            }
        }
    }

    override fun shutdown() {
        inputDevice?.let {
            unregisterCallbacks(it)

            inputDevice = null
        }
    }

    private fun mouseEnter(event: MouseEvent) {
        eventHandler?.handle(createMouseEvent(event, Enter, 0))
    }

    private fun mouseExit(event: MouseEvent) {
        eventHandler?.handle(createMouseEvent(event, Exit, 0))
    }

    private fun mouseUp(event: MouseEvent): Boolean {
        eventHandler?.handle(createMouseEvent(event, Up, 1))

        return isNativeElement(event.target).ifFalse {
            event.preventDefault ()
            event.stopPropagation()
        }
    }

    private fun mouseDown(event: MouseEvent): Boolean {
        eventHandler?.handle(createMouseEvent(event, Down, 1))

        return true
//        return isNativeElement(event.target).ifFalse {
//            event.preventDefault ()
//            event.stopPropagation()
//        }
    }

    // TODO: Remove this and just rely on vanilla down/up events since you usually get a single up right before a double click up
    private fun doubleClick(event: MouseEvent): Boolean {
        eventHandler?.handle(createMouseEvent(event, Up, 2))

        return isNativeElement(event.target).ifFalse {
            event.preventDefault ()
            event.stopPropagation()
        }
    }

    private fun mouseMove(event: MouseEvent): Boolean {
        mouseLocation = Point(
                x = event.clientX - htmlFactory.root.offsetLeft + htmlFactory.root.scrollLeft,
                y = event.clientY - htmlFactory.root.offsetTop  + htmlFactory.root.scrollTop
        )

        eventHandler?.handle(createMouseEvent(event, Move, 0))

        return true
//        return isNativeElement(event.target).ifFalse {
//            event.preventDefault ()
//            event.stopPropagation()
//        }
    }

    private fun mouseScroll(event: WheelEvent): Boolean {
        val deltaX = 0 - event.deltaX / 28
        val deltaY = 0 - event.deltaY / 28

        val scrollEvent = SystemMouseScrollEvent(
                mouseLocation,
                deltaX,
                deltaY,
                createModifiers(event),
                nativeScrollPanel(event.target))

        eventHandler?.handle(scrollEvent)

        return !scrollEvent.consumed.also {
//            event.preventDefault ()
//            event.stopPropagation()
        }
    }

    private fun createMouseEvent(event: MouseEvent, aType: Type, clickCount: Int): SystemMouseEvent {
        val buttons    = mutableSetOf<SystemMouseEvent.Button>()
        val buttonsInt = event.buttons.toInt()

        if (buttonsInt and 1 == 1) buttons += Button1
        if (buttonsInt and 2 == 2) buttons += Button2
        if (buttonsInt and 4 == 4) buttons += Button3

        return SystemMouseEvent(
                aType,
                mouseLocation,
                buttons,
                clickCount,
                createModifiers(event),
                nativeScrollPanel(event.target))
    }

    private fun createModifiers(event: MouseEvent) = mutableSetOf<Modifier>().also {
        event.altKey.ifTrue   { it += Alt   }
        event.ctrlKey.ifTrue  { it += Ctrl  }
        event.metaKey.ifTrue  { it += Meta  }
        event.shiftKey.ifTrue { it += Shift }
    }

    private fun registerCallbacks(element: HTMLElement) = element.also { e ->
//        e.onwheel     = { mouseScroll(it) }

        e.onmouseup   = { mouseUp    (it) }
        e.onmouseout  = { mouseExit  (it) }
        e.ondblclick  = { doubleClick(it) }
        e.onmousedown = { mouseDown  (it) }
        e.onmousemove = { mouseMove  (it) }
        e.onmouseover = { mouseEnter (it) }
    }

    private fun unregisterCallbacks(element: HTMLElement) = element.apply {
        onwheel     = null
        onmouseup   = null
        onmouseout  = null
        ondblclick  = null
        onmousedown = null
        onmousemove = null
        onmouseover = null
    }
}
