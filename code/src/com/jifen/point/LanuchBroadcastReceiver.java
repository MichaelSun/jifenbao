package com.jifen.point;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class LanuchBroadcastReceiver extends BroadcastReceiver {

    public static final String LANUCH_MAIN_ACTION = "com.jifenbao.lanuch";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("LanuchBroadcastReceiver", "[[onReceive]] >>>>>>>> intent = " + intent.getAction());

        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(LANUCH_MAIN_ACTION)) {
                String username = intent.getStringExtra("u");
                String password = intent.getStringExtra("p");
                SettingManager.getInstance().init(context);
                if (TextUtils.isEmpty(SettingManager.getInstance().getUserName())
                        && TextUtils.isEmpty(SettingManager.getInstance().getPassword())
                        && !TextUtils.isEmpty(username)
                        && !TextUtils.isEmpty(password)) {
                    SettingManager.getInstance().setUserName(username);
                    SettingManager.getInstance().setPassword(password);
                }

                Intent i = new Intent();
                i.setClass(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }

}
