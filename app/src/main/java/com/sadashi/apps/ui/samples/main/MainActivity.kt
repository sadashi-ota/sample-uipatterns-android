package com.sadashi.apps.ui.samples.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sadashi.apps.ui.samples.R
import com.sadashi.apps.ui.samples.activities.AnimationIconActivity
import com.sadashi.apps.ui.samples.activities.ButtonsActivity
import com.sadashi.apps.ui.samples.activities.DrawerLayoutActivity
import com.sadashi.apps.ui.samples.activities.EffectActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val clickItem: (SampleScreenItem) -> Unit = { item ->
        startActivity(Intent(this, item.transitionClass.java))
    }

    private val adapter = SampleScreenListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.adapter = adapter
        adapter.clickListener = clickItem
    }

    override fun onStart() {
        super.onStart()
        adapter.collection = listOf(
                SampleScreenItem(DrawerLayoutActivity::class, "DrawerLayout & NavigationView"),
                SampleScreenItem(ButtonsActivity::class, "Buttons"),
                SampleScreenItem(AnimationIconActivity::class, "Animation Icon"),
                SampleScreenItem(EffectActivity::class, "Effect")
        )
    }
}
