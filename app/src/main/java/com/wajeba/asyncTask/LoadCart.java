package com.wajeba.asyncTask;

import android.os.AsyncTask;

import com.wajeba.interfaces.CartListener;
import com.wajeba.items.ItemCart;
import com.wajeba.utils.Constant;
import com.wajeba.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadCart extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private CartListener cartListener;
    private ArrayList<ItemCart> arrayList;
    private String verifyStatus = "0", message = "";

    public LoadCart(CartListener cartListener, RequestBody requestBody) {
        this.requestBody = requestBody;
        this.cartListener = cartListener;
        arrayList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        cartListener.onStart();
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
                    String cartid = c.getString(Constant.TAG_CART_ID);
                    String rest_id = c.getString(Constant.TAG_MENU_REST_ID);
                    String rest_name = c.getString(Constant.TAG_REST_NAME);
                    String menu_id = c.getString(Constant.TAG_CART_MENU_ID);
                    String menu_name = c.getString(Constant.TAG_MENU_NAME);
                    String menu_image = c.getString(Constant.TAG_MENU_IMAGE);
                    String menu_qty = c.getString(Constant.TAG_MENU_QYT);
                    String menu_price = c.getString(Constant.TAG_MENU_PRICE);

                    ItemCart itemCart = new ItemCart(cartid, rest_id, rest_name, menu_id, menu_name, menu_image, menu_qty, menu_price, menu_qty);
                    arrayList.add(itemCart);
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
        cartListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}