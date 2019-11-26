package com.wajeba.interfaces;

import com.wajeba.items.ItemRestaurant;

import java.util.ArrayList;

public interface HomeListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemRestaurant> arrayList_latest, ArrayList<ItemRestaurant> arrayList_featured);
}
