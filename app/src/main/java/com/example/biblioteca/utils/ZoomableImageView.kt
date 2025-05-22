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
import kotlin.math.sqrt

/**
 * aqui no explicare mucho pero hace funcionar el zoom,moverse  y tap
 */


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
        private set // Evitar modificación externa
    private val maxScale = 5f
    private val minScale = 1f
    internal var saveScale = 1f // Accesible desde el fragmento
        private set
    private var isZoomed = false
    // Listener para notificar cambios de zoom al fragmento
    var onZoomChangedListener: ((isZoomed: Boolean) -> Unit)? = null

    init {
        scaleType = ScaleType.MATRIX
        matrixScale.setTranslate(1f, 1f)
        imageMatrix = matrixScale

        // Detector para gestos de pellizco (zoom)
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
                    scaleFactor = newScale
                    matrixScale.postScale(scale, scale, detector.focusX, detector.focusY)
                    imageMatrix = matrixScale
                    onZoomChangedListener?.invoke(scaleFactor > minScale + 0.01f)
                }
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                mode = Mode.NONE
            }
        })

        // Detector para gestos de doble tap
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (!isZoomed) {
                    // Si no está en zoom, hacer un único zoom (ej: 2x)
                    val zoomFactor = 2f
                    matrixScale.set(savedMatrix) // Reiniciar base
                    matrixScale.postScale(zoomFactor, zoomFactor, width / 2f, height / 2f)
                    imageMatrix = matrixScale

                    saveScale = zoomFactor
                    scaleFactor = zoomFactor
                    isZoomed = true // Ya estamos en zoom

                    onZoomChangedListener?.invoke(true)
                } else {
                    // Si ya está en zoom, resetear a normal
                    resetZoom()
                    isZoomed = false // Volvemos a modo normal
                }
                return true
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event) // Detectar doble tap

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

    // Calcula la distancia entre dos dedos (para detectar gesto de zoom)
    private fun spacing(event: MotionEvent): Float {
        if (event.pointerCount < 2) return 0f
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    // Calcula el punto medio entre dos dedos
    private fun midPoint(point: PointF, event: MotionEvent) {
        if (event.pointerCount < 2) return
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }

    // Ajustar la imagen al centro del view
    fun fitToCenter() {
        matrixScale.reset()
        matrixScale.postTranslate(1f, 1f) // Seguridad inicial
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
            onZoomChangedListener?.invoke(false)
        } else {
            saveScale = 1f
            scaleFactor = 1f
            imageMatrix = matrixScale
            onZoomChangedListener?.invoke(false)
        }
    }

    // Método público para resetear el zoom manualmente
    fun resetZoom() {
        fitToCenter()
    }
}
