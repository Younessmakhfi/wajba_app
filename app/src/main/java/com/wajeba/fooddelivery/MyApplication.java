package com.wajeba.fooddelivery;

import android.app.Application;
import android.content.Context;

import com.wajeba.utils.DBHelper;
import com.google.android.gms.ads.MobileAds;
import com.onesignal.OneSignal;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import com.wajeba.helper.LocalHelper;
public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocalHelper.onAttach(base,"en"));
    }
    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/pop_reg.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        OneSignal.startInit(getApplicationContext()).init();

        MobileAds.initialize(getApplicationContext());

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        dbHelper.onCreate(dbHelper.getWritableDatabase());
    }
}
