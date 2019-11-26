package com.wajeba.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tiagosantos.enchantedviewpager.EnchantedViewPager;
import com.wajeba.adapter.AdapterLatestHome;
import com.wajeba.asyncTask.LoadHome;
import com.wajeba.asyncTask.LoadHotel;
import com.wajeba.fooddelivery.CartActivity;
import com.wajeba.fooddelivery.HotelByLatestActivity;
import com.wajeba.fooddelivery.HotelBySearchActivity;
import com.wajeba.fooddelivery.HotelDetailsActivity;
import com.wajeba.fooddelivery.MainActivity;
import com.wajeba.fooddelivery.R;
import com.wajeba.interfaces.ClickListener;
import com.wajeba.interfaces.HomeListener;
import com.wajeba.interfaces.InterAdListener;
import com.wajeba.interfaces.RestListener;
import com.wajeba.items.ItemRestaurant;
import com.wajeba.utils.Constant;
import com.wajeba.utils.DBHelper;
import com.wajeba.utils.Methods;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class FragmentHome extends Fragment {

    private DBHelper dbHelper;
    private Methods methods;
    private AdapterLatestHome adapterLatestHome, adapterTopRatedHome;
    private ImagePagerAdapter pagerAdapter;
    private EnchantedViewPager viewPager_home;
    private RecyclerView recyclerView_latest, recyclerView_toprated;
    private ArrayList<ItemRestaurant> arrayList_lat, arrayList_toprated, arrayList_feat;
    private TextView textView_latest_empty;
    private TextView textView_toprated_empty;
    private AppCompatButton button_more_latest, button_more_toprated;
    private ProgressDialog progressDialog;
    private Menu menu;
    public static MenuItem item_cart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new DBHelper(getActivity());
        methods = new Methods(getActivity(), new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                switch (type) {
                    case "latest":
                        Intent intent_lat = new Intent(getActivity(), HotelDetailsActivity.class);
                        Constant.itemRestaurant = arrayList_lat.get(position);
                        startActivity(intent_lat);
                        break;
                    case "top":
                        Intent intent_top = new Intent(getActivity(), HotelDetailsActivity.class);
                        Constant.itemRestaurant = arrayList_toprated.get(position);
                        startActivity(intent_top);
                        break;
                    case "feat":
                        Intent intent = new Intent(getActivity(), HotelDetailsActivity.class);
                        Constant.itemRestaurant = arrayList_feat.get(position);
                        startActivity(intent);
                        break;
                }
            }
        });

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));

        arrayList_feat = new ArrayList<>();
        arrayList_lat = new ArrayList<>();
        arrayList_toprated = new ArrayList<>();

        pagerAdapter = new ImagePagerAdapter();

        viewPager_home = rootView.findViewById(R.id.vp_home);
        viewPager_home.removeAlpha();
        viewPager_home.removeScale();

        LinearLayoutManager llm_latest = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        button_more_latest = rootView.findViewById(R.id.button_home_latest);
        button_more_toprated = rootView.findViewById(R.id.button_home_toprated);
        textView_latest_empty = rootView.findViewById(R.id.textView_latest_empty);
        textView_toprated_empty = rootView.findViewById(R.id.textView_toprated_empty);

        TextView tv1 = rootView.findViewById(R.id.tv1);
        TextView tv2 = rootView.findViewById(R.id.tv2);
        tv1.setTypeface(tv1.getTypeface(), Typeface.BOLD);
        tv2.setTypeface(tv2.getTypeface(), Typeface.BOLD);

        recyclerView_latest = rootView.findViewById(R.id.rv_home_latest);
        recyclerView_latest.setLayoutManager(llm_latest);
        recyclerView_latest.setItemAnimator(new DefaultItemAnimator());
        recyclerView_latest.setHasFixedSize(true);

        LinearLayoutManager llm_toprated = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView_toprated = rootView.findViewById(R.id.rv_home_toprated);
        recyclerView_toprated.setLayoutManager(llm_toprated);
        recyclerView_toprated.setItemAnimator(new DefaultItemAnimator());
        recyclerView_toprated.setHasFixedSize(true);

        if (methods.isNetworkAvailable()) {
            loadHomeApi();
        } else {
            methods.showToast(getString(R.string.error_net_not_conn));
        }

        button_more_latest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.arrayList_latest.clear();
                Constant.arrayList_latest.addAll(arrayList_lat);
                Intent intent = new Intent(getActivity(), HotelByLatestActivity.class);
                intent.putExtra("type", getString(R.string.latest));
                startActivity(intent);
            }
        });

        button_more_toprated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.arrayList_latest.clear();
                Constant.arrayList_latest.addAll(arrayList_toprated);
                Intent intent = new Intent(getActivity(), HotelByLatestActivity.class);
                intent.putExtra("type", getString(R.string.top_rated
                ));
                startActivity(intent);
            }
        });

        viewPager_home.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                View v = viewPager_home.findViewWithTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);
                ImageView ivBackground = v.findViewById(R.id.iv_fav_pager);
                if (dbHelper.checkIsFav(arrayList_feat.get(viewPager_home.getCurrentItem()).getId())) {
                    ivBackground.setImageResource(R.mipmap.fav_hover);
                } else {
                    ivBackground.setImageResource(R.mipmap.fav);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setHasOptionsMenu(true);
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        this.menu = menu;
        methods.changeCart(menu);

        item_cart = menu.findItem(R.id.menu_cart_search);

        MenuItem item_search = menu.findItem(R.id.menu_search);

        final MenuItem item_filter = menu.findItem(R.id.menu_filter).setIcon(R.mipmap.filter);

        item_search.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_ALWAYS);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
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
    }

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            Constant.search_text = s;
            Intent intent = new Intent(getActivity(), HotelBySearchActivity.class);
            startActivity(intent);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {

            return false;
        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_cart_search) {
            Intent intent = new Intent(getActivity(), CartActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_filter) {
            methods.openSearchFilter();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        if (((MainActivity) getActivity()).toolbar != null && menu != null && menu.findItem(R.id.menu_cart_search) != null) {
            methods.changeCart(menu);
        }
        super.onResume();
    }

    private void loadHomeApi() {
        LoadHome loadHome = new LoadHome(new HomeListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, ArrayList<ItemRestaurant> arrayList_latest, ArrayList<ItemRestaurant> arrayList_featured) {
                if (getActivity() != null) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    if (success.equals("1")) {
                        arrayList_lat.addAll(arrayList_latest);
                        arrayList_feat.addAll(arrayList_featured);
                        adapterLatestHome = new AdapterLatestHome(getActivity(), arrayList_lat, new ClickListener() {
                            @Override
                            public void onClick(int position) {
                                methods.showInterAd(position, "latest");
                            }
                        });

                        recyclerView_latest.setAdapter(adapterLatestHome);
                        viewPager_home.setAdapter(pagerAdapter);
                    } else {
                        methods.showToast(getString(R.string.error_server_conneting));
                    }

                    if (arrayList_latest.size() > 0) {
                        textView_latest_empty.setVisibility(View.GONE);
                    } else {
                        textView_latest_empty.setVisibility(View.VISIBLE);
                    }

                    loadTopRatedApi();
                }
            }
        }, methods.getAPIRequest(Constant.METHOD_HOME, 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", null));

        loadHome.execute();
    }

    private void loadTopRatedApi() {
        LoadHotel loadHotel = new LoadHotel(new RestListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemRestaurant> arrayListRestaurant) {
                if (getActivity() != null) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            arrayList_toprated.addAll(arrayListRestaurant);
                            adapterTopRatedHome = new AdapterLatestHome(getActivity(), arrayList_toprated, new ClickListener() {
                                @Override
                                public void onClick(int position) {
                                    methods.showInterAd(position, "top");
                                }
                            });
                            recyclerView_toprated.setAdapter(adapterTopRatedHome);
                        } else {
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                        }
                    } else {
                        methods.showToast(getString(R.string.error_server_conneting));
                    }

                    if (arrayList_toprated.size() > 0) {
                        textView_toprated_empty.setVisibility(View.GONE);
                    } else {
                        textView_toprated_empty.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, methods.getAPIRequest(Constant.METHOD_REST_TOP_RATED, 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", null));
        loadHotel.execute();
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;
        private Animation anim;

        ImagePagerAdapter() {
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            anim = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up_fav);
        }

        @Override
        public int getCount() {
            return arrayList_feat.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {

            View imageLayout = inflater.inflate(R.layout.viewpager_home, container, false);
            assert imageLayout != null;
            RoundedImageView imageView = imageLayout.findViewById(R.id.iv_pager_home);
            ImageView imageView_rest_type = imageLayout.findViewById(R.id.iv_home_rest_type);
            final ImageView imageView_fav = imageLayout.findViewById(R.id.iv_fav_pager);
            final ProgressBar spinner = imageLayout.findViewById(R.id.loading_home);
            TextView title = imageLayout.findViewById(R.id.tv_pager_home_title);
            TextView address = imageLayout.findViewById(R.id.tv_pager_home_address);
            TextView textView_tot_rating = imageLayout.findViewById(R.id.tv_pager_home_tot_rating);
            RelativeLayout rl = imageLayout.findViewById(R.id.rl_homepager);
            RatingBar rating = imageLayout.findViewById(R.id.rating_pager_home);

            if (position == 0) {
                if (dbHelper.checkIsFav(arrayList_feat.get(viewPager_home.getCurrentItem()).getId())) {
                    imageView_fav.setImageResource(R.mipmap.fav_hover);
                } else {
                    imageView_fav.setImageResource(R.mipmap.fav);
                }
            }

            if (arrayList_feat.get(position).getType().equals(Constant.TAG_VEG)) {
                imageView_rest_type.setImageResource(R.mipmap.veg);
            } else if (arrayList_feat.get(position).getType().equals(Constant.TAG_NONVEG)) {
                imageView_rest_type.setImageResource(R.mipmap.nonveg);
            }

            rating.setRating(arrayList_feat.get(position).getAvgRatings());
            title.setText(arrayList_feat.get(position).getName());
            address.setText(arrayList_feat.get(position).getAddress());
            textView_tot_rating.setText("(" + String.valueOf(arrayList_feat.get(position).getTotalRating()) + ")");

            Picasso.get()
                    .load(arrayList_feat.get(position).getImage())
                    .placeholder(R.drawable.placeholder_hotel)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            spinner.setVisibility(View.GONE);
                        }
                    });

            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    methods.showInterAd(position, "feat");
                }
            });

            imageView_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageView_fav.startAnimation(anim);
                    if (dbHelper.addtoFavourite(arrayList_feat.get(viewPager_home.getCurrentItem()))) {
                        imageView_fav.setImageResource(R.mipmap.fav_hover);
                    } else {
                        imageView_fav.setImageResource(R.mipmap.fav);
                    }
                }
            });

            imageLayout.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);

            container.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}