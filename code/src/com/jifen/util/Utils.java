package com.jifen.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.jifen.api.LoginRequest;
import com.jifen.api.LoginResponse;
import com.jifen.api.UploadPointRequest;
import com.jifen.api.UploadPointResponse;
import com.plugin.common.utils.CustomThreadPool;
import com.plugin.internet.InternetUtils;

public class Utils {

    public interface PointFetchListener {
        void onPointFetchSuccess(int current);
        
        void onPointFetchFailed(int code, String data);
    }
    
    public interface PointUploadListener {
        void onPointUploadSuccess(int currentPoint);
        
        void onPointUploadFailed(int code, String data);
    }
    
    public static void sendLoginOrRegisteBroadcast(Context conext, String userName, String password) {
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password) && conext != null) {
            Intent i = new Intent();
            i.setAction("com.jifenbao.account.login");
            i.putExtra("u", userName);
            i.putExtra("p", password);
            conext.sendBroadcast(i);
        }
    }
    
    public static void asyncFetchCurrentPoint(final Context context, final String userName, final String password, final PointFetchListener pointFetchListener) {
        CustomThreadPool.asyncWork(new Runnable() {
            
            @Override
            public void run() {
                LoginRequest request = new LoginRequest(userName, password);
                try {
                    LoginResponse response = InternetUtils.request(context, request);
                    if (response != null) {
                        Log.d(">>>>>>>", response.toString());
                        if (response.code == LoginResponse.CODE_SUCCESS) {
                            if (pointFetchListener != null) {
                                pointFetchListener.onPointFetchSuccess(Integer.valueOf(response.data));
                            }
                        } else {
                            if (pointFetchListener != null) {
                                pointFetchListener.onPointFetchFailed(response.code, response.data);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (pointFetchListener != null) {
                        pointFetchListener.onPointFetchFailed(LoginResponse.CODE_UNKNOWN, e.getMessage());
                    }
                }
            }
        });
    }
    
    public static void asyncUploadPoint(final Context context, final String userName, final int currentPoint, final PointUploadListener l) {
        if (!TextUtils.isEmpty(userName) && currentPoint > 0) {
            CustomThreadPool.asyncWork(new Runnable() {
                
                @Override
                public void run() {
                    UploadPointRequest request = new UploadPointRequest(userName, String.valueOf(currentPoint));
                    try {
                        UploadPointResponse response = InternetUtils.request(context, request);
                        if (response != null) {
                            if (response.code == LoginResponse.CODE_SUCCESS) {
                                if (l != null) {
                                    l.onPointUploadSuccess(Integer.valueOf(response.data));
                                }
                            } else {
                                if (l != null) {
                                    l.onPointUploadFailed(response.code, response.data);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (l != null) {
                            l.onPointUploadFailed(LoginResponse.CODE_UNKNOWN, e.getMessage());
                        }
                    }
                }
            });
        }
    }
    
}
