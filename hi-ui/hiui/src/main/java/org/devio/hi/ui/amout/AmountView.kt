package org.devio.hi.ui.amout

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.core.view.setPadding

/**
 * <TextView
android:id="@+id/action_decrease"
android:layout_width="@dimen/dp_20"
android:layout_height="@dimen/dp_20"
android:background="@color/color_eee"
android:gravity="center"
android:text="—"
android:textColor="@color/color_999" />

<TextView
android:id="@+id/goods_count"
android:layout_width="@dimen/dp_20"
android:layout_height="@dimen/dp_20"
android:layout_marginLeft="@dimen/dp_5"
android:layout_marginRight="@dimen/dp_5"
android:gravity="center"
android:text="1"
android:textColor="@color/color_999" />


<TextView
android:id="@+id/action_increase"
android:layout_width="@dimen/dp_20"
android:layout_height="@dimen/dp_20"
android:background="@color/color_eee"
android:gravity="center"
android:text="+"
android:textColor="@color/color_999" />
 */
class AmountView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var amountValueChangedCallback: (Int) -> Unit
    private val amountViewAttrs = AttrsParse.parseAmountViewAttrs(context, attrs, defStyleAttr)
    private var amountValue = amountViewAttrs.amountValue

    init {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER
        applyAttrs()
    }

    private fun applyAttrs() {
        val increaseBtn = generateBtn()
        increaseBtn.text = "+"

        val decreaseBtn = generateBtn()
        decreaseBtn.text = "—"

        val amountView = generateAmountTextView()
        amountView.text = amountValue.toString()

        addView(decreaseBtn)
        addView(amountView)
        addView(increaseBtn)

        decreaseBtn.isEnabled = amountValue > amountViewAttrs.amountMinValue
        increaseBtn.isEnabled = amountValue < amountViewAttrs.amountMaxValue

        decreaseBtn.setOnClickListener {
            amountValue--
            amountView.text = amountValue.toString()
            decreaseBtn.isEnabled = amountValue > amountViewAttrs.amountMinValue
            increaseBtn.isEnabled = true
            amountValueChangedCallback(amountValue)
        }

        increaseBtn.setOnClickListener {
            amountValue++
            amountView.text = amountValue.toString()
            increaseBtn.isEnabled = amountValue < amountViewAttrs.amountMaxValue
            decreaseBtn.isEnabled = true
            amountValueChangedCallback(amountValue)
        }
    }

    private fun generateAmountTextView(): TextView {
        val view = TextView(context)
        view.setPadding(0)
        view.setTextColor(amountViewAttrs.amountTextColor)
        view.setBackgroundColor(amountViewAttrs.amountBackground)
        view.gravity = Gravity.CENTER
        view.includeFontPadding = false

        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        params.leftMargin = amountViewAttrs.margin
        params.rightMargin = amountViewAttrs.margin
        view.layoutParams = params
        view.minWidth = amountViewAttrs.amountSize

        return view
    }

    private fun generateBtn(): Button {
        val button = Button(context)
        button.setBackgroundResource(0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.elevation = 0f
        }
        button.setPadding(0)
        button.includeFontPadding = false
        button.setTextColor(amountViewAttrs.btnTextColor)
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, amountViewAttrs.btnTextSize)
        button.setBackgroundColor(amountViewAttrs.btnBackground)
        button.gravity = Gravity.CENTER

        button.layoutParams = LayoutParams(amountViewAttrs.btnSize, amountViewAttrs.btnSize)
        return button
    }

    fun getAmountValue(): Int {
        return amountValue
    }

    fun setAmountValueChangedListener(amountValueChangedCallback: (Int) -> Unit) {
        this.amountValueChangedCallback = amountValueChangedCallback
    }
}