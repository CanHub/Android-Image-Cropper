package com.canhub.cropper

import android.graphics.Matrix
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.graphics.RectF
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Transformation
import android.widget.ImageView

/**
 * Animation to handle smooth cropping image matrix transformation change, specifically for
 * zoom-in/out.
 */
internal class CropImageAnimation(
    private val imageView: ImageView, private val cropOverlayView: CropOverlayView
) : Animation(), AnimationListener {

    private val startBoundPoints = FloatArray(8)
    private val endBoundPoints = FloatArray(8)
    private val startCropWindowRect = RectF()
    private val endCropWindowRect = RectF()
    private val startImageMatrix = FloatArray(9)
    private val endImageMatrix = FloatArray(9)

    init {
        duration = 300
        fillAfter = true
        interpolator = AccelerateDecelerateInterpolator()
        setAnimationListener(this)
    }

    fun setStartState(boundPoints: FloatArray?, imageMatrix: Matrix) {
        reset()
        System.arraycopy(boundPoints, 0, startBoundPoints, 0, 8)
        startCropWindowRect.set(cropOverlayView.cropWindowRect)
        imageMatrix.getValues(startImageMatrix)
    }

    fun setEndState(boundPoints: FloatArray?, imageMatrix: Matrix) {
        System.arraycopy(boundPoints, 0, endBoundPoints, 0, 8)
        endCropWindowRect.set(cropOverlayView.cropWindowRect)
        imageMatrix.getValues(endImageMatrix)
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {

        val animRect = RectF()
        animRect.left = (
            startCropWindowRect.left
                + (endCropWindowRect.left - startCropWindowRect.left)
                * interpolatedTime
            )
        animRect.top = (
            startCropWindowRect.top
                + (endCropWindowRect.top - startCropWindowRect.top)
                * interpolatedTime
            )
        animRect.right = (
            startCropWindowRect.right
                + (endCropWindowRect.right - startCropWindowRect.right)
                * interpolatedTime
            )
        animRect.bottom = (
            startCropWindowRect.bottom
                + (endCropWindowRect.bottom - startCropWindowRect.bottom)
                * interpolatedTime
            )

        cropOverlayView.cropWindowRect = animRect

        val animPoints = FloatArray(8)
        for (i in animPoints.indices) {
            animPoints[i] = (
                startBoundPoints[i]
                    + (endBoundPoints[i] - startBoundPoints[i])
                    * interpolatedTime
                )
        }
        cropOverlayView.setBounds(animPoints, imageView.width, imageView.height)

        val animMatrix = FloatArray(9)
        for (i in animMatrix.indices) {
            animMatrix[i] = (
                startImageMatrix[i]
                    + (endImageMatrix[i] - startImageMatrix[i])
                    * interpolatedTime
                )
        }
        val matrix = imageView.imageMatrix

        matrix.setValues(animMatrix)
        imageView.imageMatrix = matrix
        imageView.invalidate()
        cropOverlayView.invalidate()
    }

    override fun onAnimationStart(animation: Animation) {}
    override fun onAnimationEnd(animation: Animation) {
        imageView.clearAnimation()
    }

    override fun onAnimationRepeat(animation: Animation) {}
}