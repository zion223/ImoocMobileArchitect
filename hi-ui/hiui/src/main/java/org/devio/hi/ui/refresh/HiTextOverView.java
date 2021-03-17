package org.devio.hi.ui.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import org.devio.hi.imooc.R;

public class HiTextOverView extends HiOverView {
    private TextView mText;
    private View mRotateView;

    public HiTextOverView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HiTextOverView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HiTextOverView(Context context) {
        super(context);
    }

    @Override
    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.hi_refresh_overview, this, true);
        mText = findViewById(R.id.text);
        mRotateView = findViewById(R.id.iv_rotate);
    }

    @Override
    protected void onScroll(int scrollY, int pullRefreshHeight) {
    }

    @Override
    public void onVisible() {
        mText.setText("下拉刷新");
    }

    @Override
    public void onOver() {
        mText.setText("松开刷新");
    }

    @Override
    public void onRefresh() {
        mText.setText("正在刷新...");
        Animation operatingAnim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_anim);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        mRotateView.startAnimation(operatingAnim);
    }

    @Override
    public void onFinish() {
        mRotateView.clearAnimation();
    }


}