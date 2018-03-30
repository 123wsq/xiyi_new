package com.example.wsq.android.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.wsq.android.R;
import com.example.wsq.android.activity.AboutWeActivity;
import com.example.wsq.android.activity.KnowledgeActivity;
import com.example.wsq.android.activity.cash.BalanceActivity;
import com.example.wsq.android.activity.cash.BankActivity;
import com.example.wsq.android.activity.cash.BillDetailsActivity;
import com.example.wsq.android.activity.cash.ReceiptsActivity;
import com.example.wsq.android.activity.order.DeviceWarrantyActivity;
import com.example.wsq.android.activity.order.MessageActivity;
import com.example.wsq.android.activity.order.OrderActivity;
import com.example.wsq.android.activity.share.ShareRecordActivity;
import com.example.wsq.android.activity.user.CollectActivity;
import com.example.wsq.android.activity.user.IntegralActivity;
import com.example.wsq.android.activity.user.LoginActivity;
import com.example.wsq.android.activity.user.SettingActivity;
import com.example.wsq.android.activity.user.SignActivity;
import com.example.wsq.android.activity.user.UpdatePsdActivity;
import com.example.wsq.android.activity.user.UserInfoActivity;
import com.example.wsq.android.constant.Constant;
import com.example.wsq.android.constant.ResponseKey;
import com.example.wsq.android.constant.Urls;
import com.example.wsq.android.inter.HttpResponseCallBack;
import com.example.wsq.android.inter.HttpResponseListener;
import com.example.wsq.android.inter.OnDialogClickListener;
import com.example.wsq.android.inter.PopupItemListener;
import com.example.wsq.android.service.OrderTaskService;
import com.example.wsq.android.service.UserService;
import com.example.wsq.android.service.impl.OrderTaskServiceImpl;
import com.example.wsq.android.service.impl.UserServiceImpl;
import com.example.wsq.android.tools.AppImageLoad;
import com.example.wsq.android.tools.AppImageView;
import com.example.wsq.android.tools.JGIM;
import com.example.wsq.android.tools.ShowDialog;
import com.example.wsq.android.utils.AmountUtils;
import com.example.wsq.android.utils.IntentFormat;
import com.example.wsq.android.utils.ToastUtis;
import com.example.wsq.android.utils.ValidateParam;
import com.example.wsq.android.view.CustomDefaultDialog;
import com.example.wsq.android.view.CustomPopup;
import com.example.wsq.android.view.RoundImageView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

/**
 * Created by wsq on 2017/12/7.
 */

public class UserFragment extends Fragment {


    @BindView(R.id.tv_username) TextView tv_username;
    @BindView(R.id.tv_sex) TextView tv_sex;
    @BindView(R.id.tv_role) TextView tv_role;
    @BindView(R.id.ll_user_background) LinearLayout ll_user_background;
    @BindView(R.id.ll_company_order) LinearLayout ll_company_order;
    @BindView(R.id.ll_server_order) LinearLayout ll_server_order;
    @BindView(R.id.ll_manager) LinearLayout ll_manager;
    @BindView(R.id.ll_device_server) LinearLayout ll_device_server;
    @BindView(R.id.ll_fee) LinearLayout ll_fee;
    @BindView(R.id.roundImage_header) RoundImageView roundImage_header;
    @BindView(R.id.tv_uncheck) TextView tv_uncheck;
    @BindView(R.id.tv_hascheck) TextView tv_hascheck;
    @BindView(R.id.tv_processed) TextView tv_processed;
    @BindView(R.id.tv_done) TextView tv_done;
    @BindView(R.id.tv_assigned) TextView tv_assigned;
    @BindView(R.id.tv_server_processed) TextView tv_server_processed;
    @BindView(R.id.tv_feedback) TextView tv_feedback;
    @BindView(R.id.tv_server_done) TextView tv_server_done;
    @BindView(R.id.tv_pay_num) TextView tv_pay_num;
    @BindView(R.id.tv_money) TextView tv_money;
    @BindView(R.id.tv_money_amount) TextView tv_money_amount;
    @BindView(R.id.ll_iv_integral) LinearLayout ll_iv_integral;
    @BindView(R.id.ll_sign_integral) LinearLayout ll_sign_integral;
    @BindView(R.id.tv_integral) TextView tv_integral;
    @BindView(R.id.tv_quit) TextView tv_quit;
    @BindView(R.id.view_point) View view_point;
    @BindView(R.id.ll_integral_shopping) LinearLayout ll_integral_shopping;


    public static final String FLAG_ORDER_KEY = "flag_order";
    private Map<String, Object> orderMap;
    private UserService userService;
    private OrderTaskService orderTaskService;

    private SharedPreferences shared;
    private CustomPopup popup;

    private final int httpSec = 1; //获取用户信息
    public static Map<String, Object> mUserData; //用户信息
    private CustomDefaultDialog defaultDialog;

    public static UserFragment getInstance(){
        return new UserFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.layout_fragment_user, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        initView();
        setIcon();
    }

    @Override
    public void onResume() {
        super.onResume();

        getUserInfo();
        getOrderNum();
    }

    public void init() {

        mUserData = new HashMap<>();
        userService = new UserServiceImpl();
        orderTaskService = new OrderTaskServiceImpl();
        orderMap = new HashMap<>();
        shared = getActivity().getSharedPreferences(Constant.SHARED_NAME, Context.MODE_PRIVATE);

        String sRole = shared.getString(Constant.SHARED.JUESE,"0");

        ll_sign_integral.setVisibility(sRole.equals("1") ? View.VISIBLE : View.GONE);
        ll_integral_shopping.setVisibility(sRole.equals("1") ? View.VISIBLE: View.GONE);
        view_point.setVisibility(shared.getBoolean(Constant.SHARED.MESSAGE, false) ? View.VISIBLE : View.GONE);
    }



    public void initView() {

        /**
         * 显示相关角色下面的订单菜单
         */
        if (shared.getString(Constant.SHARED.JUESE, "").equals("1")){
            ll_company_order.setVisibility(View.GONE);
            ll_server_order.setVisibility(View.VISIBLE);
            ll_manager.setVisibility(View.GONE);
            ll_device_server.setVisibility(View.VISIBLE);
            ll_fee.setVisibility(View.VISIBLE);
        }else{
            ll_company_order.setVisibility(View.VISIBLE);
            ll_server_order.setVisibility(View.GONE);
            ll_manager.setVisibility(View.VISIBLE);
            ll_device_server.setVisibility(View.GONE);
            ll_fee.setVisibility(View.GONE);
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case httpSec:
                    Map<String, Object> result = (Map<String, Object>) msg.obj;
                    tv_username.setText(result.get(ResponseKey.USERNAME)+"");

                    //设置头像
//                    Glide.with(getActivity()).load(Urls.HOST+result.get(ResponseKey.USER_PIC)).into(roundImage_header);
                    RequestOptions options = new RequestOptions();
                    options.error(R.drawable.image_header_bg);
                    options.fallback(R.drawable.image_header_bg);
                    options.placeholder(R.drawable.image_header_bg);
                    if (!TextUtils.isEmpty(result.get(ResponseKey.USER_PIC)+"")) {
                        try{
                        Glide.with(getActivity())
                                .load(Urls.HOST + result.get(ResponseKey.USER_PIC))
                                .apply(options)
                                .into(roundImage_header);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    //判断性别是否为空
                    String strSex = result.get(ResponseKey.SEX)+"";
                    if(!ValidateParam.validateParamIsNull(strSex)){
                        int sex = Integer.parseInt(strSex);
                        tv_sex.setText(Constant.SEX[sex-1]+"!");
                    }


                    //判断角色是否为空
                    String strRole = result.get(ResponseKey.JUESE)+"";
                    if (!ValidateParam.validateParamIsNull(strRole)){
                        int role = Integer.parseInt(strRole);
                        tv_role.setText(Constant.ROLE[role-1]);
                    }
                    //设置积分
                    tv_integral.setText("积分： "+result.get(ResponseKey.MEMBER_POINTS)+" 分");
                    //账单
                    tv_pay_num.setText(AmountUtils.changeY2Y(result.get(ResponseKey.CACH_COUNT)+""));
                    tv_money.setText(AmountUtils.changeY2Y(result.get(ResponseKey.MONEY)+""));
                    tv_money_amount.setText(AmountUtils.changeY2Y(result.get(ResponseKey.MONEY_AMOUNT)+""));
                    break;
            }
        }
    };


    @OnClick({R.id.tv_quit, R.id.ll_user_layout, R.id.ll_order_unfinish, R.id.ll_order_handler,
            R.id.ll_order_finish, R.id.ll_order, R.id.ll_order_fllocation, R.id.ll_order_progress,
            R.id.ll_order_feedback,R.id.ll_server_finish, R.id.ll_device_assert, R.id.ll_device_report,
            R.id.ll_device_bank_code, R.id.ll_device_server_share, R.id.ll_server_call,
            R.id.ll_password, R.id.ll_about, R.id.ll_fault, R.id.ll_collect,R.id.ll_manager_shared,
            R.id.ll_manager_upload, R.id.ll_device_knowledge, R.id.ll_balance, R.id.ll_pay_Record,
            R.id.tv_sign, R.id.ll_iv_integral, R.id.roundImage_header, R.id.ll_receipts, R.id.iv_message,
            R.id.iv_setting, R.id.ll_integral_shopping})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_setting:
                IntentFormat.startActivity(getActivity(), SettingActivity.class);
                break;
            case R.id.iv_message:
                IntentFormat.startActivity(getActivity(), MessageActivity.class);
                break;
            case R.id.roundImage_header: //点击头像查看
                //Urls.HOST+result.get(ResponseKey.USER_PIC)
                List<LocalMedia> listMedia = new ArrayList<>();
                LocalMedia media = new LocalMedia();
                if (TextUtils.isEmpty(mUserData.get(ResponseKey.USER_PIC)+"")){
                    ToastUtis.onToast("请先上传头像");
                    return;
                }
                media.setPath(Urls.HOST+mUserData.get(ResponseKey.USER_PIC));
                listMedia.add(media);
                PictureSelector.create(getActivity()).externalPicturePreview(0, listMedia);
                break;
            case R.id.tv_quit:

                CustomDefaultDialog.Builder builderExit = new CustomDefaultDialog.Builder(getActivity());
                builderExit.setTitle("提示");
                builderExit.setMessage("您确定退出该应用吗？");
                builderExit.setOkBtn("退出", new OnDialogClickListener() {
                    @Override
                    public void onClick(CustomDefaultDialog dialog, String result) {
                        JGIM.JGIM_logout();
                        IntentFormat.startActivity(getActivity(), LoginActivity.class);
                        dialog.dismiss();

                        getActivity().finish();
                    }
                });
                builderExit.setCancelBtn("还想玩", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderExit.create().show();

                break;

            case R.id.ll_user_layout:
                IntentFormat.startActivity(getActivity(), UserInfoActivity.class);
                break;
            case R.id.ll_order_unfinish:
                orderMap.put(FLAG_ORDER_KEY, 1);
                IntentFormat.startActivity(getActivity(), OrderActivity.class, orderMap);
                break;
            case R.id.ll_order_handler:
                orderMap.put(FLAG_ORDER_KEY, 3);
                IntentFormat.startActivity(getActivity(), OrderActivity.class, orderMap);
                break;
            case R.id.ll_order_finish:
                orderMap.put(FLAG_ORDER_KEY, 2);
                IntentFormat.startActivity(getActivity(), OrderActivity.class, orderMap);
                break;
            case R.id.ll_order:
                orderMap.put(FLAG_ORDER_KEY, 4);
                IntentFormat.startActivity(getActivity(), OrderActivity.class, orderMap);
                break;
            case R.id.ll_order_fllocation:
                orderMap.put(FLAG_ORDER_KEY, 5);
                IntentFormat.startActivity(getActivity(), OrderActivity.class, orderMap);
                break;
            case R.id.ll_order_progress:
                orderMap.put(FLAG_ORDER_KEY, 6);
                IntentFormat.startActivity(getActivity(), OrderActivity.class, orderMap);
                break;
            case R.id.ll_order_feedback:
                orderMap.put(FLAG_ORDER_KEY, 7);
                IntentFormat.startActivity(getActivity(), OrderActivity.class, orderMap);
                break;
            case R.id.ll_server_finish:
                orderMap.put(FLAG_ORDER_KEY, 8);
                IntentFormat.startActivity(getActivity(), OrderActivity.class, orderMap);
                break;
            case R.id.ll_receipts://收入
                IntentFormat.startActivity(getActivity(), ReceiptsActivity.class);
                break;
            case R.id.ll_balance:  //余额
                IntentFormat.startActivity(getActivity(), BalanceActivity.class);
                break;
            case R.id.ll_pay_Record: //提现
                IntentFormat.startActivity(getActivity(), BillDetailsActivity.class);
                break;
            case R.id.ll_device_assert:  //我要报修
                if (!shared.getString(Constant.SHARED.JUESE,"0").equals("1")){
                    IntentFormat.startActivity(getActivity(), DeviceWarrantyActivity.class);
                }else{
                    Toast.makeText(getActivity(), "您没有权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_fault:  //资料
                    Intent intent1 = new Intent();
                    intent1.setAction(Constant.ACTION.USER_PAGE_FAULT);
                    getActivity().sendBroadcast(intent1);
                break;
            case R.id.ll_manager_shared:   //企业 管理工程师分享

            case R.id.ll_manager_upload:  //设备上传
                CustomDefaultDialog.Builder builder = new CustomDefaultDialog.Builder(getActivity());
                builder.setTitle("提示");
                builder.setMessage("请至公司官网 "+getResources().getString(R.string.company_net)
                        +" 进行操作，谢谢合作");
                builder.setOkBtn("好的", new OnDialogClickListener() {
                    @Override
                    public void onClick(CustomDefaultDialog dialog, String result) {
                        dialog.dismiss();
                    }
                });
                defaultDialog = builder.create();
                defaultDialog.show();
                break;
            case R.id.ll_device_report:  //写报告
                orderMap.put(FLAG_ORDER_KEY, 7);
                IntentFormat.startActivity(getActivity(), OrderActivity.class, orderMap);
                break;
            case R.id.ll_device_bank_code:  //银行卡
                Map<String, Object> map = new HashMap<>();
                map.put(ResponseKey.BANK_CARD, mUserData.get(ResponseKey.BANK_CARD)+"");
                IntentFormat.startActivity(getActivity(), BankActivity.class, map);
                break;
            case R.id.ll_device_server_share:  //服务工程师   分享

                IntentFormat.startActivity(getActivity(), ShareRecordActivity.class);

                break;
            case R.id.ll_device_knowledge:  //圈内知识
                IntentFormat.startActivity(getActivity(), KnowledgeActivity.class);
                break;
            case R.id.ll_collect:  //我的收藏
                IntentFormat.startActivity(getActivity(), CollectActivity.class);
                break;
            case R.id.ll_server_call:  //客服中心
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_default_popup,null);
                TextView popup_title = view.findViewById(R.id.tv_title);
                popup_title.setText("联系电话");
                List<String> list = new ArrayList<>();
                list.add(getResources().getString(R.string.server_tel));
                popup = new CustomPopup(getActivity(), view, list, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    popup.dismiss();
                    }
                }, new PopupItemListener() {
                    @Override
                    public void onClickItemListener(int position, String result) {
                        onRequestPermission(result);
                    }
                });

                popup.setTextColor("#3F51B5");
                popup.showAtLocation(getActivity().findViewById(R.id.ll_layout), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.ll_password:  //修改密码

                IntentFormat.startActivity(getActivity(), UpdatePsdActivity.class);
                break;
            case R.id.ll_about:  //关于我们
                IntentFormat.startActivity(getActivity(), AboutWeActivity.class);
                break;
            case R.id.tv_sign:  //签到
                IntentFormat.startActivity(getActivity(), SignActivity.class);
                break;
            case R.id.ll_iv_integral:  //我的积分
                IntentFormat.startActivity(getActivity(), IntegralActivity.class);
                break;
            case R.id.ll_integral_shopping:
                ShowDialog.onShowDialog(getActivity(), "好的", "", "提示", "活动尚未开启，敬请期待！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }, null);
                break;

        }
    }

    /**
     * 设置标题信息
     */
    public void onSetShowPoint(){

        if (view_point != null) {
            view_point.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取用户信息
     */
    public void getUserInfo(){

        //获取用户信息
        Map<String, String> param = new HashMap<>();
        param.put(ResponseKey.TOKEN, shared.getString(Constant.SHARED.TOKEN, ""));

        userService.getUserInfo(getActivity(), param, new HttpResponseListener() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                mUserData.putAll(result);
                Message msg = new Message();
                msg.obj = result;
                msg.what = httpSec;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure() {

            }
        });


    }

    public void getOrderNum(){

        Map<String, String> param = new HashMap<>();
        param.put(ResponseKey.TOKEN, shared.getString(Constant.SHARED.TOKEN, ""));
        param.put(ResponseKey.CAT, shared.getString(Constant.SHARED.JUESE,"0"));

        orderTaskService.onGetOrderCount(getActivity(), param, new HttpResponseListener() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                if (!shared.getString(Constant.SHARED.JUESE,"0").equals("1")){

                    if (result.get(ResponseKey.UNCHECK).toString().equals("0")){
                        tv_uncheck.setVisibility(View.GONE);
                    }else{
                        tv_uncheck.setVisibility(View.VISIBLE);
                        tv_uncheck.setText(result.get(ResponseKey.UNCHECK).toString());
                    }

                    if (result.get(ResponseKey.HASCHECK).toString().equals("0")){
                        tv_hascheck.setVisibility(View.GONE);
                    }else{
                        tv_hascheck.setVisibility(View.VISIBLE);
                        tv_hascheck.setText(result.get(ResponseKey.HASCHECK).toString());
                    }

                    if (result.get(ResponseKey.PROCESSED).toString().equals("0")){
                        tv_processed.setVisibility(View.GONE);
                    }else{
                        tv_processed.setVisibility(View.VISIBLE);
                        tv_processed.setText(result.get(ResponseKey.PROCESSED).toString());
                    }
                    if (result.get(ResponseKey.DONE).toString().equals("0")){
                        tv_done.setVisibility(View.GONE);
                    }else{
                        tv_done.setVisibility(View.VISIBLE);
                        tv_done.setText(result.get(ResponseKey.DONE).toString());
                    }
                }else{

                    if (result.get(ResponseKey.ASSIGNED).toString().equals("0")){
                        tv_assigned.setVisibility(View.GONE);
                    }else{
                        tv_assigned.setVisibility(View.VISIBLE);
                        tv_assigned.setText(result.get(ResponseKey.ASSIGNED).toString());
                    }

                    if (result.get(ResponseKey.PROCESSED).toString().equals("0")){
                        tv_server_processed.setVisibility(View.GONE);
                    }else{
                        tv_server_processed.setVisibility(View.VISIBLE);
                        tv_server_processed.setText(result.get(ResponseKey.PROCESSED).toString());
                    }

                    if (result.get(ResponseKey.FEEDBACK).toString().equals("0")){
                        tv_feedback.setVisibility(View.GONE);
                    }else{
                        tv_feedback.setVisibility(View.VISIBLE);
                        tv_feedback.setText(result.get(ResponseKey.FEEDBACK).toString());
                    }
                    if (result.get(ResponseKey.DONE).toString().equals("0")){
                        tv_server_done.setVisibility(View.GONE);
                    }else{
                        tv_server_done.setVisibility(View.VISIBLE);
                        tv_server_done.setText(result.get(ResponseKey.DONE).toString());
                    }

                }
            }

            @Override
            public void onFailure() {

            }
        });

    }

    public void setIcon(){
        SharedPreferences preferences = getActivity().getSharedPreferences(Constant.SHARED_FACE, Context.MODE_PRIVATE);
        String path = preferences.getString(Constant.SHARED.IMG_PATH,"");
        if (shared.getString(Constant.SHARED.JUESE, "").equals("1")) {
            //服务工程师订单
            ((ImageView) getActivity().findViewById(R.id.iv_device_report)).setImageResource(R.drawable.image_write);
            ((ImageView) getActivity().findViewById(R.id.iv_device_bank_code)).setImageResource(R.drawable.image_tab_bank);
            ((ImageView) getActivity().findViewById(R.id.iv_device_server_share)).setImageResource(R.drawable.image_tab_share);
            ((ImageView) getActivity().findViewById(R.id.iv_device_knowledge)).setImageResource(R.drawable.image_tab_knowledge);
            if (preferences.getInt(Constant.SHARED.TYPE, 0)==0){
                ((ImageView) getActivity().findViewById(R.id.iv_order_fllocation)).setImageResource(R.drawable.image_my_approved);
                ((ImageView) getActivity().findViewById(R.id.iv_order_progress)).setImageResource(R.drawable.image_clz_s);
                ((ImageView) getActivity().findViewById(R.id.iv_order_feedback)).setImageResource(R.drawable.image_feedback);
                ((ImageView) getActivity().findViewById(R.id.iv_server_finish)).setImageResource(R.drawable.image_tab_finish);


            }else {
                AppImageView.onImageView(getActivity(), (ImageView) getActivity().findViewById(R.id.iv_order_fllocation), path + "image_my_approved.png");
                AppImageView.onImageView(getActivity(), (ImageView) getActivity().findViewById(R.id.iv_order_progress), path + "image_clz_s.png");
                AppImageView.onImageView(getActivity(), (ImageView) getActivity().findViewById(R.id.iv_order_feedback), path + "image_feedback.png");
                AppImageView.onImageView(getActivity(), (ImageView) getActivity().findViewById(R.id.iv_server_finish), path + "image_tab_finish.png");

            }
        }else {
            //企业-管理工程师
            ((ImageView) getActivity().findViewById(R.id.iv_device_assert)).setImageResource(R.drawable.image_sbwh);
            ((ImageView) getActivity().findViewById(R.id.iv_fault)).setImageResource(R.drawable.image_material);
            ((ImageView) getActivity().findViewById(R.id.iv_manager_shared)).setImageResource(R.drawable.image_tab_share);
            ((ImageView) getActivity().findViewById(R.id.iv_manager_upload)).setImageResource(R.drawable.image_tab_repair);

            if (preferences.getInt(Constant.SHARED.TYPE, 0)==0){
                ((ImageView) getActivity().findViewById(R.id.iv_order_unfinish)).setImageResource(R.drawable.image_tab_untreated);
                ((ImageView) getActivity().findViewById(R.id.iv_order_finish)).setImageResource(R.drawable.image_tab_treated);
                ((ImageView) getActivity().findViewById(R.id.iv_order_handler)).setImageResource(R.drawable.image_clz);
                ((ImageView) getActivity().findViewById(R.id.iv_order)).setImageResource(R.drawable.image_tab_finish);

            }else {
                AppImageView.onImageView(getActivity(), (ImageView) getActivity().findViewById(R.id.iv_order_unfinish), path+ "image_tab_untreated.png");
                AppImageView.onImageView(getActivity(), (ImageView) getActivity().findViewById(R.id.iv_order_finish), path + "image_tab_treated.png");
                AppImageView.onImageView(getActivity(), (ImageView) getActivity().findViewById(R.id.iv_order_handler), path + "image_clz.png");
                AppImageView.onImageView(getActivity(), (ImageView) getActivity().findViewById(R.id.iv_order), path + "image_tab_finish.png");

            }
        }
        if (preferences.getInt(Constant.SHARED.TYPE, 0)==0){

            ((ImageView) getActivity().findViewById(R.id.iv_collect)).setImageResource(R.drawable.image_my_sc);
            ((ImageView) getActivity().findViewById(R.id.iv_server_call)).setImageResource(R.drawable.image_kf);
            ((ImageView) getActivity().findViewById(R.id.iv_password)).setImageResource(R.drawable.chang_password);
            ((ImageView) getActivity().findViewById(R.id.iv_about)).setImageResource(R.drawable.tab_about);
            ll_user_background.setBackgroundResource(R.drawable.image_default_user);
        }else {
            AppImageView.onImageView(getActivity(), (ImageView) getActivity().findViewById(R.id.iv_collect), path + "image_my_sc.png");
            AppImageView.onImageView(getActivity(), (ImageView) getActivity().findViewById(R.id.iv_server_call), path + "image_kf.png");
            AppImageView.onImageView(getActivity(), (ImageView) getActivity().findViewById(R.id.iv_password), path + "chang_password.png");
            AppImageView.onImageView(getActivity(), (ImageView) getActivity().findViewById(R.id.iv_about), path + "tab_about.png");
            //个人信息layout的背景色
            AppImageView.onLayoutBackgroundImage(getActivity(), ll_user_background, path + "image_default_user.png");
            AppImageView.onLayoutBackgroundImage(getActivity(), tv_quit, preferences.getString(Constant.SHARED.BACKGROUND, "#0AC8CF"));

        }

    }

    public void onRequestPermission(final String result){//READ_PHONE_STATE

        List<PermissionItem> permissions = new ArrayList<PermissionItem>();
        permissions.add(new PermissionItem(Manifest.permission.CALL_PHONE, "手机权限", R.drawable.permission_ic_phone));
        HiPermission.create(getActivity()).permissions(permissions).checkMutiPermission(new PermissionCallback() {
            @Override
            public void onClose() {
                Logger.d("用户关闭权限申请");
                ToastUtis.onToast("请在设置中打开通话权限");
            }

            @Override
            public void onFinish() {
                Logger.d("所有权限申请完成");
                Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+result));
                startActivity(intent);
                popup.dismiss();

            }

            @Override
            public void onDeny(String permission, int position) {

            }

            @Override
            public void onGuarantee(String permission, int position) {

            }
        });
    }
}
