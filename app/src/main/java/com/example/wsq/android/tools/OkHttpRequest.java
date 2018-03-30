package com.example.wsq.android.tools;

import android.util.Log;

import com.example.wsq.android.constant.Constant;
import com.example.wsq.android.constant.ResponseKey;
import com.example.wsq.android.constant.Urls;
import com.example.wsq.android.inter.HttpResponseCallBack;
import com.example.wsq.android.inter.OnDownloadListener;
import com.example.wsq.android.utils.FileUtil;
import com.example.wsq.android.utils.MD5Util;
import com.example.wsq.android.utils.ParamFormat;
import com.example.wsq.android.utils.ToastUtis;
import com.example.wsq.android.utils.UnicodeUtil;
import com.example.wsq.plugin.okhttp.CallBackUtil;
import com.example.wsq.plugin.okhttp.OkhttpUtil;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by wsq on 2017/12/12.
 */

public class OkHttpRequest {
    private static final String TAG = "OkHttpRequest";

    /**
     * 简单请求
     * @param url
     * @param params
     * @param callBack
     */
    public static void sendPost(String url, Map<String, String> params, final HttpResponseCallBack callBack){


        long timeMillis = System.currentTimeMillis();
        params.put("timestamp", timeMillis+"");
        String sign = MD5Util.encrypt(Constant.SECRET+timeMillis+Constant.SECRET);
        params.put("sign", sign);

        String path = Urls.HOST + url;
        Logger.d("url="+path +"\nparam="+params.toString());
        OkhttpUtil.okHttpPost(path, params, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response) {

                String result = UnicodeUtil.unicodeToString(response);
                Logger.json(result);
                try {

                    Map<String, Object> map = ParamFormat.onJsonToMap(result);

                    if (map.containsKey(ResponseKey.DATA)){

                        if (map.get(ResponseKey.DATA).toString().startsWith("{")) {
                            map.put(ResponseKey.DATA, ParamFormat.onJsonToMap(map.get(ResponseKey.DATA).toString()));

                        }else if(map.get(ResponseKey.DATA).toString().startsWith("[")){
                            JSONArray jsona = new JSONArray(map.get(ResponseKey.DATA).toString());
                            List<Map<String, Object>> list = new ArrayList<>();

                            for (int i = 0 ; i< jsona.length(); i ++){
                                list.add(ParamFormat.onJsonToMap(jsona.getJSONObject(i).toString()));
                            }
                            map.put(ResponseKey.DATA, list);
                        }else{
                            Log.e(TAG, "ERROR = "+"未知数据格式");

                        }

                    }

                    if(!map.containsKey(ResponseKey.CODE)){
                        callBack.callBack(map);
                    }else {
                        if ((int) map.get(ResponseKey.CODE) == 1001) {//成功

                            callBack.callBack(map);

                        } else if((int)map.get(ResponseKey.CODE) == 1000){ //用户名或密码错误
                            map.putAll(ParamFormat.onJsonToMap(result));
                            callBack.callBack(map);
                        }else {
                            callBack.onCallFail(map.get(ResponseKey.MESSAGE).toString());
                        }
                    }
                } catch (Exception e) {
                    callBack.onCallFail("数据解析异常");
                    e.printStackTrace();
                }

            }
        });
    }


    public static void sendGet(String url, Map<String, String> params, final HttpResponseCallBack callBack){


        long timeMillis = System.currentTimeMillis();
        params.put("timestamp", timeMillis+"");
        String sign = MD5Util.encrypt(Constant.SECRET+timeMillis+Constant.SECRET);
        params.put("sign", sign);

        String path = Urls.HOST + url;
        Logger.d("url="+path +"\nparam="+params.toString());
        OkhttpUtil.okHttpGet(path, params, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response) {

                String result = UnicodeUtil.unicodeToString(response);

                Logger.json(result);
                try {

                    Map<String, Object> map = ParamFormat.onJsonToMap(result);

                    if (map.containsKey(ResponseKey.DATA)){

                        if (map.get(ResponseKey.DATA).toString().startsWith("{")) {
                            map.put(ResponseKey.DATA, ParamFormat.onJsonToMap(map.get(ResponseKey.DATA).toString()));

                        }else if(map.get(ResponseKey.DATA).toString().startsWith("[")){
                            JSONArray jsona = new JSONArray(map.get(ResponseKey.DATA).toString());
                            List<Map<String, Object>> list = new ArrayList<>();

                            for (int i = 0 ; i< jsona.length(); i ++){
                                list.add(ParamFormat.onJsonToMap(jsona.getJSONObject(i).toString()));
                            }
                            map.put(ResponseKey.DATA, list);
                        }else{
                            callBack.onCallFail("未知数据格式");
                        }

                    }


                    if(!map.containsKey(ResponseKey.CODE)){
                        callBack.callBack(map);
                    }else {
                        if ((int) map.get(ResponseKey.CODE) == 1001) {//成功

                            callBack.callBack(map);

                        } else {
                            callBack.onCallFail(map.get(ResponseKey.MESSAGE).toString());
                        }
                    }
                } catch (Exception e) {
                    callBack.onCallFail("数据解析异常");
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     *
     * @param url
     * @param params
     * @param callBack
     */
    public static void uploadPostFile(String url, Map<String, String> params, List<Map<String, Object>> list,  final HttpResponseCallBack callBack){


        long timeMillis = System.currentTimeMillis();
        params.put("timestamp", timeMillis+"");
        String sign = MD5Util.encrypt(Constant.SECRET+timeMillis+Constant.SECRET);
        params.put("sign", sign);

        String path = Urls.HOST + url;
        Logger.d("url = "+path+"\nparam = "+params.toString());

        OkhttpUtil.okHttpFilePost(path, params, list, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                e.printStackTrace();
                callBack.onCallFail("请求失败");
            }

            @Override
            public void onResponse(String response) {

                String result = UnicodeUtil.unicodeToString(response);
                Logger.d(response);
//                Logger.json(result);
                try {
                    Map<String, Object> map = ParamFormat.onJsonToMap(result);
                    callBack.callBack(map);
                } catch (Exception e) {
                    callBack.onCallFail("数据解析异常");
                    e.printStackTrace();
                }
            }

            @Override
            public void onProgress(float progress, long total) {
                super.onProgress(progress, total);
                Log.d(TAG, "uploadProgress = "+ progress +"   total = "+total);
            }
        });

    }


    /**
     *
     * @param url
     * @param params
     * @param callBack
     */
    public static void uploadGetFile(String url, Map<String, String> params, List<Map<String, Object>> list,  final HttpResponseCallBack callBack){


        long timeMillis = System.currentTimeMillis();
        params.put("timestamp", timeMillis+"");
        String sign = MD5Util.encrypt(Constant.SECRET+timeMillis+Constant.SECRET);
        params.put("sign", sign);

        String path = Urls.HOST + url;

        Logger.d("url = "+path+"\nparam = "+params.toString());
        OkhttpUtil.okHttpFileGet(path, params, list, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                e.printStackTrace();
                callBack.onCallFail("请求失败");
            }

            @Override
            public void onResponse(String response) {
                String result = UnicodeUtil.unicodeToString(response);
               Logger.d(result);
                try {
                    Map<String, Object> map = ParamFormat.onJsonToMap(result);
                    callBack.callBack(map);
                } catch (Exception e) {
                    callBack.onCallFail("JSON解析异常");
                    e.printStackTrace();

                }
            }


            @Override
            public void onProgress(float progress, long total) {
                super.onProgress(progress, total);
                Log.d(TAG, "uploadProgress = "+ progress +"   total = "+total);
            }
        });

    }


    /**
     * ******************************************************************************************
     * @param url
     * @param params
     * @param callBack
     */
    public static void sendHttpGet(String url, Map<String, String> params, final HttpResponseCallBack callBack){


        long timeMillis = System.currentTimeMillis();
        params.put("timestamp", timeMillis+"");
        String sign = MD5Util.encrypt(Constant.SECRET+timeMillis+Constant.SECRET);
        params.put("sign", sign);

        String path = Urls.HOST + url;
        Logger.d("url="+path +"\nparam="+params.toString());
        OkhttpUtil.okHttpGet(path, params, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                callBack.onCallFail("请求失败");
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response) {

                String result = UnicodeUtil.unicodeToString(response);

                Logger.json(result);
                try {

                    Map<String, Object> map = ParamFormat.onAllJsonToMap(result);
                    if(!map.containsKey(ResponseKey.CODE)){
                        callBack.callBack(map);
                    }else {

                        if ((int) map.get(ResponseKey.CODE) == 1001) {//成功

                            callBack.callBack(map);

                        }else if((int) map.get(ResponseKey.CODE) == 2004){ //账号已注销
                            callBack.onCallFail("2004");
                        } else {
                            Logger.d(map);
                            callBack.onCallFail(map.get(ResponseKey.MESSAGE).toString());
                        }
                    }
                } catch (Exception e) {
                    callBack.onCallFail("数据解析异常");
                    e.printStackTrace();
                }

            }
        });
    }


    /**
     *
     * @param url
     * @param params
     * @param callBack
     */
    public static void sendHttpPost(String url, Map<String, String> params, final HttpResponseCallBack callBack){


        long timeMillis = System.currentTimeMillis();
        params.put("timestamp", timeMillis+"");
        String sign = MD5Util.encrypt(Constant.SECRET+timeMillis+Constant.SECRET);
        params.put("sign", sign);

        String path = Urls.HOST + url;
        Logger.d("url="+path +"\nparam="+params.toString());


        OkhttpUtil.okHttpPost(path, params, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                callBack.onCallFail("请求失败");
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response) {

                String result = UnicodeUtil.unicodeToString(response);
                Logger.json(result);
                try {

                    Map<String, Object> map = ParamFormat.onAllJsonToMap(result);

                    if(!map.containsKey(ResponseKey.CODE)){
                        callBack.callBack(map);
                    }else {
                        if ((int) map.get(ResponseKey.CODE) == 1001) {//成功

                            callBack.callBack(map);

                        }else if((int) map.get(ResponseKey.CODE) == 2004){ //账号已注销
                            callBack.onCallFail("2004");
                        } else { //用户名或密码错误
//                            map.putAll(ParamFormat.onJsonToMap(result));
//                            callBack.callBack(map);
//                            callBack.onCallFail(map.get(ResponseKey.MESSAGE)+"");
//                        }else {
                            callBack.onCallFail(map.get(ResponseKey.MESSAGE)+"");
                        }
                    }
                } catch (Exception e) {
                    callBack.onCallFail("数据返回异常");
                    e.printStackTrace();
                }

            }
        });
    }


    /**
     *
     * @param url
     * @param params
     * @param callBack
     */
    public static void onGetUploadFile(String url, Map<String, String> params, List<Map<String, Object>> list,  final HttpResponseCallBack callBack){


        long timeMillis = System.currentTimeMillis();
        params.put("timestamp", timeMillis+"");
        String sign = MD5Util.encrypt(Constant.SECRET+timeMillis+Constant.SECRET);
        params.put("sign", sign);

        String path = Urls.HOST + url;

        Logger.d("url = "+path+"\nparam = "+params.toString());
        OkhttpUtil.okHttpFileGet(path, params, list, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                e.printStackTrace();
                callBack.onCallFail("请求失败");
            }

            @Override
            public void onResponse(String response) {
                String result = UnicodeUtil.unicodeToString(response);
                Logger.json(result);
                try {
                    Map<String, Object> map = ParamFormat.onJsonToMap(result);
                    callBack.callBack(map);
                } catch (Exception e) {
                    callBack.onCallFail("数据返回异常");
                }
            }


            @Override
            public void onProgress(float progress, long total) {
                super.onProgress(progress, total);
                Log.d(TAG, "uploadProgress = "+ progress +"   total = "+total);
            }
        });

    }



    /**
     *
     * @param url
     * @param params
     * @param callBack
     */
    public static void onPostUploadFile(String url, Map<String, String> params, List<Map<String, Object>> list,  final HttpResponseCallBack callBack){


        long timeMillis = System.currentTimeMillis();
        params.put("timestamp", timeMillis+"");
        String sign = MD5Util.encrypt(Constant.SECRET+timeMillis+Constant.SECRET);
        params.put("sign", sign);

        String path = Urls.HOST + url;

        Logger.d("url = "+path+"\nparam = "+params.toString());
        OkhttpUtil.okHttpFilePost(path, params, list, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                e.printStackTrace();
                callBack.onCallFail("请求失败");
            }

            @Override
            public void onResponse(String response) {
                String result = UnicodeUtil.unicodeToString(response);
                Logger.d(result);
                try {
                    Map<String, Object> map = ParamFormat.onJsonToMap(result);
                    callBack.callBack(map);
                } catch (Exception e) {
                    callBack.onCallFail("数据返回异常");
                }
            }


            @Override
            public void onProgress(float progress, long total) {
                super.onProgress(progress, total);
                Log.d(TAG, "uploadProgress = "+ progress +"   total = "+total);
            }
        });

    }


    public static void onDownloadFile(String url, Map<String, String> param, String dir, String fileName, final OnDownloadListener listener){

        Logger.d("下载路径： "+url);
        OkhttpUtil.okHttpDownloadFile(url, param, new CallBackUtil.CallBackFile(dir, fileName) {
            @Override
            public void onFailure(Call call, Exception e) {
                listener.onFail();
            }

            @Override
            public void onResponse(File response) {
                if (response==null) return;
                if (response.exists()){
                    Logger.d("文件名称： "+response.getName()+"\n保存路径："+response.getAbsolutePath());
                    listener.onSuccess(response);
                }else{
                    listener.onFail();
                }
            }

            @Override
            public void onProgress(float progress, long total) {
                super.onProgress(progress, total);

                listener.onProgress(progress, total);
            }
        });
    }



    /**
     *
     * @param url
     * @param params
     * @param callBack
     */
    public static void sendDefaultHttpPost(String url, Map<String, String> params, final HttpResponseCallBack callBack){


        long timeMillis = System.currentTimeMillis();
        params.put("timestamp", timeMillis+"");
        String sign = MD5Util.encrypt(Constant.SECRET+timeMillis+Constant.SECRET);
        params.put("sign", sign);

        String path = Urls.HOST + url;
        Logger.d("url="+path +"\nparam="+params.toString());


        OkhttpUtil.okHttpPost(path, params, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                callBack.onCallFail("请求失败");
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response) {

                String result = UnicodeUtil.unicodeToString(response);
                Logger.d(result);
                Map<String, Object> data = new HashMap<>();
                data.put("data", result);
                callBack.callBack(data);
            }
        });
    }

}
