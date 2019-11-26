package com.wajeba.interfaces;

import com.wajeba.items.ItemRestaurant;

import java.util.ArrayList;

public interface RestListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemRestaurant> arrayListRestaurant);
}
