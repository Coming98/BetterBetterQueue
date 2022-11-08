package com.example.betterbetterqueue

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
// Done
class SoftKeyBoardListener(val activity: Activity, val threshold: Int) {

    companion object {
        /**
         * 静态方法
         * 提供外部使用的接口
         */
        fun setListener(activity: Activity, threshold: Int, onSoftKeyBoardChangeListener: OnSoftKeyBoardChangeListener) {
            val softKeyBoardListener: SoftKeyBoardListener = SoftKeyBoardListener(activity, threshold)
            softKeyBoardListener.setOnSoftKeyBoardChangeListener(onSoftKeyBoardChangeListener)
        }

        interface OnSoftKeyBoardChangeListener {
            fun keyBoardShow(height: Int)
            fun keyBoardHide(height: Int)
        }
    }

    val rootView: View by lazy { activity.window.decorView } // activity 的根视图
    private var onSoftKeyBoardChangeListener: OnSoftKeyBoardChangeListener? = null
    init {
        var predViewVisibleHeight = -1
        /**
         * Register a callback to be invoked when the global layout state or the visibility of views within the view tree changes
         *      维护前一个状态的视图高度
         *      如果软键盘弹出了将影响当前视图高度这样就能检测软键盘弹出和隐藏事件
         *      当然别的举动也可能影响视图高度，这时候需要在逻辑时检测软件盘的状态进一步保证是软件盘引起的高度变化
         */
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                val rect = Rect()
                rootView.getWindowVisibleDisplayFrame(rect)
                val currentVisibleHeight = rect.height()

                // 初始化布局时不捕捉
                if(predViewVisibleHeight == -1) {
                    predViewVisibleHeight = currentVisibleHeight
                    return
                }

                if(predViewVisibleHeight == currentVisibleHeight) return

                if(predViewVisibleHeight - currentVisibleHeight >= threshold) {
                    onSoftKeyBoardChangeListener?.let { it.keyBoardShow(predViewVisibleHeight - currentVisibleHeight) }
                    predViewVisibleHeight = currentVisibleHeight
                    return
                }
                if(currentVisibleHeight - predViewVisibleHeight >= threshold) {
                    onSoftKeyBoardChangeListener?.let { it.keyBoardHide(currentVisibleHeight - predViewVisibleHeight) }
                    predViewVisibleHeight = currentVisibleHeight
                    return
                }
            }
        })
    }

    // 私有的 set 方法
    private fun setOnSoftKeyBoardChangeListener(onSoftKeyBoardChangeListener: OnSoftKeyBoardChangeListener) {
        this.onSoftKeyBoardChangeListener = onSoftKeyBoardChangeListener
    }
}