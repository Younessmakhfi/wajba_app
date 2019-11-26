package com.wajeba.asyncTask;

import android.os.AsyncTask;

import com.wajeba.interfaces.MenuCatListener;
import com.wajeba.items.ItemMenu;
import com.wajeba.items.ItemMenuCat;
import com.wajeba.utils.Constant;
import com.wajeba.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadMenuCat extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private MenuCatListener menuCatListener;
    private ArrayList<ItemMenuCat> arrayList;
    private String verifyStatus = "0", message = "";

    public LoadMenuCat(MenuCatListener menuCatListener, RequestBody requestBody) {
        this.requestBody = requestBody;
        this.menuCatListener = menuCatListener;
        arrayList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        menuCatListener.onStart();
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
                    String hotel_id = c.getString(Constant.TAG_REST_ID);

                    ArrayList<ItemMenu> arrayListMenu = new ArrayList<>();
                    if (c.has("menu_list")) {
                        JSONArray jA = c.getJSONArray("menu_list");
                        for (int j = 0; j < jA.length(); j++) {
                            JSONObject jsonObject = jA.getJSONObject(j);

                            String menu_id = jsonObject.getString(Constant.TAG_MENU_ID);
                            String menu_name = jsonObject.getString(Constant.TAG_MENU_NAME);
                            String menu_type = jsonObject.getString(Constant.TAG_MENU_TYPE);
                            String desc = jsonObject.getString(Constant.TAG_MENU_DESC);
                            String price = jsonObject.getString(Constant.TAG_MENU_PRICE);
                            String image = jsonObject.getString(Constant.TAG_MENU_IMAGE);
                            String cat_id = jsonObject.getString(Constant.TAG_MENU_CAT);
                            String res_id = jsonObject.getString(Constant.TAG_MENU_REST_ID);

                            ItemMenu itemMenu = new ItemMenu(menu_id, menu_name, menu_type, image, desc, price, res_id, cat_id);
                            arrayListMenu.add(itemMenu);
                        }
                    }

                    ItemMenuCat itemMenuCat = new ItemMenuCat(id, name, hotel_id, arrayListMenu);
                    arrayList.add(itemMenuCat);
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
        menuCatListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}