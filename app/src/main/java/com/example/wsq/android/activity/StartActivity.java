package com.example.wsq.android.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.example.wsq.android.R;
import com.example.wsq.android.activity.user.LoginActivity;
import com.example.wsq.android.constant.Constant;
import com.example.wsq.android.utils.IntentFormat;


/**
 * Created by wsq on 2017/12/12.
 */

public class StartActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_start);
        initView();
    }


    public void initView() {

        handler.post(thread);
    }




    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
                IntentFormat.startActivity(StartActivity.this, WellcomeActivity.class);
                overridePendingTransition(R.anim.anim_activity_default_in, R.anim.anim_activity_default_out);
            finish();
        }
    };

    Runnable thread = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessageDelayed(0, 1*1000);
        }
    };
}
