package org.devio.hi.ui.icfont

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
/**
 * 用以支持全局iconfont资源的引用，可以在布局中直接设置text
 */
class IconFontTextView
@JvmOverloads constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int = 0) :
    AppCompatTextView(context, attributeSet, defStyle) {
    init {
        val typeface = Typeface.createFromAsset(
            context.assets,
            "fonts/iconfont.ttf"
        );
        setTypeface(typeface)
    }
}