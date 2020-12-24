package com.kme.kaltura.kmesdk.content.whiteboard

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import androidx.appcompat.widget.AppCompatImageView
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage.WhiteboardPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardShapeType
import com.kme.kaltura.kmesdk.ws.message.whiteboard.KmeWhiteboardPath
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

private const val WEB_MARGIN = 10f

class KmeWhiteboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), IKmeWhiteboardLayout {

    private val TAG = javaClass.simpleName

    private val paint: Paint = Paint()
    private val pathMatrix: Matrix = Matrix()
    private val imageBounds: RectF = RectF()

    private val paths: LinkedList<Path> = LinkedList<Path>()

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
        Log.e(TAG, "init: ")
        this.originalImageSize = originalImageSize
        invalidatePaths()
    }

    override fun applyDrawings(drawings: List<WhiteboardPayload.Drawing>) {
        Log.e(TAG, "applyDrawings: ")
        this.drawings = drawings
        invalidatePaths()
    }

    private fun invalidatePaths() {
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
            Log.e(TAG, "drawable.bounds: $imageBounds")
        }
    }

    private fun calculatePath(drawing: WhiteboardPayload.Drawing) {
        val path = Path()

        when (drawing.path?.type) {
            KmeWhiteboardShapeType.RECTANGLE -> {
                path.calculateRect(drawing)
            }
        }

        paths.add(path)
    }

    private fun Path.calculateRect(drawing: WhiteboardPayload.Drawing) {
        val drawingPath = drawing.path
        val rectSize = drawingPath?.size

        if (drawingPath == null || rectSize == null || rectSize.size != 2) return

        val fromX = imageBounds.left
        val fromY = imageBounds.top - WEB_MARGIN.toY()
        val toX = rectSize[0].toX()
        val toY = rectSize[1].toY()

        addRect(fromX, fromY, toX, toY, Path.Direction.CW)

        Log.e(TAG, "==== calculateRect: rect($fromX, $fromY, $toX, $toY)")

        applyRectMatrix(rectSize, drawingPath)
    }

    private fun Path.applyRectMatrix(size: FloatArray, drawingPath: KmeWhiteboardPath) {
        pathMatrix.reset()

        val matrix = drawingPath.matrix
        if (matrix == null || matrix.isEmpty() || matrix.size != 6) return

        val scaleX = matrix[0]
        val scaleY = matrix[3]
        val scalePointX = matrix[4].toX()
        val scalePointY = (matrix[5]).toY() + WEB_MARGIN.toY()
        val translateX = scalePointX - (size[0] / 2).toX()
        val translateY = scalePointY - (size[1] / 2).toY()

        pathMatrix.setTranslate(translateX, translateY)
        pathMatrix.postScale(
            scaleX,
            scaleY,
            scalePointX,
            scalePointY
        )

        transform(pathMatrix)

        Log.e(
            TAG, "applyRectMatrix: scale($scaleX, $scaleY), " +
                    "scalePoint($scalePointX, $scalePointY), " +
                    "translate($translateX, $translateY)"
        )
    }

    private fun Float.toX(): Float {
        return this * sqrt((imageBounds.right - imageBounds.left).pow(2)) / (originalImageSize?.width
            ?: defaultSize.width)
    }

    private fun Float.toY(): Float {
        return this * sqrt((imageBounds.bottom - imageBounds.top).pow(2)) / (originalImageSize?.height
            ?: defaultSize.height)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        Log.e(TAG, "dispatchDraw: paths.size = ${paths.size} ")

        for (path in paths) {
            canvas?.drawPath(path, paint)
        }
    }

    class Config
}