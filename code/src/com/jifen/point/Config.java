package com.jifen.point;

import android.util.Log;

public class Config {
    
    public static final boolean DEBUG = false;

    public static final String YOUMI_APP_ID = "85c4a46bf61d61b9";
    
    public static final String YOUMI_SECRECT_KEY = "66bfc44a62475800";
    
    public static final String YOUMENG_KEY = "51d6a52956240b6052005e2c";
 
    public static void LOGD(String msg) {
        if (DEBUG) {
            Log.d("JiFenBao", msg);
        }
    }
    
}
