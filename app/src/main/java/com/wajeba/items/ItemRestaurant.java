package com.wajeba.items;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemRestaurant implements Serializable{

 	private String id, name, image, type, address, monday, tuesday, wednesday, thursday, friday, saturday, sunday, cid, cimage, cname;
	private int total_rating;
	private float avgRatings;
	private ArrayList<ItemReview> arrayList_review;

	public ItemRestaurant(String id, String name, String image, String type, String address, float avgRatings, int total_rating, String cname) {
		this.id = id;
		this.name = name;
		this.image = image;
		this.address = address;
		this.total_rating = total_rating;
		this.avgRatings = avgRatings;
		this.cname = cname;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMonday() {
		return monday;
	}

	public void setMonday(String monday) {
		this.monday = monday;
	}

	public String getTuesday() {
		return tuesday;
	}

	public void setTuesday(String tuesday) {
		this.tuesday = tuesday;
	}

	public String getWednesday() {
		return wednesday;
	}

	public void setWednesday(String wednesday) {
		this.wednesday = wednesday;
	}

	public String getThursday() {
		return thursday;
	}

	public void setThursday(String thursday) {
		this.thursday = thursday;
	}

	public String getFriday() {
		return friday;
	}

	public void setFriday(String friday) {
		this.friday = friday;
	}

	public String getSaturday() {
		return saturday;
	}

	public void setSaturday(String saturday) {
		this.saturday = saturday;
	}

	public String getSunday() {
		return sunday;
	}

	public void setSunday(String sunday) {
		this.sunday = sunday;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCimage() {
		return cimage;
	}

	public void setCimage(String cimage) {
		this.cimage = cimage;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public int getTotalRating() {
		return total_rating;
	}

	public void setTotalRating(int total_rating) {
		this.total_rating = total_rating;
	}

	public float getAvgRatings() {
		return avgRatings;
	}

	public void setAvgRatings(float avgRatings) {
		this.avgRatings = avgRatings;
	}

	public ArrayList<ItemReview> getArrayListReview() {
		return arrayList_review;
	}

	public void setArrayListReview(ArrayList<ItemReview> arrayList_review) {
		this.arrayList_review = arrayList_review;
	}
}
