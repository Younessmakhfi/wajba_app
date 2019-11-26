package com.wajeba.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.wajeba.items.ItemUser;

public class SharedPref {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static String SHARED_PREF_LOGIN ="login", TAG_UID = "uid" ,TAG_USERNAME = "name", TAG_EMAIL = "email", TAG_MOBILE = "mobile", TAG_ADDRESS = "address", TAG_IMAGE = "city", TAG_REMEMBER = "rem",
            TAG_PASSWORD = "pass", SHARED_PREF_AUTOLOGIN = "autologin";

    public SharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_LOGIN,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Boolean getIsNotification() {
        return sharedPreferences.getBoolean("noti", true);
    }

    public void setIsNotification(Boolean isNotification) {
        editor.putBoolean("noti", isNotification);
        editor.apply();
    }

    public void setIsFirst(Boolean flag) {
        editor.putBoolean("firstopen", flag);
        editor.apply();
    }

    public Boolean getIsFirst() {
        return sharedPreferences.getBoolean("firstopen", true);
    }

    public void setLoginDetails(ItemUser itemUser, Boolean isRemember, String password) {
        editor.putString(TAG_UID, Methods.encrypt(itemUser.getId()));
        editor.putString(TAG_USERNAME, Methods.encrypt(itemUser.getName()));
        editor.putString(TAG_MOBILE, Methods.encrypt(itemUser.getMobile()));
        editor.putString(TAG_EMAIL, Methods.encrypt(itemUser.getEmail()));
        editor.putString(TAG_IMAGE, Methods.encrypt(itemUser.getImage()));
        editor.putString(TAG_ADDRESS, Methods.encrypt(itemUser.getAddress()));
        editor.putBoolean(TAG_REMEMBER, isRemember);
        editor.putString(TAG_PASSWORD, Methods.encrypt(password));
        editor.apply();
    }

    public void setLoginDetails(ItemUser itemUser) {
        editor.putString(TAG_USERNAME, Methods.encrypt(itemUser.getName()));
        editor.putString(TAG_MOBILE, Methods.encrypt(itemUser.getMobile()));
        editor.putString(TAG_EMAIL, Methods.encrypt(itemUser.getEmail()));
        editor.putString(TAG_IMAGE, Methods.encrypt(itemUser.getImage()));
        editor.putString(TAG_ADDRESS, Methods.encrypt(itemUser.getAddress()));
        editor.apply();
    }

    public void setRemeber(Boolean isRemember) {
        editor.putBoolean(TAG_REMEMBER, isRemember);
        editor.putString(TAG_PASSWORD, "");
        editor.apply();
    }

    public void getUserDetails() {
        Constant.itemUser = new ItemUser(Methods.decrypt(sharedPreferences.getString(TAG_UID,"")), Methods.decrypt(sharedPreferences.getString(TAG_USERNAME,"")), Methods.decrypt(sharedPreferences.getString(TAG_EMAIL,"")), Methods.decrypt(sharedPreferences.getString(TAG_MOBILE,"")), Methods.decrypt(sharedPreferences.getString(TAG_IMAGE,"")), Methods.decrypt(sharedPreferences.getString(TAG_ADDRESS,"")));
    }

    public String getEmail() {
        return Methods.decrypt(sharedPreferences.getString(TAG_EMAIL,""));
    }

    public String getMobile() {
        return Methods.decrypt(sharedPreferences.getString(TAG_MOBILE,""));
    }

    public String getAddress() {
        return Methods.decrypt(sharedPreferences.getString(TAG_ADDRESS,""));
    }

    public String getCity() {
        return Methods.decrypt(sharedPreferences.getString(TAG_IMAGE,""));
    }

    public String getPassword() {
        return Methods.decrypt(sharedPreferences.getString(TAG_PASSWORD,""));
    }

    public Boolean isRemember() {
        return sharedPreferences.getBoolean(TAG_REMEMBER, false);
    }

    public Boolean getIsAutoLogin() {
        return sharedPreferences.getBoolean(SHARED_PREF_AUTOLOGIN, false);
    }

    public void setIsAutoLogin(Boolean isAutoLogin) {
        editor.putBoolean(SHARED_PREF_AUTOLOGIN, isAutoLogin);
        editor.apply();
    }
}