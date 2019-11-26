package com.wajeba.items;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemOrderList implements Serializable {

    private String id, uniqueId, address, comment, date, totalBill, totalQuantity, status;
    private ArrayList<ItemOrderMenu> arrayListOrderMenu;

    public ItemOrderList(String id, String uniqueId, String address, String comment, String date, String totalQuantity, String totalBill, String status, ArrayList<ItemOrderMenu> arrayListOrderMenu) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.address = address;
        this.comment = comment;
        this.date = date;
        this.totalBill = totalBill;
        this.totalQuantity = totalQuantity;
        this.status = status;
        this.arrayListOrderMenu = arrayListOrderMenu;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(String totalbill) {
        this.totalBill = totalbill;
    }

    public String getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(String totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<ItemOrderMenu> getArrayListOrderMenu() {
        return arrayListOrderMenu;
    }

    public void setArrayListOrderMenu(ArrayList<ItemOrderMenu> arrayListOrderMenu) {
        this.arrayListOrderMenu = arrayListOrderMenu;
    }
}
