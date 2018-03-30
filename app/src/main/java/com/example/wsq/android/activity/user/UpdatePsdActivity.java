package com.example.wsq.android.activity.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.wsq.android.utils.ValidateParam;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 修改密码
 * Created by wsq on 2017/12/19.
 */

public class UpdatePsdActivity extends BaseActivity {

    @BindView(R.id.et_new_password1) EditText et_new_password1;
    @BindView(R.id.et_new_password2) EditText et_new_password2;
    @BindView(R.id.et_old_password) EditText et_old_password;
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.tv_submit_password) TextView tv_submit_password;

    private UserService userService;
    private SharedPreferences shared;

    @Override
    public int getByLayoutId() {
        return R.layout.layout_update_password;
    }

    public void init(){

        userService = new UserServiceImpl();
        shared = getSharedPreferences(Constant.SHARED_NAME, Context.MODE_PRIVATE);

        tv_title.setText("修改密码");
        tv_submit_password.setBackgroundResource(R.drawable.shape_disable_button);

        onEditChangeListener();
    }

    @OnClick({R.id.tv_submit_password, R.id.iv_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_submit_password:

                if (validate()){
                submitPassword();
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    public void submitPassword(){
        Map<String, String> param = new HashMap<>();
        param.put(ResponseKey.TOKEN, shared.getString(Constant.SHARED.TOKEN, ""));
        param.put(ResponseKey.NEW_PSD, et_new_password1.getText().toString());
        param.put(ResponseKey.OLD_PSD, et_old_password.getText().toString());

        userService.updateUserPassword(this, param, new HttpResponseListener() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                Toast.makeText(UpdatePsdActivity.this, result.get(ResponseKey.MESSAGE).toString(), Toast.LENGTH_SHORT).show();
                if ((int)result.get(ResponseKey.CODE) == 1001){
                    finish();
                }
            }

            @Override
            public void onFailure() {

            }
        });

    }

    /**
     * 验证参数
     * @return
     */
    public boolean validate(){

        String oldPsd = et_old_password.getText().toString();
        String psd1 = et_new_password1.getText().toString();
        String psd2 = et_new_password2.getText().toString();
        //验证旧的密码
        if(ValidateParam.validateParamIsNull(oldPsd)){
            Toast.makeText(this, "旧密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            if (oldPsd.length() < 6){
                Toast.makeText(this, "旧密码长度不能小于6位", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        //验证新密码
        if (ValidateParam.validateParamIsNull(psd1, psd2)){
            Toast.makeText(this, "新密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }else{

            if (!psd1.equals(psd2)){
                Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (psd1.length()<6){
                Toast.makeText(this, "新密码长度不能小于6位", Toast.LENGTH_SHORT).show();
                return false;
            }

        }

        return true;
    }


    public void onEditChangeListener(){
        et_old_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String s = editable.toString();

                if (s.length()<6){
                    tv_submit_password.setBackgroundResource(R.drawable.shape_disable_button);
                    tv_submit_password.setClickable(false);
                }else{
                    tv_submit_password.setBackgroundResource(R.drawable.shape_button);
                    tv_submit_password.setClickable(true);
                }
            }
        });
    }
}
