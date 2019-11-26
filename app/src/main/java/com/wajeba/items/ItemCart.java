package com.wajeba.items;

import java.io.Serializable;

public class ItemCart implements Serializable {

    private String id, restId, restName, menuId, menuName, menuImage, menuQty,menuPrice, memuTempQty;

    public ItemCart(String id, String restId, String restName, String menuId, String menuName,String menuImage, String menuQty, String menuPrice, String memuTempQty) {
        this.id = id;
        this.restId = restId;
        this.menuId = menuId;
        this.menuName = menuName;
        this.menuImage = menuImage;
        this.menuQty = menuQty;
        this.menuPrice = menuPrice;
        this.memuTempQty = memuTempQty;
        this.restName = restName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestId() {
        return restId;
    }

    public void setRestId(String restId) {
        this.restId = restId;
    }

    public String getRestName() {
        return restName;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getMenuImage() {
        return menuImage;
    }

    public void setMenuImage(String menuImage) {
        this.menuImage = menuImage;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuQty() {
        return menuQty;
    }

    public void setMenuQty(String menuQty) {
        this.menuQty = menuQty;
    }

    public String getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(String menuPrice) {
        this.menuPrice = menuPrice;
    }

    public String getMemuTempQty() {
        return memuTempQty;
    }

    public void setMemuTempQty(String memuTempQty) {
        this.memuTempQty = memuTempQty;
    }
}
