package com.sadashi.apps.ui.samples.activities

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout

class CustomBehavior(context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<LinearLayout>(context, attrs) {

    private var defaultDependencyTop = -1

    override fun layoutDependsOn(parent: CoordinatorLayout, layout: LinearLayout, dependency: View): Boolean {
//        return dependency is AppBarLayout
        return true
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, layout: LinearLayout, dependency: View): Boolean {
        Log.d("TEST", "dependency.top:${dependency.top} layout.bottom:${layout.bottom}")
        if (dependency.top > layout.bottom) {
            return true
        }
        if (defaultDependencyTop == -1) {
            defaultDependencyTop = dependency.top
        }
        var value: Float = (defaultDependencyTop - dependency.top).toFloat()
        if (value > layout.height) {
            value = layout.height.toFloat()
        }
        layout.translationY = -value
        return true
    }

}