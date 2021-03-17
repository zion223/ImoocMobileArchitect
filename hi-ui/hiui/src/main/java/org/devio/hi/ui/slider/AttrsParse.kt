package org.devio.hi.ui.slider

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import org.devio.hi.library.util.HiDisplayUtil
import org.devio.hi.library.util.HiRes
import org.devio.hi.imooc.R

internal object AttrsParse {
    private val MENU_WIDTH = HiDisplayUtil.dp2px(100f)
    private val MENU_HEIGHT = HiDisplayUtil.dp2px(45f)
    private val MENU_TEXT_SIZE = HiDisplayUtil.sp2px(14f)

    private val TEXT_COLOR_NORMAL = HiRes.getColor(R.color.color_666)//Color.parseColor("#666666")
    private val TEXT_COLOR_SELECT = HiRes.getColor(R.color.color_127)//Color.parseColor("#DD3127")

    private val BG_COLOR_NORMAL = HiRes.getColor(R.color.color_8f9)//Color.parseColor("#F7F8F9")
    private val BG_COLOR_SELECT = HiRes.getColor(R.color.color_white)//Color.parseColor("#ffffff")

    fun parseMenuItemAttr(context: Context, attrs: AttributeSet?): MenuItemAttr {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.HiSliderView)
        val menuItemWidth =
            typedArray.getDimensionPixelOffset(R.styleable.HiSliderView_menuItemWidth, MENU_WIDTH)
        val menuItemHeight =
            typedArray.getDimensionPixelOffset(R.styleable.HiSliderView_menuItemHeight, MENU_HEIGHT)
        val menuItemTextSize = typedArray.getDimensionPixelOffset(
            R.styleable.HiSliderView_menuItemTextSize,
            MENU_TEXT_SIZE
        )
        val menuItemSelectTextSize = typedArray.getDimensionPixelOffset(
            R.styleable.HiSliderView_menuItemSelectTextSize,
            MENU_TEXT_SIZE
        )

        val menuItemTextColor =
            typedArray.getColorStateList(R.styleable.HiSliderView_menuItemTextColor)
                ?: generateColorStateList()

        val menuItemIndicator = typedArray.getDrawable(R.styleable.HiSliderView_menuItemIndicator)
            ?: ContextCompat.getDrawable(context, R.drawable.shape_hi_slider_indicator)

        val menuItemBackgroundColor =
            typedArray.getColor(R.styleable.HiSliderView_menuItemBackgroundColor, BG_COLOR_NORMAL)
        val menuItemBackgroundSelectColor = typedArray.getColor(
            R.styleable.HiSliderView_menuItemSelectBackgroundColor,
            BG_COLOR_SELECT
        )


        typedArray.recycle()

        return MenuItemAttr(
            menuItemWidth,
            menuItemHeight,
            menuItemTextColor,
            menuItemBackgroundSelectColor,
            menuItemBackgroundColor,
            menuItemTextSize,
            menuItemSelectTextSize,
            menuItemIndicator
        )
    }

    data class MenuItemAttr(
        val width: Int,
        val height: Int,
        val textColor: ColorStateList,
        val selectBackgroundColor: Int,
        val normalBackgroundColor: Int,
        val textSize: Int,
        val selectTextSize: Int,
        val indicator: Drawable?
    )

    private fun generateColorStateList(): ColorStateList {
        val states = Array(2) { IntArray(2) }
        val colors = IntArray(2)

        colors[0] = TEXT_COLOR_SELECT
        colors[1] = TEXT_COLOR_NORMAL

        states[0] = IntArray(1) { android.R.attr.state_selected }
        states[1] = IntArray(1)


        return ColorStateList(states, colors)
    }
}