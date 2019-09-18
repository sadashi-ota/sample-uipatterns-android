package com.sadashi.apps.ui.samples.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.view.View
import com.sadashi.apps.ui.samples.R
import kotlinx.android.synthetic.main.activity_bottom_sheet.*
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.RecyclerView
import android.util.Log

class BottomSheetActivity : AppCompatActivity() {

    private val adapter = SimpleAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_sheet)

        list.adapter = adapter

        val behavior = BottomSheetBehavior.from(bottom_sheet)
        behavior.setBottomSheetCallback(callback)
        behavior.isFitToContents = false
    }

    private val callback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(p0: View, p1: Float) {
        }

        override fun onStateChanged(p0: View, p1: Int) {
            Log.d("TEST","state : $p1")
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.collection = listOf(
                SimpleItem("Test 1"),
                SimpleItem("Test 2"),
                SimpleItem("Test 3"),
                SimpleItem("Test 4"),
                SimpleItem("Test 5"),
                SimpleItem("Test 6"),
                SimpleItem("Test 7"),
                SimpleItem("Test 8"),
                SimpleItem("Test 9"),
                SimpleItem("Test 10"),
                SimpleItem("Test 1"),
                SimpleItem("Test 2"),
                SimpleItem("Test 3"),
                SimpleItem("Test 4"),
                SimpleItem("Test 5"),
                SimpleItem("Test 6"),
                SimpleItem("Test 7"),
                SimpleItem("Test 8"),
                SimpleItem("Test 9"),
                SimpleItem("Test 10")
        )
    }
}
