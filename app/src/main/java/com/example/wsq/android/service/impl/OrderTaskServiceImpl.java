package com.example.wsq.android.service.impl;

import android.content.Context;
import android.widget.Toast;

import com.example.wsq.android.activity.user.LoginActivity;
import com.example.wsq.android.constant.ResponseKey;
import com.example.wsq.android.constant.Urls;
import com.example.wsq.android.inter.HttpResponseCallBack;
import com.example.wsq.android.inter.HttpResponseListener;
import com.example.wsq.android.service.OrderTaskService;
import com.example.wsq.android.tools.OkHttpRequest;
import com.example.wsq.android.utils.IntentFormat;
import com.example.wsq.android.utils.ValidateParam;

import java.util.List;
import java.util.Map;

/**
 * Created by wsq on 2017/12/14.
 */

public class OrderTaskServiceImpl implements OrderTaskService {

    /**
     * 设备报修
     * @param params
     * @param callBack
     * @throws Exception
     */
    @Override
    public void onDeviceRepairs(final Context context, Map<String, String> params, List<Map<String, Object>> list,  final HttpResponseListener callBack){




        try {

            //验证必填参数
            ValidateParam.validateParam(params, ResponseKey.XINGHAO, ResponseKey.BIANHAO,
                    ResponseKey.DES, ResponseKey.TOKEN, ResponseKey.IMG_COUNT);

            OkHttpRequest.onPostUploadFile(Urls.TOREPAIR, params,list, new HttpResponseCallBack(){
                @Override
                public void callBack(Map<String, Object> result) {

                    callBack.onSuccess(result);
                }

                @Override
                public void onCallFail(String msg) {
                    onExitApp(context, msg);
                    callBack.onFailure();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            callBack.onFailure();
            Toast.makeText(context, "必要参数未填写", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取订单列表
     * @param params
     * @param callBack
     * @throws Exception
     */
    @Override
    public void onGetOrderList(final Context context, Map<String, String> params, final HttpResponseListener callBack)  {
        //验证必填参数
        String url = "";
        if (params.get(ResponseKey.JUESE).equals("1")){
            url = Urls.SERVER_ORDER;
        }else if (params.get(ResponseKey.JUESE).equals("2")){
            url = Urls.DEVICE_ORDER;
        }else if(params.get(ResponseKey.JUESE).equals("3")){
            url = Urls.MANAGER_ORDER_INFO;
        }

        try {

            ValidateParam.validateParam(params, ResponseKey.TOKEN, ResponseKey.PAGE,
                    ResponseKey.STATUS);

            OkHttpRequest.sendHttpPost(url, params, new HttpResponseCallBack(){
                @Override
                public void callBack(Map<String, Object> result) {

                    callBack.onSuccess(result);
                }

                @Override
                public void onCallFail(String msg) {
                    onExitApp(context, msg);
                    callBack.onFailure();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            callBack.onFailure();
            Toast.makeText(context, "必要参数未填写", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取订单详情
     * @param params
     * @param callBack
     * @throws Exception
     */
    @Override
    public void ongetOrderInfo(final Context context, Map<String, String> params, final HttpResponseListener callBack) {

        String url = "";

        String s = params.get(ResponseKey.JUESE);
        if (s.equals("1")){
            url = Urls.SERVER_ORDER_INFO;
        }else {
            url = Urls.GET_ORDER_INFO;
        }
        try {

            //参数验证
            ValidateParam.validateParam(params, ResponseKey.TOKEN, ResponseKey.ID);

            OkHttpRequest.sendHttpGet(url, params, new HttpResponseCallBack(){
                @Override
                public void callBack(Map<String, Object> result) {

                    callBack.onSuccess(result);
                }

                @Override
                public void onCallFail(String msg) {
                    onExitApp(context, msg);
                    callBack.onFailure();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            callBack.onFailure();
            Toast.makeText(context, "必要参数未填写", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 审核
     * @param params
     * @param callBack
     * @throws Exception
     */
    @Override
    public void onAudit(final Context context, Map<String, String> params, final HttpResponseListener callBack)  {

        try {

            //参数验证
            ValidateParam.validateParam(params, ResponseKey.TOKEN, ResponseKey.ID,
                    ResponseKey.ACTION);

            OkHttpRequest.sendHttpGet(Urls.AUDIT, params, new HttpResponseCallBack(){
                @Override
                public void callBack(Map<String, Object> result) {

                    callBack.onSuccess(result);
                }

                @Override
                public void onCallFail(String msg) {
                    onExitApp(context, msg);
                    callBack.onFailure();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            callBack.onFailure();
            Toast.makeText(context, "必要参数未填写", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 服务工程师订单状态
     * @param params
     * @param callBack
     * @throws Exception
     */
    @Override
    public void onOrderStatus(final Context context, Map<String, String> params, final HttpResponseListener callBack)  {


        try {

            //参数验证
            ValidateParam.validateParam(params, ResponseKey.TOKEN, ResponseKey.ID,
                    ResponseKey.ACTION);

            OkHttpRequest.sendHttpGet(Urls.SERVER_STATUS, params, new HttpResponseCallBack(){
                @Override
                public void callBack(Map<String, Object> result) {

                    callBack.onSuccess(result);
                }

                @Override
                public void onCallFail(String msg) {
                    onExitApp(context, msg);
                    callBack.onFailure();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            callBack.onFailure();
            Toast.makeText(context, "必要参数未填写", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 提交移交报告
     * @param params
     * @param list
     * @param callBack
     * @throws Exception
     */
    @Override
    public void onSubmitReport(final Context context, Map<String, String> params, List<Map<String, Object>> list, final HttpResponseListener callBack) {


        //验证必填参数
        try {
            ValidateParam.validateParam(params, ResponseKey.TOKEN, ResponseKey.ID,
                    ResponseKey.DIDIAN, ResponseKey.LXR, ResponseKey.TEL, ResponseKey.CONTENT,
                    ResponseKey.YILIU, ResponseKey.IMG_COUNT);

            OkHttpRequest.uploadPostFile(Urls.SUBMIT_REPORT, params, list, new HttpResponseCallBack() {
                @Override
                public void callBack(Map<String, Object> result) {
                    callBack.onSuccess(result);
                }

                @Override
                public void onCallFail(String msg) {
                    onExitApp(context, msg);
                    callBack.onFailure();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            callBack.onFailure();
            Toast.makeText(context, "必要参数未填写", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onGetProductInformation(final Context context, Map<String, String> param, final HttpResponseListener listener){



//        OkHttpRequest.sendGet(Urls.GET_PRODUCT, params, callBack);

        try {
            //参数验证
            ValidateParam.validateParam(param, ResponseKey.CAT, ResponseKey.PAGE);

            OkHttpRequest.sendHttpGet(Urls.GET_PRODUCT, param, new HttpResponseCallBack(){
                @Override
                public void callBack(Map<String, Object> result) {

                    listener.onSuccess(result);
                }

                @Override
                public void onCallFail(String msg) {
                    onExitApp(context, msg);
                    listener.onFailure();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            listener.onFailure();
            Toast.makeText(context, "必要参数未填写", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGetProductInfo(final Context context, Map<String, String> params, final HttpResponseListener callBack)  {

        try {

            //参数验证
            ValidateParam.validateParam(params, ResponseKey.ID, ResponseKey.TOKEN);

            OkHttpRequest.sendHttpGet(Urls.GET_DETAIL, params, new HttpResponseCallBack(){
                @Override
                public void callBack(Map<String, Object> result) {

                    callBack.onSuccess(result);
                }

                @Override
                public void onCallFail(String msg) {
                    onExitApp(context, msg);
                    callBack.onFailure();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            callBack.onFailure();
            Toast.makeText(context, "必要参数未填写", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取设备列表
     * @param params
     * @param callBack
     * @throws Exception
     */
    @Override
    public void onGetDeviceList(final Context context, Map<String, String> params, final HttpResponseListener callBack) {
        //参数验证
//        ValidateParam.validateParam(params, ResponseKey.ID, ResponseKey.TOKEN);

//        OkHttpRequest.sendGet(Urls.GET_DEVICE, params, callBack);

        try {
            OkHttpRequest.sendHttpPost(Urls.GET_DEVICE, params, new HttpResponseCallBack(){
                @Override
                public void callBack(Map<String, Object> result) {

                    callBack.onSuccess(result);
                }

                @Override
                public void onCallFail(String msg) {
                    onExitApp(context, msg);
                    callBack.onFailure();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            callBack.onFailure();
            Toast.makeText(context, "必要参数未填写", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取设备详情
     * @param params
     * @param callBack
     * @throws Exception
     */
    @Override
    public void onGetDeviceInfo(final  Context context, Map<String, String> params, final HttpResponseListener callBack){

        try {

            //参数验证
            ValidateParam.validateParam(params, ResponseKey.ID, ResponseKey.TOKEN);

            OkHttpRequest.sendHttpGet(Urls.GET_DEVICE_INFO, params, new HttpResponseCallBack(){
                @Override
                public void callBack(Map<String, Object> result) {

                    callBack.onSuccess(result);
                }

                @Override
                public void onCallFail(String msg) {
                    onExitApp(context, msg);
                    callBack.onFailure();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            callBack.onFailure();
            Toast.makeText(context, "必要参数未填写", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取新闻列表
     * @param params
     * @param callBack
     * @throws Exception
     */
    @Override
    public void onGetNewsList(final Context context, Map<String, String> params, final HttpResponseListener callBack){

        //参数验证

        try {

            ValidateParam.validateParam(params, ResponseKey.PAGE);

            OkHttpRequest.sendHttpGet(Urls.GET_NEWS, params, new HttpResponseCallBack(){
                @Override
                public void callBack(Map<String, Object> result) {

                    callBack.onSuccess(result);
                }

                @Override
                public void onCallFail(String msg) {
                    onExitApp(context, msg);
                    callBack.onFailure();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            callBack.onFailure();
            Toast.makeText(context, "必要参数未填写", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取圈内知识列表
     * @param params
     * @param callBack
     * @throws Exception
     */
    @Override
    public void onGetKnowledgeList(final Context context, Map<String, String> params, final HttpResponseListener callBack)  {

        try {

            //参数验证
            ValidateParam.validateParam(params, ResponseKey.PAGE);

            OkHttpRequest.sendHttpGet(Urls.KNOWLEDGE, params, new HttpResponseCallBack(){
                @Override
                public void callBack(Map<String, Object> result) {

                    callBack.onSuccess(result);
                }

                @Override
                public void onCallFail(String msg) {
                    onExitApp(context, msg);
                    callBack.onFailure();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            callBack.onFailure();
            Toast.makeText(context, "必要参数未填写", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSearchDeviceList(final Context context, Map<String, String> params, final HttpResponseListener callBack) {

        try {

            ValidateParam.validateParam(params, ResponseKey.PAGE, ResponseKey.KEYWORDS, ResponseKey.CAT);

            OkHttpRequest.sendHttpGet(Urls.SEARCH, params, new HttpResponseCallBack(){
                @Override
                public void callBack(Map<String, Object> result) {

                    callBack.onSuccess(result);
                }

                @Override
                public void onCallFail(String msg) {
                    onExitApp(context, msg);
                    callBack.onFailure();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {

            callBack.onFailure();
            Toast.makeText(context, "必要参数未填写", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取处理订单个数
     * @param params
     * @param callBack
     * @throws Exception
     */
    @Override
    public void onGetOrderCount(final  Context context, Map<String, String> params, final HttpResponseListener callBack) {

        try {

            //验证必填参数
            ValidateParam.validateParam(params, ResponseKey.TOKEN,  ResponseKey.CAT);

            OkHttpRequest.sendHttpGet(Urls.ORDER_COUNT, params, new HttpResponseCallBack(){
                @Override
                public void callBack(Map<String, Object> result) {

                    callBack.onSuccess(result);
                }

                @Override
                public void onCallFail(String msg) {
                    onExitApp(context, msg);
                    callBack.onFailure();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            callBack.onFailure();
            Toast.makeText(context, "必要参数未填写", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 修改订单列表
     * @param params
     * @param callBack
     * @throws Exception
     */
    @Override
    public void onUpdateOrder(final Context context, Map<String, String> params, List<Map<String, Object>> list, final HttpResponseListener callBack){


        try {

            //验证必填参数
            ValidateParam.validateParam(params, ResponseKey.XINGHAO, ResponseKey.BIANHAO,
                    ResponseKey.DES, ResponseKey.TOKEN, ResponseKey.IMG_COUNT);

            OkHttpRequest.onPostUploadFile(Urls.ORDER_UPDATE, params, list, new HttpResponseCallBack(){
                @Override
                public void callBack(Map<String, Object> result) {

                    callBack.onSuccess(result);
                }

                @Override
                public void onCallFail(String msg) {
                    onExitApp(context, msg);
                    callBack.onFailure();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            callBack.onFailure();
            Toast.makeText(context, "必要参数未填写", Toast.LENGTH_SHORT).show();
        }
    }

    public void onExitApp(Context context, String msg){
        if (msg.equals("2004")) {
            IntentFormat.startActivity(context, LoginActivity.class);
        }
    }
}
