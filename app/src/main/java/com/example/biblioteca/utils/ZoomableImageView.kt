// ZoomableImageView.kt
package com.example.biblioteca.utils

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min
import kotlin.math.sqrt

class ZoomableImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {

    private var matrixScale = Matrix()
    private var savedMatrix = Matrix()

    private enum class Mode {
        NONE, DRAG, ZOOM
    }

    private var mode = Mode.NONE
    private val start = PointF()
    private val mid = PointF()
    private var oldDist = 1f
    private val scaleDetector: ScaleGestureDetector
    private val gestureDetector: GestureDetector

    private var scaleFactor = 1f
        private set // Hacer que la modificación directa sea privada
    private val maxScale = 5f
    private val minScale = 1f
    internal var saveScale = 1f // Para acceder desde el fragmento
        private set

    // Listener para notificar al fragmento sobre el estado del zoom
    var onZoomChangedListener: ((isZoomed: Boolean) -> Unit)? = null

    init {
        scaleType = ScaleType.MATRIX
        matrixScale.setTranslate(1f, 1f)
        imageMatrix = matrixScale

        scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                mode = Mode.ZOOM
                return true
            }

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scale = detector.scaleFactor
                val newScale = saveScale * scale

                if (newScale in minScale..maxScale) {
                    saveScale = newScale
                    scaleFactor = newScale // Actualizar el factor de escala actual
                    matrixScale.postScale(scale, scale, detector.focusX, detector.focusY)
                    imageMatrix = matrixScale
                    onZoomChangedListener?.invoke(scaleFactor > minScale + 0.01f) // Notificar cambio de zoom
                }
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                mode = Mode.NONE
            }
        })

        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                resetZoom()
                return true
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event) // Detectar doble toque

        val pointCount = event.pointerCount

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrixScale)
                start.set(event.x, event.y)
                mode = Mode.DRAG
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 10f) {
                    savedMatrix.set(matrixScale)
                    midPoint(mid, event)
                    mode = Mode.ZOOM
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                mode = Mode.NONE
            }

            MotionEvent.ACTION_MOVE -> {
                // Mover la imagen solo si está ampliada (scaleFactor > minScale)
                if (mode == Mode.DRAG && pointCount == 1 && scaleFactor > minScale + 0.01f) {
                    val dx = event.x - start.x
                    val dy = event.y - start.y
                    matrixScale.set(savedMatrix)
                    matrixScale.postTranslate(dx, dy)
                    imageMatrix = matrixScale
                }
            }
        }

        return true
    }

    private fun spacing(event: MotionEvent): Float {
        if (event.pointerCount < 2) return 0f
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        if (event.pointerCount < 2) return
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }

    fun fitToCenter() {
        matrixScale.reset()
        matrixScale.postTranslate(1f, 1f) // Reiniciar traslación por seguridad
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val drawableWidth = drawable?.intrinsicWidth?.toFloat() ?: 0f
        val drawableHeight = drawable?.intrinsicHeight?.toFloat() ?: 0f

        if (drawableWidth > 0 && drawableHeight > 0 && viewWidth > 0 && viewHeight > 0) {
            val scaleX = viewWidth / drawableWidth
            val scaleY = viewHeight / drawableHeight
            val scale = minOf(scaleX, scaleY)

            matrixScale.setScale(scale, scale)
            val dx = (viewWidth - drawableWidth * scale) / 2f
            val dy = (viewHeight - drawableHeight * scale) / 2f
            matrixScale.postTranslate(dx, dy)
            imageMatrix = matrixScale
            saveScale = scale
            scaleFactor = scale
            onZoomChangedListener?.invoke(false) // Notificar que ya no está zoomed
        } else {
            saveScale = 1f
            scaleFactor = 1f
            imageMatrix = matrixScale
            onZoomChangedListener?.invoke(false)
        }
    }

    fun resetZoom() {
        fitToCenter()
    }
}