package com.wajeba.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wajeba.fooddelivery.BuildConfig;
import com.wajeba.fooddelivery.CartActivity;
import com.wajeba.fooddelivery.LoginActivity;
import com.wajeba.fooddelivery.R;
import com.wajeba.interfaces.InterAdListener;
import com.wajeba.items.ItemRestaurant;
import com.wajeba.items.ItemUser;

import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class Methods {

    private Context context;
    private InterAdListener interAdListener;

    private static final String ALGORITHM = "Blowfish";
    private static final String MODE = "Blowfish/CBC/PKCS5Padding";

    public Methods(Context context) {
        this.context = context;
    }

    public Methods(Context context, InterAdListener interAdListener) {
        this.context = context;
        this.interAdListener = interAdListener;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfoMob = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo netInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return (netInfoMob != null && netInfoMob.isConnectedOrConnecting()) || (netInfoWifi != null && netInfoWifi.isConnectedOrConnecting());
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    private static void openLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("from", "app");
        context.startActivity(intent);
    }

    private void logout(Activity activity) {
        SharedPref sharePref = new SharedPref(context);
        sharePref.setIsAutoLogin(false);

        Constant.isLogged = false;
        Constant.itemUser = new ItemUser("", "", "", "", "", "");
        Constant.menuCount = 0;
        Constant.arrayList_cart.clear();
        Intent intent1 = new Intent(context, LoginActivity.class);
        intent1.putExtra("from","");
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent1);
        activity.finish();
    }

    public void clickLogin() {
        if (Constant.isLogged) {
            logout((Activity) context);
            ((Activity) context).finish();
            showToast(context.getString(R.string.logout_success));
        } else {
            openLogin(context);
        }
    }

    public void setStatusColor(Window window, Toolbar toolbar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getResources().getColor(R.color.status_bar));
            if (toolbar != null) {
                toolbar.setElevation(10);
            }
        }
    }

    public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            return packageManager.getApplicationInfo(packagename, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void changeCart(Menu menu) {
        View cart = menu.findItem(R.id.menu_cart_search).getActionView();
        if (Constant.isLogged) {
            TextView textView = cart.findViewById(R.id.textView_menu_no);
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
            textView.setText("" + Constant.menuCount);

            cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Constant.isLogged) {
                        Intent intent = new Intent(context, CartActivity.class);
                        context.startActivity(intent);
                    } else {
                        Intent i = new Intent(context, LoginActivity.class);
                        i.putExtra("from","");
                        context.startActivity(i);
                    }
                }
            });
        } else {
            MenuItem menuItem = menu.findItem(R.id.menu_cart_search);
            menuItem.setVisible(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void forceRTLIfSupported(Window window) {
        if (context.getResources().getString(R.string.isRTL).equals("true")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
    }

    public String getPathImage(Uri uri) {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String filePath = "";
                String wholeID = DocumentsContract.getDocumentId(uri);

                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];

                String[] column = {MediaStore.Images.Media.DATA};

                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";

                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
                return filePath;
            } else {

                if (uri == null) {
                    return null;
                }
                // try to retrieve the image from the media store first
                // this will only work for images selected from gallery
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    int column_index = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String retunn = cursor.getString(column_index);
                    cursor.close();
                    return retunn;
                }
                // this is our fallback here
                return uri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (uri == null) {
                return null;
            }
            // try to retrieve the image from the media store first
            // this will only work for images selected from gallery
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String returnn = cursor.getString(column_index);
                cursor.close();
                return returnn;
            }
            // this is our fallback here
            return uri.getPath();
        }
    }

    public String getOpenTime(ItemRestaurant itemRestaurant) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.SUNDAY:
                return itemRestaurant.getSunday();
            case Calendar.MONDAY:
                return itemRestaurant.getMonday();
            case Calendar.TUESDAY:
                return itemRestaurant.getTuesday();
            case Calendar.WEDNESDAY:
                return itemRestaurant.getWednesday();
            case Calendar.THURSDAY:
                return itemRestaurant.getThursday();
            case Calendar.FRIDAY:
                return itemRestaurant.getFriday();
            case Calendar.SATURDAY:
                return itemRestaurant.getSaturday();
            default:
                return "";
        }
    }

    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public boolean isPasswordValid(String password) {
        return password.length() > 0;
    }

    public String getCartIds() {
        String ids = "";

        if (Constant.arrayList_cart.size() > 0) {
            ids = Constant.arrayList_cart.get(0).getId();
            for (int i = 1; i < Constant.arrayList_cart.size(); i++) {
                ids = ids + "," + Constant.arrayList_cart.get(i).getId();
            }
        }
        return ids;
    }

    private void showPersonalizedAds(LinearLayout linearLayout) {
        if (Constant.isBannerAd) {
            AdView adView = new AdView(context);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.setAdUnitId(Constant.ad_banner_id);
            adView.setAdSize(AdSize.SMART_BANNER);
            linearLayout.addView(adView);
            adView.loadAd(adRequest);
        }
    }

    private void showNonPersonalizedAds(LinearLayout linearLayout) {
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        if (Constant.isBannerAd) {
            AdView adView = new AdView(context);
            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build();
            adView.setAdUnitId(Constant.ad_banner_id);
            adView.setAdSize(AdSize.SMART_BANNER);
            linearLayout.addView(adView);
            adView.loadAd(adRequest);
        }
    }

    public void showBannerAd(LinearLayout linearLayout) {
        if (isNetworkAvailable()) {
            if (ConsentInformation.getInstance(context).getConsentStatus() == ConsentStatus.NON_PERSONALIZED) {
                showNonPersonalizedAds(linearLayout);
            } else {
                showPersonalizedAds(linearLayout);
            }
        }
    }

    public void showInterAd(final int pos, final String type) {
        if (Constant.isInterAd) {
            Constant.adCount = Constant.adCount + 1;
            if (Constant.adCount % Constant.adShow == 0) {
                final InterstitialAd interstitialAd = new InterstitialAd(context);
                AdRequest adRequest;
                if (ConsentInformation.getInstance(context).getConsentStatus() == ConsentStatus.PERSONALIZED) {
                    adRequest = new AdRequest.Builder()
                            .build();
                } else {
                    Bundle extras = new Bundle();
                    extras.putString("npa", "1");
                    adRequest = new AdRequest.Builder()
                            .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                            .build();
                }
                interstitialAd.setAdUnitId(Constant.ad_inter_id);
                interstitialAd.loadAd(adRequest);
                interstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        interstitialAd.show();
                    }

                    public void onAdClosed() {
                        interAdListener.onClick(pos, type);
                        super.onAdClosed();
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        interAdListener.onClick(pos, type);
                        super.onAdFailedToLoad(i);
                    }
                });
            } else {
                interAdListener.onClick(pos, type);
            }
        } else {
            interAdListener.onClick(pos, type);
        }
    }

    public void openSearchFilter() {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(context.getString(R.string.filter))
                .setSingleChoiceItems(Constant.search_type_array, Constant.search_type_pos, null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        Constant.search_type_pos = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        if(Constant.search_type_pos == 0) {
                            Constant.search_type = "Restaurant";
                        } else {
                            Constant.search_type = "menu";
                        }
                    }
                })
                .show();
    }

    public static String encrypt(String value) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(BuildConfig.ENC_KEY.getBytes(), ALGORITHM);
        Cipher cipher;
        byte[] values;
        try {
            cipher = Cipher.getInstance(MODE);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(BuildConfig.IV.getBytes()));
            values = cipher.doFinal(value.getBytes());
            return Base64.encodeToString(values, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String decrypt(String value) {
        byte[] values;
        Cipher cipher;
        try {
            values = Base64.decode(value, Base64.DEFAULT);
            SecretKeySpec secretKeySpec = new SecretKeySpec(BuildConfig.ENC_KEY.getBytes(), ALGORITHM);
            cipher = Cipher.getInstance(MODE);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(BuildConfig.IV.getBytes()));
            return new String(cipher.doFinal(values));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void getVerifyDialog(String title, String message) {
        final androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.ThemeDialog);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    public RequestBody getAPIRequest(String method, int page, String restID, String objectID, String catID, String menuID, String searchText, String searchType, String rate, String price, String quantity, String email, String password, String name, String phone, String city, String address, String userID, String message, File file) {
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", method);
        jsObj.addProperty("package_name", context.getPackageName());

        switch (method) {
            case Constant.METHOD_LOGIN:

                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);

                break;
            case Constant.METHOD_REGISTER:

                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);
                jsObj.addProperty("user_image", "");

                break;
            case Constant.METHOD_FORGOT_PASSWORD:

                jsObj.addProperty("email", email);

                break;
            case Constant.METHOD_PROFILE:

                jsObj.addProperty("id", userID);

                break;
            case Constant.METHOD_PROFILE_EDIT:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);
                jsObj.addProperty("city", city);
                jsObj.addProperty("address", address);

                break;
            case Constant.METHOD_REST_SEARCH:

                jsObj.addProperty("search_type", searchType);
                jsObj.addProperty("search_text", searchText);

                break;
            case Constant.METHOD_REST_SINGLE:

                jsObj.addProperty("restaurant_id", restID);

                break;
            case Constant.METHOD_REST_BY_CAT:

                jsObj.addProperty("cat_id", catID);

                break;
            case Constant.METHOD_MENU_CAT_BY_REST:

                jsObj.addProperty("menu_cat", restID);

                break;
            case Constant.METHOD_CART:

                jsObj.addProperty("user_id", userID);

                break;
            case Constant.METHOD_CART_CLEAR:

                jsObj.addProperty("user_id", userID);

                break;
            case Constant.METHOD_CART_DELETE_ITEM:

                jsObj.addProperty("cart_id", objectID);

                break;
            case Constant.METHOD_ORDER_LIST:

                jsObj.addProperty("user_id", userID);

                break;
            case Constant.METHOD_ORDER_CANCEL:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("order_unique_id", objectID);

                break;
            case Constant.METHOD_RATE:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("rate", rate);
                jsObj.addProperty("rest_id", restID);
                jsObj.addProperty("msg", message);

                break;
            case Constant.METHOD_CART_ADD_ITEM:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("rest_id", restID);
                jsObj.addProperty("menu_id", menuID);
                jsObj.addProperty("restaurants_id", restID);
                jsObj.addProperty("menu_name", name);
                jsObj.addProperty("menu_qty", quantity);
                jsObj.addProperty("menu_price", price);

                break;
            case Constant.METHOD_ORDER_CHECKOUT:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("order_address", address);
                jsObj.addProperty("order_comment", message);
                jsObj.addProperty("cat_ids", objectID);

                break;
        }

        if (Constant.METHOD_PROFILE_EDIT.equals(method) && file != null) {
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
            return new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("user_image", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file))
                    .addFormDataPart("data", API.toBase64(jsObj.toString()))
                    .build();
        } else {
            return new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("data", API.toBase64(jsObj.toString()))
                    .build();
        }
    }
}