package com.wajeba.interfaces;

import com.wajeba.items.ItemCart;

import java.util.ArrayList;

public interface CartListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCart> arrayListMenu);
}