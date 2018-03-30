package com.example.wsq.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wsq.android.R;
import com.example.wsq.android.activity.user.LoginActivity;
import com.example.wsq.android.base.BaseActivity;
import com.example.wsq.android.constant.Constant;
import com.example.wsq.android.utils.IntentFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/3/14 0014.
 */

public class DialogActivity extends Activity {

    @BindView(R.id.dialog_message)
    TextView dialog_message;
    @BindView(R.id.dialog_ok)
    Button dialog_ok;
    @BindView(R.id.dialog_cancel) Button dialog_cancel;
    @BindView(R.id.dialig_view)
    View dialig_view;
    @BindView(R.id.iv_close)
    ImageView iv_close;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_dialog);
        ButterKnife.bind(this);
        init();
    }



    public void init() {

        dialog_message.setText("您的账号已在其他设备行登录，如果不是您本人操作，请及时修改唯一密码");
        dialog_cancel.setVisibility(View.GONE);
        dialig_view.setVisibility(View.GONE);
        iv_close.setVisibility(View.GONE);
        dialog_ok.setBackgroundResource(R.drawable.shape_dialog_buttom);
    }

    @OnClick(R.id.dialog_ok)
    public void onClick(View view){
        switch (view.getId()){
            case R.id.dialog_ok:
//                IntentFormat.startActivity(DialogActivity.this, LoginActivity.class);
                Intent intent = new Intent();
                intent.setAction(Constant.ACTION.BACK);
                sendBroadcast(intent);
                finish();
                break;
        }
    }

}
