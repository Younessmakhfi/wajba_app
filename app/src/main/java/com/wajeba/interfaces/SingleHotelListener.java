package com.wajeba.interfaces;

import com.wajeba.items.ItemRestaurant;

public interface SingleHotelListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ItemRestaurant itemRestaurant);
}