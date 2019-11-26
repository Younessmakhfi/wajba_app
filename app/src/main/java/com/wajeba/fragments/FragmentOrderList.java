package com.wajeba.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wajeba.adapter.AdapterOrderList;
import com.wajeba.asyncTask.LoadOderList;
import com.wajeba.fooddelivery.CartActivity;
import com.wajeba.fooddelivery.MainActivity;
import com.wajeba.fooddelivery.R;
import com.wajeba.interfaces.OrderListListener;
import com.wajeba.items.ItemOrderList;
import com.wajeba.utils.Constant;
import com.wajeba.utils.Methods;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class FragmentOrderList extends Fragment {

    private RecyclerView recyclerView;
    private AdapterOrderList adapterOrderList;
    private Methods methods;
    private ArrayList<ItemOrderList> arrayList;
    private CircularProgressBar progressBar;
    private Menu menu;
    private SearchView searchView;

    private TextView textView_empty;
    private LinearLayout ll_empty;
    private String errr_msg;
    private AppCompatButton button_try;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_orderlist, container, false);

        methods = new Methods(getActivity());

        arrayList = new ArrayList<>();
        LinearLayoutManager llm_latest = new LinearLayoutManager(getActivity());

        ll_empty = v.findViewById(R.id.ll_empty);
        textView_empty = v.findViewById(R.id.textView_empty_msg);
        button_try = v.findViewById(R.id.button_empty_try);

        progressBar = v.findViewById(R.id.pb_orderlist);

        recyclerView = v.findViewById(R.id.rv_orderlist);
        recyclerView.setLayoutManager(llm_latest);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadOrderListApi();
            }
        });

        loadOrderListApi();

        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        this.menu = menu;
        methods.changeCart(menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
    }

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (adapterOrderList != null) {
                if (!searchView.isIconified()) {
                    adapterOrderList.getFilter().filter(s);
                    adapterOrderList.notifyDataSetChanged();
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

    private void loadOrderListApi() {
        if (Constant.isLogged) {
            if (methods.isNetworkAvailable()) {
                LoadOderList loadOderList = new LoadOderList(new OrderListListener() {
                    @Override
                    public void onStart() {
                        arrayList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        ll_empty.setVisibility(View.GONE);
                    }

                    @Override
                    public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemOrderList> arrayListOrderList) {
                        if (getActivity() != null) {
                            if (success.equals("1")) {
                                if (!verifyStatus.equals("-1")) {
                                    errr_msg = getString(R.string.no_data_found);
                                    arrayList.addAll(arrayListOrderList);
                                    setAdapter();
                                } else {
                                    methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                                }
                                progressBar.setVisibility(View.GONE);
                            } else {
                                errr_msg = getString(R.string.error_server_conneting);
                                setEmpty();
                            }
                        }
                    }
                }, methods.getAPIRequest(Constant.METHOD_ORDER_LIST, 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "", null));
                loadOderList.execute();
            } else {
                errr_msg = getString(R.string.error_net_not_conn);
                setEmpty();
            }
        } else {
            errr_msg = getString(R.string.not_log);
            setEmpty();
        }
    }

    private void setAdapter() {
        adapterOrderList = new AdapterOrderList(getActivity(), arrayList);
        recyclerView.setAdapter(adapterOrderList);
        progressBar.setVisibility(View.GONE);
        setEmpty();
    }

    private void setEmpty() {
        progressBar.setVisibility(View.GONE);
        if (arrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            recyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        if (((MainActivity) getActivity()).toolbar != null && menu != null && menu.findItem(R.id.menu_cart_search) != null) {
            methods.changeCart(menu);
        }

        if(Constant.isCancelOrder) {
            if (adapterOrderList != null && adapterOrderList.getItemCount() > 0) {
                adapterOrderList.notifyDataSetChanged();
            }
            Constant.isCancelOrder = false;
        }
        super.onResume();
    }
}