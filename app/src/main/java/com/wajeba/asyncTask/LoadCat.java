package com.wajeba.asyncTask;

import android.os.AsyncTask;

import com.wajeba.interfaces.CategoryListener;
import com.wajeba.items.ItemCat;
import com.wajeba.utils.Constant;
import com.wajeba.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadCat extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private CategoryListener categoryListener;
    private ArrayList<ItemCat> arrayList_cat;
    private String verifyStatus = "0", message = "";

    public LoadCat(CategoryListener categoryListener, RequestBody requestBody) {
        this.requestBody = requestBody;
        this.categoryListener = categoryListener;
        arrayList_cat = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        categoryListener.onStart();
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
                    String id = c.getString(Constant.TAG_CAT_ID);
                    String name = c.getString(Constant.TAG_CAT_NAME);
                    String image = c.getString(Constant.TAG_CAT_IMAGE);

                    ItemCat itemCat = new ItemCat(id, name, image);
                    arrayList_cat.add(itemCat);
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
        categoryListener.onEnd(s, verifyStatus, message, arrayList_cat);
        super.onPostExecute(s);
    }
}