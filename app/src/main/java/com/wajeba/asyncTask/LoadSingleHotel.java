package com.wajeba.asyncTask;

import android.os.AsyncTask;

import com.wajeba.interfaces.SingleHotelListener;
import com.wajeba.items.ItemRestaurant;
import com.wajeba.items.ItemReview;
import com.wajeba.utils.Constant;
import com.wajeba.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadSingleHotel extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private SingleHotelListener singleHotelListener;
    private ItemRestaurant itemRestaurant;
    private ArrayList<ItemReview> arrayList_review = new ArrayList<>();
    private String verifyStatus = "0", message = "";

    public LoadSingleHotel(ItemRestaurant itemRestaurant, SingleHotelListener singleHotelListener, RequestBody requestBody) {
        this.requestBody = requestBody;
        this.singleHotelListener = singleHotelListener;
        this.itemRestaurant = itemRestaurant;
    }

    @Override
    protected void onPreExecute() {
        singleHotelListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody);
            JSONObject jOb = new JSONObject(json);
            JSONArray jsonArray = jOb.getJSONArray(Constant.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);

                if (!c.has(Constant.TAG_SUCCESS)) {
                    itemRestaurant.setMonday(c.getString(Constant.TAG_REST_MONDAY));
                    itemRestaurant.setTuesday(c.getString(Constant.TAG_REST_TUESDAY));
                    itemRestaurant.setWednesday(c.getString(Constant.TAG_REST_WEDNESDAY));
                    itemRestaurant.setThursday(c.getString(Constant.TAG_REST_THURSDAY));
                    itemRestaurant.setFriday(c.getString(Constant.TAG_REST_FRRIDAY));
                    itemRestaurant.setSaturday(c.getString(Constant.TAG_REST_SATURDAY));
                    itemRestaurant.setSunday(c.getString(Constant.TAG_REST_SUNDAY));
                    itemRestaurant.setCid(c.getString(Constant.TAG_CAT_ID));
                    itemRestaurant.setCname(c.getString(Constant.TAG_CAT_NAME));
                    itemRestaurant.setCimage(c.getString(Constant.TAG_CAT_IMAGE));

                    if (c.has(Constant.TAG_RATING_REVIEW)) {
                        JSONArray jA = c.getJSONArray(Constant.TAG_RATING_REVIEW);
                        for (int j = 0; j < jA.length(); j++) {
                            JSONObject cc = jA.getJSONObject(j);

                            String rate_id = cc.getString(Constant.TAG_RATING_ID);
                            String username = cc.getString(Constant.TAG_NAME_USER);
                            String rate = cc.getString(Constant.TAG_RATING);
                            String msg = cc.getString(Constant.TAG_RATING_MSG);

                            ItemReview itemReview = new ItemReview(rate_id, username, rate, msg);
                            arrayList_review.add(itemReview);
                        }
                    }
                    Constant.itemRestaurant.setArrayListReview(arrayList_review);
                } else {
                    verifyStatus = c.getString(Constant.TAG_SUCCESS);
                    message = c.getString(Constant.TAG_MSG);
                }
            }
            return "1";
        } catch (Exception ee) {
            ee.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        singleHotelListener.onEnd(s, verifyStatus, message, itemRestaurant);
        super.onPostExecute(s);
    }
}