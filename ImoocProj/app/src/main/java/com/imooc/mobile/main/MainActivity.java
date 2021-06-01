package com.imooc.mobile.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.imooc.mobile.common.ui.component.HiBaseActivity;
import com.imooc.mobile.main.logic.MainActivityLogic;

public class MainActivity extends HiBaseActivity implements MainActivityLogic.ActivityProvider {


    private MainActivityLogic activityLogic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
         * TODO tips: 开发者选项中设置不保留活动时，应用切后台然后切回应用后就会导致Fragment重叠问题
         *  此时Activity被回收但是Fragment没有被回收
         */
        activityLogic = new MainActivityLogic(this, savedInstanceState);
    }

    /**
     * Activity被回收时回调用此方法保留当前应用的状态
     * @param outState outState
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        activityLogic.onSaveInstanceState(outState);
    }
}