package com.wajeba.items;

import java.io.Serializable;

public class ItemUser implements Serializable {

    private String id, name, email, mobile, image, address;

    public ItemUser(String id, String name, String email, String mobile, String image, String address)
    {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.image = image;
        this.address = address;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }

    public String getMobile()
    {
        return mobile;
    }

    public String getImage()
    {
        return image;
    }

    public String getAddress()
    {
        return address;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

}
