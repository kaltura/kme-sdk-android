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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    private val defaultMatrixArray: FloatArray by lazy { floatArrayOf(1f, 0f, 0f, 1f, 0f, 0f) }

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val eraseXfermode by lazy { PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    private var imageWidth: Float = 0f
    private var imageHeight: Float = 0f

    private var pathsMap: MutableMap<WhiteboardPayload.Drawing, Any?> = LinkedHashMap()

    private val drawings: MutableList<WhiteboardPayload.Drawing> by lazy { mutableListOf() }

    private var config: Config? = null
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

    override fun init(config: Config?) {
        this.config = config
        config?.let {
            this.originalImageSize = it.originalImageSize
            this.imageBounds.set(it.imageBounds)
            invalidatePaths()
        }
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
//        ioScope.cancel()
        measureBounds()

        if (imageBounds.isEmpty || imageWidth <= 0 || imageHeight <= 0) return

        pathsMap.clear()

//        ioScope.launch {
        drawings.forEach {
            Log.e(TAG, "invalidatePaths: ${it.path}")
            calculatePath(it)
        }
        pathsMap = pathsMap.toSortedMap { o1, o2 ->
            o1.layer.getCreatingDate().compareTo(o2.layer.getCreatingDate())
        }

        postInvalidate()
//        }
    }

    private fun measureBounds() {
        if (!imageBounds.isEmpty) {
            imageWidth = computeLength(imageBounds.left, imageBounds.right)
            imageHeight = computeLength(imageBounds.top, imageBounds.bottom)

            canvasBitmap = Bitmap.createBitmap(
                imageWidth.toInt(),
                imageHeight.toInt(),
                Bitmap.Config.ARGB_8888
            ).also {
                drawCanvas = Canvas(it)
            }
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun calculatePath(drawing: WhiteboardPayload.Drawing) {
        val path = when {
            drawing.isRect() -> {
//                calculateRectPath(drawing)
                calculateRectanglePath(drawing)
            }
            drawing.isImage() -> {
                calculateBitmap(drawing)
            }
            drawing.isLine() -> {
                calculateLinePath(drawing)
            }
            drawing.isPencilPath() -> {
                calculatePencilPath(drawing)
            }
            drawing.isClosedPath() -> {
                calculateClosedPath(drawing)
            }
            else -> null
        }

        pathsMap[drawing] = path
    }

    private fun calculateBitmap(drawing: WhiteboardPayload.Drawing): WhiteboardImagePath? {
        val cookie: String = this.config?.cookie ?: return null
        val fileUrl: String = this.config?.fileUrl ?: return null
        val drawingPath = drawing.path ?: return null
        val originalImageWidth = originalImageSize?.width ?: return null
        val originalImageHeight = originalImageSize?.height ?: return null

        context.getBitmap(drawingPath.source, cookie, fileUrl)?.let {
            val matrix = Matrix()

            val matrixArray = drawingPath.matrix ?: defaultMatrixArray
            if (matrixArray.size == 6) {
                val scaleX = matrixArray[0]
                val scaleY = matrixArray[3]
                val scalePointX = matrixArray[4].toX()
                val scalePointY = matrixArray[5].toY()

                Log.e(TAG, "image: (${originalImageWidth}, ${originalImageHeight})")
                Log.e(TAG, "bitmap: (${it.width}, ${it.height})")


                val relativeScaleX = if (it.width > originalImageWidth) {
                    scaleX * it.width / originalImageWidth
//                    scaleX
                } else {
                    scaleX
                }
                val relativeScaleY = if (it.height > originalImageHeight) {
                    scaleY * it.height / originalImageHeight
//                    scaleY
                } else {
                    scaleY
                }

                Log.e(TAG, "relativeScaleX: $relativeScaleX")
                Log.e(TAG, "relativeScaleY: $relativeScaleY")

                val translateX = scalePointX + imageBounds.left - (relativeScaleX * it.width / 2)
                val translateY = scalePointY + imageBounds.top - (relativeScaleY * it.height / 2)

                matrix.postScale(relativeScaleX, relativeScaleY)
                matrix.postTranslate(translateX, translateY)
            }

            return WhiteboardImagePath(it, matrix)
        }

        return null
    }

    private fun calculatePencilPath(drawing: WhiteboardPayload.Drawing): Path? {
        val drawingPath = drawing.getDrawingPath() ?: return null
        val segments =
            drawingPath.segments.validatePencilSegments<List<List<List<Float>>>>() ?: return null
        val matrix = (drawing.path?.matrix ?: defaultMatrixArray).copyOf()

        matrix[4] = matrix[4].toX() + imageBounds.left
        matrix[5] = matrix[5].toY() + imageBounds.top

        val path = Path()

        segments.forEachIndexed { index, segment ->
            val segmentX1 = segment[0][0].toX()
            val segmentY1 = segment[0][1].toY()
            val segmentX2 = segment[2][0].toX()
            val segmentY2 = segment[2][1].toY()

            val point1 = PointF(
                segmentX1.transformX(segmentY1, matrix),
                segmentY1.transformY(segmentX1, matrix)
            )
            val point2 = PointF(
                segmentX2.transformX(segmentY2, matrix),
                segmentY2.transformY(segmentX2, matrix)
            )
//            val point3 = PointF(point1.x, point1.y)
//            val point4 = PointF(point2.x, point2.y)

//            val point1 = PointF(segmentX1, segmentY1)
//            val point2 = PointF(segmentX2, segmentY2)
            var point3 = point1
            var point4 = point2

            if (index + 1 < segments.size) {
                val segmentX3 = segments[index + 1][0][0].toX()
                val segmentY3 = segments[index + 1][0][1].toY()
                val segmentX4 = segments[index + 1][1][0].toX()
                val segmentY4 = segments[index + 1][1][1].toY()
                point3.set(
                    segmentX3.transformX(segmentY3, matrix),
                    segmentY3.transformY(segmentX3, matrix)
                )
                point4.set(
                    segmentX4.transformX(segmentY4, matrix),
                    segmentY4.transformY(segmentX4, matrix)
                )
//
//                point3 = PointF(segmentX3, segmentY3)
//                point4 = PointF(segmentX4, segmentY4)
            }

            if (index == 0) {
                path.moveTo(
                    point1.x,
                    point1.y
                )
            } else {
                path.cubicTo(
                    point1.x + point2.x,
                    point1.y + point2.y,
                    point3.x + point4.x,
                    point3.y + point4.y,
                    point3.x,
                    point3.y,
                )

            }
        }


        return path
    }

    private fun calculateLinePath(drawing: WhiteboardPayload.Drawing): Path? {
        val drawingPath = drawing.getDrawingPath() ?: return null
        val segments = drawingPath.segments.validateSegments<List<List<Float>>>() ?: return null
        val matrix = (drawing.path?.matrix ?: defaultMatrixArray).copyOf()
        val path = Path()

        matrix[4] = matrix[4].toX() + imageBounds.left
        matrix[5] = matrix[5].toY() + imageBounds.top

        segments.forEachIndexed { index, segment ->
            val segmentX = segment[0].toX()
            val segmentY = segment[1].toY()
            val x = segmentX.transformX(segmentY, matrix)
            val y = segmentY.transformY(segmentX, matrix)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        return path
    }

    private fun calculateClosedPath(drawing: WhiteboardPayload.Drawing): Path? {
        val drawingPath = drawing.getDrawingPath() ?: return null
        val segments = drawingPath.segments.validateSegments<List<List<Float>>>() ?: return null

        val path = Path()

        val matrix = (drawing.path?.matrix ?: defaultMatrixArray).copyOf()

        matrix[4] = matrix[4].toX() + imageBounds.left
        matrix[5] = matrix[5].toY() + imageBounds.top

        segments.forEachIndexed { index, segment ->
            val segmentX = segment[0].toX()
            val segmentY = segment[1].toY()
            val x = segmentX.transformX(segmentY, matrix)
            val y = segmentY.transformY(segmentX, matrix)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()

        return path
    }

    private fun Float.transformX(y: Float, matrix: FloatArray): Float {
        return this.scaleX(matrix) + skewX(y, matrix) + translateX(matrix)
    }

    private fun Float.transformY(x: Float, matrix: FloatArray): Float {
        return this.scaleY(matrix) + skewY(x, matrix) + translateY(matrix)
    }

    private fun Float.scaleX(matrix: FloatArray): Float {
        return matrix[0] * this
    }

    private fun Float.scaleY(matrix: FloatArray): Float {
        return matrix[3] * this
    }

    private fun skewX(y: Float, matrix: FloatArray): Float {
        return matrix[2] * y
    }

    private fun skewY(x: Float, matrix: FloatArray): Float {
        return matrix[1] * x
    }

    private fun translateX(matrix: FloatArray): Float {
        return matrix[4]
    }

    private fun translateY(matrix: FloatArray): Float {
        return matrix[5]
    }

    private fun calculateRectPath(drawing: WhiteboardPayload.Drawing): Path? {
        val drawingPath = drawing.getDrawingPath()
        val parentPath = drawing.path
        val rectSize = drawingPath?.size

        if (parentPath == null || drawingPath == null || rectSize == null || rectSize.size != 2) return null

        val matrix = drawing.path?.matrix ?: defaultMatrixArray
        val childrenMatrix = drawing.path?.childrenPath?.matrix
        if (childrenMatrix != null) {
            matrix[0] = if (childrenMatrix[0] != 1f) childrenMatrix[0] else matrix[0]
            matrix[1] = childrenMatrix[1]
            matrix[2] = childrenMatrix[2]
            matrix[3] = if (childrenMatrix[3] != 1f) childrenMatrix[3] else matrix[3]
            matrix[4] = childrenMatrix[4] + matrix[4]
            matrix[5] = childrenMatrix[5] + matrix[5]
        }

        val path = Path()

        val fromSegmentX = 0f
        val fromSegmentY = 0f
        val toSegmentX = rectSize[0].toX()
        val toSegmentY = rectSize[1].toY()


        val fromX = fromSegmentX.transformX(fromSegmentY, matrix)
        val fromY = fromSegmentY.transformY(fromSegmentX, matrix)
        val toX = toSegmentX.transformX(toSegmentY, matrix)
        val toY = toSegmentY.transformY(toSegmentX, matrix)


        val radiusX = drawingPath.radius?.get(0)?.toX() ?: 0f
        val radiusY = drawingPath.radius?.get(1)?.toY() ?: 0f

        if (radiusX > 0f && radiusY > 0f) {
            path.addRoundRect(fromX, fromY, toX, toY, radiusX, radiusY, Path.Direction.CW)
        } else {
            path.addRect(fromX, fromY, toX, toY, Path.Direction.CW)
        }

//        path.applyRectMatrix(drawing)
        return path
    }

    private fun calculateRectanglePath(drawing: WhiteboardPayload.Drawing): Path? {
        val parentPath = drawing.path
        val childrenPath = parentPath?.childrenPath
        val rectSize = parentPath?.size ?: childrenPath?.size

        if (parentPath == null || rectSize == null || rectSize.size != 2) return null

        val parentMatrix = (parentPath.matrix ?: defaultMatrixArray).copyOf()
        val childrenMatrix = childrenPath?.matrix?.copyOf()

        parentMatrix[4] = parentMatrix[4].toX() + imageBounds.left
        parentMatrix[5] = parentMatrix[5].toY() + imageBounds.top

        val path = Path()

        val segmentWidth = rectSize[0].toX()
        val segmentHeight = rectSize[1].toY()

        val segmentLeft = -segmentWidth / 2
        val segmentTop = -segmentHeight / 2
        val segmentRight = segmentLeft + segmentWidth
        val segmentBottom = segmentTop + segmentHeight

        var cx1 = segmentLeft
        var cy1 = segmentTop
        var cx2 = segmentRight
        var cy2 = segmentTop
        var cx3 = segmentRight
        var cy3 = segmentBottom
        var cx4 = segmentLeft
        var cy4 = segmentBottom

        if (childrenMatrix != null) {
            childrenMatrix[4] = childrenMatrix[4].toX()
            childrenMatrix[5] = childrenMatrix[5].toY()

            cx1 = segmentLeft.transformX(segmentTop, childrenMatrix)
            cy1 = segmentTop.transformY(segmentLeft, childrenMatrix)
            cx2 = segmentRight.transformX(segmentTop, childrenMatrix)
            cy2 = segmentTop.transformY(segmentRight, childrenMatrix)
            cx3 = segmentRight.transformX(segmentBottom, childrenMatrix)
            cy3 = segmentBottom.transformY(segmentRight, childrenMatrix)
            cx4 = segmentLeft.transformX(segmentBottom, childrenMatrix)
            cy4 = segmentBottom.transformY(segmentLeft, childrenMatrix)
        }

        val x1 = cx1.transformX(cy1, parentMatrix)
        val y1 = cy1.transformY(cx1, parentMatrix)
        val x2 = cx2.transformX(cy2, parentMatrix)
        val y2 = cy2.transformY(cx2, parentMatrix)
        val x3 = cx3.transformX(cy3, parentMatrix)
        val y3 = cy3.transformY(cx3, parentMatrix)
        val x4 = cx4.transformX(cy4, parentMatrix)
        val y4 = cy4.transformY(cx4, parentMatrix)

        path.moveTo(x1, y1)
        path.lineTo(x2, y2)
        path.lineTo(x3, y3)
        path.lineTo(x4, y4)

        path.close()

        return path
    }

    private fun Path.applyDefaultPathMatrix(drawing: WhiteboardPayload.Drawing) {
        pathMatrix.reset()

        val drawingPath = drawing.path ?: return

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
            canvas?.clipRect(
                imageBounds.left,
                imageBounds.top,
                imageBounds.right,
                imageBounds.bottom
            )

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
                        when (it) {
                            is Path -> {
                                canvas?.drawPath(it, paint)
                            }
                            is WhiteboardImagePath -> {
                                canvas?.drawBitmap(it.bitmap, it.matrix, paint)
                            }
                            else -> {
                            }
                        }
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

    private fun WhiteboardPayload.Drawing?.isRect(): Boolean {
        return this != null && (this.path?.type == KmeWhiteboardShapeType.RECTANGLE
                || this.path?.childrenPath?.type == KmeWhiteboardShapeType.RECTANGLE)
    }

    private fun WhiteboardPayload.Drawing?.isImage(): Boolean {
        return this != null && this.path != null
                && !this.path?.pathType.isNullOrEmpty() && !this.path?.source.isNullOrEmpty()
    }

    private fun WhiteboardPayload.Drawing?.isLine(): Boolean {
        return this != null && this.tool == KmeWhiteboardToolType.LINE_TOOL && this.path != null
                && (!this.path?.segments.isNullOrEmpty() || !this.path?.childrenPath?.segments.isNullOrEmpty())
    }

    private fun WhiteboardPayload.Drawing?.isClosedPath(): Boolean {
        if (this == null || (this.path?.segments.isNullOrEmpty() && this.path?.childrenPath?.segments.isNullOrEmpty())) return false
        return this.path?.closed == true || this.path?.childrenPath?.closed == true
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

    class Config(
        val originalImageSize: Size,
        val imageBounds: RectF
    ) {
        var cookie: String? = null
        var fileUrl: String? = null
    }

}