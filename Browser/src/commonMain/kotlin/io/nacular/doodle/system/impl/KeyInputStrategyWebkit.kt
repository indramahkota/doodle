package io.nacular.doodle.system.impl

import io.nacular.doodle.dom.Event
import io.nacular.doodle.dom.HTMLElement
import io.nacular.doodle.dom.HtmlFactory
import io.nacular.doodle.dom.KeyboardEvent
import io.nacular.doodle.event.KeyCode
import io.nacular.doodle.event.KeyCode.Companion.Space
import io.nacular.doodle.event.KeyState
import io.nacular.doodle.event.KeyState.Type
import io.nacular.doodle.event.KeyState.Type.Down
import io.nacular.doodle.event.KeyState.Type.Up
import io.nacular.doodle.event.KeyText
import io.nacular.doodle.system.KeyInputService
import io.nacular.doodle.system.KeyInputService.KeyResponse.Consumed
import io.nacular.doodle.system.KeyInputService.KeyResponse.Ignored
import io.nacular.doodle.system.SystemInputEvent.Modifier
import io.nacular.doodle.system.SystemInputEvent.Modifier.Alt
import io.nacular.doodle.system.SystemInputEvent.Modifier.Ctrl
import io.nacular.doodle.system.SystemInputEvent.Modifier.Meta
import io.nacular.doodle.system.SystemInputEvent.Modifier.Shift
import io.nacular.doodle.system.impl.KeyInputServiceStrategy.EventHandler
import io.nacular.doodle.utils.ifFalse
import io.nacular.doodle.utils.ifTrue

/**
 * Created by Nicholas Eddy on 3/10/18.
 */
internal class KeyInputStrategyWebkit(private val htmlFactory: HtmlFactory): KeyInputServiceStrategy {

    private var eventHandler = null as EventHandler?
    private var inputDevice  = null as HTMLElement?

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

    private var previousKeyDownResponse = false

    private fun keyUp(event: KeyboardEvent) = when {
        dispatchKeyEvent(event, Up) == Consumed -> false
        isNativeElement(event.target)           -> true
        else                                    -> false
    }.ifFalse { event.preventBrowserDefault() }

    private fun keyDown (event: KeyboardEvent) = when {
        dispatchKeyEvent(event, Down).also { previousKeyDownResponse = it == Consumed } == Consumed -> false
        isNativeElement(event.target)                                                               -> true
        else                                                                                        -> false
    }.ifFalse { event.preventBrowserDefault() }

    private fun keyPress(event: KeyboardEvent) = when (event.code?.let { KeyCode(it) }) {
        Space -> previousKeyDownResponse || isNativeElement(event.target)
        else  -> true
    }

    private fun dispatchKeyEvent(event: KeyboardEvent, type: Type): KeyInputService.KeyResponse = when {
        event.code != null && event.key != null -> eventHandler?.invoke(
            KeyState(KeyCode(event.code!!), KeyText(event.key!!), createModifiers(event), type), event.target
        ) ?: Ignored
        else -> Ignored
    }

    private fun createModifiers(event: KeyboardEvent) = mutableSetOf<Modifier>().also {
        event.altKey.ifTrue   { it += Alt   }
        event.ctrlKey.ifTrue  { it += Ctrl  }
        event.metaKey.ifTrue  { it += Meta  }
        event.shiftKey.ifTrue { it += Shift }
    }

    private fun registerCallbacks(element: HTMLElement) = element.apply {
        onkeyup    = { this@KeyInputStrategyWebkit.keyUp   (it) }
        onkeydown  = { this@KeyInputStrategyWebkit.keyDown (it) }
        onkeypress = { this@KeyInputStrategyWebkit.keyPress(it) }
    }

    private fun unregisterCallbacks(element: HTMLElement) = element.apply {
        onkeyup   = null
        onkeydown = null
    }

    private fun Event.preventBrowserDefault() {
        preventDefault ()
        stopPropagation()
    }
}