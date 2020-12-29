package com.kme.kaltura.kmesdk.content.whiteboard

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Size
import androidx.appcompat.widget.AppCompatImageView
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage.WhiteboardPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardShapeType
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardToolType
import com.kme.kaltura.kmesdk.ws.message.whiteboard.KmeWhiteboardPath
import kotlin.math.pow
import kotlin.math.sqrt

class KmeWhiteboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), IKmeWhiteboardLayout {

    private val TAG = javaClass.simpleName

    private val paint: Paint = Paint()
    private val pathMatrix: Matrix = Matrix()
    private val imageBounds: RectF = RectF()

    private var imageWidth: Float = 0f
    private var imageHeight: Float = 0f

    private val paths = mutableMapOf<String, Path>()

    private var drawings: List<WhiteboardPayload.Drawing>? = null

    private var originalImageSize: Size? = null
    private val defaultSize = Size(1280, 720)

    init {
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

    override fun init(originalImageSize: Size) {
        this.originalImageSize = originalImageSize
        invalidatePaths()
    }

    override fun applyDrawings(drawings: List<WhiteboardPayload.Drawing>) {
        this.drawings = drawings
        invalidatePaths()
    }

    private fun invalidatePaths() {
        paths.clear()

        if (drawings.isNullOrEmpty() || originalImageSize?.width ?: 0 <= 0) return

        measureBounds()

        drawings?.forEach {
            calculatePath(it)
        }

        invalidate()
    }

    private fun measureBounds() {
        if (drawable != null) {
            imageMatrix.mapRect(imageBounds, RectF(drawable.bounds))

            imageWidth = sqrt((imageBounds.right - imageBounds.left).pow(2))
            imageHeight = sqrt((imageBounds.bottom - imageBounds.top).pow(2))
        }
    }

    private fun calculatePath(drawing: WhiteboardPayload.Drawing) {
        val path = when {
            drawing.path?.type == KmeWhiteboardShapeType.RECTANGLE -> {
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

        path?.let {
            paths.put(drawing.layer ?: "", path)
        }
    }

    private fun calculatePencilPath(drawing: WhiteboardPayload.Drawing): Path? {
        val drawingPath = drawing.path ?: return null
        val segments = drawingPath.segments.checkSegments<List<List<List<Float>>>>() ?: return null

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

        path.applyDefaultPathMatrix(drawingPath)
        return path
    }

    private fun calculateLinePath(drawing: WhiteboardPayload.Drawing): Path? {
        val drawingPath = drawing.path ?: return null
        val segments = drawingPath.segments.checkSegments<List<List<Float>>>() ?: return null

        val path = Path()

        segments.forEachIndexed { index, segment ->
            if (index == 0) {
                path.moveTo(segment[0].toX(), segment[1].toY())
            } else {
                path.lineTo(segment[0].toX(), segment[1].toY())
            }
        }

        path.applyDefaultPathMatrix(drawingPath)
        return path
    }

    private fun calculateClosedPath(drawing: WhiteboardPayload.Drawing): Path? {
        val drawingPath = drawing.path ?: return null
        val segments = drawingPath.segments.checkSegments<List<List<Float>>>() ?: return null

        val path = Path()

        segments.forEachIndexed { index, segment ->
            if (index == 0) {
                path.moveTo(segment[0].toX(), segment[1].toY())
            } else {
                path.lineTo(segment[0].toX(), segment[1].toY())
            }
        }

        path.close()
        path.applyDefaultPathMatrix(drawingPath)
        return path
    }

    private fun calculateRectPath(drawing: WhiteboardPayload.Drawing): Path? {
        val drawingPath = drawing.path
        val rectSize = drawingPath?.size

        if (drawingPath == null || rectSize == null || rectSize.size != 2) return null

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

        path.applyRectMatrix(rectSize, drawingPath)
        return path
    }

    private fun Path.applyDefaultPathMatrix(drawingPath: KmeWhiteboardPath) {
        pathMatrix.reset()

        val matrix = drawingPath.matrix
        if (matrix == null || matrix.isEmpty() || matrix.size != 6) return

        val scaleX = matrix[0]
        val scaleY = matrix[3]
        val scalePointX = matrix[4].toX()
        val scalePointY = matrix[5].toY()
        val translateX = imageBounds.left + scalePointX
        val translateY = scalePointY + imageBounds.top

        pathMatrix.setTranslate(translateX, translateY)

        val coefY = if (imageWidth >= imageHeight) 1 else -1
        pathMatrix.postScale(
            scaleX,
            scaleY,
            imageBounds.left + scalePointX,
            scalePointY + coefY * imageBounds.top
        )

        transform(pathMatrix)
    }

    private fun Path.applyRectMatrix(size: FloatArray, drawingPath: KmeWhiteboardPath) {
        pathMatrix.reset()

        val matrix = drawingPath.matrix
        if (matrix == null || matrix.isEmpty() || matrix.size != 6) return

        val scaleX = matrix[0]
        val scaleY = matrix[3]
        val scalePointX = matrix[4].toX()
        val scalePointY = matrix[5].toY()
        val translateX = imageBounds.left + scalePointX - (size[0].toX() / 2)
        val translateY = imageBounds.top + scalePointY - (size[1].toY() / 2)

        val coefY = if (imageWidth >= imageHeight) 1 else -1

        pathMatrix.setTranslate(translateX, translateY)
        pathMatrix.postScale(
            scaleX,
            scaleY,
            imageBounds.left + scalePointX,
            scalePointY + coefY * imageBounds.top
        )

        transform(pathMatrix)
    }

    private fun Float.toX(): Float {
        return this * imageWidth / (originalImageSize?.width
            ?: defaultSize.width)
    }

    private fun Float.toY(): Float {
        return this * imageHeight / (originalImageSize?.height
            ?: defaultSize.height)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        for (path in paths) {
            canvas?.drawPath(path.value, paint)
        }
    }

    private fun WhiteboardPayload.Drawing?.isLine(): Boolean {
        return this != null && this.tool == KmeWhiteboardToolType.LINE_TOOL && this.path != null
                && !this.path.segments.isNullOrEmpty()
    }

    private fun KmeWhiteboardPath?.isClosedPath(): Boolean {
        if (this == null || this.segments.isNullOrEmpty()) return false
        return this.closed == true
    }

    private fun WhiteboardPayload.Drawing?.isPencilPath(): Boolean {
        return this != null && this.tool == KmeWhiteboardToolType.PENCIL && this.path != null
                && !this.path.segments.isNullOrEmpty()
    }

    private inline fun <reified T> List<List<Any>>?.checkSegments(): T? {
        return if (this is T) {
            this
        } else {
            null
        }
    }

}