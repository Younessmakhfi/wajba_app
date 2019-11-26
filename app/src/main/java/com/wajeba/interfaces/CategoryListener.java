package com.wajeba.interfaces;

import com.wajeba.items.ItemCat;

import java.util.ArrayList;

public interface CategoryListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCat> arrayListCat);
}
