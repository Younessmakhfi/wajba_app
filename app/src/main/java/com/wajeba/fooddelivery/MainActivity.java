package com.wajeba.fooddelivery;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.irfaan008.irbottomnavigation.SpaceItem;
import com.irfaan008.irbottomnavigation.SpaceNavigationView;
import com.irfaan008.irbottomnavigation.SpaceOnClickListener;
import com.wajeba.asyncTask.LoadAbout;
import com.wajeba.fragments.FragmentCat;
import com.wajeba.fragments.FragmentHome;
import com.wajeba.fragments.FragmentOrderList;
import com.wajeba.fragments.FragmentProfile;
import com.wajeba.helper.LocalHelper;
import com.wajeba.interfaces.AboutListener;
import com.wajeba.interfaces.AdConsentListener;
import com.wajeba.utils.AdConsent;
import com.wajeba.utils.Constant;
import com.wajeba.utils.DBHelper;
import com.wajeba.utils.Methods;
import com.wajeba.utils.SharedPref;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Locale;

import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public Toolbar toolbar;
    DBHelper dbHelper;
    LoadAbout loadAbout;
    AdConsent adConsent;
    Methods methods;
    DrawerLayout drawer;
    TextView textView_header_message;
    MenuItem menuItem_login;
    FragmentManager fm;
    ProgressDialog pbar;
    String selectedFragment = "";
    NavigationView navigationView;
    static SpaceNavigationView spaceNavigationView;
    Locale myLocale;
    SharedPref sharedPref;
    String showlang="no";
    public static final String LOAD_METHOD_ID = "load_method_id";
    public static final int LOAD_METHOD_CODE = 92840;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
       // super.attachBaseContext(LocalHelper.onAttach(newBase,"en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //lanuage changing implementation start :
        //Intent intent = getIntent();
       // showlang = intent.getStringExtra("showlang");
        /*if (showlang.equals("yes")){
            showLanguagechoice();
        } */
       // Toast.makeText(this, showlang, Toast.LENGTH_SHORT).show();
       // checkFirstRun();
        sharedPref = new SharedPref(this);
        //language_changer = findViewById(R.id.language_changer);
        Paper.init(this);
        String language = Paper.book().read("language");
        if(language == null)
            Paper.book().write("language","en");
        //updateView((String)Paper.book().read("language"));
        //lanuage changing implementation finish :
        fm = getSupportFragmentManager();
        pbar = new ProgressDialog(this);
        pbar.setMessage(getString(R.string.loading));

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        dbHelper = new DBHelper(this);
        methods = new Methods(this);
        methods.setStatusColor(getWindow(), toolbar);
        methods.forceRTLIfSupported(getWindow());

//        ll_adView_main = findViewById(R.id.ll_adView_main);
        adConsent = new AdConsent(this, new AdConsentListener() {
            @Override
            public void onConsentUpdate() {
//                methods.showBannerAd(ll_adView_main);
            }
        });

        spaceNavigationView = findViewById(R.id.space);
        spaceNavigationView.showIconOnly();
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem(getString(R.string.home), R.mipmap.home));
        spaceNavigationView.addSpaceItem(new SpaceItem(getString(R.string.categories), R.mipmap.cat));
        spaceNavigationView.addSpaceItem(new SpaceItem(getString(R.string.orderlist), R.mipmap.list));
        spaceNavigationView.addSpaceItem(new SpaceItem(getString(R.string.profile), R.mipmap.profile));
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex) {
                    case 0:
                        FragmentHome f1 = new FragmentHome();
                        loadFrag(f1, getString(R.string.home), fm);
                        toolbar.setTitle(getString(R.string.app_name));
                        break;
                    case 1:
                        FragmentCat fcat = new FragmentCat();
                        loadFrag(fcat, getString(R.string.categories), fm);
                        toolbar.setTitle(getString(R.string.categories));
                        break;
                    case 2:
                        FragmentOrderList forder = new FragmentOrderList();
                        loadFrag(forder, getString(R.string.orderlist), fm);
                        toolbar.setTitle(getString(R.string.orderlist));
                        break;
                    case 3:
                        FragmentProfile fprof = new FragmentProfile();
                        loadFrag(fprof, getString(R.string.profile), fm);
                        toolbar.setTitle(getString(R.string.profile));
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

            }
        });

        if (methods.isNetworkAvailable()) {
            loadAboutTask();
        } else {
            adConsent.checkForConsent();
            dbHelper.getAbout();
            methods.showToast(getString(R.string.error_net_not_conn));
        }

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.setDrawerIndicatorEnabled(false);

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        toggle.setHomeAsUpIndicator(R.mipmap.nav);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        menuItem_login = navigationView.getMenu().findItem(R.id.nav_login);
        textView_header_message = navigationView.getHeaderView(0).findViewById(R.id.tv_header_msg);

        changeLoginTitle();

        if (!Constant.isFromCheckOut) {
            FragmentHome f1 = new FragmentHome();
            loadFrag(f1, getString(R.string.home), fm);
            getSupportActionBar().setTitle(getResources().getString(R.string.home));
            navigationView.setCheckedItem(R.id.nav_home);
        } else {
            Constant.isFromCheckOut = false;
            spaceNavigationView.changeCurrentItem(2);
        }

        checkPer();
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                spaceNavigationView.changeCurrentItem(0);

//                FragmentHome f1 = new FragmentHome();
//                loadFrag(f1, getString(R.string.home), fm);
                break;
            case R.id.nav_fav:
                Intent intent_fav = new Intent(MainActivity.this, FavouriteActivity.class);
                startActivity(intent_fav);
                break;
            case R.id.nav_hotel_list:
                Intent intent_hotel = new Intent(MainActivity.this, HotelByLatestActivity.class);
                intent_hotel.putExtra("type", getString(R.string.hotel_list));
                startActivity(intent_hotel);
                break;
            case R.id.nav_rate:
                final String appName = getPackageName();//your application package name i.e play store application url
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id="
                                    + appName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id="
                                    + appName)));
                }
                break;
            case R.id.nav_shareapp:
                Intent ishare = new Intent(Intent.ACTION_SEND);
                ishare.setType("text/plain");
                ishare.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(ishare);
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_login:
                methods.clickLogin();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        selectedFragment = name;
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.frame_nav, f1, name);
        ft.commit();
        getSupportActionBar().setTitle(name);
    }

    private void changeLoginTitle() {
        if (Constant.isLogged) {
            menuItem_login.setTitle(getString(R.string.logout));
            menuItem_login.setIcon(ContextCompat.getDrawable(MainActivity.this, R.mipmap.logout));
            textView_header_message.setText(getString(R.string.hi) + " " + Constant.itemUser.getName());

            try {
                FragmentHome.item_cart.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            menuItem_login.setTitle(getString(R.string.login));
            menuItem_login.setIcon(ContextCompat.getDrawable(MainActivity.this, R.mipmap.login));
            textView_header_message.setText(getString(R.string.hi) + " " + getString(R.string.guest));
            try {
                FragmentHome.item_cart.setVisible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadAboutTask() {
        loadAbout = new LoadAbout(MainActivity.this, new AboutListener() {
            @Override
            public void onStart() {
                pbar.show();
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message) {
                pbar.dismiss();
                if (!verifyStatus.equals("-1")) {
                    adConsent.checkForConsent();

                    if (success.equals("1")) {
                        dbHelper.addtoAbout();
                    }
                } else {
                    methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                }
            }
        });
        loadAbout.execute();
    }

    public void checkPer() {
        if ((ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean canUseExternalStorage = false;

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canUseExternalStorage = true;
                }

                if (!canUseExternalStorage) {
                    methods.showToast(getString(R.string.cannot_use_save));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (selectedFragment.equals(getString(R.string.home))) {
                exitDialog();
            } else {
                spaceNavigationView.changeCurrentItem(0);
            }
        }
    }

    private void exitDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this, R.style.ThemeDialog);

        alert.setTitle(getString(R.string.exit));
        alert.setMessage(getString(R.string.sure_exit));
        alert.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
    }

    @Override
    protected void onResume() {
        if (menuItem_login != null) {
            changeLoginTitle();
        }
        super.onResume();
    }
}