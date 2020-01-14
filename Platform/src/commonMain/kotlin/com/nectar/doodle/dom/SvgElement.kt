package com.nectar.doodle.dom

import com.nectar.doodle.SVGCircleElement
import com.nectar.doodle.SVGElement
import com.nectar.doodle.SVGEllipseElement
import com.nectar.doodle.SVGGeometryElement
import com.nectar.doodle.SVGGradientElement
import com.nectar.doodle.SVGRectElement
import com.nectar.doodle.drawing.AffineTransform
import com.nectar.doodle.drawing.Color
import com.nectar.doodle.drawing.Renderer
import com.nectar.doodle.geometry.Circle
import com.nectar.doodle.geometry.Ellipse
import com.nectar.doodle.geometry.Point
import com.nectar.doodle.geometry.Rectangle
import com.nectar.doodle.geometry.Size
import com.nectar.measured.units.Angle
import com.nectar.measured.units.Measure
import com.nectar.measured.units.degrees
import kotlin.math.max
import kotlin.math.min

inline val SVGElement.parent get() = parentNode


inline fun SVGRectElement.setRX  (value: Double) = setAttribute("rx",     "$value")
inline fun SVGRectElement.setRY  (value: Double) = setAttribute("ry",     "$value")

inline fun SVGElement.setId      (value: String   ) { setAttributeNS(null, "id", value ); }
inline fun SVGElement.setX       (value: Double   ) = setAttribute("x",      "$value")
inline fun SVGElement.setY       (value: Double   ) = setAttribute("y",      "$value")
inline fun SVGElement.setSize    (value: Size     ) { setWidth(value.width); setHeight(value.height) }
inline fun SVGElement.setWidth   (value: Double   ) = setAttribute("width",  "$value")
inline fun SVGElement.setHeight  (value: Double   ) = setAttribute("height", "$value")
inline fun SVGElement.setPosition(value: Point    ) { setX(value.x); setY(value.y) }
inline fun SVGElement.setBounds  (value: Rectangle) { setPosition(value.position); setSize(value.size) }

inline fun SVGGradientElement.setX1(value: Double) = setAttribute("x1", "$value")
inline fun SVGGradientElement.setX2(value: Double) = setAttribute("x2", "$value")
inline fun SVGGradientElement.setY1(value: Double) = setAttribute("y1", "$value")
inline fun SVGGradientElement.setY2(value: Double) = setAttribute("y2", "$value")

inline fun SVGEllipseElement.setRX     (value: Double ) = setAttribute("rx", "$value")
inline fun SVGEllipseElement.setRY     (value: Double ) = setAttribute("ry", "$value")
inline fun SVGEllipseElement.setCX     (value: Double ) = setAttribute("cx", "$value")
inline fun SVGEllipseElement.setCY     (value: Double ) = setAttribute("cy", "$value")
inline fun SVGEllipseElement.setEllipse(value: Ellipse) {
    setCX(value.center.x)
    setCY(value.center.y)
    setRX(value.xRadius)
    setRY(value.yRadius)
}

inline fun SVGCircleElement.setCX    (value: Double) = setAttribute  ("cx", "$value")
inline fun SVGCircleElement.setCY    (value: Double) = setAttribute  ("cy", "$value")
inline fun SVGCircleElement.setR     (value: Double) = setAttribute  ("r",  "$value")
inline fun SVGCircleElement.setCircle(value: Circle) { setCX(value.center.x); setCY(value.center.y); setR(value.radius) }

inline fun SVGElement.setPathData   (value: String      ) = setAttribute  ("d",                  value        )
inline fun SVGElement.setStrokeWidth(value: Double      ) = setAttribute  ("stroke-width",     "$value"       )
inline fun SVGElement.setStrokeDash (value: String      ) = setAttribute  ("stroke-dasharray",   value        )
//inline fun SVGElement.setClipPath   (clipId: String      ) = setAttribute  ("clip-path",        "url(#$clipId)")
//inline fun SVGElement.setXLinkHref  (value : String      ) = setAttributeNS( "http://www.w3.org/1999/xlink", "xlink:href", value )

inline fun SVGGeometryElement.setPoints(vararg points: Point) = setAttribute("points", points.joinToString(" ") { "${it.x},${it.y}" })

fun SVGElement.setStopColor(color: Color) {
    setStopColor("#${color.hexString}")

    if (color.opacity != 1f) {
        setStopOpacity(color.opacity)
    }
}

fun SVGElement.setStopOffset(value: kotlin.Float) {
    setStopOffsetInternal(min(1f, max(0f, value)))
}

fun SVGElement.setGradientRotation(value: Measure<Angle>) { setAttribute("gradientTransform", "rotate(${value `in` degrees})") }

fun SVGElement.setSpreadMethod (value: String) { setAttribute("spreadMethod",  value) }
fun SVGElement.setGradientUnits(value: String) { setAttribute("gradientUnits", value) }


private fun SVGElement.setStopColor         (value: String      ) { setAttribute("stop-color",     value ) }
private fun SVGElement.setStopOpacity       (value: kotlin.Float) { setAttribute("stop-opacity", "$value") }
private fun SVGElement.setStopOffsetInternal(value: kotlin.Float) { setAttribute("offset",       "$value") }

//var SVGElement.shapeRendering
//    get() = when(getAttribute("shape-rendering")) {
//        CrispEdges.value -> CrispEdges
//        else             -> Auto
//    }
//    set(value) = setAttribute("shape-rendering", value.value)


fun convert(color: Color?, block: (String) -> Unit) = block(when (color) {
    null -> none
    else -> "#${color.hexString}"
})

inline fun SVGElement.setAlignmentBaseline(value: AlignmentBaseline) {
    setAttribute("alignment-baseline", value.value)
}

fun SVGElement.setFill(color: Color?) = convert(color) {
    setAttribute("fill", it)
    color?.let { setAttribute("fill-opacity", "${it.opacity}") }
}

inline fun SVGElement.setDefaultFill() {
    removeAttribute("fill")
}

fun SVGElement.setFillRule(fillRule: Renderer.FillRule?) {
    setAttribute("fill-rule", when (fillRule) {
        Renderer.FillRule.EvenOdd -> "evenodd"
        Renderer.FillRule.NonZero -> "nonzero"
        else                      -> ""
    })
}

fun SVGElement.setFloodColor(color: Color?) = convert(color) {
    setAttribute("flood-color",  it)
}

fun SVGElement.setFillPattern(pattern: SVGElement?) = setAttribute("fill", when (pattern) {
    null -> none
    else -> "url(#${pattern.id})"
})

fun SVGElement.setStroke(color: Color?) = convert(color) {
    setAttribute("stroke", it)
    color?.let { setAttribute("stroke-opacity", "${it.opacity}") }
}

fun SVGElement.setTransform(transform: AffineTransform?) = when(transform) {
    null -> removeTransform()
    else -> setTransform(transform.run { "matrix($scaleX,$shearY,$shearX,$scaleY,$translateX,$translateY)" })
}

inline fun SVGElement.setTransform(transform: String) = setAttribute("transform", transform)

inline fun SVGElement.removeTransform() = removeAttribute("transform")

enum class AlignmentBaseline(val value: String) {
    TextBeforeEdge("text-before-edge")
}

enum class ShapeRendering(val value: String) {
    CrispEdges("crispEdges"),
    Auto      ("auto"      )
}

private const val none = "none"