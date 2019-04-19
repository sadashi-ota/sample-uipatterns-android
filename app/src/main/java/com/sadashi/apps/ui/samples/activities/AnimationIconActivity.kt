package com.sadashi.apps.ui.samples.activities

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sadashi.apps.ui.samples.R
import kotlinx.android.synthetic.main.activity_animation_icon.*

class AnimationIconActivity : AppCompatActivity() {

    private var menuFlag = true
    private var menuDrawable: AnimatedVectorDrawable? = null
    private var arrowDrawable: AnimatedVectorDrawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation_icon)

        menuDrawable = getDrawable(R.drawable.ic_menu_24dp) as AnimatedVectorDrawable
        arrowDrawable = getDrawable(R.drawable.ic_arrow_24dp) as AnimatedVectorDrawable

        toolbar.navigationIcon = menuDrawable
        toolbar.setNavigationOnClickListener {
            if (menuFlag) {
                toolbar.navigationIcon = menuDrawable
                menuDrawable!!.start()
            } else {
                toolbar.navigationIcon = arrowDrawable
                arrowDrawable!!.start()
            }
            menuFlag = !menuFlag
        }
    }
}
