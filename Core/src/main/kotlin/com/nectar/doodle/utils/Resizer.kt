package com.nectar.doodle.utils

import com.nectar.doodle.core.Gizmo
import com.nectar.doodle.event.MouseEvent
import com.nectar.doodle.event.MouseListener
import com.nectar.doodle.event.MouseMotionListener
import com.nectar.doodle.geometry.Point
import com.nectar.doodle.geometry.Rectangle
import com.nectar.doodle.geometry.Size
import com.nectar.doodle.system.Cursor
import com.nectar.doodle.utils.Direction.East
import com.nectar.doodle.utils.Direction.North
import com.nectar.doodle.utils.Direction.South
import com.nectar.doodle.utils.Direction.West
import kotlin.math.max
import kotlin.properties.Delegates

class Resizer(gizmo: Gizmo? = null): MouseListener, MouseMotionListener {

    var gizmo: Gizmo? by Delegates.observable(gizmo) { _, old, new ->
        old?.let { it.mouseChanged -= this; it.mouseMotionChanged -= this }
        new?.let { it.mouseChanged += this; it.mouseMotionChanged += this }
    }

    init {
        this.gizmo?.let { it.mouseChanged += this; it.mouseMotionChanged += this }
    }

//    var gizmo: Gizmo? = null
//        set(new) {
//            if (gizmo == null || !gizmo!!.equals(new)) {
//                if (gizmo != null) {
//                    gizmo!!.removeMouseListener(this)
//                    gizmo!!.removePropertyListener(this)
//                    gizmo!!.removeMouseMotionListener(this)
//
//                    gizmo!!.setCursor(mOldCursor)
//                }
//
//                field = new
//
//                if (gizmo != null) {
//                    gizmo!!.addMouseListener(this)
//                    gizmo!!.addPropertyListener(this)
//                    gizmo!!.addMouseMotionListener(this)
//
//                    mOldCursor = if (new.isCursorSet()) new.getCursor() else null
//                }
//            }
//        }

    var movable     = true
    var directions  = mutableSetOf<Direction>(North, East, South, West)
    var hotspotSize = 5.0

    private var dragMode             = mutableSetOf<Direction>()
    private var oldCursor            = gizmo?.cursor
    private var initialSize          = Size.Empty
    private var initialPosition      = Point.Origin
    private var ignorePropertyChange = false

//    fun propertyChanged(aPropertyEvent: PropertyEvent) {
//        if (!ignorePropertyChange && aPropertyEvent.getProperty() === Gizmo.CURSOR) {
//            oldCursor = if ((aPropertyEvent.getSource() as Gizmo).isCursorSet()) aPropertyEvent.getNewValue() as Cursor else null
//        }
//    }

    override fun mouseReleased(event: MouseEvent) {
        dragMode.clear()

        updateCursor(event)
    }

    override fun mousePressed(event: MouseEvent) {
        dragMode.clear()

        initialPosition = event.location
        initialSize     = gizmo!!.size

        when {
            initialPosition.y <= hotspotSize                  -> dragMode.plusAssign(North)
            initialPosition.y >= gizmo!!.height - hotspotSize -> dragMode.plusAssign(South)
        }

        when {
            initialPosition.x >= gizmo!!.width  - hotspotSize -> dragMode.plusAssign(East )
            initialPosition.x <= hotspotSize                  -> dragMode.plusAssign(West )
        }
    }

    override fun mouseEntered(event: MouseEvent) {
        updateCursor(event)
    }

    override fun mouseExited(event: MouseEvent) {
        if (dragMode.isEmpty()) {
            gizmo!!.cursor = oldCursor
        }
    }

    override fun mouseMoved(mouseEvent: MouseEvent) {
        updateCursor(mouseEvent)
    }

    override fun mouseDragged(mouseEvent: MouseEvent) {
        val delta = mouseEvent.location - initialPosition

        if (dragMode.isEmpty() && movable) {
            gizmo!!.position += delta
        } else if (!dragMode.isEmpty()) {
            val bounds = gizmo!!.bounds

            var x      = bounds.x
            var y      = bounds.y
            var width  = bounds.width
            var height = bounds.height

            val minWidth  = gizmo!!.minimumSize.width
            val minHeight = gizmo!!.minimumSize.height

            if (dragMode.contains(West) && directions.contains(West)) {
                width = max(minWidth, gizmo!!.width - delta.x)
                x += bounds.width - width
            } else if (dragMode.contains(East) && directions.contains(East)) {
                width = max(minWidth, initialSize.width + delta.x)
            }

            if (dragMode.contains(North) && directions.contains(North)) {
                height = max(minHeight, gizmo!!.height - delta.y)
                y += bounds.height - height
            } else if (dragMode.contains(South) && directions.contains(South)) {
                height = max(minHeight, initialSize.height + delta.y)
            }

            gizmo!!.bounds = Rectangle(x, y, width, height)
        }
    }

    private fun updateCursor(mouseEvent: MouseEvent) {
        if (!dragMode.isEmpty()) {
            return
        }

        val x      = mouseEvent.location.x
        val y      = mouseEvent.location.y
        val mask   = mutableSetOf<Direction>()
        var innerX = false
        var innerY = false

        if (x <= hotspotSize) {
            if (directions.contains(West)) {
                mask += West
            }
        } else if (x >= gizmo!!.width - hotspotSize) {
            if (directions.contains(East)) {
                mask += East
            }
        } else {
            innerX = true
        }
        if (y <= hotspotSize) {
            if (directions.contains(North)) {
                mask += North
            }
        } else if (y >= gizmo!!.height - hotspotSize) {
            if (directions.contains(South)) {
                mask += South
            }
        } else {
            innerY = true
        }

        ignorePropertyChange = true

        gizmo!!.cursor = when {
            mask.contains(North) -> when {
                mask.contains(East)     -> Cursor.NeResize
                mask.contains(West)     -> Cursor.NwResize
                else                    -> Cursor.NResize
            }
            mask.contains(South) -> when {
                mask.contains(East)     -> Cursor.SeResize
                mask.contains(West)     -> Cursor.SwResize
                else                    -> Cursor.SResize
            }
            mask.contains(East )        -> Cursor.EResize
            mask.contains(West )        -> Cursor.WResize
            movable && innerX && innerY -> Cursor.Move
            else                        -> oldCursor
        }

        ignorePropertyChange = false
    }
}