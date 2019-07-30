package com.nectar.doodle.drawing.impl

import com.nectar.doodle.dom.BorderStyle.Solid
import com.nectar.doodle.dom.HtmlFactory
import com.nectar.doodle.dom.add
import com.nectar.doodle.dom.childAt
import com.nectar.doodle.dom.clearBoundStyles
import com.nectar.doodle.dom.clearVisualStyles
import com.nectar.doodle.dom.index
import com.nectar.doodle.dom.left
import com.nectar.doodle.dom.numChildren
import com.nectar.doodle.dom.parent
import com.nectar.doodle.dom.remove
import com.nectar.doodle.dom.setBackgroundColor
import com.nectar.doodle.dom.setBorderColor
import com.nectar.doodle.dom.setBorderRadius
import com.nectar.doodle.dom.setBorderStyle
import com.nectar.doodle.dom.setBorderWidth
import com.nectar.doodle.dom.setBounds
import com.nectar.doodle.dom.setColor
import com.nectar.doodle.dom.setHeightPercent
import com.nectar.doodle.dom.setLeft
import com.nectar.doodle.dom.setOpacity
import com.nectar.doodle.dom.setSize
import com.nectar.doodle.dom.setTop
import com.nectar.doodle.dom.setTransform
import com.nectar.doodle.dom.setWidthPercent
import com.nectar.doodle.dom.top
import com.nectar.doodle.dom.translate
import com.nectar.doodle.drawing.AffineTransform
import com.nectar.doodle.drawing.AffineTransform.Companion.Identity
import com.nectar.doodle.drawing.Brush
import com.nectar.doodle.drawing.Canvas
import com.nectar.doodle.drawing.ColorBrush
import com.nectar.doodle.drawing.Font
import com.nectar.doodle.drawing.InnerShadow
import com.nectar.doodle.drawing.OuterShadow
import com.nectar.doodle.drawing.PatternBrush
import com.nectar.doodle.drawing.Pen
import com.nectar.doodle.drawing.Renderer
import com.nectar.doodle.drawing.Renderer.FillRule
import com.nectar.doodle.drawing.Renderer.Optimization.Quality
import com.nectar.doodle.drawing.Shadow
import com.nectar.doodle.drawing.TextFactory
import com.nectar.doodle.geometry.Circle
import com.nectar.doodle.geometry.ConvexPolygon
import com.nectar.doodle.geometry.Ellipse
import com.nectar.doodle.geometry.Path
import com.nectar.doodle.geometry.Point
import com.nectar.doodle.geometry.Point.Companion.Origin
import com.nectar.doodle.geometry.Rectangle
import com.nectar.doodle.geometry.Size
import com.nectar.doodle.geometry.Size.Companion.Empty
import com.nectar.doodle.image.Image
import com.nectar.doodle.image.impl.ImageImpl
import com.nectar.doodle.text.StyledText
import com.nectar.measured.units.Angle
import com.nectar.measured.units.Measure
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.Node
import org.w3c.dom.Text
import kotlin.dom.clear
import kotlin.math.max



/*internal*/ open class CanvasImpl(
        private val renderParent           : HTMLElement,
        private val htmlFactory            : HtmlFactory,
        private val textFactory            : TextFactory,
        private val vectorBackgroundFactory: VectorBackgroundFactory,
        rendererFactory: VectorRendererFactory): Canvas, Renderer, CanvasContext {

    override var size           = Empty
    override var renderRegion   = renderParent
    override var optimization   = Quality
    override var renderPosition = null as Node?

    private val vectorRenderer by lazy { rendererFactory(this) }

    override val shadows = mutableListOf<Shadow>()

    protected open fun isSimple(brush: Brush) = when (brush) {
        is ColorBrush, is PatternBrush -> true
        else                           -> false
    }

    override fun rect(rectangle: Rectangle,           brush: Brush ) = if (isSimple(brush)) present(brush = brush) { getRect(rectangle) } else vectorRenderer.rect(rectangle, brush)
    override fun rect(rectangle: Rectangle, pen: Pen, brush: Brush?) = vectorRenderer.rect(rectangle, pen, brush)

    override fun rect(rectangle: Rectangle, radius: Double,           brush: Brush ) = if (isSimple(brush)) present(brush = brush) { roundedRect(rectangle, radius) } else vectorRenderer.rect(rectangle, radius, brush)
    override fun rect(rectangle: Rectangle, radius: Double, pen: Pen, brush: Brush?) = vectorRenderer.rect(rectangle, radius, pen, brush)

    override fun circle(circle: Circle,           brush: Brush ) = if (isSimple(brush)) present(brush = brush) { roundedRect(circle.boundingRectangle, circle.radius) } else vectorRenderer.circle(circle, brush)
    override fun circle(circle: Circle, pen: Pen, brush: Brush?) = vectorRenderer.circle(circle, pen, brush)

    override fun ellipse(ellipse: Ellipse,           brush: Brush ) = if (isSimple(brush)) present(brush = brush) { roundedRect(ellipse.boundingRectangle, ellipse.xRadius, ellipse.yRadius) } else vectorRenderer.ellipse(ellipse, brush)
    override fun ellipse(ellipse: Ellipse, pen: Pen, brush: Brush?) = vectorRenderer.ellipse(ellipse, pen, brush)

    // =============== Complex =============== //

    override fun line(point1: Point, point2: Point, pen: Pen) = vectorRenderer.line(point1, point2, pen)

    override fun path(points: List<Point>, pen: Pen                                   ) = vectorRenderer.path(points, pen                 )
    override fun path(points: List<Point>,           brush: Brush, fillRule: FillRule?) = vectorRenderer.path(points,      brush, fillRule)
    override fun path(points: List<Point>, pen: Pen, brush: Brush, fillRule: FillRule?) = vectorRenderer.path(points, pen, brush, fillRule)

    override fun path(path: Path, pen: Pen                                   ) = vectorRenderer.path(path, pen                 )
    override fun path(path: Path,           brush: Brush, fillRule: FillRule?) = vectorRenderer.path(path,      brush, fillRule)
    override fun path(path: Path, pen: Pen, brush: Brush, fillRule: FillRule?) = vectorRenderer.path(path, pen, brush, fillRule)

    override fun poly(polygon: ConvexPolygon,           brush: Brush ) = vectorRenderer.poly(polygon,      brush)
    override fun poly(polygon: ConvexPolygon, pen: Pen, brush: Brush?) = vectorRenderer.poly(polygon, pen, brush)

    override fun arc(center: Point, radius: Double, sweep: Measure<Angle>, rotation: Measure<Angle>,           brush: Brush ) = vectorRenderer.arc(center, radius, sweep, rotation,      brush)
    override fun arc(center: Point, radius: Double, sweep: Measure<Angle>, rotation: Measure<Angle>, pen: Pen, brush: Brush?) = vectorRenderer.arc(center, radius, sweep, rotation, pen, brush)

    override fun wedge(center: Point, radius: Double, sweep: Measure<Angle>, rotation: Measure<Angle>,           brush: Brush ) = vectorRenderer.wedge(center, radius, sweep, rotation,      brush)
    override fun wedge(center: Point, radius: Double, sweep: Measure<Angle>, rotation: Measure<Angle>, pen: Pen, brush: Brush?) = vectorRenderer.wedge(center, radius, sweep, rotation, pen, brush)

//    override val imageData: ImageData
//        get () {
//            val elements = (0 until region.numChildren).mapTo(mutableListOf()) { region.childAt(it)!! }
//
//            return ImageDataImpl(elements)
//        }

//    override fun import(imageData: ImageData, at: Point) {
//        if (imageData is ImageDataImpl) {
//            val elements = imageData.elements
//            val clones   = elements.mapTo(ArrayList(elements.size)) { it.cloneNode(deep = true) as HTMLElement }
//
//            addData(clones, at)
//        }
//    }

    override fun text(text: StyledText, at: Point) {
        completeOperation(createStyledTextGlyph(text, at))
    }

    override fun text(text: String, font: Font?, at: Point, brush: Brush) {
        when {
            text.isEmpty() || !brush.visible -> return
            brush is ColorBrush              -> completeOperation(createTextGlyph(brush, text, font, at))
            else                             -> return // TODO IMPLEMENT
        }
    }

    override fun wrapped(text: String, font: Font, point: Point, leftMargin: Double, rightMargin: Double, brush: Brush) {
        when {
            text.isEmpty() || !brush.visible -> return
            brush is ColorBrush              -> completeOperation(createWrappedTextGlyph(brush,
                                                                  text,
                                                                  font,
                                                                  point,
                                                                  leftMargin,
                                                                  rightMargin))
            else                             -> return // TODO IMPLEMENT
        }
    }

    override fun wrapped(text: StyledText, point: Point, leftMargin: Double, rightMargin: Double) {
        completeOperation(createWrappedStyleTextGlyph(
                    text,
                    point,
                    leftMargin,
                    rightMargin))
    }

    override fun image(image: Image, destination: Rectangle, radius: Double, opacity: Float) {
        if (shouldDrawImage(image, Rectangle(size = image.size), destination, opacity)) {
            completeOperation(createImage(image, destination, radius, opacity))
        }
    }

    override fun image(image: Image, source: Rectangle, destination: Rectangle, opacity: Float) {
        if (shouldDrawImage(image, source, destination, opacity)) {
            val clipRect          = getRect(destination)
            val oldRenderPosition = renderPosition

            renderPosition = clipRect.childAt(0)

            val xRatio = destination.width  / source.width
            val yRatio = destination.height / source.height

            val imageElement = createImage(image,
                    Rectangle(0 - xRatio * source.x,
                              0 - yRatio * source.y,
                              xRatio * image.size.width,
                              yRatio * image.size.height),
                    0.0,
                    opacity)

            if (renderPosition !== imageElement) {
                clipRect.add(imageElement)
            }

            renderPosition = oldRenderPosition

            completeOperation(clipRect)
        }
    }

    override fun transform(transform: AffineTransform, block: Canvas.() -> Unit) = when (transform.isIdentity) {
        true -> block()
        else -> subFrame(block) {
            it.style.setTransform(transform)
        }
    }

    override fun clear() {
        renderPosition = renderParent.childAt(0)

        vectorRenderer.clear()
    }

    override fun flush() {
        renderPosition?.let {
            val index = renderParent.index(it)

            if (index >= 0) {
                while (index < renderParent.numChildren) {
                    renderParent.remove(renderParent.childAt(index)!!)
                }
            }
        }

        vectorRenderer.flush()
    }

    override fun clip(rectangle: Rectangle, block: Canvas.() -> Unit) = subFrame({ translate(-rectangle.position, block) }) {
        it.style.setBounds(rectangle)
    }

    override fun shadow(shadow: Shadow, block: Canvas.() -> Unit) {
        shadows += shadow

        apply(block)

        shadows -= shadow
    }

    private fun subFrame(block: Canvas.() -> Unit, configure: (HTMLElement) -> Unit) {
        // TODO: Not sure if this is causing more element creations than necessary on re-draw

        val clipRect = getRectElement()

        if (clipRect.parentNode == null) {
            renderPosition?.let {
                it.parent?.replaceChild(clipRect, it)
            } ?: renderRegion.add(clipRect)
        }

        clipRect.style.setSize(size)

        configure(clipRect)

        renderRegion   = clipRect
        renderPosition = clipRect.firstChild

        apply(block)

        renderRegion   = renderRegion.parent as HTMLElement
        renderPosition = clipRect.nextSibling
    }

    fun addData(elements: List<HTMLElement>, at: Point = Origin) = elements.forEach { element ->

        if (at.y != 0.0 ) element.style.setTop (element.top  + at.y)
        if (at.x != 0.0 ) element.style.setLeft(element.left + at.x)

        if (renderPosition != null) {
            renderPosition?.let {
                val nextSibling = it.nextSibling

                if (element !== it) {
                    renderRegion.replaceChild(element, it)
                }

                renderPosition = nextSibling
            }
        } else {
            renderRegion.add(element)
        }
    }

    private fun visible(pen: Pen?, brush: Brush?) = (pen?.visible ?: false) || (brush?.visible ?: false)

    private fun present(pen: Pen? = null, brush: Brush?, block: () -> HTMLElement?) {
        if (visible(pen, brush)) {
            block()?.let {
                when (brush) {
                    is ColorBrush   -> it.style.setBackgroundColor(brush.color)
                    is PatternBrush -> it.style.background = vectorBackgroundFactory(brush)
                }
                if (pen != null) {
                    it.style.setBorderWidth(pen.thickness)
                    it.style.setBorderStyle(Solid()      )
                    it.style.setBorderColor(pen.color    )
                }

                completeOperation(it)
            }
        }
    }

    private fun getRectElement(): HTMLElement = htmlFactory.createOrUse("B", renderPosition).also {
        it.clear()
        it.style.border    = ""
        it.style.transform = ""
        it.style.setWidthPercent (100.0)
        it.style.setHeightPercent(100.0)
    }

    private fun getRect(rectangle: Rectangle): HTMLElement = getRectElement().also {
        /*
         * This is done b/c there's an issue w/ handling half-pixels in Chrome: https://movier.me/blog/2017/realize-half-pixel-border-in-chrome/
         */

        var transform = Identity.translate(rectangle.position)
        var width     = rectangle.width
        var height    = rectangle.height

        if (rectangle.height < 1) {
            height    *= 2
            transform  = transform.scale(y = 0.5)
        }

        if (rectangle.width < 1) {
            width     *= 2
            transform  = transform.scale(x = 0.5)
        }

        it.style.setSize     (Size(width, height))
        it.style.setTransform(transform          )
    }

    private fun roundedRect(rectangle: Rectangle,                   radius: Double) = getRect(rectangle).also { it.style.setBorderRadius(radius          ) }
    private fun roundedRect(rectangle: Rectangle, xRadius: Double, yRadius: Double) = getRect(rectangle).also { it.style.setBorderRadius(xRadius, yRadius) }

    private fun shouldDrawImage(image: Image, source: Rectangle, destination: Rectangle, opacity: Float) = image is ImageImpl && opacity > 0 && !(source.empty || destination.empty)

    private fun completeOperation(element: HTMLElement): HTMLElement {
        shadows.forEach {
            // FIXME: Need to move this to Style and avoid raw px
            val shadow = "${when(it) {
                is InnerShadow -> "inset "
                is OuterShadow -> ""
            }}${it.horizontal}px ${it.vertical}px ${it.blurRadius}px #${it.color.hexString}"

            when (element.firstChild) {
                is Text -> element.style.textShadow += shadow
                else    -> element.style.boxShadow  += shadow
            }
        }

        if (renderPosition == null) {
            renderRegion.add(element)
        } else {
            if (element !== renderPosition) {
                renderPosition?.parent?.replaceChild(element, renderPosition!!)
            }

            renderPosition = element.nextSibling
        }

        return element
    }

    private fun createTextGlyph(brush: ColorBrush, text: String, font: Font?, at: Point): HTMLElement {
        val element = textFactory.create(text, font, if (renderPosition is HTMLElement) renderPosition as HTMLElement else null)

        return configure(element, brush, at)
    }

    private fun createWrappedTextGlyph(brush: ColorBrush, text: String, font: Font, at: Point, leftMargin: Double, rightMargin: Double): HTMLElement {
        val indent  = max(0.0, at.x - leftMargin)
        val element = textFactory.wrapped(
                text,
                font,
                width    = rightMargin - leftMargin,
                indent   = indent,
                possible = if (renderPosition is HTMLElement) renderPosition as HTMLElement else null)

        return configure(element, brush, at)
    }

    private fun createStyledTextGlyph(text: StyledText, at: Point): HTMLElement {
        val element = textFactory.create(text, if (renderPosition is HTMLElement) renderPosition as HTMLElement else null)

        element.style.translate(at)

        return element
    }

    private fun createWrappedStyleTextGlyph(text: StyledText, at: Point, leftMargin: Double, rightMargin: Double): HTMLElement {
        val indent  = max(0.0, at.x - leftMargin)
        val element = textFactory.wrapped(
                text     = text,
                width    = rightMargin - leftMargin,
                indent   = indent,
                possible = if (renderPosition is HTMLElement) renderPosition as HTMLElement else null)

        element.style.translate(at)

        return element
    }

    private fun configure(element: HTMLElement, brush: ColorBrush, position: Point): HTMLElement = element.also {
        it.style.apply {
            translate (position           )
            setColor  (brush.color        )
            setOpacity(brush.color.opacity)
        }
    }

    private fun createImage(image: Image, rectangle: Rectangle, radius: Double, opacity: Float): HTMLImageElement = pickImageElement((image as ImageImpl).image, renderPosition).also {
        it.style.apply {
            translate      (rectangle.position)
            setSize        (rectangle.size    )
            setOpacity     (opacity           )
            setBorderRadius(radius            )
        }
    }

    private fun pickImageElement(image: HTMLImageElement, possible: Node?): HTMLImageElement {
        var result = possible

        if (result == null || result !is HTMLImageElement || result.parentNode != null && result.nodeName != image.nodeName) {
            result = image.cloneNode(false)
            (result as? HTMLImageElement)?.ondragstart = { false } // TODO: This is a work-around for Firefox not honoring the draggable (= false) property for images
        } else {
            result.clearBoundStyles ()
            result.clearVisualStyles()
            result.src = image.src
        }

        return result as HTMLImageElement
    }
}

//class ImageDataImpl(val elements: List<HTMLElement>): ImageData