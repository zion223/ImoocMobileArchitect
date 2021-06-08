package org.devio.as.hi.module_trade;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.List;
import java.util.Map;

@Route(path = "/trade/detail/activity")
public class TradeDetailActivity extends AppCompatActivity {
    @Autowired
    public String saleId;

    @Autowired
    public String shopId;


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        ARouter.getInstance().inject(this);
    }
}
