package com.wajeba.items;

import java.io.Serializable;

public class ItemOrderMenu implements Serializable {

    private String restId, restName, menuId, menuName, menuImage, menuQty,menuPrice, menuTotalPrice, menuType;

    public ItemOrderMenu(String restId, String restName, String menuId, String menuName, String menuImage, String menuQty, String menuPrice, String menuTotalPrice, String menuType) {
        this.restName = restName;
        this.restId = restId;
        this.menuId = menuId;
        this.menuName = menuName;
        this.menuImage = menuImage;
        this.menuQty = menuQty;
        this.menuPrice = menuPrice;
        this.menuType = menuType;
        this.menuTotalPrice = menuTotalPrice;
    }

    public String getRestId() {
        return restId;
    }

    public void setRestId(String restId) {
        this.restId = restId;
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

    public String getRestName() {
        return restName;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public String getMenuTotalPrice() {
        return menuTotalPrice;
    }

    public void setMenuTotalPrice(String menuTotalPrice) {
        this.menuTotalPrice = menuTotalPrice;
    }

    public String getMenuType() {
        return menuType;
    }
}
