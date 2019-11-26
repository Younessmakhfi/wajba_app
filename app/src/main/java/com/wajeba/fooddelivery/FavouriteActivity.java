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
import com.wajeba.interfaces.ClickListener;
import com.wajeba.interfaces.InterAdListener;
import com.wajeba.items.ItemRestaurant;
import com.wajeba.utils.Constant;
import com.wajeba.utils.DBHelper;
import com.wajeba.utils.Methods;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FavouriteActivity extends AppCompatActivity {

    Toolbar toolbar;
    DBHelper dbHelper;
    private Methods methods;
    private AdapterHotelList adapterHotelList;
    private RecyclerView recyclerView;
    private ArrayList<ItemRestaurant> arrayList_hotel;
    private SearchView searchView;
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

        dbHelper = new DBHelper(this);
        methods = new Methods(this, new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                Intent intent = new Intent(FavouriteActivity.this, HotelDetailsActivity.class);
                Constant.itemRestaurant = arrayList_hotel.get(getPosition(Integer.parseInt(adapterHotelList.getID(position))));
                startActivity(intent);
            }
        });

        toolbar = findViewById(R.id.toolbar_bycat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.favourites));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayout ll_adView = findViewById(R.id.ll_adView_bycat);
        methods.showBannerAd(ll_adView);

        arrayList_hotel = new ArrayList<>();
        errr_msg = getString(R.string.no_data_found);

        ll_empty = findViewById(R.id.ll_empty);
        textView_empty = findViewById(R.id.textView_empty_msg);
        button_try = findViewById(R.id.button_empty_try);

        progressBar = findViewById(R.id.pb_bycat);
        progressBar.setVisibility(View.GONE);
        button_try.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.rv_hotel_bycat);
        recyclerView.setLayoutManager(new GridLayoutManager(FavouriteActivity.this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        arrayList_hotel = dbHelper.getRestaurant();
        adapterHotelList = new AdapterHotelList(FavouriteActivity.this, arrayList_hotel, new ClickListener() {
            @Override
            public void onClick(int position) {
                methods.showInterAd(position, "");
            }
        });
        recyclerView.setAdapter(adapterHotelList);

        setEmpty();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        this.menu = menu;
        methods.changeCart(menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (adapterHotelList != null) {
                if (!searchView.isIconified()) {
                    adapterHotelList.getFilter().filter(s);
                    adapterHotelList.notifyDataSetChanged();
                }
            }
            return true;
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_cart_search:
                Intent intent = new Intent(FavouriteActivity.this, CartActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setEmpty() {
        if (arrayList_hotel.size() > 0) {
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
        for (int i = 0; i < arrayList_hotel.size(); i++) {
            if (id == Integer.parseInt(arrayList_hotel.get(i).getId())) {
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

        if (adapterHotelList != null && dbHelper != null) {
            arrayList_hotel.clear();
            arrayList_hotel.addAll(dbHelper.getRestaurant());
            adapterHotelList.notifyDataSetChanged();
        }

        super.onResume();
    }
}