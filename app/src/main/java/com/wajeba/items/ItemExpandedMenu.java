package com.wajeba.items;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemExpandedMenu implements Serializable {

    private String id;
    private ArrayList<String> arrayList;

    public ItemExpandedMenu(String id, ArrayList<String> arrayList) {
        this.id = id;
        this.arrayList = arrayList;
    }

    public String getId()
    {
        return id;
    }

    public ArrayList<String> getArrayList() {
        return arrayList;
    }
}