package com.example.wsq.android.activity.user;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wsq.android.R;
import com.example.wsq.android.base.BaseActivity;
import com.example.wsq.android.constant.Constant;
import com.example.wsq.android.constant.ResponseKey;
import com.example.wsq.android.inter.HttpResponseListener;
import com.example.wsq.android.service.UserService;
import com.example.wsq.android.service.impl.UserServiceImpl;
import com.example.wsq.android.tools.RegisterParam;
import com.example.wsq.android.utils.IntentFormat;
import com.example.wsq.android.utils.ToastUtis;
import com.example.wsq.android.utils.ValidateDataFormat;
import com.example.wsq.android.utils.ValidateParam;
import com.example.wsq.android.view.LoddingDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by wsq on 2017/12/11.
 */


public class ForgetPsdActivity extends BaseActivity {

    @BindView(R.id.tv_title)TextView tv_title;
    @BindView(R.id.et_tel) EditText et_tel;
    @BindView(R.id.tv_getCode) TextView tv_getCode;
    @BindView(R.id.et_validateCode) EditText et_validateCode;
    @BindView(R.id.et_username) EditText et_username;
    @BindView(R.id.et_password1) EditText et_password1;
    @BindView(R.id.et_password2) EditText et_password2;
    @BindView(R.id.tv_ok) TextView  tv_ok;

    private UserService userService;
    private int curLen = 60;
    private LoddingDialog dialog;


    @Override
    public int getByLayoutId() {
        return R.layout.layout_forget_psd;
    }

    @Override
    public void init(){
        userService = new UserServiceImpl();
        tv_ok = this.findViewById(R.id.tv_ok);
        tv_title.setText("找回密码");
        dialog = new LoddingDialog(this);

        onEditTextChange();
    }


    Handler handler = new Handler(){};
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            curLen--;
            if (curLen == 0){
                curLen = 60;
                tv_getCode.setText("获取验证码");
                tv_getCode.setBackgroundResource(R.drawable.shape_button);
                tv_getCode.setClickable(true);
            }else{
                tv_getCode.setText(curLen+"秒后重发");
                tv_getCode.setClickable(false);
                tv_getCode.setBackgroundResource(R.drawable.shape_disable_button);
                handler.postDelayed(this, 1000);
            }

        }
    };

    @OnClick({R.id.iv_back, R.id.tv_getCode, R.id.tv_ok})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
//                moveTaskToBack(false);
                break;
            case R.id.tv_getCode:
                //验证手机号码
                String tel = et_tel.getText().toString();
                if(!ValidateDataFormat.isMobile(tel)){

                    Toast.makeText(ForgetPsdActivity.this, "输入的手机号有误", Toast.LENGTH_SHORT).show();
                    return;
                }

                RegisterParam.TEL = tel;
                Map<String, String> map = new HashMap<>();
                map.put(ResponseKey.TEL, tel);
//                handler.postDelayed(runnable, 1000);
                dialog.show();
                userService.getValidateCode(this, map, new HttpResponseListener() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        ToastUtis.onToast(result.get(ResponseKey.MESSAGE)+"");
                        handler.postDelayed(runnable, 1000);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure() {
                        if (dialog.isShowing())dialog.dismiss();
                    }
                });

                break;
            case R.id.tv_ok:
                if(validate()){
                    Map<String, String> param = new HashMap<>();
                    param.put(ResponseKey.USERNAME, et_username.getText().toString());
                    param.put(ResponseKey.NEW_PSD, et_password1.getText().toString());
                    param.put(ResponseKey.TEL, et_tel.getText().toString());
                    param.put(ResponseKey.YZM, et_validateCode.getText().toString());
                    userService.updatePassword(this, param, new HttpResponseListener() {
                        @Override
                        public void onSuccess(Map<String, Object> result) {
                            Toast.makeText(ForgetPsdActivity.this, result.get(ResponseKey.MESSAGE)+"", Toast.LENGTH_SHORT).show();
                            IntentFormat.startActivity(ForgetPsdActivity.this, LoginActivity.class);
                            finish();
                        }

                        @Override
                        public void onFailure() {

                        }
                    });

                }

                break;
        }
    }

    public boolean validate(){

        //验证手机号码
        String tel = et_tel.getText().toString();
        if(ValidateDataFormat.isMobile(tel)){
        }else{
            Toast.makeText(ForgetPsdActivity.this, "输入的手机号有误", Toast.LENGTH_SHORT).show();
            return false;
        }


        //验证密码是否一致
        String password1 = et_password1.getText().toString();
        String password2 = et_password2.getText().toString();
        //检验密码是否为空
        if (ValidateParam.validateParamIsNull(password1, password2)){
            Toast.makeText(ForgetPsdActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        //检验密码是否两次输入一致
        if (!password1.equals(password2)){
            Toast.makeText(ForgetPsdActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return false;
        }
        //检验密码长度
        if(password1.length()<=16 && password1.length() >= 6){

        }else{
            Toast.makeText(ForgetPsdActivity.this, "请输入6-16位的密码", Toast.LENGTH_SHORT).show();
            return false;
        }


        //用户名验证
        String userName = et_username.getText().toString();
        //输入的字母是否正确
        if(!userName.matches("[A-Za-z0-9_]+")){
            Toast.makeText(ForgetPsdActivity.this, "用户名只能为数字，字母，下划线", Toast.LENGTH_SHORT).show();
            return false;
        }
        //输入的长度是否正确 6-18
        if(userName.length()>= 6 && userName.length() <= 18){

        }else {
            Toast.makeText(ForgetPsdActivity.this, "请输入6-18位的用户名", Toast.LENGTH_SHORT).show();
            return false;
        }


        //验证验证码
        String code = et_validateCode.getText().toString();

        if (ValidateParam.validateParamIsNull(code)){
            Toast.makeText(ForgetPsdActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(code.length()!= Constant.CODE_LENGTH){
            Toast.makeText(ForgetPsdActivity.this, "验证码必须为"+Constant.CODE_LENGTH, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode ==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(false);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onEditTextChange(){

        et_tel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.toString().length() == 11){
                    tv_getCode.setBackgroundResource(R.drawable.shape_button);
                    tv_getCode.setClickable(true);
                }else{
                    tv_getCode.setBackgroundResource(R.drawable.shape_disable_button);
                    tv_getCode.setClickable(false);
                }
            }
        });

        et_password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.toString().length()< Constant.PASSWORD_COUNT){
                    tv_ok.setBackgroundResource(R.drawable.shape_disable_button);
                    tv_ok.setClickable(false);
                }else{
                    tv_ok.setBackgroundResource(R.drawable.shape_button);
                    tv_ok.setClickable(true);
                }
            }
        });
    }
}
