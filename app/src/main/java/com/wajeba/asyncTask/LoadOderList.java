package com.wajeba.asyncTask;

import android.os.AsyncTask;

import com.wajeba.interfaces.OrderListListener;
import com.wajeba.items.ItemOrderList;
import com.wajeba.items.ItemOrderMenu;
import com.wajeba.utils.Constant;
import com.wajeba.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadOderList extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private OrderListListener orderListListener;
    private ArrayList<ItemOrderList> arrayList;
    private String verifyStatus = "0", message = "";

    public LoadOderList(OrderListListener orderListListener, RequestBody requestBody) {
        this.orderListListener = orderListListener;
        this.requestBody = requestBody;
        arrayList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        orderListListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody);
            JSONObject jOb = new JSONObject(json);
            JSONArray jsonArray = jOb.getJSONArray(Constant.TAG_ROOT);

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject c = jsonArray.getJSONObject(j);

                if (!c.has(Constant.TAG_SUCCESS)) {
                    String id = c.getString(Constant.TAG_ORDER_ID);
                    String unique_id = c.getString(Constant.TAG_ORDER_UNIQUE_ID);
                    String address = c.getString(Constant.TAG_ORDER_ADDRESS);
                    String comment = c.getString(Constant.TAG_ORDER_COMMENT);
                    String date = c.getString(Constant.TAG_ORDER_DATE);
                    String status = c.getString(Constant.TAG_ORDER_STATUS);

                    JSONArray jA = c.getJSONArray(Constant.TAG_ORDER_ITEMS);

                    ArrayList<ItemOrderMenu> arrayList_ordermenu = new ArrayList<>();
                    int totalQnt = 0;
                    float totalPrice = 0;

                    for (int i = 0; i < jA.length(); i++) {
                        JSONObject jO = jA.getJSONObject(i);

                        String rest_id = jO.getString(Constant.TAG_MENU_REST_ID);
                        String rest_name = jO.getString(Constant.TAG_ORDER_REST_NAME);
                        String menu_id = jO.getString(Constant.TAG_CART_MENU_ID);
                        String menu_name = jO.getString(Constant.TAG_MENU_NAME);
                        String menu_image = jO.getString(Constant.TAG_MENU_IMAGE);
                        String menu_qty = jO.getString(Constant.TAG_MENU_QYT);
                        String menu_price = jO.getString(Constant.TAG_MENU_PRICE);
                        String menu_total_price = jO.getString(Constant.TAG_MENU_TOTAL_PRICE);
                        String menu_type = jO.getString(Constant.TAG_MENU_TYPE);

                        totalPrice = totalPrice + Float.parseFloat(menu_total_price);
                        totalQnt = totalQnt + Integer.parseInt(menu_qty);

                        ItemOrderMenu itemOrderMenu = new ItemOrderMenu(rest_id, rest_name, menu_id, menu_name, menu_image, menu_qty, menu_price, menu_total_price, menu_type);
                        arrayList_ordermenu.add(itemOrderMenu);
                    }

                    ItemOrderList itemOrderList = new ItemOrderList(id, unique_id, address, comment, date, String.valueOf(totalQnt), String.valueOf(totalPrice), status, arrayList_ordermenu);
                    arrayList.add(itemOrderList);
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
        orderListListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}