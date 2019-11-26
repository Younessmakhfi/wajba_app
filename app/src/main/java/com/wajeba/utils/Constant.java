package com.wajeba.utils;

import com.wajeba.fooddelivery.BuildConfig;
import com.wajeba.items.ItemAbout;
import com.wajeba.items.ItemCart;
import com.wajeba.items.ItemMenuCat;
import com.wajeba.items.ItemOrderList;
import com.wajeba.items.ItemRestaurant;
import com.wajeba.items.ItemUser;

import java.io.Serializable;
import java.util.ArrayList;

public class Constant implements Serializable {

   public static String SERVER_URL = BuildConfig.SERVER_URL + "api.php";
   //public static String SERVER_URL ="http://www.wajeba.com/api.php";
    public static final String METHOD_LOGIN = "users_login";
    public static final String METHOD_REGISTER = "user_register";
    public static final String METHOD_FORGOT_PASSWORD = "user_forgot_pass";
    public static final String METHOD_PROFILE = "user_profile";
    public static final String METHOD_PROFILE_EDIT = "user_profile_update";
    public static final String METHOD_APP_DETAILS = "get_app_details";

    public static final String METHOD_HOME = "get_home";
    public static final String METHOD_CAT = "get_category";

    public static final String METHOD_REST_LIST = "get_all_restaurants";
    public static final String METHOD_REST_TOP_RATED = "top_rated";
    public static final String METHOD_REST_BY_CAT = "get_restaurant_by_cat_id";
    public static final String METHOD_REST_SEARCH = "get_restaurant_search";
    public static final String METHOD_REST_SINGLE = "get_restaurant_details";

    public static final String METHOD_MENU_CAT_BY_REST = "get_restaurant_by_menu_cat_id";

    public static final String METHOD_CART = "get_cart_list";
    public static final String METHOD_CART_CLEAR = "get_cart_item_empty";
    public static final String METHOD_CART_ADD_ITEM = "get_cart_add_update";
    public static final String METHOD_CART_DELETE_ITEM = "get_cart_item_delete";

    public static final String METHOD_ORDER_LIST = "user_order_list";
    public static final String METHOD_ORDER_CANCEL = "get_order_cancel";
    public static final String METHOD_ORDER_CHECKOUT = "get_order_add";

    public static final String METHOD_RATE = "rating";

    public static final String URL_ABOUT_US_LOGO = BuildConfig.SERVER_URL + "images/";

    public static final String TAG_ROOT = "FOOD_APP";
    public static final String TAG_FEATURED_REST = "featured_restaurant";
    public static final String TAG_LATEST_REST = "latest_restaurant";

    public static final String TAG_REST_ID = "restaurant_id";
    public static final String TAG_REST_NAME = "restaurant_name";
    public static final String TAG_REST_IMAGE = "restaurant_image";
    public static final String TAG_REST_ADDRESS = "restaurant_address";
    public static final String TAG_REST_TYPE = "restaurant_type";
    public static final String TAG_REST_MONDAY = "restaurant_open_mon";
    public static final String TAG_REST_TUESDAY = "restaurant_open_tues";
    public static final String TAG_REST_WEDNESDAY = "restaurant_open_wed";
    public static final String TAG_REST_THURSDAY = "restaurant_open_thur";
    public static final String TAG_REST_FRRIDAY = "restaurant_open_fri";
    public static final String TAG_REST_SATURDAY = "restaurant_open_sat";
    public static final String TAG_REST_SUNDAY = "restaurant_open_sun";
    public static final String TAG_REST_TOTAL_RATE = "total_rate";
    public static final String TAG_REST_AVG_RATE = "rate_avg";

    public static final String TAG_NONVEG = "Non Veg";
    public static final String TAG_VEG = "Veg";
    public static final String TAG_VEG_NONVEG = "Veg/Non Veg";

    public static final String TAG_ID = "id";

    public static final String TAG_CAT_ID = "cid";
    public static final String TAG_CAT_NAME = "category_name";
    public static final String TAG_CAT_IMAGE = "category_image";

    public static final String TAG_MENU_ID = "mid";
    public static final String TAG_MENU_NAME = "menu_name";
    public static final String TAG_MENU_TYPE = "menu_type";
    public static final String TAG_MENU_DESC = "menu_info";
    public static final String TAG_MENU_PRICE = "menu_price";
    public static final String TAG_MENU_IMAGE = "menu_image";
    public static final String TAG_MENU_CAT = "menu_cat_id";
    public static final String TAG_MENU_REST_ID = "rest_id";
    public static final String TAG_MENU_QYT = "menu_qty";
    public static final String TAG_MENU_TOTAL_PRICE = "menu_total_price";

    public static final String TAG_ORDER_ID = "order_id";
    public static final String TAG_ORDER_UNIQUE_ID = "order_unique_id";
    public static final String TAG_ORDER_ADDRESS = "order_address";
    public static final String TAG_ORDER_COMMENT = "order_comment";
    public static final String TAG_ORDER_DATE = "order_date";
    public static final String TAG_ORDER_ITEMS = "order_items";
    public static final String TAG_ORDER_REST_NAME = "rest_name";
    public static final String TAG_ORDER_STATUS = "status";

    public static final String TAG_RATING_REVIEW = "rating_review";
    public static final String TAG_RATING_ID = "r_id";
    public static final String TAG_RATING = "rate";
    public static final String TAG_RATING_MSG = "review";

    public static final String TAG_MSG = "msg";
    public static final String TAG_SUCCESS = "success";

    public static final String TAG_NAME_USER = "user_name";

    public static final String TAG_USER_ID = "user_id";
    public static final String TAG_USER_NAME = "name";
    public static final String TAG_USER_EMAIL = "email";
    public static final String TAG_USER_PHONE = "phone";
    public static final String TAG_USER_CITY = "city";
    public static final String TAG_USER_ADDRESS = "address";
    public static final String TAG_USER_IMAGE = "user_image";

    public static final String TAG_CART_ID = "cart_id";
    public static final String TAG_CART_MENU_ID = "menu_id";
    public static final String TAG_CART_COUNT = "cart_items";

    public static ItemUser itemUser;

    public static Boolean isUpdate = false;
    public static Boolean isLogged = false;
    public static Boolean isCartRefresh = false;
    public static Boolean isFromCheckOut = false;

    public static ItemAbout itemAbout;
    public static ArrayList<ItemRestaurant> arrayList_latest = new ArrayList<>();
    public static ArrayList<ItemMenuCat> arrayList_menuCat = new ArrayList<>();
    public static ArrayList<ItemCart> arrayList_cart = new ArrayList<>();
    public static ItemRestaurant itemRestaurant;
    public static ItemOrderList itemOrderList;

    public static Boolean isBannerAd = true, isInterAd = true;

    public static String search_text = "", search_type = "Restaurant";
    public static String[] search_type_array = {"Restaurant", "Menu"};
    public static int search_type_pos = 0;

    public static final String TAG_PENDING = "Pending";
    public static final String TAG_PROCESS = "Process";
    public static final String TAG_COMPLETE = "Complete";
    public static final String TAG_CANCEL = "Cancel";
    public static Boolean isCancelOrder = false;

    public static String ad_publisher_id = "pub-8356404931736973";
    public static String ad_banner_id = "ca-app-pub-3940256099942544/6300978111";
    public static String ad_inter_id = "ca-app-pub-3940256099942544/1033173712";

    public static int adCount = 0;
    public static int menuCount = 0;

    public static int adShow = 3;
}