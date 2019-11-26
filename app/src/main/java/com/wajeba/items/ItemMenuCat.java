package com.wajeba.items;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemMenuCat implements Serializable {

    private String id, name, hotelID;
    private ArrayList<ItemMenu> arrayList = new ArrayList<>();

    public ItemMenuCat(String id, String name, String hotelID, ArrayList<ItemMenu> arrayList) {
        this.id = id;
        this.name = name;
        this.hotelID = hotelID;
        this.arrayList = arrayList;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getHotelID() {
        return hotelID;
    }

    public void setHotelID(String hotelID) {
        this.hotelID = hotelID;
    }

    public ArrayList<ItemMenu> getMenuArrayList() {
        return arrayList;
    }

    public void setMenuArrayList(ArrayList<ItemMenu> arrayList) {
        this.arrayList = arrayList;
    }
}
