package com.jifen.point;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingManager {

    private static SettingManager gSettingManager;
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    
    
    private static final String KEY_USERNAME = "userName";
    private static final String KEY_PASSWORD = "password";

    public static synchronized SettingManager getInstance() {
        if (gSettingManager == null) {
            gSettingManager = new SettingManager();
        }
        return gSettingManager;
    }

    public void init(Context context) {
        if (context != null) {
            mContext = context.getApplicationContext();
        }
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSharedPreferences.edit();
    }

    public String getUserName() {
        return mSharedPreferences.getString(KEY_USERNAME, null);
    }
    
    public void setUserName(String userName) {
        mEditor.putString(KEY_USERNAME, userName);
        mEditor.commit();
    }
    
    public String getPassword() {
        return mSharedPreferences.getString(KEY_PASSWORD, null);
    }
    
    public void setPassword(String password) {
        mEditor.putString(KEY_PASSWORD, password);
        mEditor.commit();
    }
    
}
