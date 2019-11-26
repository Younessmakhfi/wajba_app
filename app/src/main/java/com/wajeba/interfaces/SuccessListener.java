package com.wajeba.interfaces;

public interface SuccessListener {
    void onStart();
    void onEnd(String success, String isWorkSuccess, String message);
}