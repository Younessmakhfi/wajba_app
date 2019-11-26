package com.wajeba.items;

import java.io.Serializable;

public class ItemMenu implements Serializable {

    private String id, name,type, image, desc, price,restId, catID;

    public ItemMenu(String id, String name,String type, String image, String desc, String price, String restId, String catID) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.desc = desc;
        this.price = price;
        this.restId = restId;
        this.catID = catID;
        this.type = type;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getImage()
    {
        return image;
    }

    public String getDesc()
    {
        return desc;
    }

    public String getPrice()
    {
        return price;
    }

    public String getType() {
        return type;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public void setPrice(String price)
    {
        this.price = price;
    }

    public String getRestId() {
        return restId;
    }

    public void setRestId(String restId) {
        this.restId = restId;
    }

    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }
}
