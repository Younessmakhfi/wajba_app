package com.wajeba.fooddelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import com.wajeba.helper.LocalHelper;

import io.paperdb.Paper;

public class ChangeLanguage extends AppCompatActivity {
    //language functions
    private void updateView(String lang) {
        Context context = LocalHelper.setLocale(this,lang);
        Resources resources = context.getResources();
        Intent mStartActivity = new Intent(ChangeLanguage.this, SplashActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(ChangeLanguage.this, mPendingIntentId, mStartActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) ChangeLanguage.this.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }
    public void Enlgishlanguage(View view) {
        Paper.book().write("language","en");
        updateView((String)Paper.book().read("language"));
    }
    public void Arabiclanguage(View view) {
        Paper.book().write("language","ar");
        updateView((String)Paper.book().read("language"));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
    }

    public void Francaislanguage(View view) {
        Paper.book().write("language","fr");
        updateView((String)Paper.book().read("language"));
    }
}
