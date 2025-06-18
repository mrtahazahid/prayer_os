package com.iw.android.prayerapp.utils.anim

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.widget.AppCompatImageView

fun hideDetailView(view: View,imageView: AppCompatImageView) {
    imageView.animate().rotation(0f).setDuration(300).start()
    val initialHeight = view.measuredHeight
    val targetHeight = 0

    val animator = ValueAnimator.ofInt(initialHeight, targetHeight)
    animator.addUpdateListener {
        val value = it.animatedValue as Int
        view.layoutParams.height = value
        view.requestLayout()
    }

    animator.duration = 300L
    animator.interpolator = AccelerateDecelerateInterpolator()


    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            view.visibility = View.GONE
        }
    })


    animator.start()
}

fun showDetailView(detailView: View,imageView: AppCompatImageView) {
    imageView.animate().rotation(90f).setDuration(300).start()
    detailView.scaleX = 0.8f
    detailView.scaleY = 0.8f
    detailView.alpha = 0f
    detailView.visibility = View.VISIBLE

    detailView.animate()
        .scaleX(1f)
        .scaleY(1f)
        .alpha(1f)
        .setDuration(300)
        .setInterpolator(OvershootInterpolator()) // spring-like effect
        .start()
}