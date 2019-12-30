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

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BSRPNode(var i : Int, val state : State = State()) {

        private var next : BSRPNode? = null
        private var prev : BSRPNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = BSRPNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBSRPNode(i, state.scale, paint)
            prev?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BSRPNode {
            var curr : BSRPNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class BiSideRectPull(var i : Int) {

        private var curr : BSRPNode = BSRPNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : BiSideRectPullView) {

        private val animator : Animator = Animator(view)
        private val bsrp : BiSideRectPull = BiSideRectPull(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            bsrp.draw(canvas, paint)
            animator.animate {
                bsrp.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            bsrp.startUpdating {
                animator.start()
            }
        }
    }
}
