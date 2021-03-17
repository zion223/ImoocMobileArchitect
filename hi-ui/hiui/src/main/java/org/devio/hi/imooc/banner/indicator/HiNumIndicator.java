package org.devio.hi.imooc.banner.indicator;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.devio.hi.imooc.R;
import org.devio.hi.imooc.util.HiDisplayUtil;

public class HiNumIndicator extends FrameLayout implements HiIndicator<FrameLayout> {
    /**
     * 指示点左右内间距
     */
    private int mPointLeftRightPadding;

    /**
     * 指示点上下内间距
     */
    private int mPointTopBottomPadding;
    private View rootView;

    public HiNumIndicator(@NonNull Context context) {
        this(context, null);
    }

    public HiNumIndicator(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HiNumIndicator(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPointLeftRightPadding = HiDisplayUtil.dp2px(10, getContext().getResources());
        mPointTopBottomPadding = HiDisplayUtil.dp2px(10, getContext().getResources());
    }

    @Override
    public FrameLayout get() {
        return this;
    }

    @Override
    public void onInflate(int count) {
        if (count <= 0) {
            return;
        }
        /**
         * //此处相当于布局文件中的Android:layout_gravity属性
         * lp.gravity = Gravity.RIGHT;
         * button.setLayoutParams(lp);
         * //此处相当于布局文件中的Android：gravity属性
         * button.setGravity(Gravity.CENTER);
         */
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END | Gravity.BOTTOM;
        linearLayout.setLayoutParams(params);

        linearLayout.setPadding(0, 0, mPointLeftRightPadding, mPointTopBottomPadding);
        // 指示器样式  1 / 5
        TextView indexView = new TextView(getContext());
        indexView.setText("1");
        indexView.setTextColor(Color.WHITE);
        linearLayout.addView(indexView);

        TextView symbolTv = new TextView(getContext());
        symbolTv.setText(" / ");
        symbolTv.setTextColor(Color.WHITE);
        linearLayout.addView(symbolTv);

        TextView countTv = new TextView(getContext());
        countTv.setText(String.valueOf(count));
        countTv.setTextColor(Color.WHITE);
        linearLayout.addView(countTv);

        //addView(linearLayout);
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.hi_num_indicator, this);

    }

    @Override
    public void onPointChange(int current, int count) {
//        LinearLayout ll = (LinearLayout) getChildAt(0);
//        TextView indexView = (TextView) ll.getChildAt(0);
//        indexView.setText(String.valueOf(current + 1));
//
//        TextView countTv = (TextView) ll.getChildAt(2);
//        countTv.setText(String.valueOf(count));
        TextView indexView = rootView.findViewById(R.id.indexView);
        TextView countView = rootView.findViewById(R.id.countTv);
        indexView.setText(String.valueOf(current + 1));

        countView.setText(String.valueOf(count));

    }
}
