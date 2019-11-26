package com.wajeba.fooddelivery;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.wajeba.adapter.AdapterMenuExpandable;
import com.wajeba.adapter.AdapterReview;
import com.wajeba.asyncTask.LoadMenuCat;
import com.wajeba.asyncTask.LoadRating;
import com.wajeba.asyncTask.LoadSingleHotel;
import com.wajeba.interfaces.MenuCatListener;
import com.wajeba.interfaces.RatingListener;
import com.wajeba.interfaces.SingleHotelListener;
import com.wajeba.items.ItemMenuCat;
import com.wajeba.items.ItemRestaurant;
import com.wajeba.utils.Constant;
import com.wajeba.utils.DBHelper;
import com.wajeba.utils.Methods;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HotelDetailsActivity extends AppCompatActivity {

    DBHelper dbHelper;
    private Toolbar toolbar;
    TextView textView_name, testView_address, textView_tot_rate, textView_hotelcat, textView_total_items, textView_total, textView_currency;
    ImageView imageView_Rest, imageView_info, imageView_editRate, imageView_type, iv_next;
    LinearLayout ll_rating, ll_checkout;
    Methods methods;
    ProgressDialog progressDialog;
    Dialog dialog;
    Menu menu;
    ArrayList<ExpandableGroup> arrayList = new ArrayList<>();

    ArrayList<ItemMenuCat> arrayList_menucat;
    RecyclerView recyclerView;
    AdapterMenuExpandable adapterMenuExpandable;
    Animation anim_slideup;
    MenuItem menuItem;
    Boolean isReviewGiven = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_details);

        methods = new Methods(this);
        dbHelper = new DBHelper(this);

        toolbar = findViewById(R.id.toolbar_rest_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Constant.itemRestaurant.getName());

        methods.setStatusColor(getWindow(), toolbar);

        progressDialog = new ProgressDialog(HotelDetailsActivity.this);
        progressDialog.setMessage(getString(R.string.loading));

        anim_slideup = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
        anim_slideup.setInterpolator(new OvershootInterpolator());

        arrayList_menucat = new ArrayList<>();

        ll_checkout = findViewById(R.id.ll_checkout);
        ll_rating = findViewById(R.id.ll_detail_rate);
        textView_total = findViewById(R.id.tv_details_total);
        textView_total_items = findViewById(R.id.tv_details_items);
        textView_currency = findViewById(R.id.tv_details_currency);
        textView_hotelcat = findViewById(R.id.tv_details_hotelcat);
        textView_tot_rate = findViewById(R.id.tv_latest_details_tot_rating);
        textView_name = findViewById(R.id.tv_details_name);
        imageView_Rest = findViewById(R.id.iv_details);
        imageView_info = findViewById(R.id.iv_details_info);
        imageView_editRate = findViewById(R.id.iv_add_review);
        imageView_type = findViewById(R.id.iv_details_type);
        iv_next = findViewById(R.id.iv_next);
        testView_address = findViewById(R.id.tv_details_address);

        textView_currency.setTypeface(null, Typeface.BOLD);
        textView_total.setTypeface(textView_total.getTypeface(), Typeface.BOLD);

        iv_next.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        textView_tot_rate.setText("" + Constant.itemRestaurant.getAvgRatings());
        textView_hotelcat.setText(Constant.itemRestaurant.getCname());
        textView_name.setText(Constant.itemRestaurant.getName());
        testView_address.setText(Constant.itemRestaurant.getAddress());

        switch (Constant.itemRestaurant.getType()) {
            case Constant.TAG_VEG:
                imageView_type.setImageResource(R.mipmap.veg);
                break;
            case Constant.TAG_NONVEG:
                imageView_type.setImageResource(R.mipmap.nonveg);
                break;
            default:
                imageView_type.setVisibility(View.GONE);
                break;
        }

        Picasso.get()
                .load(Constant.itemRestaurant.getImage())
                .fit().centerInside()
                .into(imageView_Rest);

        imageView_editRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.isLogged) {
                    if (methods.isNetworkAvailable()) {
                        openRateDialog();
                    } else {
                        methods.showToast(getString(R.string.error_net_not_conn));
                    }
                } else {
                    methods.showToast(getString(R.string.not_log));
                }
            }
        });

        imageView_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.itemRestaurant.getMonday() == null) {
                    loadSingleHotelApi("info");
                } else {
                    openHotelInfo();
                }
            }
        });

        ll_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.itemRestaurant.getArrayListReview() == null || isReviewGiven) {
                    if (methods.isNetworkAvailable()) {
                        isReviewGiven = false;
                        loadSingleHotelApi("review");
                    } else {
                        methods.showToast(getString(R.string.error_net_not_conn));
                    }
                } else {
                    openReviewList();
                }
            }
        });

        ll_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HotelDetailsActivity.this, CheckOut.class);
                intent.putExtra("from", "home");
                intent.putExtra("cart_ids", methods.getCartIds());
                intent.putExtra("total", textView_total.getText().toString());
                intent.putExtra("rest_name", Constant.arrayList_cart.get(0).getRestName());
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(true);
        }

        setCheckout();
        loadMenuCatApi();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        this.menu = menu;
        menu.findItem(R.id.menu_search).setVisible(false);
        menuItem = menu.findItem(R.id.menu_fav).setVisible(true);
        if (dbHelper != null && dbHelper.checkIsFav(Constant.itemRestaurant.getId())) {
            menuItem.setIcon(R.mipmap.fav_hover);
        } else {
            menuItem.setIcon(R.mipmap.fav);
        }
        methods.changeCart(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_cart_search:
                Intent intent = new Intent(HotelDetailsActivity.this, CartActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_fav:
                if (dbHelper.addtoFavourite(Constant.itemRestaurant)) {
                    menuItem.setIcon(R.mipmap.fav_hover);
                } else {
                    menuItem.setIcon(R.mipmap.fav);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openRateDialog() {
        dialog = new Dialog(HotelDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_review);

        final RatingBar ratingBar = dialog.findViewById(R.id.rating_addreview);
        final EditText editText = dialog.findViewById(R.id.et_add_review_msg);
        final Button button = dialog.findViewById(R.id.button_submit_rating);
        ImageView imageView = dialog.findViewById(R.id.iv_rate_close);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ratingBar.getRating() > 0 && !editText.getText().toString().trim().isEmpty()) {
                    if (methods.isNetworkAvailable()) {
                        loadRatingApi(String.valueOf(ratingBar.getRating()), editText.getText().toString().replace(" ", "%20"));
                    } else {
                        methods.showToast(getString(R.string.error_net_not_conn));
                    }
                } else {
                    methods.showToast(getString(R.string.select_rating_msg));
                }
            }
        });

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void loadRatingApi(String rate, String message) {
        LoadRating loadRating = new LoadRating(new RatingListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, String isWorkSuccess, String message, float rating) {
                progressDialog.dismiss();
                dialog.dismiss();

                if (success.equals("1")) {
                    if (isWorkSuccess.equals("1")) {
                        methods.showToast(message);

                        if (rating != -1) {
                            isReviewGiven = true;
                            Constant.itemRestaurant.setAvgRatings(rating);
                            Constant.itemRestaurant.setTotalRating(Constant.itemRestaurant.getTotalRating() + 1);
                            textView_tot_rate.setText(String.valueOf(Constant.itemRestaurant.getAvgRatings()));
                        }
                    }
                    methods.showToast(message);
                } else {
                    methods.showToast(getString(R.string.error_server_conneting));
                }
            }
        }, methods.getAPIRequest(Constant.METHOD_RATE, 0, Constant.itemRestaurant.getId(), "", "", "", "", "", rate, "", "", "", "", "", "", "", "", Constant.itemUser.getId(), message, null));

        loadRating.execute();
    }

    private void loadMenuCatApi() {
        if (methods.isNetworkAvailable()) {
            LoadMenuCat loadMenuCat = new LoadMenuCat(new MenuCatListener() {
                @Override
                public void onStart() {
                    Constant.arrayList_menuCat.clear();
                    arrayList_menucat.clear();
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemMenuCat> arrayListMenuCat) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {

                            arrayList_menucat.addAll(arrayListMenuCat);
                            for (int i = 0; i < arrayList_menucat.size(); i++) {
                                arrayList.add(new ExpandableGroup(arrayList_menucat.get(i).getName(), arrayList_menucat.get(i).getMenuArrayList()));
                            }
                            adapterMenuExpandable = new AdapterMenuExpandable(HotelDetailsActivity.this, arrayList);
                            recyclerView.setAdapter(adapterMenuExpandable);
                            if (adapterMenuExpandable.getItemCount() > 0) {
                                adapterMenuExpandable.toggleGroup(0);
                            }
                        } else {
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                        }
                    } else {
                        methods.showToast(getString(R.string.error_server_conneting));
                    }
                    progressDialog.dismiss();
                }
            }, methods.getAPIRequest(Constant.METHOD_MENU_CAT_BY_REST, 0, Constant.itemRestaurant.getId(), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", null));
            loadMenuCat.execute();
        } else {
            methods.showToast(getString(R.string.error_net_not_conn));
        }
    }

    private void loadSingleHotelApi(final String type) {
        if (methods.isNetworkAvailable()) {
            LoadSingleHotel loadSingleHotel = new LoadSingleHotel(Constant.itemRestaurant, new SingleHotelListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ItemRestaurant itemRestaurant) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            Constant.itemRestaurant = itemRestaurant;
                            if (type.equals("review")) {
                                openReviewList();
                            } else {
                                openHotelInfo();
                            }
                        } else {
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                        }
                    } else {
                        methods.showToast(getString(R.string.error_server_conneting));
                    }
                    progressDialog.dismiss();
                }
            }, methods.getAPIRequest(Constant.METHOD_REST_SINGLE, 0, Constant.itemRestaurant.getId(), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", null));
            loadSingleHotel.execute();
        } else {
            methods.showToast(getString(R.string.error_net_not_conn));
        }
    }

    private void openHotelInfo() {
        final Dialog dialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new Dialog(HotelDetailsActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            dialog = new Dialog(HotelDetailsActivity.this);
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_hotel_info);

        ImageView iv_close = dialog.findViewById(R.id.iv_info_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView tv_name = dialog.findViewById(R.id.tv_info_name);
        TextView tv_schedule = dialog.findViewById(R.id.tv_info_schedule);
        TextView tv_address = dialog.findViewById(R.id.tv_info_address);
        ImageView iv_view = dialog.findViewById(R.id.iv_info_view);

        tv_name.setText(Constant.itemRestaurant.getName());
        tv_address.setText(Constant.itemRestaurant.getAddress());
        tv_schedule.setText(methods.getOpenTime(Constant.itemRestaurant));

        iv_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSchedule();
            }
        });

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void openSchedule() {
        final Dialog dialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new Dialog(HotelDetailsActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            dialog = new Dialog(HotelDetailsActivity.this);
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_schedule);

        ImageView iv_close = dialog.findViewById(R.id.iv_schedule_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView textView_mon = dialog.findViewById(R.id.tv_dialog_monday);
        TextView textView_tue = dialog.findViewById(R.id.tv_dialog_tuesday);
        TextView textView_wed = dialog.findViewById(R.id.tv_dialog_wednesday);
        TextView textView_thur = dialog.findViewById(R.id.tv_dialog_thursday);
        TextView textView_fri = dialog.findViewById(R.id.tv_dialog_friday);
        TextView textView_sat = dialog.findViewById(R.id.tv_dialog_saturday);
        TextView textView_sun = dialog.findViewById(R.id.tv_dialog_sunday);

        textView_sun.setText(Constant.itemRestaurant.getSunday());
        textView_mon.setText(Constant.itemRestaurant.getMonday());
        textView_tue.setText(Constant.itemRestaurant.getTuesday());
        textView_wed.setText(Constant.itemRestaurant.getWednesday());
        textView_thur.setText(Constant.itemRestaurant.getThursday());
        textView_fri.setText(Constant.itemRestaurant.getFriday());
        textView_sat.setText(Constant.itemRestaurant.getSaturday());

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void openReviewList() {
        final Dialog dialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new Dialog(HotelDetailsActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            dialog = new Dialog(HotelDetailsActivity.this);
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_reviewlist);

        ImageView iv_close = dialog.findViewById(R.id.iv_reviewlist_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView textView_empty = dialog.findViewById(R.id.tv_reviewlist_empty);
        TextView textView_tot_review = dialog.findViewById(R.id.tv_reviewlist_tot_rating);

        textView_tot_review.setText(String.valueOf(Constant.itemRestaurant.getAvgRatings()));

        RecyclerView recyclerView = dialog.findViewById(R.id.rv_reviewlist);

        recyclerView.setLayoutManager(new LinearLayoutManager(HotelDetailsActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        AdapterReview adapterReview = new AdapterReview(HotelDetailsActivity.this, Constant.itemRestaurant.getArrayListReview());
        recyclerView.setAdapter(adapterReview);

        if (Constant.itemRestaurant.getArrayListReview().size() == 0) {
            textView_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textView_empty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onResume() {
        changeMenu();
        if (adapterMenuExpandable != null && adapterMenuExpandable.getItemCount() > 0) {
            adapterMenuExpandable.notifyDataSetChanged();
        }

        super.onResume();
    }

    public void changeMenu() {
        if (toolbar != null && menu != null && menu.findItem(R.id.menu_cart_search) != null) {
            methods.changeCart(menu);
            setCheckout();
        }
    }

    public void setCheckout() {
        int total_items = 0;
        float totalAmount = 0;
        if (Constant.arrayList_cart.size() > 0) {
            if (ll_checkout.getVisibility() == View.GONE) {
                ll_checkout.setVisibility(View.VISIBLE);
                ll_checkout.startAnimation(anim_slideup);
            }

            for (int i = 0; i < Constant.arrayList_cart.size(); i++) {
                total_items = total_items + Integer.parseInt(Constant.arrayList_cart.get(i).getMenuQty());
                totalAmount = totalAmount + (Integer.parseInt(Constant.arrayList_cart.get(i).getMenuQty()) * Float.parseFloat(Constant.arrayList_cart.get(i).getMenuPrice()));
            }
        } else {
            ll_checkout.setVisibility(View.GONE);
        }

        textView_total_items.setText(String.valueOf(total_items));
        textView_total.setText(String.valueOf(totalAmount));
    }
}