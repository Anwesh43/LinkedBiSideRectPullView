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
val hFactor : Float = 5f
val foreColor1 : Int = Color.parseColor("#3F51B5")
val foreColor2 : Int = Color.parseColor("#4CAF50")
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 30

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n: Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()
fun Float.cosify() : Float = Math.sin(Math.PI / 2 + (Math.PI / 2) * this).toFloat()

fun Canvas.drawSideRectPull(i : Int, scale : Float, size : Float, w : Float, paint : Paint) {
    val sf : Float = scale.sinify().divideScale(i, sides)
    val sc : Float = scale.divideScale(1, 2).cosify().divideScale(i, sides)
    save()
    scale(1f - 2 * i, 1f)
    paint.color = foreColor1
    drawRect(RectF(0f, -size / 2, (w / 2) * sf, size / 2), paint)
    paint.color = foreColor2
    drawRect(RectF(w / 2 - (w / 2) * sc, -size / 2, w / 2, size / 2), paint)
    restore()
}

fun Canvas.drawBiSideRectPull(scale : Float, size : Float, w : Float, paint : Paint) {
    for (j in 0..(sides - 1)) {
        drawSideRectPull(j, scale, size, w, paint)
    }
}

fun Canvas.drawBSRPNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    val size : Float = gap / hFactor
    save()
    translate(w / 2, gap * (i + 1))
    drawBiSideRectPull(scale, size, w, paint)
    restore()
}

class BiSideRectPullView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}
