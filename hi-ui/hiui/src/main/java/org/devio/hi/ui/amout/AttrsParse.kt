package org.devio.hi.ui.amout

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import org.devio.hi.imooc.R
import org.devio.hi.library.util.HiDisplayUtil

/**
 * 解析AmountView的属性返回Attrs对象
 */
internal object AttrsParse {
    fun parseAmountViewAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int): Attrs {
        val array =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.AmountView,
                defStyleAttr,
                R.style.AmountStyle
            )

        val btnTextSize =
            array.getDimensionPixelSize(R.styleable.AmountView_btn_size, HiDisplayUtil.sp2px(14f))
        val btnTextColor = array.getColorStateList(R.styleable.AmountView_btn_color)
        val btnSize =
            array.getDimensionPixelSize(R.styleable.AmountView_btn_size, HiDisplayUtil.dp2px(20f))
        val margin = array.getDimensionPixelOffset(R.styleable.AmountView_btn_margin, 0)
        val btnBackground = array.getColor(
            R.styleable.AmountView_btn_background,
            Color.parseColor("#eeeeee")
        )

        val amountTextSize = array.getDimensionPixelSize(
            R.styleable.AmountView_amount_text_size,
            HiDisplayUtil.sp2px(14f)
        )
        val amountTextColor = array.getColor(R.styleable.AmountView_amount_color, Color.BLACK)
        val amountSize = array.getDimensionPixelSize(
            R.styleable.AmountView_amount_size,
            HiDisplayUtil.dp2px(20f)
        )
        val amountBackground =
            array.getColor(R.styleable.AmountView_amount_background, Color.WHITE)


        val amountValue = array.getInteger(R.styleable.AmountView_value, 1)
        val amountMinValue = array.getInteger(R.styleable.AmountView_min_value, 1)
        val amountMaxValue = array.getInteger(R.styleable.AmountView_max_value, Int.MAX_VALUE)


        array.recycle()


        return Attrs(
            btnTextSize.toFloat(),
            btnTextColor,
            btnSize,
            margin,
            btnBackground,
            amountTextSize.toFloat(),
            amountTextColor,
            amountSize,
            amountBackground,
            amountValue,
            amountMinValue,
            amountMaxValue
        )
    }

    data class Attrs(
        val btnTextSize: Float,
        val btnTextColor: ColorStateList?,
        val btnSize: Int,
        val margin: Int,
        val btnBackground: Int,
        val amountTextSize: Float,
        val amountTextColor: Int,
        val amountSize: Int,
        val amountBackground: Int,
        val amountValue: Int,
        val amountMinValue: Int,
        val amountMaxValue: Int
    )
}