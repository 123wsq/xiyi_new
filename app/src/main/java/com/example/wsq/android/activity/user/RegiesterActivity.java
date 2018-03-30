package com.example.wsq.android.activity.user;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wsq.android.R;
import com.example.wsq.android.activity.ProductInfoActivity;
import com.example.wsq.android.activity.ProtocolsActivity;
import com.example.wsq.android.base.BaseActivity;
import com.example.wsq.android.constant.Constant;
import com.example.wsq.android.constant.ResponseKey;
import com.example.wsq.android.inter.HttpResponseListener;
import com.example.wsq.android.inter.PopupItemListener;
import com.example.wsq.android.service.UserService;
import com.example.wsq.android.service.impl.UserServiceImpl;
import com.example.wsq.android.tools.RegisterParam;
import com.example.wsq.android.utils.IntentFormat;
import com.example.wsq.android.utils.SystemUtils;
import com.example.wsq.android.utils.ToastUtis;
import com.example.wsq.android.utils.ValidateDataFormat;
import com.example.wsq.android.utils.ValidateParam;
import com.example.wsq.android.view.CustomPopup;
import com.example.wsq.android.view.LoddingDialog;
import com.orhanobut.logger.Logger;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

/**
 * Created by wsq on 2017/12/11.
 */

public class RegiesterActivity extends BaseActivity{

    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.et_username) EditText et_username;
    @BindView(R.id.et_password1)EditText et_password1;
    @BindView(R.id.et_password2)EditText et_password2;
    @BindView(R.id.et_tel) EditText et_tel;
    @BindView(R.id.tv_juese) TextView tv_juese;
    @BindView(R.id.et_validateCode) EditText et_validateCode;
    @BindView(R.id.tv_getCode) TextView tv_getCode;
    @BindView(R.id.ll_layout) LinearLayout ll_layout;
    @BindView(R.id.cb_checkBox) CheckBox cb_checkBox;
    @BindView(R.id.btn_register) Button btn_register;

    private int curLen = 60;
    private UserService userService;
    private LoddingDialog dialog;
    private CustomPopup popup;
    private int isService = 1;
    private String point = "0";

    @Override
    public int getByLayoutId() {
        return R.layout.layout_register;
    }


    public void init() {

        tv_title.setText("会员注册");
        userService = new UserServiceImpl();
        dialog = new LoddingDialog(this);
        onTextChange();

        onRequestPermission();
    }

    @OnClick({R.id.btn_register, R.id.iv_back, R.id.tv_getCode,R.id.tv_juese, R.id.tv_protocols})
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_register:
                onRegister();

                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_getCode:
                onGetValidateCode();
                break;
            case R.id.tv_juese:
                View view1 = LayoutInflater.from(RegiesterActivity.this).inflate(R.layout.layout_default_popup,null);
                List<String> list1 = new ArrayList<>();

                list1.add("服务工程师");
                list1.add("企业工程师");

                popup = new CustomPopup(RegiesterActivity.this, view1, list1, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.dismiss();
                    }
                }, new PopupItemListener(){
                    @Override
                    public void onClickItemListener(int position, String result) {
                        tv_juese.setText(result);
                        isService = position+1;
                        popup.dismiss();
                    }
                });
                popup.setTitle("角色");
                popup.showAtLocation(ll_layout, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.tv_protocols:
                startActivity(new Intent(RegiesterActivity.this,  ProtocolsActivity.class));
                break;
        }
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

    /**
     * 获取验证码
     */
    public void onGetValidateCode(){
        tv_getCode.setClickable(false);
        tv_getCode.setBackgroundResource(R.drawable.shape_disable_button);
        Map<String, String> param = new HashMap<>();
        if (!onValidate(0)){
            tv_getCode.setBackgroundResource(R.drawable.shape_button);
            tv_getCode.setClickable(true);
            return;
        }
        param.put(ResponseKey.JUESE, isService+"");
        param.put(ResponseKey.TEL, et_tel.getText().toString().trim());
        userService.getValidateRegisterCode(this, param, new HttpResponseListener() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                ToastUtis.onToast(result.get(ResponseKey.MESSAGE)+"");
                handler.postDelayed(runnable, 1000);
            }

            @Override
            public void onFailure() {
                tv_getCode.setClickable(true);
                tv_getCode.setBackgroundResource(R.drawable.shape_button);
            }
        });
    }

    /**
     * 注册
     */
    public void onRegister(){
        Map<String, String> param = new HashMap<>();
        if (!onValidate(1)){
            return;
        }
        param.put(ResponseKey.USERNAME, et_username.getText().toString().trim());
        param.put(ResponseKey.PASSWORD, et_password1.getText().toString().trim());
        param.put(ResponseKey.JUESE, isService+"");
        param.put(ResponseKey.TEL, et_tel.getText().toString().trim());
        param.put(ResponseKey.YZM, et_validateCode.getText().toString().trim());
        param.put(ResponseKey.DEVICE_TYPE, "1"); //设备类型   1 android 2 ios
        param.put(ResponseKey.DEVICE_ID, SystemUtils.getIMEI(this));
        param.put(ResponseKey.POINT, point+"");
        userService.register(this, param, new HttpResponseListener() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                IntentFormat.startActivity(RegiesterActivity.this,  LoginActivity.class);
                finish();
                if(dialog.isShowing())dialog.dismiss();
            }

            @Override
            public void onFailure() {
                if(dialog.isShowing())dialog.dismiss();
            }
        });
    }

    /**
     * 验证手机  获取积分
     */
    public void onValidatePhone(){

        Map<String, String> param = new HashMap<>();
        param.put(ResponseKey.DEVICE_TYPE, "1");
        param.put(ResponseKey.DEVICE_ID, SystemUtils.getIMEI(this));

        userService.validatePhone(this, param, new HttpResponseListener() {
            @Override
            public void onSuccess(Map<String, Object> result) {

                point = result.get(ResponseKey.POINT)+"";
            }

            @Override
            public void onFailure() {

            }
        });
    }

    public void onRequestPermission(){//READ_PHONE_STATE

        List<PermissionItem> permissions = new ArrayList<PermissionItem>();
        permissions.add(new PermissionItem(Manifest.permission.READ_PHONE_STATE, "手机权限", R.drawable.permission_ic_phone));
        HiPermission.create(this).permissions(permissions).checkMutiPermission(new PermissionCallback() {
            @Override
            public void onClose() {
                Logger.d("用户关闭权限申请");
                finish();
            }

            @Override
            public void onFinish() {
                Logger.d("所有权限申请完成");
                onValidatePhone();

            }

            @Override
            public void onDeny(String permission, int position) {

            }

            @Override
            public void onGuarantee(String permission, int position) {

            }
        });
    }

    /**
     * 文本输入监听
     */
    public void onTextChange(){

        cb_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    btn_register.setBackgroundResource(R.drawable.shape_button);
                    btn_register.setClickable(true);
                }else{
                    btn_register.setBackgroundResource(R.drawable.shape_disable_button);
                    btn_register.setClickable(false);
                }
            }
        });

        et_tel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length()==11){
                    tv_getCode.setBackgroundResource(R.drawable.shape_button);
                    tv_getCode.setClickable(true);
                }else{
                    tv_getCode.setBackgroundResource(R.drawable.shape_disable_button);
                    tv_getCode.setClickable(false);
                }
            }
        });
    }

    /**
     * 参数验证
     * @param flag 0 获取验证码  1 注册
     * @return
     */
    public boolean onValidate(int flag){


        String telphone = et_tel.getText().toString();
        if (TextUtils.isEmpty(telphone)){
            ToastUtis.onToast("请输入手机号码");
            return false;
        }

        if(!ValidateDataFormat.isMobile(telphone)){
            ToastUtis.onToast( "输入的手机号有误");
            return false;
        }

        //以下之后再注册的时候验证
        if (flag == 1){

            String userName = et_username.getText().toString();
            if (userName.length() < 6 && userName.length() > 18){
                ToastUtis.onToast("请输入6-18位的用户名");
                return false;
            }
            if (!userName.matches("[A-Za-z0-9_]+")){
                ToastUtis.onToast("用户名只能为数字，字母，下划线");
                return  false;
            }
            //验证密码是否一致
            String password1 = et_password1.getText().toString();
            String password2 = et_password2.getText().toString();
            //检验密码是否为空
            if (ValidateParam.validateParamIsNull(password1, password2)){
                ToastUtis.onToast("密码不能为空");
                return false;
            }
            //检验密码是否两次输入一致
            if (!password1.equals(password2)){
                ToastUtis.onToast("两次密码不一致");
                return false;
            }
            //检验密码长度
            if(password1.length() > 16 && password1.length() < 6){
                ToastUtis.onToast("请输入6-16位的密码");
                return false;
            }

            String validateCode = et_validateCode.getText().toString();
            if (TextUtils.isEmpty(validateCode)){
                ToastUtis.onToast( "验证码不能为空");
                return false;
            }
            if (validateCode.trim().length() != Constant.CODE_LENGTH){
                ToastUtis.onToast( "验证码必须为"+Constant.CODE_LENGTH+"位");
                return false;
            }
        }


        return  true;
    }
}
