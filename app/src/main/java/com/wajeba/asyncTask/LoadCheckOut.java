package com.wajeba.asyncTask;

import android.os.AsyncTask;

import com.wajeba.interfaces.SuccessListener;
import com.wajeba.utils.Constant;
import com.wajeba.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadCheckOut extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private String suc = "0";
    private String msg = "";
    private SuccessListener successListener;

    public LoadCheckOut(SuccessListener successListener, RequestBody requestBody) {
        this.requestBody = requestBody;
        this.successListener = successListener;
    }

    @Override
    protected void onPreExecute() {
        successListener.onStart();
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
                suc = c.getString(Constant.TAG_SUCCESS);
                msg = c.getString(Constant.TAG_MSG);
            }
            return "1";
        } catch (Exception ee) {
            ee.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        successListener.onEnd(s, suc, msg);
        super.onPostExecute(s);
    }
}
