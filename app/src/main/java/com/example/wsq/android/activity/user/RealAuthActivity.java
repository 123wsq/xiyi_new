package com.example.wsq.android.activity.user;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wsq.android.R;
import com.example.wsq.android.activity.order.DeviceWarrantyActivity;
import com.example.wsq.android.base.BaseActivity;
import com.example.wsq.android.constant.Constant;
import com.example.wsq.android.constant.ResponseKey;
import com.example.wsq.android.inter.HttpResponseListener;
import com.example.wsq.android.inter.PopupItemListener;
import com.example.wsq.android.service.UserService;
import com.example.wsq.android.service.impl.UserServiceImpl;
import com.example.wsq.android.utils.BitmapUtils;
import com.example.wsq.android.utils.DateUtil;
import com.example.wsq.android.utils.DensityUtil;
import com.example.wsq.android.utils.IdentityCardValidate;
import com.example.wsq.android.utils.ImageUtil;
import com.example.wsq.android.utils.ToastUtis;
import com.example.wsq.android.view.CustomPopup;
import com.example.wsq.android.view.LoddingDialog;
import com.example.wsq.plugin.okhttp.OkhttpUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.text.ParseException;
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
 * 实名认证
 * Created by Administrator on 2018/3/26 0026.
 */

public class RealAuthActivity extends BaseActivity{


    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.et_code) EditText et_code;
    @BindView(R.id.et_name) EditText et_name;
    @BindView(R.id.tv_code) TextView tv_code;
    @BindView(R.id.tv_name) TextView tv_name;
    @BindView(R.id.tv_name_p) TextView tv_name_p;
    @BindView(R.id.et_name_p) EditText et_name_p;
    @BindView(R.id.iv_image_1) ImageView iv_image_1;
    @BindView(R.id.iv_image_2) ImageView iv_image_2;
    @BindView(R.id.ll_layout) LinearLayout ll_layout;
    @BindView(R.id.ll_company_p) LinearLayout ll_company_p;

    private SharedPreferences shared;
    private String token;
    private String roleUser;
    private UserService userService;
    private LoddingDialog dialog;
    private CustomPopup popup;

    private final int AUTH_USER_IMAGE_1 = 1001;
    private final int AUTH_USER_IMAGE_2 = 1002;
    private final int AUTH_COMPANY_IMAGE = 1003;
    private String auth_image1, auth_image2;
    private int curAuth;

    @Override
    public int getByLayoutId() {
        return R.layout.layout_real_auth;
    }

    @Override
    public void init() {

        userService = new UserServiceImpl();
        dialog = new LoddingDialog(this);
        shared = getSharedPreferences(Constant.SHARED_NAME, Context.MODE_PRIVATE);
        token = shared.getString(Constant.SHARED.TOKEN, "");
        roleUser = shared.getString(Constant.SHARED.JUESE, "");

        tv_title.setText(roleUser.equals("1") ? "实名认证" :"企业认证");
        tv_name.setText(roleUser.equals("1") ? "真实姓名" : "公司名称");
        et_name.setHint(roleUser.equals("1") ? "请填写真实姓名" : "请填写真实名称");
        tv_code.setText(roleUser.equals("1") ? "证件号" : "证件号");
        et_code.setHint(roleUser.equals("1") ? "如证件号不满18位，请用X代替" : "请输入执照注册号");
        iv_image_2.setVisibility(roleUser.equals("1") ? View.VISIBLE : View.INVISIBLE);
        ll_company_p.setVisibility(roleUser.equals("1") ? View.GONE: View.VISIBLE);

    }

    @OnClick({R.id.iv_back, R.id.tv_finish_auth, R.id.iv_image_1 , R.id.iv_image_2})
    public void onClick(View view){

        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_finish_auth:
                onAuth();
                break;
            case R.id.iv_image_1:
                curAuth = roleUser.equals("1") ? AUTH_USER_IMAGE_1 : AUTH_COMPANY_IMAGE;
                onInitPopup();
                break;
            case R.id.iv_image_2:
                curAuth = AUTH_USER_IMAGE_2;
                onInitPopup();
                break;
        }
    }

    /**
     * 开始认证
     */
    private void onAuth(){

        if (roleUser.equals("1")){
            onRealAuth();
        }else {
            onCompanyAuth();
        }
    }

    /**
     * 开始实名认证
     */
    private void onRealAuth(){
        if (onValidateParam()){

            Map<String, String> param = new HashMap<>();
            param.put(ResponseKey.TOKEN, token);
            param.put(ResponseKey.SFZ, et_code.getText().toString().trim());
            param.put(ResponseKey.NAME, et_name.getText().toString().trim());
            userService.onRealAuth(this, param, new HttpResponseListener() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    ToastUtis.onToast(result.get(ResponseKey.MESSAGE)+"");
                    finish();
                }

                @Override
                public void onFailure() {

                }
            });
        }

    }

    /**
     * 公司认证
     */
    private void onCompanyAuth(){
        if (onValidateParam()){
            Map<String, String> param = new HashMap<>();
            param.put(ResponseKey.TOKEN, token);
            param.put(ResponseKey.COMPANY_NUM, et_code.getText().toString().trim());
            param.put(ResponseKey.COMPANY, et_name.getText().toString().trim());
            param.put(ResponseKey.USERNAME, et_name_p.getText().toString().trim());
            userService.onCompanyAuth(this, param, new HttpResponseListener() {
                @Override
                public void onSuccess(Map<String, Object> result) {

                    ToastUtis.onToast(result.get(ResponseKey.MESSAGE)+"");
                    finish();
                }

                @Override
                public void onFailure() {

                }
            });
        }
    }

    /**
     * 上传证件
     * @param path
     * @param type  0身份证正面 1反面 2营业执照
     */
    private void onUploadCard(String path, String type){

        List<Map<String, Object>> fileList = new ArrayList<>();
        Map<String, Object> images = new HashMap<>();
        images.put("fileType", OkhttpUtil.FILE_TYPE_IMAGE);
        File file = new File(path);
        Logger.d("icon的本地路径   " + path);
        images.put(ResponseKey.FILE, file);
        fileList.add(images);

        Map<String, String> param = new HashMap<>();
        param.put(ResponseKey.TYPE, type);
        param.put(ResponseKey.TOKEN, token);
        userService.onUploadCard(this, param, fileList, new HttpResponseListener() {
            @Override
            public void onSuccess(Map<String, Object> result) {

            }

            @Override
            public void onFailure() {

            }
        });


    }

    /**
     * 参数验证
     * @return
     */
    private boolean onValidateParam(){

        String name = et_name.getText().toString();
        if (TextUtils.isEmpty(name)){
            ToastUtis.onToast("名称不能为空");
            return false;
        }
        if (roleUser.equals("1")){
            //验证身份证
            String sfz = et_code.getText().toString();

            if (TextUtils.isEmpty(sfz)) {
                ToastUtis.onToast("身份证号码不能为空");
                return false;
            }

            try {
                if (!TextUtils.isEmpty(IdentityCardValidate.IDCard.IDCardValidate(sfz))) {
                    ToastUtis.onToast("请输入正确的身份证号码");
                    return false;
                }
            } catch (ParseException e) {
                ToastUtis.onToast("请输入正确的身份证号码");
                e.printStackTrace();
                return false;
            }
            if (TextUtils.isEmpty(auth_image1)){
                ToastUtis.onToast("请上传身份证正面照片");
                return false;
            }
            if (TextUtils.isEmpty(auth_image2)){
                ToastUtis.onToast("请上传身份证反面照片");
                return false;
            }
        }else {

            String namep = et_name_p.getText().toString();
            if (TextUtils.isEmpty(namep)){
                ToastUtis.onToast("上报人不能为空");
                return false;
            }
            String code = et_code.getText().toString();
            if (TextUtils.isEmpty(code)){
                ToastUtis.onToast("执照注册号不能为空");
                return false;
            }
            if (TextUtils.isEmpty(auth_image1)){
                ToastUtis.onToast("请上传营业执照照片");
                return false;
            }
        }


        return true;
    }

    private void onInitPopup(){
        View view = LayoutInflater.from(this).inflate(R.layout.layout_default_popup,null);
        List<String> list = new ArrayList<>();

        list.add("预览");
        list.add("重新拍摄");
        popup = new CustomPopup(this, view, list, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popup.dismiss();
            }
        }, new PopupItemListener() {
            @Override
            public void onClickItemListener(int position, String result) {
                switch (position){
                    case 0:
                        onPreviewImage();
                        break;
                    case 1:
                        onRequestPermission();
                        break;
                }
                popup.dismiss();

            }
        });
        popup.showAtLocation(ll_layout, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 图片预览
     */
    public void onPreviewImage(){
        List<LocalMedia> listImage = new ArrayList<>();
        LocalMedia media = new LocalMedia();
        if (!TextUtils.isEmpty(auth_image1)){
            media.setPath(auth_image1);
            listImage.add(media);
        }
        if (!TextUtils.isEmpty(auth_image2)){
            media.setPath(auth_image2);
            listImage.add(media);
        }

        int num = 0;
        if (listImage.size() ==0){
            return;
        }else if (listImage.size()==1){
            num = 0;
        }else if(listImage.size() == 2){
            if (curAuth == AUTH_USER_IMAGE_2){
                num = 1;
            }else {
                num = 0;
            }
        }
        PictureSelector.create(RealAuthActivity.this).externalPicturePreview(num, listImage);
    }

    /**
     * 权限申请
     */
    public void onRequestPermission(){//READ_PHONE_STATE

        List<PermissionItem> permissions = new ArrayList<PermissionItem>();
        permissions.add(new PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE, "手机权限", R.drawable.permission_ic_phone));
        permissions.add(new PermissionItem(Manifest.permission.CAMERA, "手机权限", R.drawable.permission_ic_phone));
        HiPermission.create(this).permissions(permissions).checkMutiPermission(new PermissionCallback() {
            @Override
            public void onClose() {
                Logger.d("用户关闭权限申请");
                finish();
            }

            @Override
            public void onFinish() {
//                Logger.d("所有权限申请完成");
               handler.sendMessage(new Message());
            }

            @Override
            public void onDeny(String permission, int position) {

            }

            @Override
            public void onGuarantee(String permission, int position) {

            }
        });
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            PictureSelector.create(RealAuthActivity.this)
                    .openCamera(PictureMimeType.ofImage())
                    .imageFormat(PictureMimeType.JPEG)
                    .compress(true)// 是否压缩 true or false
                    .minimumCompressSize(70)// 小于100kb的图片不压缩
                    .forResult(curAuth);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);

        switch (requestCode){
            case AUTH_USER_IMAGE_1:
                auth_image1 = onBitmapCompress(selectList.get(0).getCompressPath());
//                Bitmap bitmap = BitmapFactory.decodeFile(auth_image1);
                iv_image_1.setImageBitmap(BitmapUtils.getLocalImage(auth_image1));
                onUploadCard(auth_image1, "0");
                break;
            case AUTH_USER_IMAGE_2:
                auth_image2 = onBitmapCompress(selectList.get(0).getCompressPath());
                iv_image_2.setImageBitmap(BitmapUtils.getLocalImage(auth_image2));
                onUploadCard(auth_image1, "1");
                break;
            case AUTH_COMPANY_IMAGE:
                auth_image1 = onBitmapCompress(selectList.get(0).getCompressPath());
                iv_image_1.setImageBitmap(BitmapUtils.getLocalImage(auth_image1));
                onUploadCard(auth_image1, "2");
                break;
        }
        PictureFileUtils.deleteCacheDirFile(RealAuthActivity.this);

    }

    /**
     * 图片添加水印和质量压缩
     * @param path
     * @return
     */
    public  String onBitmapCompress(String path){
        //得到该路径下的图片bitmap
        Bitmap bitmap = BitmapUtils.getLocalImage(path);
        int textSize = DensityUtil.sp2px(this, 12);
//        Bitmap newBitmap = ImageUtil.drawTextToLeftBottom(RealAuthActivity.this, bitmap,
//                new String[]{ shared.getString(Constant.SHARED.LOCATION, ""), DateUtil.onDateFormat(DateUtil.DATA_FORMAT)},
//                textSize, Color.RED, textSize, textSize);

        //只对图片进行质量压缩
        Bitmap commBitmap = ImageUtil.compressImage(bitmap, 100);
        //对图片进行大小 质量压缩
//        Bitmap commBitmap = ImageUtil.zoomImage(newBitmap, ScreenUtils.getScreenWidth(this),ScreenUtils.getScreenHeight(this), 100);
        File file = BitmapUtils.saveImage(commBitmap);
//       String savePath = BitmapUtils.addBitmapWatermark(DeviceWarrantyActivity.this, path);
        return file.getAbsolutePath();
    }
}
