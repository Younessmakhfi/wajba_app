package com.wajeba.interfaces;

import com.wajeba.items.ItemMenu;

import java.util.ArrayList;

public interface MenuListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemMenu> arrayList_menu);
}
