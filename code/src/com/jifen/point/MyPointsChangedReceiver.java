package com.jifen.point;

import net.youmi.android.offers.EarnPointsOrderInfo;
import net.youmi.android.offers.EarnPointsOrderList;
import net.youmi.android.offers.PointsManager;
import net.youmi.android.offers.PointsReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.jifen.util.Utils;
import com.jifen.util.Utils.PointFetchListener;
import com.jifen.util.Utils.PointUploadListener;

public class MyPointsChangedReceiver extends PointsReceiver {

    private Context mContext;

    private static final int UPLOAD_POINT_SUCCESS = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case UPLOAD_POINT_SUCCESS:
                Intent intent = new Intent();
                intent.setAction(ContentActivity.CURRENT_POINT_CHANGED);
                intent.putExtra("point", msg.arg1);
                
                Config.LOGD("[[MyPointsChangedReceiver]] try to send action : " + ContentActivity.CURRENT_POINT_CHANGED
                        + " with point : " + msg.arg1);
                
                mContext.sendBroadcast(intent);
                break;
            }
        }
    };

    @Override
    protected void onEarnPoints(final Context arg0, EarnPointsOrderList arg1) {
        mContext = arg0;
        if (arg1 != null) {
            int size = arg1.size();
            int earnPoint = 0;
            for (int i = 0; i < size; ++i) {
                EarnPointsOrderInfo info = arg1.get(i);
                if (info != null) {
                    earnPoint += info.getPoints();
                    
                    Config.LOGD("[[onEarnPoints]] Order Info : " + info.getMessage() + "  point = " + info.getPoints());
                }
            }

            final int finalEarnPoint = earnPoint;
            SettingManager.getInstance().init(arg0);
            SettingManager sm = SettingManager.getInstance();
            if (!TextUtils.isEmpty(sm.getUserName()) && !TextUtils.isEmpty(sm.getPassword())) {
                Utils.asyncFetchCurrentPoint(arg0, sm.getUserName(), sm.getPassword(), new PointFetchListener() {

                    @Override
                    public void onPointFetchSuccess(int current) {
                        Config.LOGD("[[onPointFetchSuccess]] current point = " + current + " ::: try to upload the point >>>>>");
                        
                        int currentAddPoint = current + finalEarnPoint;
                        Utils.asyncUploadPoint(arg0, SettingManager.getInstance().getUserName(), currentAddPoint,
                                new PointUploadListener() {

                                    @Override
                                    public void onPointUploadSuccess(int currentPoint) {
                                        Config.LOGD("[[onPointUploadSuccess]] upload point success, point = " + currentPoint);
                                        
                                        // cost all the point
                                        int curPoint = PointsManager.getInstance(arg0).queryPoints();
                                        PointsManager.getInstance(arg0).spendPoints(curPoint);

                                        Message msg = Message.obtain();
                                        msg.what = UPLOAD_POINT_SUCCESS;
                                        msg.arg1 = curPoint;
                                        mHandler.sendMessage(msg);
                                    }

                                    @Override
                                    public void onPointUploadFailed(int code, String data) {

                                    }
                                });
                    }

                    @Override
                    public void onPointFetchFailed(int code, String data) {

                    }

                });
            }
        }
    }

    @Override
    protected void onViewPoints(Context arg0) {
        // TODO Auto-generated method stub

    }

}
