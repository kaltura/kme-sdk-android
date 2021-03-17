package com.kme.kaltura.kmesdk.content.whiteboard

import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
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
import kotlinx.coroutines.sync.Mutex
import okhttp3.internal.toImmutableList
import java.util.*
import kotlin.math.*

/**
 * An implementation of whiteboard view in the room
 */
class KmeWhiteboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), IKmeWhiteboardListener {

    var changeListener: IKmeWhiteboardChangeListener? = null

    private val paint: Paint = Paint()
    private val canvasPaint: Paint = Paint(Paint.DITHER_FLAG)
    private var backgroundPaint: Paint? = null

    private var drawCanvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null
    private val laserBitmap by lazy { context.getBitmap(R.drawable.ic_cursor) }

    private val imageBounds: RectF = RectF()

    private val defaultMatrixArray: FloatArray by lazy { floatArrayOf(1f, 0f, 0f, 1f, 0f, 0f) }

    private val measureScope = CoroutineScope(Dispatchers.IO)
    private var measureJob: Job? = null

    private val eraseXfermode by lazy { PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    private val drawingDoneSignal = Mutex()

    private var imageWidth: Float = 0f
    private var imageHeight: Float = 0f

    private var isAnnotationEnabled = true

    private var pathsMap: SortedMap<WhiteboardPayload.Drawing, Any?> =
        Collections.synchronizedSortedMap(
            sortedMapOf({ o1, o2 ->
                o1.layer.getCreatingDate().compareTo(o2.layer.getCreatingDate())
            })
        )

    private val drawings: MutableList<WhiteboardPayload.Drawing> by lazy { mutableListOf() }

    private val laserDrawing: WhiteboardPayload.Drawing by lazy {
        WhiteboardPayload.Drawing().apply {
            layer = System.currentTimeMillis().toString()
            path = KmeWhiteboardPath().apply {
                isLaser = true
            }
        }
    }

    private var config: Config? = null
    private var originalImageSize: Size? = null
    private val defaultSize = Size(1280, 720)

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        paint.apply {
            strokeWidth = 6f
            style = Paint.Style.STROKE
            isAntiAlias = true
            isDither = true
            color = Color.BLACK
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND

        }
    }

    /**
     * Initialize function. Setting config.
     */
    override fun init(whiteboardConfig: Config?) {
        this.config = whiteboardConfig
        whiteboardConfig?.let {
            this.originalImageSize = it.originalImageSize
            this.imageBounds.set(it.imageBounds)
            invalidatePaths()
        }
    }

    override fun enableAnnotations(enable: Boolean) {
        isAnnotationEnabled = enable
        if (!isAnnotationEnabled) {
            removeDrawings()
        }
    }

    /**
     * Sets the list of drawings.
     */
    override fun setDrawings(drawings: List<WhiteboardPayload.Drawing>) {
        if (!isAnnotationEnabled) return

        this.drawings.clear()
        this.drawings.addAll(drawings)
        invalidatePaths()
    }

    /**
     * Adds the drawing.
     */
    override fun addDrawing(drawing: WhiteboardPayload.Drawing) {
        if (!isAnnotationEnabled) return

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

    /**
     * Updates the whiteboard background.
     */
    override fun updateBackground(backgroundType: KmeWhiteboardBackgroundType?) {
        this.config?.backgroundType = backgroundType
        invalidatePaths()
    }

    /**
     * Removes the existing drawing from the whiteboard view.
     */
    override fun removeDrawing(layer: String) {
        this.drawings.removeAll { drawing -> drawing.layer == layer }
        invalidatePaths()
    }

    /**
     * Removes all drawings from the whiteboard view.
     */
    override fun removeDrawings() {
        this.drawings.clear()
        invalidatePaths()
    }

    /**
     * Updates the current position of the laser pointer. Show pointer if not already created.
     */
    override fun updateLaserPosition(point: PointF) {
        laserDrawing.path?.laserPosition = point
        addDrawing(laserDrawing)
    }

    /**
     * Hide the laser pointer.
     */
    override fun hideLaser() {
        laserDrawing.layer?.let { removeDrawing(it) }
    }

    /**
     * Function invalidates all data before drawing
     */
    private fun invalidatePaths() {
        measureBounds()

        if (imageBounds.isEmpty || imageWidth <= 0 || imageHeight <= 0) return

        measureJob = measureScope.launch {
            drawingDoneSignal.lock()

            val unmodifiedDrawings: List<WhiteboardPayload.Drawing> = drawings.toImmutableList()
            invalidateBackground()

            pathsMap.clear()

            unmodifiedDrawings.forEach {
                measurePath(it)
            }

            launch(Dispatchers.Main) {
                if (pathsMap.isEmpty()) {
                    changeListener?.onChanged()
                }
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
            GRID -> context.getBitmap(R.drawable.ic_grid)
            LARGE_GRID -> context.getBitmap(R.drawable.ic_large_grid)
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

    /**
     * Creates and measures a path for the next drawing steps.
     * Prepares a drawing object for other types that can be drawn on the Canvas.
     */
    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun measurePath(drawing: WhiteboardPayload.Drawing) {
        val path = when {
            drawing.isRect() -> {
                measureRectanglePath(drawing)
            }
            drawing.isText() -> {
                measureText(drawing)
            }
            drawing.isLaser() -> {
                measureLaser(drawing)
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

    /**
     * Creates [WhiteboardImagePath] instance and apply transformation matrix for this one.
     *
     * @return WhiteboardImagePath The image container contains bitmap with transformation matrix
     * that will be applied before rendering this bitmap.
     */
    private fun measureImage(drawing: WhiteboardPayload.Drawing): WhiteboardImagePath? {
        val cookie: String = this.config?.cookie ?: return null
        val fileUrl: String = this.config?.fileUrl ?: return null
        val drawingPath = drawing.path ?: return null

        context.getBitmap(drawingPath.source, cookie, fileUrl)?.let {

            val parentMatrix = (drawingPath.matrix ?: defaultMatrixArray).copyOf()

            val affineMatrix = floatArrayOf(
                parentMatrix[0],
                parentMatrix[1],
                parentMatrix[2],
                parentMatrix[3],
                parentMatrix[4].toX(),
                parentMatrix[5].toY()
            )

            val transformationMatrix = Matrix()

            val rotation = (180 / PI * atan2(affineMatrix[1], affineMatrix[0])).toFloat()
            val denominator = affineMatrix[0].pow(2) + affineMatrix[1].pow(2)
            var scaleX = sqrt(denominator)
            var scaleY =
                (affineMatrix[0] * affineMatrix[3] - affineMatrix[2] * affineMatrix[1]) / scaleX
            val skewX = atan(
                (affineMatrix[0] * affineMatrix[2] + affineMatrix[1] * affineMatrix[3]) / sqrt(
                    affineMatrix[0].pow(2) + affineMatrix[1].pow(2)
                )
            )
            val skewY = 0f

            originalImageSize?.let { size ->
                scaleX = scaleX * imageBounds.width() / size.width
                scaleY = scaleY * imageBounds.height() / size.height
            }

            transformationMatrix.setValues(
                floatArrayOf(
                    scaleX, skewX, 0f,
                    skewY, scaleY, 0f,
                    0f, 0f, 1f
                )
            )

            transformationMatrix.postRotate(rotation, affineMatrix[4], affineMatrix[5])

            val resizedBitmap = Bitmap.createBitmap(
                it, 0, 0,
                it.width, it.height, transformationMatrix, true
            )

            transformationMatrix.reset()
            transformationMatrix.postTranslate(
                affineMatrix[4] - (resizedBitmap.width / 2) + imageBounds.left,
                affineMatrix[5] - (resizedBitmap.height / 2) + imageBounds.top
            )

            return WhiteboardImagePath(resizedBitmap, transformationMatrix)
        }

        return null
    }

    /**
     * Creates [WhiteboardImagePath] instance and apply transformation matrix for this one.
     *
     * @return WhiteboardImagePath The image container contains the laser pointer bitmap with
     * transformation matrix that will be applied before rendering this bitmap.
     */
    private fun measureLaser(drawing: WhiteboardPayload.Drawing): WhiteboardImagePath? {
        val laserPosition = drawing.path?.laserPosition ?: return null

        laserBitmap?.let {
            val transformationMatrix = Matrix()
            transformationMatrix.postTranslate(
                laserPosition.x.toX() + imageBounds.left - it.width / 2,
                laserPosition.y.toY() + imageBounds.top - it.height / 2
            )

            return WhiteboardImagePath(it, transformationMatrix)
        }

        return null
    }

    /**
     * Creates [WhiteboardImagePath] instance and apply transformation matrix for this one.
     *
     * @return WhiteboardImagePath The image container contains the text bitmap with
     * transformation matrix that will be applied before rendering this bitmap.
     */
    private fun measureText(drawing: WhiteboardPayload.Drawing?): WhiteboardImagePath? {
        val textDrawing = drawing?.path?.childrenPath
        val parentMatrixArray = drawing?.path?.matrix ?: defaultMatrixArray
        val childrenMatrixArray = textDrawing?.matrix ?: defaultMatrixArray

        invalidatePaint(drawing.getDrawingPath())
        if (textDrawing != null && !textDrawing.content.isNullOrEmpty()) {
            val bounds = Rect()
            paint.getTextBounds(textDrawing.content, 0, textDrawing.content.length, bounds)

            val childAffineMatrix = floatArrayOf(
                childrenMatrixArray[0],
                childrenMatrixArray[1],
                childrenMatrixArray[2],
                childrenMatrixArray[3],
                childrenMatrixArray[4].toX(),
                childrenMatrixArray[5].toY()
            )

            val parentAffineMatrix = floatArrayOf(
                parentMatrixArray[0],
                parentMatrixArray[1],
                parentMatrixArray[2],
                parentMatrixArray[3],
                parentMatrixArray[4].toX() + imageBounds.left,
                parentMatrixArray[5].toY() + imageBounds.top
            )

            val childMatrix = Matrix()
            val parentMatrix = Matrix()

            val scaleX = sqrt(parentAffineMatrix[0].pow(2) + parentAffineMatrix[1].pow(2))
            val width = if (textDrawing.rectangle?.size == 4) {
                textDrawing.rectangle[2].toX() * scaleX
            } else {
                0f
            }

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
                    setLineSpacing(0.0f, 0.9f)
                }.build()
            } else {
                StaticLayout(
                    textDrawing.content,
                    textPaint,
                    width.toInt(),
                    Layout.Alignment.ALIGN_NORMAL,
                    0.9f,
                    0.0f,
                    false
                )
            }

            childMatrix.setValues(
                floatArrayOf(
                    childAffineMatrix[0], childAffineMatrix[2], childAffineMatrix[4],
                    childAffineMatrix[1], childAffineMatrix[3], childAffineMatrix[5],
                    0f, 0f, 1f
                )
            )

            parentMatrix.setValues(
                floatArrayOf(
                    parentAffineMatrix[0], parentAffineMatrix[2], parentAffineMatrix[4],
                    parentAffineMatrix[1], parentAffineMatrix[3], parentAffineMatrix[5],
                    0f, 0f, 1f
                )
            )

            childMatrix.postConcat(parentMatrix)

            val bitmap = Bitmap.createBitmap(
                staticLayout.width,
                staticLayout.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            staticLayout.draw(canvas)

            return WhiteboardImagePath(bitmap, childMatrix)
        }

        return null
    }

    /**
     * Creates and measures a pencil path. Measures a pencil path by [android.graphics.Path.cubicTo] function.
     * All required points are obtained from an array of segments from [drawing].
     *
     * @return Path The path representing a pencil path from drawing object.
     */
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

    /**
     * Creates and measures a line path. Measures a line path by [android.graphics.Path.lineTo]
     * functions. All required points are obtained from an array of segments from [drawing].
     *
     * @return Path The path representing a line path from drawing object.
     */
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

    /**
     * Creates and measures a closed path. Measures a closed path by [android.graphics.Path.lineTo]
     * functions with [android.graphics.Path.close]. All required points are obtained from
     * an array of segments from [drawing].
     *
     * @return Path The path representing a closed path from drawing object.
     */
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

    /**
     * Creates and measures a rectangle path. Measures a rectangle path by [android.graphics.Path.lineTo]
     * functions with [android.graphics.Path.close]. All required points are obtained from
     * an array of segments from [drawing].
     *
     * @return Path The path representing a rectangle path from drawing object.
     */
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

    /*
    * Updates the paint before rendering any drawings
    * */
    private fun invalidatePaint(path: KmeWhiteboardPath?) {
        path?.let {
            paint.color = it.getPaintColor()
            paint.strokeWidth = it.strokeWidth.toFloat()
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
                            is Path -> canvas?.drawPath(it, paint)
                            is WhiteboardImagePath -> canvas?.drawBitmap(it.bitmap, it.matrix, null)
                            else -> {
                            }
                        }
                    }
                }
            }
        }

        if (drawingDoneSignal.isLocked) {
            drawingDoneSignal.unlock()
        }
        changeListener?.onChanged()
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

    private fun WhiteboardPayload.Drawing?.isLaser(): Boolean {
        return this != null && this.path?.isLaser ?: false
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