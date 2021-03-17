package org.devio.hi.ui.search

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.setPadding
import org.devio.hi.library.util.MainHandler
import org.devio.hi.imooc.R
import org.devio.hi.ui.icfont.IconFontTextView

class HiSearchView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    companion object {
        const val LEFT = 1
        const val CENTER = 0

        const val DEBOUNCE_TRIGGER_DURATION = 200L
    }

    private var simpleTextWatcher: SimpleTextWatcher? = null
    var editText: EditText? = null

    //搜索小图标 和 默认提示语 ，以及 container
    private var searchIcon: IconFontTextView? = null
    private var hintTv: TextView? = null
    private var searchIconHintContainer: LinearLayout? = null

    //右侧清除小图标
    private var clearIcon: IconFontTextView? = null

    //keyword
    private var keywordContainer: LinearLayout? = null
    private var keywordTv: TextView? = null
    private var kwClearIcon: IconFontTextView? = null

    private val viewAttrs: AttrsParse.Attrs =
        AttrsParse.parseSearchViewAttrs(context, attrs, defStyleAttr)

    init {
        //初始化editText  --create-bind property --addview
        initEditText()
        //初始化右侧一键清楚地小按钮   create-bind property --addview
        initClearIcon()
        //初始化 默认的提示语 和 searchIcon  create-bind property --addview
        initSearchIconHintContainer()

        background = viewAttrs.searchBackground
        editText?.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                val hasContent = s?.trim()?.length ?: 0 > 0
                changeVisibility(clearIcon, hasContent)
                changeVisibility(searchIconHintContainer, !hasContent)

                if (simpleTextWatcher != null) {
                    MainHandler.remove(debounceRunnable)
                    MainHandler.postDelay(DEBOUNCE_TRIGGER_DURATION, debounceRunnable)
                }
            }
        })
    }

    private val debounceRunnable = Runnable {
        if (simpleTextWatcher != null) {
            simpleTextWatcher!!.afterTextChanged(editText?.text)
        }
    }

    fun setDebounceTextChangedListener(simpleTextWatcher: SimpleTextWatcher) {
        this.simpleTextWatcher = simpleTextWatcher
    }

    fun setHintText(hintText: String) {
        hintTv?.text = hintText
    }

    fun setKeyWord(keyword: String?, listener: OnClickListener) {
        //当用户点击 联想词面板的时候，会调用该方法，把关键词设置到搜索框上面
        ensureKeywordContainer()
        toggleSearchViewsVisibility(true)

        editText?.text = null
        keywordTv?.text = keyword
        kwClearIcon?.setOnClickListener {
            //点击了keywordk-clearicon ,此时应该恢复默认提示语views显示¬
            toggleSearchViewsVisibility(false)
            listener.onClick(it)
        }
    }

    fun setClearIconClickListener(listener: OnClickListener) {
        clearIcon?.setOnClickListener {
            editText?.text = null
            changeVisibility(clearIcon, false)
            changeVisibility(searchIcon, true)
            changeVisibility(hintTv, true)
            changeVisibility(searchIconHintContainer, true)

            listener.onClick(it)
        }
    }

    private fun toggleSearchViewsVisibility(showkword: Boolean) {
        changeVisibility(editText, !showkword)
        changeVisibility(clearIcon, false)
        changeVisibility(searchIconHintContainer, !showkword)
        changeVisibility(searchIcon, !showkword)
        changeVisibility(hintTv, !showkword)
        changeVisibility(keywordContainer, showkword)
    }

    private fun ensureKeywordContainer() {
        if (keywordContainer != null) return

        if (!TextUtils.isEmpty(viewAttrs.keywordClearIcon)) {
            kwClearIcon = IconFontTextView(context, null)
            kwClearIcon?.setTextSize(TypedValue.COMPLEX_UNIT_PX, viewAttrs.keywordSize)
            kwClearIcon?.setTextColor(viewAttrs.keywordColor)
            kwClearIcon?.text = viewAttrs.keywordClearIcon
            kwClearIcon?.id = R.id.id_search_keyword_clear_icon
            kwClearIcon?.setPadding(
                viewAttrs.iconPadding,
                viewAttrs.iconPadding / 2,
                viewAttrs.iconPadding,
                viewAttrs.iconPadding / 2
            )
        }

        keywordTv = TextView(context)
        keywordTv?.setTextSize(TypedValue.COMPLEX_UNIT_PX, viewAttrs.keywordSize)
        keywordTv?.setTextColor(viewAttrs.keywordColor)
        keywordTv?.includeFontPadding = false
        keywordTv?.isSingleLine = true
        keywordTv?.ellipsize = TextUtils.TruncateAt.END
        keywordTv?.filters = arrayOf(InputFilter.LengthFilter(viewAttrs.keywordMaxLen))
        keywordTv?.id = R.id.id_search_keyword_text_view
        keywordTv?.setPadding(
            viewAttrs.iconPadding,
            viewAttrs.iconPadding / 2,
            if (kwClearIcon == null) viewAttrs.iconPadding else 0,
            viewAttrs.iconPadding / 2
        )

        keywordContainer = LinearLayout(context)
        keywordContainer?.orientation = LinearLayout.HORIZONTAL
        keywordContainer?.gravity = Gravity.CENTER
        keywordContainer?.background = viewAttrs.keywordBackground

        keywordContainer?.addView(
            keywordTv,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        if (kwClearIcon != null) {
            keywordContainer?.addView(
                kwClearIcon,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
        }

        val kwParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        kwParams.addRule(CENTER_VERTICAL)
        kwParams.addRule(ALIGN_PARENT_LEFT)
        kwParams.leftMargin = viewAttrs.iconPadding
        kwParams.rightMargin = viewAttrs.iconPadding
        addView(keywordContainer, kwParams)
    }

    private fun initSearchIconHintContainer() {
        //hint view --start
        hintTv = TextView(context)
        hintTv?.setTextColor(viewAttrs.hintTextColor)
        hintTv?.setTextSize(TypedValue.COMPLEX_UNIT_PX, viewAttrs.hintTextSize)
        hintTv?.isSingleLine = true
        hintTv?.text = viewAttrs.hintText
        hintTv?.id = R.id.id_search_hint_view
        //hint view --end

        //search icon --start
        searchIcon = IconFontTextView(context, null)
        searchIcon?.setTextSize(TypedValue.COMPLEX_UNIT_PX, viewAttrs.searchIconSize)
        searchIcon?.setTextColor(viewAttrs.hintTextColor)
        searchIcon?.text = viewAttrs.searchIcon
        searchIcon?.id = R.id.id_search_icon
        searchIcon?.setPadding(viewAttrs.iconPadding, 0, viewAttrs.iconPadding / 2, 0)
        //search icon --end

        //icon hint container--start
        searchIconHintContainer = LinearLayout(context)
        searchIconHintContainer?.orientation = LinearLayout.HORIZONTAL
        searchIconHintContainer?.gravity = Gravity.CENTER

        searchIconHintContainer?.addView(searchIcon)
        searchIconHintContainer?.addView(hintTv)

        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.addRule(CENTER_VERTICAL)
        when (viewAttrs.gravity) {
            CENTER -> params.addRule(CENTER_IN_PARENT)
            LEFT -> params.addRule(ALIGN_PARENT_LEFT)
            else -> throw IllegalStateException("not support gravity for now.")
        }
        addView(searchIconHintContainer, params)
        //icon hint container--end
    }

    private fun initClearIcon() {
        if (TextUtils.isEmpty(viewAttrs.clearIcon)) return
        clearIcon = IconFontTextView(context, null)
        clearIcon?.setTextSize(TypedValue.COMPLEX_UNIT_PX, viewAttrs.clearIconSize)
        clearIcon?.text = viewAttrs.clearIcon
        clearIcon?.setTextColor(viewAttrs.searchTextColor)

        clearIcon?.setPadding(viewAttrs.iconPadding)

        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.addRule(CENTER_VERTICAL)
        params.addRule(ALIGN_PARENT_RIGHT)
        clearIcon?.layoutParams = params
        //默认隐藏，只有当输入文字才会显示
        changeVisibility(clearIcon, false)
        clearIcon?.id = R.id.id_search_clear_icon

        addView(clearIcon, params)

    }

    private fun initEditText() {
        editText = EditText(context)
        editText?.setTextColor(viewAttrs.searchTextColor)
        editText?.setBackgroundColor(Color.TRANSPARENT)
        editText?.setTextSize(TypedValue.COMPLEX_UNIT_PX, viewAttrs.searchTextSize)
        //防止 文字输入你过于贴近输入框的两边
        editText?.setPadding(viewAttrs.iconPadding, 0, viewAttrs.iconPadding, 0)
        editText?.id = R.id.id_search_edit_view

        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        params.addRule(CENTER_VERTICAL)
        addView(editText, params)
    }

    private fun changeVisibility(view: View?, show: Boolean) {
        view?.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        MainHandler.remove(debounceRunnable)
    }

    fun getKeyword(): String? {
        return keywordTv?.text.toString()
    }
}

