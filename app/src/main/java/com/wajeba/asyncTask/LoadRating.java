package com.wajeba.asyncTask;

import android.os.AsyncTask;

import com.wajeba.interfaces.RatingListener;
import com.wajeba.utils.Constant;
import com.wajeba.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadRating extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private String msg = "", rate = "-1", success = "0";
    private RatingListener ratingListener;

    public LoadRating(RatingListener ratingListener, RequestBody requestBody) {
        this.requestBody = requestBody;
        this.ratingListener = ratingListener;
    }

    @Override
    protected void onPreExecute() {
        ratingListener.onStart();
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

                success = c.getString(Constant.TAG_SUCCESS);
                msg = c.getString(Constant.TAG_MSG);

                if (c.has("rate_avg")) {
                    rate = c.getString("rate_avg");
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
        ratingListener.onEnd(s, success, msg, Float.parseFloat(rate));
        super.onPostExecute(s);
    }
}