package com.wajeba.asyncTask;

import android.os.AsyncTask;

import com.wajeba.interfaces.LoginListener;
import com.wajeba.items.ItemCart;
import com.wajeba.items.ItemUser;
import com.wajeba.utils.Constant;
import com.wajeba.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadLogin extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private LoginListener loginListener;
    private String user_id = "", user_name = "", success = "0", message = "";

    public LoadLogin(LoginListener loginListener, RequestBody requestBody) {
        this.loginListener = loginListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        loginListener.onStart();
        Constant.arrayList_cart.clear();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject jObj = jsonArray.getJSONObject(j);

                success = jObj.getString(Constant.TAG_SUCCESS);
                if (success.equals("1")) {
                    user_id = jObj.getString(Constant.TAG_USER_ID);
                    user_name = jObj.getString(Constant.TAG_USER_NAME);
                    Constant.menuCount = Integer.parseInt(jObj.getString(Constant.TAG_CART_COUNT));
                    Constant.itemUser = new ItemUser(user_id, user_name, jObj.getString(Constant.TAG_USER_EMAIL), jObj.getString(Constant.TAG_USER_PHONE), jObj.getString(Constant.TAG_USER_IMAGE), jObj.getString(Constant.TAG_USER_ADDRESS));

                    JSONArray jA_cart = jObj.getJSONArray("cart_list");
                    for (int i = 0; i < jA_cart.length(); i++) {
                        JSONObject c = jA_cart.getJSONObject(i);

                        String cartid = c.getString(Constant.TAG_CART_ID);
                        String rest_id = c.getString(Constant.TAG_MENU_REST_ID);
                        String rest_name = c.getString(Constant.TAG_REST_NAME);
                        String menu_id = c.getString(Constant.TAG_CART_MENU_ID);
                        String menu_name = c.getString(Constant.TAG_MENU_NAME);
                        String menu_image = c.getString(Constant.TAG_MENU_IMAGE);
                        String menu_qty = c.getString(Constant.TAG_MENU_QYT);
                        String menu_price = c.getString(Constant.TAG_MENU_PRICE);

                        ItemCart itemCart = new ItemCart(cartid, rest_id, rest_name, menu_id, menu_name, menu_image, menu_qty, menu_price, menu_qty);
                        Constant.arrayList_cart.add(itemCart);
                    }
                } else {
                    message = jObj.getString(Constant.TAG_MSG);
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
        loginListener.onEnd(s, success, message, user_id, user_name);
        super.onPostExecute(s);
    }
}