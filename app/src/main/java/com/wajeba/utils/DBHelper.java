package com.wajeba.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wajeba.items.ItemAbout;
import com.wajeba.items.ItemRestaurant;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "fooddelivery.db";
    private SQLiteDatabase db;

    // Table Name
    private static final String TABLE_ABOUT = "about";
    private static final String TABLE_REST = "rest";

    private static final String TAG_ID = "id";
    private static final String TAG_REST_ID = "rid";
    private static final String TAG_REST_NAME = "name";
    private static final String TAG_REST_IMAGE = "image";
    private static final String TAG_REST_TYPE = "type";
    private static final String TAG_REST_ADDRESS = "address";
    private static final String TAG_REST_AVG_RATING = "avgRating";
    private static final String TAG_REST_TOTAL_RATING = "totalRating";
    private static final String TAG_REST_CAT_NAME = "cat_name";

    private static final String TAG_ABOUT_NAME = "name";
    private static final String TAG_ABOUT_LOGO = "logo";
    private static final String TAG_ABOUT_VERSION = "version";
    private static final String TAG_ABOUT_AUTHOR = "author";
    private static final String TAG_ABOUT_CONTACT = "contact";
    private static final String TAG_ABOUT_EMAIL = "email";
    private static final String TAG_ABOUT_WEBSITE = "website";
    private static final String TAG_ABOUT_DESC = "description";
    private static final String TAG_ABOUT_DEVELOPED = "developed";
    private static final String TAG_ABOUT_PRIVACY = "privacy";
    private static final String TAG_ABOUT_PUB_ID = "ad_pub";
    private static final String TAG_ABOUT_BANNER_ID = "ad_banner";
    private static final String TAG_ABOUT_INTER_ID = "ad_inter";
    private static final String TAG_ABOUT_IS_BANNER = "isbanner";
    private static final String TAG_ABOUT_IS_INTER = "isinter";
    private static final String TAG_ABOUT_CLICK = "click";

    private String[] columns_about = new String[]{TAG_ABOUT_NAME, TAG_ABOUT_LOGO, TAG_ABOUT_VERSION, TAG_ABOUT_AUTHOR, TAG_ABOUT_CONTACT,
            TAG_ABOUT_EMAIL, TAG_ABOUT_WEBSITE, TAG_ABOUT_DESC, TAG_ABOUT_DEVELOPED, TAG_ABOUT_PRIVACY, TAG_ABOUT_PUB_ID,
            TAG_ABOUT_BANNER_ID, TAG_ABOUT_INTER_ID, TAG_ABOUT_IS_BANNER, TAG_ABOUT_IS_INTER, TAG_ABOUT_CLICK};

    private String[] columns_rest = new String[]{TAG_ID, TAG_REST_ID, TAG_REST_NAME, TAG_REST_IMAGE, TAG_REST_TYPE, TAG_REST_ADDRESS,
            TAG_REST_AVG_RATING, TAG_REST_TOTAL_RATING, TAG_REST_CAT_NAME};

    // Creating table about
    private static final String CREATE_TABLE_ABOUT = "create table " + TABLE_ABOUT + "(" + TAG_ABOUT_NAME
            + " TEXT, " + TAG_ABOUT_LOGO + " TEXT, " + TAG_ABOUT_VERSION + " TEXT, " + TAG_ABOUT_AUTHOR + " TEXT" +
            ", " + TAG_ABOUT_CONTACT + " TEXT, " + TAG_ABOUT_EMAIL + " TEXT, " + TAG_ABOUT_WEBSITE + " TEXT, " + TAG_ABOUT_DESC + " TEXT" +
            ", " + TAG_ABOUT_DEVELOPED + " TEXT, " + TAG_ABOUT_PRIVACY + " TEXT, " + TAG_ABOUT_PUB_ID + " TEXT, " + TAG_ABOUT_BANNER_ID + " TEXT" +
            ", " + TAG_ABOUT_INTER_ID + " TEXT, " + TAG_ABOUT_IS_BANNER + " TEXT, " + TAG_ABOUT_IS_INTER + " TEXT, " + TAG_ABOUT_CLICK + " TEXT);";

    // Creating table rest
    private static final String CREATE_TABLE_REST = "create table " + TABLE_REST + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_REST_ID + " TEXT," +
            TAG_REST_NAME + " TEXT," +
            TAG_REST_IMAGE + " TEXT," +
            TAG_REST_TYPE + " TEXT," +
            TAG_REST_ADDRESS + " TEXT," +
            TAG_REST_AVG_RATING + " TEXT," +
            TAG_REST_TOTAL_RATING + " TEXT," +
            TAG_REST_CAT_NAME + " TEXT);";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_ABOUT);
            db.execSQL(CREATE_TABLE_REST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ItemRestaurant> getRestaurant() {
        ArrayList<ItemRestaurant> arrayList = new ArrayList<>();
        try {
            Cursor cursor = db.query(TABLE_REST, columns_rest, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {

                    String id = cursor.getString(cursor.getColumnIndex(TAG_REST_ID));
                    String name = cursor.getString(cursor.getColumnIndex(TAG_REST_NAME));
                    String image = Methods.decrypt(cursor.getString(cursor.getColumnIndex(TAG_REST_IMAGE)));
                    String type = cursor.getString(cursor.getColumnIndex(TAG_REST_TYPE));
                    String address = cursor.getString(cursor.getColumnIndex(TAG_REST_ADDRESS));
                    float avgRating = Float.parseFloat(cursor.getString(cursor.getColumnIndex(TAG_REST_AVG_RATING)));
                    int totalRating = Integer.parseInt(cursor.getString(cursor.getColumnIndex(TAG_REST_TOTAL_RATING)));
                    String cname = cursor.getString(cursor.getColumnIndex(TAG_REST_CAT_NAME));

                    ItemRestaurant itemRestaurant = new ItemRestaurant(id,name,image,type,address,avgRating,totalRating,cname);
                    arrayList.add(itemRestaurant);

                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    public Boolean checkIsFav(String id) {
        Cursor cursor = db.query(TABLE_REST, columns_rest, TAG_REST_ID + "=" + id, null, null, null, null);
        Boolean isFav = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return isFav;
    }

    public Boolean addtoFavourite(ItemRestaurant itemRestaurant) {
        if(checkIsFav(itemRestaurant.getId())) {
            removeFromFavourite(itemRestaurant.getId());
            return false;
        } else {
            try {
                String avgRating = String.valueOf(itemRestaurant.getAvgRatings());
                String totalRating = String.valueOf(itemRestaurant.getTotalRating());

                ContentValues contentValues = new ContentValues();
                contentValues.put(TAG_REST_ID, itemRestaurant.getId());
                contentValues.put(TAG_REST_NAME, itemRestaurant.getName());
                contentValues.put(TAG_REST_IMAGE, Methods.encrypt(itemRestaurant.getImage().replace(" ", "%20")));
                contentValues.put(TAG_REST_TYPE, itemRestaurant.getType());
                contentValues.put(TAG_REST_ADDRESS, itemRestaurant.getAddress());
                contentValues.put(TAG_REST_AVG_RATING, avgRating);
                contentValues.put(TAG_REST_TOTAL_RATING, totalRating);
                contentValues.put(TAG_REST_CAT_NAME, itemRestaurant.getCname());

                db.insert(TABLE_REST, null, contentValues);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    private void removeFromFavourite(String id) {
        db.delete(TABLE_REST, TAG_REST_ID + "=" + id, null);
    }

    public void addtoAbout() {
        try {
            db.delete(TABLE_ABOUT, null, null);

            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG_ABOUT_NAME, Constant.itemAbout.getAppName());
            contentValues.put(TAG_ABOUT_LOGO, Constant.itemAbout.getAppLogo());
            contentValues.put(TAG_ABOUT_VERSION, Constant.itemAbout.getAppVersion());
            contentValues.put(TAG_ABOUT_AUTHOR, Constant.itemAbout.getAuthor());
            contentValues.put(TAG_ABOUT_CONTACT, Constant.itemAbout.getContact());
            contentValues.put(TAG_ABOUT_EMAIL, Constant.itemAbout.getEmail());
            contentValues.put(TAG_ABOUT_WEBSITE, Constant.itemAbout.getWebsite());
            contentValues.put(TAG_ABOUT_DESC, Constant.itemAbout.getAppDesc());
            contentValues.put(TAG_ABOUT_DEVELOPED, Constant.itemAbout.getDevelopedby());
            contentValues.put(TAG_ABOUT_PRIVACY, Constant.itemAbout.getPrivacy());
            contentValues.put(TAG_ABOUT_PUB_ID, Constant.ad_publisher_id);
            contentValues.put(TAG_ABOUT_BANNER_ID, Constant.ad_banner_id);
            contentValues.put(TAG_ABOUT_INTER_ID, Constant.ad_inter_id);
            contentValues.put(TAG_ABOUT_IS_BANNER, Constant.isBannerAd);
            contentValues.put(TAG_ABOUT_IS_INTER, Constant.isInterAd);
            contentValues.put(TAG_ABOUT_CLICK, Constant.adShow);

            db.insert(TABLE_ABOUT, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean getAbout() {
        Cursor c = db.query(TABLE_ABOUT, columns_about, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                String appname = c.getString(c.getColumnIndex(TAG_ABOUT_NAME));
                String applogo = c.getString(c.getColumnIndex(TAG_ABOUT_LOGO));
                String desc = c.getString(c.getColumnIndex(TAG_ABOUT_DESC));
                String appversion = c.getString(c.getColumnIndex(TAG_ABOUT_VERSION));
                String appauthor = c.getString(c.getColumnIndex(TAG_ABOUT_AUTHOR));
                String appcontact = c.getString(c.getColumnIndex(TAG_ABOUT_CONTACT));
                String email = c.getString(c.getColumnIndex(TAG_ABOUT_EMAIL));
                String website = c.getString(c.getColumnIndex(TAG_ABOUT_WEBSITE));
                String privacy = c.getString(c.getColumnIndex(TAG_ABOUT_PRIVACY));
                String developedby = c.getString(c.getColumnIndex(TAG_ABOUT_DEVELOPED));

                Constant.ad_banner_id = c.getString(c.getColumnIndex(TAG_ABOUT_BANNER_ID));
                Constant.ad_inter_id = c.getString(c.getColumnIndex(TAG_ABOUT_INTER_ID));
                Constant.isBannerAd = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_BANNER)));
                Constant.isInterAd = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_INTER)));
                Constant.ad_publisher_id = c.getString(c.getColumnIndex(TAG_ABOUT_PUB_ID));
                Constant.adShow = Integer.parseInt(c.getString(c.getColumnIndex(TAG_ABOUT_CLICK)));

                Constant.itemAbout = new ItemAbout(appname, applogo, desc, appversion, appauthor, appcontact, email, website, privacy, developedby);
            }
            c.close();
            return true;
        } else {
            c.close();
            return false;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}  