package io.nacular.doodle.skia

import io.nacular.doodle.drawing.AffineTransform
import io.nacular.doodle.drawing.Color
import io.nacular.doodle.geometry.Path
import io.nacular.doodle.geometry.Point
import io.nacular.doodle.geometry.Polygon
import io.nacular.doodle.geometry.Rectangle
import io.nacular.doodle.geometry.toPath
import org.jetbrains.skija.Image
import org.jetbrains.skija.ImageInfo
import org.jetbrains.skija.Matrix33
import org.jetbrains.skija.RRect
import org.jetbrains.skija.Rect
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO


internal fun Color.skia(): Int = (((opacity * 0xFF).toUInt() shl 24) + (red.toUInt() shl 16) + (green.toUInt() shl 8) + blue.toUInt()).toInt()

internal fun Rectangle.skia() = Rect.makeXYWH(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
internal fun Rectangle.rrect(radius: Float) = RRect.makeXYWH(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), radius)

internal fun Polygon.skia() = toPath().skia()

internal fun Point.skia() = org.jetbrains.skija.Point(x.toFloat(), y.toFloat())

internal fun Path.skia() = org.jetbrains.skija.Path.makeFromSVGString(data)

internal fun AffineTransform.skia() = Matrix33(
        scaleX.toFloat    (),
        shearX.toFloat    (),
        translateX.toFloat(),
        shearY.toFloat    (),
        scaleY.toFloat    (),
        translateY.toFloat(),
        0f,
        0f,
        1f
)