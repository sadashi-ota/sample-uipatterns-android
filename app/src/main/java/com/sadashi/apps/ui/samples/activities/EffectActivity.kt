package com.sadashi.apps.ui.samples.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Point
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.Toast
import com.sadashi.apps.ui.samples.R
import kotlinx.android.synthetic.main.activity_effect.*

class EffectActivity : AppCompatActivity() {
    companion object {
        private const val REVEAL_ANIMATION_DURATION = 500
    }

    private var currentReveal = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_effect)

        nonEffect.setOnClickListener { showToast(R.string.message_tap_non_effect_text) }
        rippleEffect.setOnClickListener { showToast(R.string.message_tap_ripple_effect_text) }
        startReveal.setOnClickListener { revealAnimation() }
    }

    private fun revealAnimation() {
        val cx = (startReveal.left + startReveal.right) / 2
        val cy = (startReveal.top + startReveal.bottom) / 2

        val display = windowManager.defaultDisplay
        val point = Point()
        display.getSize(point)
        val radius = point.y

        val nextReveal = !currentReveal
        reveal.setBackgroundResource(getColorForReveal(nextReveal))

        reveal.visibility = View.VISIBLE
        val animator = ViewAnimationUtils.createCircularReveal(
                reveal, cx, cy, 0f, radius.toFloat())
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                reveal.visibility = View.INVISIBLE
                base.setBackgroundResource(getColorForReveal(nextReveal))
                currentReveal = nextReveal
            }
        })
        animator.duration = REVEAL_ANIMATION_DURATION.toLong()
        animator.start()
    }

    @ColorRes
    private fun getColorForReveal(revealFlag: Boolean): Int {
        return when (revealFlag) {
            true -> R.color.colorReveal
            false -> R.color.colorNormal
        }
    }

    private fun showToast(@StringRes stringResId: Int) {
        Toast.makeText(this, stringResId, Toast.LENGTH_SHORT).show()
    }
}
