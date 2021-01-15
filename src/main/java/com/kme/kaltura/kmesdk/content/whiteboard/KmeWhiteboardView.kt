package com.kme.kaltura.kmesdk.content.whiteboard

import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.View
import com.kme.kaltura.kmesdk.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage.WhiteboardPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardShapeType
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardToolType
import com.kme.kaltura.kmesdk.ws.message.whiteboard.KmeWhiteboardPath
import kotlin.math.pow
import kotlin.math.sqrt


class KmeWhiteboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), IKmeWhiteboardListener {

    private val TAG = javaClass.simpleName

    private val paint: Paint = Paint()
    private val canvasPaint: Paint = Paint(Paint.DITHER_FLAG)
    private var drawCanvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null
    private val pathMatrix: Matrix = Matrix()
    private val imageBounds: RectF = RectF()

    private val eraseXfermode by lazy { PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    private var imageWidth: Float = 0f
    private var imageHeight: Float = 0f

    private var pathsMap: MutableMap<WhiteboardPayload.Drawing, Path?> = LinkedHashMap()

    private val drawings: MutableList<WhiteboardPayload.Drawing> by lazy { mutableListOf() }

    private var originalImageSize: Size? = null
    private val defaultSize = Size(1280, 720)

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        paint.apply {
            strokeWidth = ptToDp(6f, context)
            style = Paint.Style.STROKE
            isAntiAlias = true
            isDither = true
            color = Color.BLACK
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    }

    override fun init(originalImageSize: Size, imageBounds: RectF) {
        this.originalImageSize = originalImageSize
        this.imageBounds.set(imageBounds)
        invalidatePaths()
    }

    override fun setDrawings(drawings: List<WhiteboardPayload.Drawing>) {
        this.drawings.clear()
        this.drawings.addAll(drawings)
        invalidatePaths()
    }

    override fun addDrawing(drawing: WhiteboardPayload.Drawing) {
        val index =
            this.drawings.indexOfFirst { savedDrawing -> savedDrawing.layer == drawing.layer }
        if (index >= 0) {
            val modifiedDrawing = this.drawings[index]
            drawing.tool = modifiedDrawing.tool
            this.drawings[index] = drawing
        } else {
            this.drawings.add(drawing)
        }
        invalidatePaths()
    }

    override fun removeDrawing(layer: String) {
        this.drawings.removeAll { drawing -> drawing.layer == layer }
        invalidatePaths()
    }

    override fun removeDrawings() {
        this.drawings.clear()
        invalidatePaths()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        invalidatePaths()
    }

    private fun invalidatePaths() {
        measureBounds()

        pathsMap.clear()
        drawings.forEach {
            Log.e(TAG, "invalidatePaths: ${it.path}")
            calculatePath(it)
        }
        pathsMap = pathsMap.toSortedMap { o1, o2 ->
            o1.layer.getCreatingDate().compareTo(o2.layer.getCreatingDate())
        }

        invalidate()
    }

    private fun measureBounds() {
        if (!imageBounds.isEmpty) {
            imageWidth = computeLength(imageBounds.left, imageBounds.right)
            imageHeight = computeLength(imageBounds.top, imageBounds.bottom)

            canvasBitmap = Bitmap.createBitmap(
                imageBounds.right.toInt(),
                imageBounds.bottom.toInt(),
                Bitmap.Config.ARGB_8888
            ).also {
                drawCanvas = Canvas(it)
            }
        }
    }

    private fun calculatePath(drawing: WhiteboardPayload.Drawing) {
        val path = when {
            drawing.path?.type == KmeWhiteboardShapeType.RECTANGLE
                    || drawing.path?.childrenPath?.type == KmeWhiteboardShapeType.RECTANGLE -> {
                calculateRectPath(drawing)
            }
            drawing.isLine() -> {
                calculateLinePath(drawing)
            }
            drawing.isPencilPath() -> {
                calculatePencilPath(drawing)
            }
            drawing.path.isClosedPath() -> {
                calculateClosedPath(drawing)
            }
            else -> null
        }

        pathsMap[drawing] = path
    }

    private fun calculatePencilPath(drawing: WhiteboardPayload.Drawing): Path? {
        val drawingPath = drawing.getDrawingPath() ?: return null
        val segments =
            drawingPath.segments.validatePencilSegments<List<List<List<Float>>>>() ?: return null

        val path = Path()

        segments.forEachIndexed { index, segment ->
            val point1 = PointF(segment[0][0].toX(), segment[0][1].toY())
            val point2 = PointF(segment[2][0].toX(), segment[2][1].toY())
            var point3 = point1
            var point4 = point2

            if (index + 1 < segments.size) {
                point3 = PointF(segments[index + 1][0][0].toX(), segments[index + 1][0][1].toY())
                point4 = PointF(segments[index + 1][1][0].toX(), segments[index + 1][1][1].toY())
            }

            if (index == 0) {
                path.moveTo(point1.x, point1.y)
            } else {
                path.cubicTo(
                    point1.x + point2.x,
                    point1.y + point2.y,
                    point3.x + point4.x,
                    point3.y + point4.y,
                    point3.x,
                    point3.y
                )
            }
        }

        path.applyDefaultPathMatrix(drawing)
        return path
    }

    private fun calculateLinePath(drawing: WhiteboardPayload.Drawing): Path? {
        val drawingPath = drawing.getDrawingPath() ?: return null
        val segments = drawingPath.segments.validateSegments<List<List<Float>>>() ?: return null

        val path = Path()

        segments.forEachIndexed { index, segment ->
            if (index == 0) {
                path.moveTo(segment[0].toX(), segment[1].toY())
            } else {
                path.lineTo(segment[0].toX(), segment[1].toY())
            }
        }

        path.applyDefaultPathMatrix(drawing)
        return path
    }

    private fun calculateClosedPath(drawing: WhiteboardPayload.Drawing): Path? {
        val drawingPath = drawing.getDrawingPath() ?: return null
        val segments = drawingPath.segments.validateSegments<List<List<Float>>>() ?: return null

        val path = Path()

        segments.forEachIndexed { index, segment ->
            if (index == 0) {
                path.moveTo(segment[0].toX(), segment[1].toY())
            } else {
                path.lineTo(segment[0].toX(), segment[1].toY())
            }
        }

        path.close()
        path.applyDefaultPathMatrix(drawing)
        return path
    }

    private fun calculateRectPath(drawing: WhiteboardPayload.Drawing): Path? {
        val drawingPath = drawing.getDrawingPath()
        val parentPath = drawing.path
        val rectSize = drawingPath?.size

        if (parentPath == null || drawingPath == null || rectSize == null || rectSize.size != 2) return null

        val path = Path()

        val fromX = 0f
        val fromY = 0f
        val toX = rectSize[0].toX()
        val toY = rectSize[1].toY()
        val radiusX = drawingPath.radius?.get(0)?.toX() ?: 0f
        val radiusY = drawingPath.radius?.get(1)?.toY() ?: 0f

        if (radiusX > 0f && radiusY > 0f) {
            path.addRoundRect(fromX, fromY, toX, toY, radiusX, radiusY, Path.Direction.CW)
        } else {
            path.addRect(fromX, fromY, toX, toY, Path.Direction.CW)
        }

        path.applyRectMatrix(drawing)
        return path
    }

    private fun Path.applyDefaultPathMatrix(drawing: WhiteboardPayload.Drawing) {
        pathMatrix.reset()

        val drawingPath = drawing.path ?: return
        val matrix = drawingPath.matrix ?: drawingPath.childrenPath?.matrix ?: return
        if (matrix.isEmpty() || matrix.size != 6) return

        var pivotX = drawingPath.pivot?.firstOrNull()?.toX() ?: 0f
        var pivotY = drawingPath.pivot?.lastOrNull()?.toY() ?: 0f

        val scaleX = when {
            drawingPath.matrix?.get(0) ?: 1 != 1 -> {
                drawingPath.matrix?.get(0) ?: 1f
            }
            drawingPath.childrenPath?.matrix?.get(0) ?: 1 != 1 -> {
                drawingPath.childrenPath?.matrix?.get(0) ?: 1f
            }
            else -> {
                1f
            }
        }

        val scaleY = when {
            drawingPath.matrix?.get(3) ?: 1 != 1 -> {
                drawingPath.matrix?.get(3) ?: 1f
            }
            drawingPath.childrenPath?.matrix?.get(3) ?: 1 != 1 -> {
                drawingPath.childrenPath?.matrix?.get(3) ?: 1f
            }
            else -> {
                1f
            }
        }

        val skewX = matrix[2]
        val skewY = matrix[1]


        val scalePointX = when {
            drawingPath.matrix?.get(4) ?: 0f != 0f -> {
                drawingPath.matrix?.get(4)?.toX() ?: 0f
            }
            drawingPath.childrenPath?.matrix?.get(4) ?: 0f != 0f -> {
                drawingPath.childrenPath?.matrix?.get(4)?.toX() ?: 0f
            }
            else -> {
                0f
            }
        }
        val scalePointY = when {
            drawingPath.matrix?.get(5) ?: 0f != 0f -> {
                drawingPath.matrix?.get(5)?.toY() ?: 0f
            }
            drawingPath.childrenPath?.matrix?.get(5) ?: 0f != 0f -> {
                drawingPath.childrenPath?.matrix?.get(5)?.toY() ?: 0f
            }
            else -> {
                0f
            }
        }

        if (pivotX > 0 || pivotY > 0) {
            pivotX += imageBounds.left
            pivotY += imageBounds.top
        }

        val translateX = scalePointX + imageBounds.left
        val translateY = scalePointY + imageBounds.top

        pathMatrix.postScale(scaleX, scaleY)
        pathMatrix.postTranslate(translateX, translateY)

        transform(pathMatrix)
    }

    private fun Path.applyRectMatrix(drawing: WhiteboardPayload.Drawing) {
        pathMatrix.reset()

        val parentPath = drawing.path ?: return
        val childrenPath = drawing.path?.childrenPath
        val parentMatrix = parentPath.matrix
        val childrenMatrix = childrenPath?.matrix

        var pivotX = drawing.path?.pivot?.firstOrNull()?.toX()
            ?: drawing.path?.childrenPath?.pivot?.firstOrNull()?.toX() ?: 0f
        var pivotY = drawing.path?.pivot?.lastOrNull()?.toY()
            ?: drawing.path?.childrenPath?.pivot?.lastOrNull()?.toY() ?: 0f

        val scaleX = when {
            parentMatrix?.get(0) ?: 1f != 1f -> {
                parentMatrix?.get(0) ?: 1f
            }
            childrenMatrix?.get(0) ?: 1f != 1f -> {
                childrenMatrix?.get(0) ?: 1f
            }
            else -> {
                1f
            }
        }

        val scaleY = when {
            parentMatrix?.get(3) ?: 1f != 1f -> {
                parentMatrix?.get(3) ?: 1f
            }
            childrenMatrix?.get(3) ?: 1f != 1f -> {
                childrenMatrix?.get(3) ?: 1f
            }
            else -> {
                1f
            }
        }


        val pathBounds = RectF()
        computeBounds(pathBounds, false)

        pivotX -= pathBounds.width() / 2
        pivotY -= pathBounds.height() / 2

        val translateX = (parentMatrix?.get(4)?.toX() ?: 0f) + imageBounds.left
        val translateY = (parentMatrix?.get(5)?.toY() ?: 0f) + imageBounds.top

        pathMatrix.postTranslate(pivotX, pivotY)
        pathMatrix.postScale(scaleX, scaleY)
        pathMatrix.postTranslate(translateX, translateY)

        transform(pathMatrix)
    }

    private fun invalidatePaint(path: KmeWhiteboardPath?) {
        path?.let {
            paint.color = it.getPaintColor()
            paint.strokeWidth = ptToDp(it.strokeWidth.toFloat(), context)
            paint.strokeCap = path.strokeCap.getPaintCap()
            paint.alpha = path.opacity.getPaintAlpha()
            paint.style = path.getPaintStyle()
            paint.textSize = path.childrenPath?.fontSize?.toFloat() ?: 25f

            val isEraseMode = path.blendMode.isEraseMode()
            if (isEraseMode) {
                paint.xfermode = eraseXfermode
            } else {
                paint.xfermode = null
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (!imageBounds.isEmpty && imageWidth > 0 && imageHeight > 0) {
            canvasBitmap?.let {
                canvas?.drawBitmap(it, 0f, 0f, canvasPaint)
            }

            for (pathItem in pathsMap) {
                val drawing = pathItem.key
                invalidatePaint(drawing.getDrawingPath())

                if (drawing.isText()) {
                    canvas?.drawTextPath(drawing)
                } else {
                    pathItem.value?.let {
                        canvas?.drawPath(it, paint)
                    }
                }
            }
        }
    }

    private fun Canvas.drawTextPath(drawing: WhiteboardPayload.Drawing?) {
        val textDrawing = drawing?.path?.childrenPath
        if (textDrawing != null && !textDrawing.content.isNullOrEmpty()) {

            val bounds = Rect()
            paint.getTextBounds(textDrawing.content, 0, textDrawing.content.length, bounds)

            val scaleX = drawing.path?.matrix?.get(0) ?: 1f
            val scaleY = drawing.path?.matrix?.get(3) ?: 1f

            val width = if (textDrawing.rectangle?.size == 4) {
                textDrawing.rectangle[2].toX() * scaleX
            } else {
                0f
            }

            val scalePointX =
                textDrawing.rectangle?.get(0)?.toX()?.times(scaleX)
                    ?.plus(drawing.path?.matrix?.get(4)?.toX() ?: 0f) ?: 0f
            val scalePointY = textDrawing.rectangle?.get(1)?.toY()?.times(scaleY)
                ?.plus(drawing.path?.matrix?.get(5)?.toY() ?: 0f) ?: 0f
            val translateX = imageBounds.left + (scalePointX)
            val translateY = imageBounds.top + (scalePointY)

            val textPaint = TextPaint(paint)

            val staticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(
                    textDrawing.content,
                    0,
                    textDrawing.content.length,
                    textPaint,
                    width.toInt()
                ).apply {
                    setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    setIncludePad(false)
                    setLineSpacing(0.0f, 1.0f)
                }.build()
            } else {
                StaticLayout(
                    textDrawing.content,
                    textPaint,
                    width.toInt(),
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0.0f,
                    false
                )
            }

            save()
            translate(translateX, translateY)
            staticLayout.draw(this)
            restore()
        }
    }

    private fun computeLength(p1: Float, p2: Float) = sqrt((p2 - p1).pow(2))

    private fun Float.toX(): Float {
        return this * imageWidth / (originalImageSize?.width
            ?: defaultSize.width)
    }

    private fun Float.toY(): Float {
        return this * imageHeight / (originalImageSize?.height
            ?: defaultSize.height)
    }

    private fun WhiteboardPayload.Drawing?.isLine(): Boolean {
        return this != null && this.tool == KmeWhiteboardToolType.LINE_TOOL && this.path != null
                && (!this.path?.segments.isNullOrEmpty() || !this.path?.childrenPath?.segments.isNullOrEmpty())
    }

    private fun KmeWhiteboardPath?.isClosedPath(): Boolean {
        if (this == null || (this.segments.isNullOrEmpty() && this.childrenPath?.segments.isNullOrEmpty())) return false
        return this.closed == true || this.childrenPath?.closed == true
    }

    private fun WhiteboardPayload.Drawing?.isPencilPath(): Boolean {
        return this != null && this.tool == KmeWhiteboardToolType.PENCIL && this.path != null
                && (!this.path?.segments.isNullOrEmpty() || !this.path?.childrenPath?.segments.isNullOrEmpty())
    }

    private fun WhiteboardPayload.Drawing?.isText(): Boolean {
        return this != null && this.tool == KmeWhiteboardToolType.TEXT && this.path != null
    }

    private inline fun <reified T> List<List<Any>>?.validateSegments(): T? {
        return if (this is T) {
            this
        } else {
            null
        }
    }

    private inline fun <reified T : List<List<List<Float>>>> List<List<Any>>?.validatePencilSegments(): T? {
        if (this == null) return null
        val segments = this.toMutableList()
        segments.forEachIndexed { index, it ->
            if (it.size == 2 && it[0] is Double && it[1] is Double) {
                val pointX = (it[0] as Double).toFloat()
                val pointY = (it[1] as Double).toFloat()

                val segment = mutableListOf<List<Float>>()
                val point = mutableListOf<Float>()
                point.add(pointX)
                point.add(pointY)
                val handlePoint = mutableListOf<Float>()
                handlePoint.add(0.0f)
                handlePoint.add(0.0f)
                segment.add(point)
                segment.add(handlePoint)
                segment.add(handlePoint)

                segments[index] = segment
            }
        }
        return segments as T
    }

    private fun String?.getCreatingDate(): Long {
        return if (this != null) {
            "(\\d+)\$".toRegex().find(this)?.value?.toLong() ?: 0L
        } else {
            0L
        }
    }

    private fun WhiteboardPayload.Drawing?.getDrawingPath(): KmeWhiteboardPath? {
        if (this == null || this.path == null) return null
        return this.path?.childrenPath ?: this.path
    }

    private fun KmeWhiteboardPath?.getDrawingMatrix(): FloatArray? {
        if (this == null) return null
        return this.childrenPath?.matrix ?: this.matrix
    }

}