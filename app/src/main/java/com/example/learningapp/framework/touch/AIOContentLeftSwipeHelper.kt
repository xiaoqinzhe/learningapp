package com.example.learningapp.framework.touch

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Scroller
import kotlin.math.absoluteValue

/**
 * 消息左滑事件的判断
 */
class AIOContentLeftSwipeHelper(
    val container: ViewGroup,
    val contentView: View?
) {

    companion object {
        const val TAG = "AIOContentLeftSwipeHelper"

        const val STATE_NONE = 0
        const val STATE_LEFT_SWIPE = 1
        const val STATE_OTHER = 2
    }

    var toggle: Boolean = true

    private val listeners = mutableListOf<ILeftSwipeListener>()

    private var touchSlop: Int = 0
    private var maxScrollDistance: Int
    private var maxDampingDistance: Int

    private var downX: Float = 0f
    private var downY: Float = 0f
    private var lastX: Float = 0f

    private var isDownOnContent = false
    private var state: Int = STATE_NONE

    private val scroller: Scroller
    private val scrollRunnable: Runnable

    init {
        touchSlop = ViewConfiguration.get(container.context).scaledTouchSlop
        maxScrollDistance = 89
        maxDampingDistance = 59

        scroller = Scroller(container.context) {
            (Math.pow(it - 1.0, 5.0) + 1.0f).toFloat()
        }
        scrollRunnable = object: Runnable {
            override fun run() {
                if (scroller.computeScrollOffset()) {
                    container.scrollTo(scroller.currX, scroller.currY)
                    listeners.forEach { it.onLeftSwipeBack(scroller.currX) }
                    container.postOnAnimation(this)
                } else {
                    Log.d(TAG, "onLeftSwipeBackEnd")
                    listeners.forEach { it.onLeftSwipeBackEnd() }
                }
            }
        }
    }

    fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (contentView == null) {
            return false
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                isDownOnContent = judgeIsDownOnView(ev, contentView)
                if (!isDownOnContent) {
                    return false
                }
                handleDownEvent(ev)
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isDownOnContent) {
                    return false
                }
                judgeMoveState(ev)
                lastX = ev.x
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!isDownOnContent) {
                    return false
                }
                resetState()
            }
        }
        return state == STATE_LEFT_SWIPE
    }

    fun onTouchEvent(ev: MotionEvent): Boolean {
        if (contentView == null) {
            return false
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                isDownOnContent = judgeIsDownOnView(ev, contentView)
                if (!isDownOnContent) {
                    return false
                }
                handleDownEvent(ev)
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isDownOnContent) {
                    return false
                }
                judgeMoveState(ev)
                when (state) {
                    STATE_LEFT_SWIPE -> {
                        var factor = 1f
                        if (container.scrollX > maxDampingDistance) {
                            factor = 1f - (container.scrollX.toFloat() - maxDampingDistance) / (maxScrollDistance - maxDampingDistance)
                        }
                        var newX = (container.scrollX + (lastX - ev.x) * factor).toInt()
                        if (newX < 0) {
                            newX = 0
                        } else if (newX > maxScrollDistance) {
                            newX = maxScrollDistance
                        }
                        container.scrollTo(newX, 0)
                        listeners.forEach { it.onLeftSwipe(newX) }
                    }
                }
                lastX = ev.x
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!isDownOnContent) {
                    return false
                }
                if (state == STATE_LEFT_SWIPE) {
                    Log.d(TAG, "onTouchEvent onLeftSwipeEnd")
                    listeners.forEach {  it.onLeftSwipeEnd(container.scrollX) }
                    recoverView()
                }
                resetState()
            }
        }
        return true
    }

    private fun handleDownEvent(ev: MotionEvent) {
        downX = ev.x
        downY = ev.y
        lastX = ev.x
        state = STATE_NONE
        // 拦截父view的处理，在手势判断后恢复
        container.parent?.requestDisallowInterceptTouchEvent(true)
    }

    private fun judgeIsDownOnView(ev: MotionEvent, view: View): Boolean {
        return ev.x > view.x && ev.x < view.x + view.width
                && ev.y > view.y && ev.y < view.y + view.height
    }

    private fun judgeMoveState(ev: MotionEvent) {
        if (state != STATE_NONE) {
            return
        }

        val dx = ev.x - downX
        val dy = ev.y - downY
        if (Math.sqrt((dx * dx + dy * dy).toDouble()) < touchSlop) {
            return
        }

        state = if (dx < 0 && -dx * 0.5 > dy.absoluteValue) {
            container.parent?.requestDisallowInterceptTouchEvent(true)
            listeners.forEach {  it.onStartLeftSwipe() }
            STATE_LEFT_SWIPE
        } else {
            container.parent?.requestDisallowInterceptTouchEvent(false)
            STATE_OTHER
        }
        Log.d(TAG, "judgeMoveState state=${state}")
    }

    private fun resetState() {
        Log.d(TAG, "resetState")
        state = STATE_NONE
        isDownOnContent = false
        container.parent?.requestDisallowInterceptTouchEvent(false)
    }

    private fun recoverView() {
        if (container.scrollX == 0) {
            return
        }
        if (!scroller.isFinished) {
            scroller.abortAnimation()
        }
        val duration = 50 + container.scrollX.absoluteValue.toFloat() / maxScrollDistance * 150
        scroller.startScroll(container.scrollX, 0, -container.scrollX, 0, duration.toInt())
        container.postOnAnimation(scrollRunnable)
    }

    fun addListener(listener: ILeftSwipeListener) {
        listeners.add(listener)
    }

    interface ILeftSwipeListener {

        fun onStartLeftSwipe() {}

        fun onLeftSwipe(dx: Int) {}

        fun onLeftSwipeEnd(scrollX: Int) {}

        fun onLeftSwipeBack(scrollX: Int) {}

        fun onLeftSwipeBackEnd() {}
    }

}