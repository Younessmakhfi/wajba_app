package com.wajeba.asyncTask;

import android.os.AsyncTask;

import com.wajeba.interfaces.RestListener;
import com.wajeba.items.ItemRestaurant;
import com.wajeba.utils.Constant;
import com.wajeba.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadHotel extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private RestListener restListener;
    private ArrayList<ItemRestaurant> arrayList;
    private String verifyStatus = "0", message = "";

    public LoadHotel(RestListener restListener, RequestBody requestBody) {
        this.restListener = restListener;
        this.requestBody = requestBody;
        arrayList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        restListener.onStart();
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
                    String id = c.getString(Constant.TAG_ID);
                    String name = c.getString(Constant.TAG_REST_NAME);
                    String address = c.getString(Constant.TAG_REST_ADDRESS);
                    String image = c.getString(Constant.TAG_REST_IMAGE);
                    String type = c.getString(Constant.TAG_REST_TYPE);
                    float avg_Rate = Float.parseFloat(c.getString(Constant.TAG_REST_AVG_RATE));
                    int total_rate = Integer.parseInt(c.getString(Constant.TAG_REST_TOTAL_RATE));
                    String cat_name = "";
                    if (c.has(Constant.TAG_CAT_NAME)) {
                        cat_name = c.getString(Constant.TAG_CAT_NAME);
                    }

                    ItemRestaurant itemRestaurant = new ItemRestaurant(id, name, image, type, address, avg_Rate, total_rate, cat_name);
                    arrayList.add(itemRestaurant);
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
        restListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}