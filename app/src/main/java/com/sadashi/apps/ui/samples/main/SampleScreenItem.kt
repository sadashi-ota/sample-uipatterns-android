package com.sadashi.apps.ui.samples.main

import android.app.Activity
import kotlin.reflect.KClass

data class SampleScreenItem(
    val transitionClass: KClass<out Activity>,
    val name: String
)