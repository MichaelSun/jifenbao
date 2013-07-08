package com.jifen.point;

import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;
import android.app.Application;

import com.plugin.common.utils.SingleInstanceBase.SingleInstanceManager;
import com.umeng.analytics.MobclickAgent;

public class JiFenBaoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        initYoumi();
        initUMeng();
    }

    private void initYoumi() {
        AdManager.getInstance(this).init(Config.YOUMI_APP_ID, Config.YOUMI_SECRECT_KEY, false);
        OffersManager.getInstance(this.getApplicationContext()).onAppLaunch();
        SingleInstanceManager.getInstance().init(getApplicationContext());
    }

    private void initUMeng() {
        MobclickAgent.setSessionContinueMillis(60 * 1000);
        MobclickAgent.setDebugMode(false);
        com.umeng.common.Log.LOG = false;
        MobclickAgent.updateOnlineConfig(this);
        MobclickAgent.onError(this);
    }
    
}
