package com.wajeba.interfaces;

public interface RatingListener {
    void onStart();
    void onEnd(String success, String isWorkSuccess, String message, float rating);
}
