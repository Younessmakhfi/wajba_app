package com.wajeba.fooddelivery;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wajeba.adapter.AdapterHotelList;
import com.wajeba.asyncTask.LoadHotel;
import com.wajeba.interfaces.ClickListener;
import com.wajeba.interfaces.InterAdListener;
import com.wajeba.interfaces.RestListener;
import com.wajeba.items.ItemRestaurant;
import com.wajeba.utils.Constant;
import com.wajeba.utils.Methods;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class HotelBySearchActivity extends AppCompatActivity {

    Toolbar toolbar;
    private Methods methods;
    private AdapterHotelList adapterHotelList;
    private RecyclerView recyclerView;
    private ArrayList<ItemRestaurant> arrayList;
    private Menu menu;
    private CircularProgressBar progressBar;

    TextView textView_empty;
    LinearLayout ll_empty;
    String errr_msg;
    AppCompatButton button_try;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_by_cat);

        methods = new Methods(this, new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                Intent intent = new Intent(HotelBySearchActivity.this, HotelDetailsActivity.class);
                Constant.itemRestaurant = arrayList.get(getPosition(Integer.parseInt(adapterHotelList.getID(position))));
                startActivity(intent);
            }
        });

        toolbar = findViewById(R.id.toolbar_bycat);
        toolbar.setTitle(Constant.search_text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayout ll_adView = findViewById(R.id.ll_adView_bycat);
        methods.showBannerAd(ll_adView);

        arrayList = new ArrayList<>();

        ll_empty = findViewById(R.id.ll_empty);
        textView_empty = findViewById(R.id.textView_empty_msg);
        button_try = findViewById(R.id.button_empty_try);

        progressBar = findViewById(R.id.pb_bycat);
        recyclerView = findViewById(R.id.rv_hotel_bycat);
        recyclerView.setLayoutManager(new GridLayoutManager(HotelBySearchActivity.this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        loadHotelApi();

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHotelApi();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        this.menu = menu;
        methods.changeCart(menu);

        MenuItem item_search = menu.findItem(R.id.menu_search);

        final MenuItem item_cart = menu.findItem(R.id.menu_cart_search);
        final MenuItem item_filter = menu.findItem(R.id.menu_filter).setIcon(R.mipmap.filter);

        item_search.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_ALWAYS);
        SearchView searchView = (SearchView) item_search.getActionView();
        searchView.setOnQueryTextListener(queryTextListener);

        item_search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                item_filter.setVisible(false);
                if (Constant.isLogged) {
                    item_cart.setVisible(true);
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                item_filter.setVisible(true);
                if (Constant.isLogged) {
                    item_cart.setVisible(false);
                }
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            Constant.search_text = s;
            getSupportActionBar().setTitle(Constant.search_text);
            loadHotelApi();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_cart_search:
                Intent intent = new Intent(HotelBySearchActivity.this, CartActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_filter:
                methods.openSearchFilter();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadHotelApi() {
        if (methods.isNetworkAvailable()) {
            LoadHotel loadHotel = new LoadHotel(new RestListener() {
                @Override
                public void onStart() {
                    arrayList.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    ll_empty.setVisibility(View.GONE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemRestaurant> arrayListRestaurant) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            errr_msg = getString(R.string.no_data_found);
                            arrayList.addAll(arrayListRestaurant);
                            setAdapter();
                        } else {
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                        }
                    } else {
                        errr_msg = getString(R.string.error_server_conneting);
                        setEmpty();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }, methods.getAPIRequest(Constant.METHOD_REST_SEARCH, 0, "", "", "", "", Constant.search_text.replace(" ", "%20"), Constant.search_type, "", "", "", "", "", "", "", "", "", "", "", null));
            loadHotel.execute();
        } else {
            errr_msg = getString(R.string.error_net_not_conn);
            setEmpty();
        }
    }

    private void setAdapter() {
        adapterHotelList = new AdapterHotelList(HotelBySearchActivity.this, arrayList, new ClickListener() {
            @Override
            public void onClick(int position) {
                methods.showInterAd(position, "");
            }
        });
        recyclerView.setAdapter(adapterHotelList);
        progressBar.setVisibility(View.GONE);
        setEmpty();
    }

    public void setEmpty() {
        if (arrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            recyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    public int getPosition(int id) {
        int count = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            if (id == Integer.parseInt(arrayList.get(i).getId())) {
                count = i;
                break;
            }
        }
        return count;
    }

    @Override
    protected void onResume() {
        if (toolbar != null && menu != null && menu.findItem(R.id.menu_cart_search) != null) {
            methods.changeCart(menu);
        }
        super.onResume();
    }
}