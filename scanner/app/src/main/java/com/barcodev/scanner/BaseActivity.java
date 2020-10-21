package com.barcodev.scanner;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class BaseActivity {

    public BaseActivity() {
    }

    private static RequestQueue queueVolley;
    public static String scanner = "https://yourwebsite.com/api/scanner";

    public static RequestQueue getQueueVolley(Context c) {
        if(queueVolley == null){
            queueVolley = Volley.newRequestQueue(c);
        }
        return queueVolley;
    }
}
