package com.anwesh.uiprojects.bisiderectpullview

/**
 * Created by anweshmishra on 31/12/19.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.app.Activity
import android.content.Context

val nodes : Int = 5
val sides : Int = 2
val scGap : Float = 0.02f / sides
val strokeFactor : Int = 90
val hFactor : Float = 5f
val foreColor1 : Int = Color.parseColor("#3F51B5")
val foreColor2 : Int = Color.parseColor("#4CAF50")
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 30
