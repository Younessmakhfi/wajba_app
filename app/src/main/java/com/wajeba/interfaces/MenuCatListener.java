package com.wajeba.interfaces;

import com.wajeba.items.ItemMenuCat;

import java.util.ArrayList;

public interface MenuCatListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemMenuCat> arrayListMenuCat);
}
