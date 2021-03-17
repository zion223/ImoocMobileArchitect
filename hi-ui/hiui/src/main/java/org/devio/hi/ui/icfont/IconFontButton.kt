package org.devio.hi.ui.icfont

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

internal class IconFontButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {
    init {
        val typeface = Typeface.createFromAsset(context.assets, "fonts/iconfont.ttf");
        setTypeface(typeface)
    }
}