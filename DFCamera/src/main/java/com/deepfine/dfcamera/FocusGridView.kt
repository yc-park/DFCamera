package com.deepfine.camera

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.NonNull
import androidx.annotation.Nullable

import android.R
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import java.util.ArrayList

/**
 * @Description FocusGridView
 * @author jh.kim (DEEP.FINE)
 * @since 2020/08/19
 * @version 1.0.0
 */
class FocusGridView @JvmOverloads constructor(
    @NonNull context: Context,
    @Nullable attrs: AttributeSet? = null,
    val lineColor: Int? = null,
    val bgColor: Int? = null,
    val textColor: Int? = null,
    val text: String? = null,
    val textSize: Int? = null,
    val marginTopBottom: Float? = 40f,
    val dimColor: Int? = 0xAB000000.toInt()
) : LinearLayout(context, attrs) {
    private var _gridMode: Grid =
        Grid.OFF
    var gridMode: Grid
        get() = _gridMode
        set(value) {
            _gridMode = value
            updateView()
            postInvalidate()
        }
    var callback: TouchCallback? = null

    interface TouchCallback {
        fun onTouch(x: Int, y: Int)
    }

    private val lineCount: Int
        private get() {
            when (gridMode) {
                Grid.DRAW_3X3, Grid.DRAW_5X3 -> return 2
                Grid.DRAW_4X4 -> return 3
                else -> return 0
            }
        }
    private val columnsCount: Int
    private get() {
        when (gridMode) {
            Grid.DRAW_3X3 -> return 2
            Grid.DRAW_4X4 -> return 3
            Grid.DRAW_5X3 -> return 4
            else -> return 0
        }
    }

    fun updateView() {
        this.removeAllViewsInLayout()

        if (lineCount > 1) {
            // 배경 상단 뷰
            val blankTop: View = View(context).also {
                it.id = View.generateViewId()

                it.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,0,1f
                )

                it.setBackgroundColor(dimColor!!)
                addView(it)
            }


            addView(LinearLayout(context).also {
                it.layoutParams = LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,0f
                )
                it.orientation = LinearLayout.HORIZONTAL

                val blankLeft: View = View(context).also { view ->
                    view.id = View.generateViewId()

                    view.layoutParams = LinearLayout.LayoutParams(
                        0, FrameLayout.LayoutParams.MATCH_PARENT,1f
                    )

                    view.setBackgroundColor(dimColor!!)
                    it.addView(view)
                }

                // 중앙 그리드 뷰
                val metrics = resources.displayMetrics

                val contentsHeight: Int = if (metrics.heightPixels > metrics.widthPixels) {
                    (metrics.widthPixels - metrics.density * marginTopBottom!!) / (columnsCount + 1)
                } else {
                    (metrics.heightPixels - metrics.density * marginTopBottom!!) / (lineCount + 1)
                }.toInt()

                val container: LinearLayout = LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        (contentsHeight * (columnsCount + 1)),
                        (contentsHeight * (lineCount + 1)),
                        0f
                    )

                    orientation = LinearLayout.VERTICAL
                }

                for (i in 0..lineCount) {
                    val row = LinearLayout(context)
                    row.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    row.orientation = LinearLayout.HORIZONTAL

                    for (j in 0..columnsCount) {
                        val textView = GridModeButton(
                            context,
                            text ?: "" + (j + 1 + i * (columnsCount + 1)).toString(),
                            lineColor
                        ).apply {
                            this.layoutParams = LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                1f
                            )
                            this.gravity = Gravity.CENTER
                            this.setTextColor(
                                textColor ?: ResourcesCompat.getColor(
                                    context.resources,
                                    R.color.black,
                                    null
                                )
                            )
                            this.id = j + 1 + i * 4

                            this.setOnClickListener { view ->
                                callback?.let {
                                    val location = IntArray(2)
                                    view.getLocationOnScreen(location)
                                    val locX = view.x;
                                    val locX2 = view.pivotX;
                                    val locX4 = view.width;

                                    val x = location[0] + view.width + (marginTopBottom/2)
                                    val y = location[1] + (view.height / 2)

                                    it.onTouch(x.toInt(), y.toInt())
                                }
                            }
                        }
                        textView.textSize = textSize?.toFloat() ?: Utils.dpToPixel(
                            context,
                            30f
                        )
                        textView.id = View.generateViewId()

                        row.addView(textView)
                    }
                    container.addView(row)
                }
                it.addView(container)

                val blankRight: View = View(context).also { view ->
                    view.id = View.generateViewId()

                    view.layoutParams = LinearLayout.LayoutParams(
                        0,LinearLayout.LayoutParams.MATCH_PARENT,1f
                    )

                    view.setBackgroundColor(dimColor!!)
                    it.addView(view)
                }
            })

            val blankBottom: View = View(context).also {
                it.id = View.generateViewId()

                it.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,0,1f
                )

                it.setBackgroundColor(dimColor!!)
                addView(it)
            }
        }

//        if (lineCount > 1) {
//            for (i in 0..lineCount) {
//                val row = LinearLayout(context)
//                row.layoutParams = LayoutParams(
//                    LayoutParams.MATCH_PARENT,
//                    LayoutParams.WRAP_CONTENT,
//                    1f
//                )
//                row.orientation = HORIZONTAL
//                row.gravity = View.TEXT_ALIGNMENT_CENTER
//                for (j in 0..columnsCount) {
//                    val textView = TextView(context)
//                    textView.layoutParams =  LayoutParams(
//                        LayoutParams.WRAP_CONTENT,
//                        LayoutParams.MATCH_PARENT,
//                        1f
//                    )
//
//                    textView.gravity = Gravity.CENTER
//                    textView.text = (j + 1 + columnsCount * lineCount).toString()
//                    textView.textSize = textSize?.toFloat() ?: Utils.dpToPixel(
//                        context,
//                        30f
//                    )
//                    textView.setTextColor(
//                        textColor ?: ResourcesCompat.getColor(
//                            context.resources,
//                            R.color.black,
//                            null
//                        )
//                    )
//
//
//                    textView.setOnClickListener { view ->
//                        callback?.let {
//                            val location = IntArray(2)
//                            view.getLocationOnScreen(location)
//                            val x = location[0] + (view.width / 2)
//                            val y = location[1] + (view.height / 2)
//
//                            it.onTouch(x, y)
//                        }
//                    }
//                    row.addView(textView)
//                }
//                addView(row)
//            }
//        }
    }

    init {
        this.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        this.orientation = VERTICAL
    }
}