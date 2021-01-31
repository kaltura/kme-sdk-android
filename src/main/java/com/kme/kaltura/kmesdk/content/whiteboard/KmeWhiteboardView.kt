package com.kme.kaltura.kmesdk.content.whiteboard

import android.content.Context
import android.graphics.*
import android.graphics.PathMeasure.POSITION_MATRIX_FLAG
import android.os.Build
import android.text.DynamicLayout
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.View
import androidx.core.graphics.toRect
import com.kme.kaltura.kmesdk.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage.WhiteboardPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardBackgroundType
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardBackgroundType.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardShapeType
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardToolType
import com.kme.kaltura.kmesdk.ws.message.whiteboard.KmeWhiteboardPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import java.util.*
import kotlin.math.*


class KmeWhiteboardView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), IKmeWhiteboardListener {

    private val TAG = javaClass.simpleName

    private val paint: Paint = Paint()
    private var backgroundPaint: Paint? = null
    private val testPaint: Paint = Paint()
    private val canvasPaint: Paint = Paint(Paint.DITHER_FLAG)
    private var drawCanvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null
    private val imageBounds: RectF = RectF()

    private val defaultMatrixArray: FloatArray by lazy { floatArrayOf(1f, 0f, 0f, 1f, 0f, 0f) }

    private val measureScope = CoroutineScope(Dispatchers.IO)
    private var measureJob: Job? = null

    private val eraseXfermode by lazy { PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    private var imageWidth: Float = 0f
    private var imageHeight: Float = 0f

    private var pathsMap: SortedMap<WhiteboardPayload.Drawing, Any?> = Collections.synchronizedSortedMap(
            sortedMapOf({ o1, o2 ->
                o1.layer.getCreatingDate().compareTo(o2.layer.getCreatingDate())
            })
    )

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

        testPaint.apply {
            strokeWidth = ptToDp(2f, context)
            style = Paint.Style.STROKE
            isAntiAlias = true
            isDither = true
            color = Color.BLACK
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    }

    override fun init(whiteboardConfig: Config?) {
        this.config = whiteboardConfig
        whiteboardConfig?.let {
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

    override fun updateBackground(backgroundType: KmeWhiteboardBackgroundType?) {
        this.config?.backgroundType = backgroundType
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
        measureJob?.cancel()
        measureBounds()

        if (imageBounds.isEmpty || imageWidth <= 0 || imageHeight <= 0) return

        measureJob = measureScope.launch {
            val unmodifiedDrawings: List<WhiteboardPayload.Drawing> = drawings.toImmutableList()
            invalidateBackground()

            pathsMap.clear()

            unmodifiedDrawings.forEach {
                Log.e(TAG, "invalidatePaths: ${it.path}")
                measurePath(it)
            }

            postInvalidate()
        }
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

    private fun invalidateBackground() {
        val backgroundType = config?.backgroundType

        val bitmap = when (backgroundType) {
            DOTS -> context.getBitmap(R.drawable.ic_dot, null, 10f)
            AXIS -> context.getBitmap(R.drawable.bg_axis, imageBounds.toRect())
            GRID -> null
            LARGE_GRID -> context.getBitmap(R.drawable.ic_large_grid)
            BLANK -> null
            else -> null

        }

        backgroundPaint = if (bitmap != null && backgroundType != null) {
            createBackgroundPaint(bitmap, backgroundType)
        } else {
            null
        }
    }

    private fun createBackgroundPaint(
            bitmap: Bitmap,
            backgroundType: KmeWhiteboardBackgroundType
    ): Paint {
        val tileMode = when (backgroundType) {
            AXIS -> Shader.TileMode.CLAMP
            else -> Shader.TileMode.REPEAT
        }

        return Paint().apply {
            isAntiAlias = true
            isDither = true
            shader = BitmapShader(bitmap, tileMode, tileMode)
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun measurePath(drawing: WhiteboardPayload.Drawing) {
        val path = when {
            drawing.isRect() -> {
                measureRectanglePath(drawing)
            }
            drawing.isText() -> {
                measureTextPath(drawing)
            }
            drawing.isImage() -> {
                measureImage(drawing)
            }
            drawing.isLine() -> {
                measureLinePath(drawing)
            }
            drawing.isPencilPath() -> {
                measurePencilPath(drawing)
            }
            drawing.isClosedPath() -> {
                measureClosedPath(drawing)
            }
            else -> null
        }

        pathsMap[drawing] = path
    }

    private fun measureImage(drawing: WhiteboardPayload.Drawing): WhiteboardImagePath? {
        val cookie: String = this.config?.cookie ?: return null
        val fileUrl: String = this.config?.fileUrl ?: return null
        val drawingPath = drawing.path ?: return null
        val originalImageWidth = originalImageSize?.width ?: return null
        val originalImageHeight = originalImageSize?.height ?: return null

        context.getBitmap(drawingPath.source, cookie, fileUrl)?.let {

            val parentMatrix = (drawingPath.matrix ?: defaultMatrixArray).copyOf()

            //MSCALE_X = 0
            //MSKEW_X  = 1
            //MTRANS_X = 2
            //MSKEW_Y  = 3
            //MSCALE_Y = 4
            //MTRANS_Y = 5
            //MPERSP_0 = 6
            //MPERSP_1 = 7
            //MPERSP_2 = 8

            val scaleX = parentMatrix[0]
            val scaleY = parentMatrix[3]
            val skewX = parentMatrix[2]
            val skewY = parentMatrix[1]
            val translateX = parentMatrix[4].toX() //+ imageBounds.left
            val translateY = parentMatrix[5].toY() //+ imageBounds.top

            val segmentWidth = it.width.toFloat() * scaleX
            val segmentHeight = it.height.toFloat() * scaleY

            val matrixValues1 = floatArrayOf(
                    scaleX, skewX, 0f,
                    skewY, scaleY, 0f,
                    0f, 0f, 1f
            )
            val matrix1 = Matrix()
            matrix1.setValues(matrixValues1)


            val matrixValues2 = floatArrayOf(
                    1f, 0f, translateX - segmentWidth / 2,
                    0f, 1f, translateY - segmentHeight / 2,
                    0f, 0f, 1f
            )
            val matrix2 = Matrix()
            matrix2.setValues(matrixValues2)

            matrix1.postConcat(matrix2)


//            matrix.setValues(
//                floatArrayOf(
//                    scaleX, skewX, translateX - (segmentWidth / 2),
//                    skewY, scaleY, translateY - (segmentHeight / 2),
//                    0f, 0f, 1f
//                )
//            )

            return WhiteboardImagePath(it, matrix1)
        }

        return null
    }

    private fun measurePencilPath(drawing: WhiteboardPayload.Drawing): Path? {
        val drawingPath = drawing.getDrawingPath() ?: return null
        val segments =
                drawingPath.segments.validatePencilSegments<List<List<List<Float>>>>()
                        ?: return null
        val matrix = (drawing.path?.matrix ?: defaultMatrixArray).copyOf()

        matrix[4] = matrix[4].toX()
        matrix[5] = matrix[5].toY()

        val path = Path()

        segments.forEachIndexed { index, segment ->
            val segmentX1 = segment[0][0].toX()
            val segmentY1 = segment[0][1].toY()

            val segmentX2 = segmentX1 + segment[2][0].toX()
            val segmentY2 = segmentY1 + segment[2][1].toY()

            val point1 = PointF(
                    segmentX1.transformX(segmentY1, matrix),
                    segmentY1.transformY(segmentX1, matrix)
            )
            val point2 = PointF(
                    segmentX2.transformX(segmentY2, matrix),
                    segmentY2.transformY(segmentX2, matrix)
            )

            var point3 = point1
            var point4 = point2

            if (index + 1 < segments.size) {
                val segmentX3 = segments[index + 1][0][0].toX()
                val segmentY3 = segments[index + 1][0][1].toY()

                val segmentX4 = segmentX3 + segments[index + 1][1][0].toX()
                val segmentY4 = segmentY3 + segments[index + 1][1][1].toY()

                point3 = PointF(
                        segmentX3.transformX(segmentY3, matrix),
                        segmentY3.transformY(segmentX3, matrix)
                )
                point4 = PointF(
                        segmentX4.transformX(segmentY4, matrix),
                        segmentY4.transformY(segmentX4, matrix)
                )
            }

            if (index == 0) {
                path.moveTo(
                        point1.x + imageBounds.left,
                        point1.y + imageBounds.top
                )
            } else {
                path.cubicTo(
                        point2.x + imageBounds.left,
                        point2.y + imageBounds.top,
                        point4.x + imageBounds.left,
                        point4.y + imageBounds.top,
                        point3.x + imageBounds.left,
                        point3.y + imageBounds.top,
                )
            }
        }

        return path
    }

    private fun measureLinePath(drawing: WhiteboardPayload.Drawing): Path? {
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

    private fun measureClosedPath(drawing: WhiteboardPayload.Drawing): Path? {
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

    private fun measureRectanglePath(drawing: WhiteboardPayload.Drawing): Path? {
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

    private fun measureTextPath(drawing: WhiteboardPayload.Drawing): WhiteboardTextPath? {
        val parentPath = drawing.path ?: return null
        val childrenPath = parentPath.childrenPath ?: return null
        val rectangle = childrenPath.rectangle ?: return null

        if (childrenPath.content == null || childrenPath.content.isEmpty()) return null

        val parentMatrix = (parentPath.matrix ?: defaultMatrixArray).copyOf()
        val childrenMatrix = childrenPath.matrix?.copyOf()

        invalidatePaint(drawing.getDrawingPath())
        val bounds = Rect()
        paint.getTextBounds(childrenPath.content, 0, childrenPath.content.length, bounds)

        parentMatrix[4] = parentMatrix[4].toX() + imageBounds.left
        parentMatrix[5] = parentMatrix[5].toY() + imageBounds.top

        val path = Path()

        val segmentWidth = rectangle[2].toX() - rectangle[0].toX()
        val segmentHeight = rectangle[3].toY() - rectangle[1].toY()


        //rectangle (x, y, width, height)

        val segmentLeft = rectangle[0].toX()
        val segmentTop = rectangle[1].toY()
        val segmentRight = segmentLeft + rectangle[2].toX()
        val segmentBottom = segmentTop + rectangle[3].toY()

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
//        path.lineTo(x3, y3)
//        path.lineTo(x4, y4)

        path.close()

        val hOffset = 0f
        val vOffset = bounds.height().toFloat()

        return WhiteboardTextPath(childrenPath.content, path, vOffset, hOffset)
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

            val cornerRadius =
                    path.radius?.get(0)?.toX()?.times(path.radius[1].toY()) ?: 0f
            if (cornerRadius > 0f) {
                paint.pathEffect = CornerPathEffect(cornerRadius)
            } else {
                paint.pathEffect = null
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (!imageBounds.isEmpty && imageWidth > 0 && imageHeight > 0) {

            // Allow drawing only inside image borders
            canvas?.clipRect(
                    imageBounds.left,
                    imageBounds.top,
                    imageBounds.right,
                    imageBounds.bottom
            )

            canvasBitmap?.let {
                canvas?.drawBitmap(it, 0f, 0f, canvasPaint)
            }

            backgroundPaint?.let {
                canvas?.drawRect(imageBounds, it)
            }

            synchronized(pathsMap) {
                val iterator = Collections.synchronizedSortedMap(pathsMap).iterator()
                while (iterator.hasNext()) {
                    val pathItem = iterator.next()
                    val drawing = pathItem.key
                    invalidatePaint(drawing.getDrawingPath())

                    pathItem.value?.let {
                        when (it) {
                            is Path -> {
                                canvas?.drawPath(it, paint)
                            }
                            is WhiteboardImagePath -> {
                                canvas?.drawBitmap(it.bitmap, it.matrix, paint)
                            }
                            is WhiteboardTextPath -> {
                                val  pm = PathMeasure(it.path, false)

                                val layout = DynamicLayout(it.text, it.text, TextPaint(paint), pm.getLength().toInt(), Layout.Alignment.ALIGN_CENTER, 1f, 0f, true)
                                canvas?.save()
                                val matrix = Matrix()
                                pm.getMatrix(0f, matrix, POSITION_MATRIX_FLAG)
                                canvas?.setMatrix(matrix)
                                layout.draw(canvas)
                                canvas?.restore()

//                                canvas?.drawPath(it.path, testPaint)
//                                canvas?.drawTextOnPath(it.text, it.path, it.hOffset, it.vOffset, paint)
//                                canvas?.drawTextPath(drawing)
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

            var a = drawing.path?.matrix?.get(0) ?: 1f
            var b = drawing.path?.matrix?.get(1) ?: 0f
            var c = drawing.path?.matrix?.get(2) ?: 0f
            var d = drawing.path?.matrix?.get(3) ?: 1f
            var e = drawing.path?.matrix?.get(4)?.toX() ?: 0f
            var f = drawing.path?.matrix?.get(5)?.toX() ?: 0f

            var delta = a * d - b * c;

            var scaleX1 = 1f
            var scaleY1 = 1f
            var skewX1 = 0f
            var skewY1 = 0f
            var rotation = 0f

            if (a != 0f || b != 0f) {
                var r = sqrt(a * a + b * b);
                rotation = if (b > 0f) acos(a / r) else -acos(a / r);
                scaleX1 = r
                scaleY1 = delta / r
                skewX1 = atan((a * c + b * d) / (r * r))
            } else if (c != 0f || d != 0f) {
                var s = sqrt(c * c + d * d);
                rotation =
                        (Math.PI / 2 - (if (d > 0f)  acos(-c / s) else -acos(c / s))).toFloat()
                scaleX1 = delta / s
                scaleY1 = s
                skewY1 = atan((a * c + b * d) / (s * s))
            }

            val bounds = Rect()
            paint.getTextBounds(textDrawing.content, 0, textDrawing.content.length, bounds)

            val scaleX = drawing.path?.matrix?.get(0) ?: 1f
            val scaleY = drawing.path?.matrix?.get(3) ?: 1f

            val skewX = drawing.path?.matrix?.get(2) ?: 0f
            val skewY = drawing.path?.matrix?.get(1) ?: 0f

            val width = if (textDrawing.rectangle?.size == 4) {
                textDrawing.rectangle[2].toX()
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

            val x = (e*(cos(rotation) + sin(rotation)) / scaleX1)
            val  y = (f*(cos(rotation) - sin(rotation)) / scaleY1)

            translate(x, y)
            scale(scaleX1, scaleY1)
            skew(skewX1, skewY1)
            rotate(rotation *100)
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

    class Config(
            val originalImageSize: Size,
            val imageBounds: RectF
    ) {
        var cookie: String? = null
        var fileUrl: String? = null
        var backgroundType: KmeWhiteboardBackgroundType? = null
    }

}