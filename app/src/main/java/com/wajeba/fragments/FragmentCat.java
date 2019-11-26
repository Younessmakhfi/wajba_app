package com.wajeba.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wajeba.adapter.AdapterCat;
import com.wajeba.asyncTask.LoadCat;
import com.wajeba.fooddelivery.CartActivity;
import com.wajeba.fooddelivery.HotelByCatActivity;
import com.wajeba.fooddelivery.MainActivity;
import com.wajeba.fooddelivery.R;
import com.wajeba.interfaces.CategoryListener;
import com.wajeba.interfaces.InterAdListener;
import com.wajeba.items.ItemCat;
import com.wajeba.utils.Constant;
import com.wajeba.utils.Methods;
import com.wajeba.utils.RecyclerItemClickListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentCat extends Fragment {

    private Methods methods;
    private RecyclerView recyclerView;
    private ArrayList<ItemCat> arrayList;
    private AdapterCat adapter;
    private SearchView searchView;
    private Menu menu;
    private CircularProgressBar progressBar;

    private TextView textView_empty;
    private LinearLayout ll_empty;
    private String errr_msg;
    private AppCompatButton button_try;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cat, container, false);

        methods = new Methods(getActivity(), new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                int real_pos = getPosition(adapter.getID(position));
                Intent intent = new Intent(getActivity(), HotelByCatActivity.class);
                intent.putExtra("cid", arrayList.get(real_pos).getId());
                intent.putExtra("cname", arrayList.get(real_pos).getName());
                startActivity(intent);
            }
        });

        arrayList = new ArrayList<>();

        ll_empty = rootView.findViewById(R.id.ll_empty);
        textView_empty = rootView.findViewById(R.id.textView_empty_msg);
        button_try = rootView.findViewById(R.id.button_empty_try);

        progressBar = rootView.findViewById(R.id.pb_cat);
        recyclerView = rootView.findViewById(R.id.rv_cat);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCatApi();
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInterAd(position, "");
            }
        }));

        loadCatApi();

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        this.menu = menu;
        methods.changeCart(menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
    }

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (adapter != null) {
                if (!searchView.isIconified()) {
                    adapter.getFilter().filter(s);
                    adapter.notifyDataSetChanged();
                }
            }
            return true;
        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cart_search:
                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCatApi() {
        if (methods.isNetworkAvailable()) {
            LoadCat loadCat = new LoadCat(new CategoryListener() {
                @Override
                public void onStart() {
                    arrayList.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    ll_empty.setVisibility(View.GONE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCat> arrayListCat) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                errr_msg = getString(R.string.no_cat_found);
                                arrayList.addAll(arrayListCat);
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
                }
            }, methods.getAPIRequest(Constant.METHOD_CAT, 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", null));
            loadCat.execute();
        } else {
            errr_msg = getString(R.string.error_net_not_conn);
            setAdapter();
        }
    }

    private void setAdapter() {
        adapter = new AdapterCat(arrayList);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        setEmpty();
    }

    private void setEmpty() {
        if (arrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            recyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    private int getPosition(String id) {
        int count = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            if (id.equals(arrayList.get(i).getId())) {
                count = i;
                break;
            }
        }
        return count;
    }

    @Override
    public void onResume() {
        if (((MainActivity) getActivity()).toolbar != null && menu != null && menu.findItem(R.id.menu_cart_search) != null) {
            methods.changeCart(menu);
        }
        super.onResume();
    }
}