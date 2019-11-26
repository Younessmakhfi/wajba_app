package com.wajeba.asyncTask;

import android.os.AsyncTask;

import com.wajeba.interfaces.SuccessListener;
import com.wajeba.items.ItemUser;
import com.wajeba.utils.Constant;
import com.wajeba.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadProfile extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private SuccessListener successListener;
    private String success = "0", message = "";

    public LoadProfile(SuccessListener successListener, RequestBody requestBody) {
        this.successListener = successListener;
        this.requestBody = requestBody;
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
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);

                success = c.getString(Constant.TAG_SUCCESS);
                String user_id = c.getString("user_id");
                if (user_id != null) {
                    String name = c.getString(Constant.TAG_USER_NAME);
                    String email = c.getString(Constant.TAG_USER_EMAIL);
                    String mobile = c.getString(Constant.TAG_USER_PHONE);
                    String address = c.getString(Constant.TAG_USER_ADDRESS);
                    String image = c.getString(Constant.TAG_USER_IMAGE);

                    Constant.itemUser = new ItemUser(user_id, name, email, mobile, image, address);
                } else {
                    success = "0";
                }
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        successListener.onEnd(s, success, message);
        super.onPostExecute(s);
    }
}