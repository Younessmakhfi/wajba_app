package com.wajeba.interfaces;

import com.wajeba.items.ItemOrderList;

import java.util.ArrayList;

public interface OrderListListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemOrderList> arrayListOrderList);
}
