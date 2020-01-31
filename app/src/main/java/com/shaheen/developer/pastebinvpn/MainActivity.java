package com.shaheen.developer.pastebinvpn;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atom.core.exceptions.AtomException;
import com.atom.core.exceptions.AtomValidationException;
import com.atom.core.models.Channel;
import com.atom.core.models.Country;
import com.atom.core.models.Protocol;
import com.atom.sdk.android.AtomManager;
import com.atom.sdk.android.ConnectionDetails;
import com.atom.sdk.android.VPNProperties;
import com.atom.sdk.android.VPNStateListener;
import com.atom.sdk.android.data.callbacks.CollectionCallback;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.shaheen.developer.pastebinvpn.Models.CountryImageMatcher;
import com.shaheen.developer.pastebinvpn.Models.PInfo;
import com.shaheen.developer.pastebinvpn.SqlieDataBase.DatabaseHelper;
import com.shaheen.developer.pastebinvpn.Utils.NetworkUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, VPNStateListener {


    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView version, c_name1, c_name2, c_name3, secure_not_secure, connecting_country, connected_country;
    ImageView toggle, shield, c_icon1, c_icon2, c_icon3, connecting_flag, connected_flag;
    RelativeLayout goto_location_layout;
    private ArrayList<Country> arrayList = new ArrayList<>();
    LinearLayout canada_layout, us_layout, uk_layout, disconnected_layout, connecting_layout, connected_layout;
    static boolean haveInternet = true;
    DatabaseHelper databaseHelper;
    Protocol c_Protocol_tcp, c_Protocol_udp;

    public static final String CONNECTED = "CONNECTED";
    public static final String DISCONNECTED = "DISCONNECTED";
    public static final String CONNECTING = "CONNECTING";
    public static final int GOTOLOCATION = 798;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AtomManager.addVPNStateListener(this);

        InitializeViews();
        GetCountriesList();
        GetProtocols();
        SetToolBarAndDrawer();
        CheckOnStart();

    }

    public void InitializeViews() {

        databaseHelper = new DatabaseHelper(MainActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        connecting_flag = (ImageView) findViewById(R.id.connecting_flag);
        connected_flag = (ImageView) findViewById(R.id.connected_flag);
        c_icon1 = (ImageView) findViewById(R.id.c_icon1);
        c_icon2 = (ImageView) findViewById(R.id.c_icon2);
        c_icon3 = (ImageView) findViewById(R.id.c_icon3);
        c_name1 = (TextView) findViewById(R.id.c_name1);
        c_name2 = (TextView) findViewById(R.id.c_name2);
        c_name3 = (TextView) findViewById(R.id.c_name3);
        connected_country = (TextView) findViewById(R.id.connected_country);
        connecting_country = (TextView) findViewById(R.id.connecting_country);
        secure_not_secure = (TextView) findViewById(R.id.secure_not_secure);
        canada_layout = (LinearLayout) findViewById(R.id.canada_layout);
        us_layout = (LinearLayout) findViewById(R.id.us_layout);
        uk_layout = (LinearLayout) findViewById(R.id.uk_layout);
        disconnected_layout = (LinearLayout) findViewById(R.id.disconnected_layout);
        connecting_layout = (LinearLayout) findViewById(R.id.connecting_layout);
        connected_layout = (LinearLayout) findViewById(R.id.connected_layout);
        toggle = (ImageView) findViewById(R.id.toggle);
        shield = (ImageView) findViewById(R.id.shield);
        goto_location_layout = (RelativeLayout) findViewById(R.id.goto_location_layout);


        toggle.setOnClickListener(this);
        goto_location_layout.setOnClickListener(this);
    }

    public void CheckOnStart(){
        if (getVpnState().equals(CONNECTED)) {
            ConnectedLayoutSetting();
        } else if (getVpnState().equals(DISCONNECTED)) {
           DisconnectedLayoutSetting();
        } else if (getVpnState().equals(CONNECTING)) {
            ConnectingLayoutSetting();
        }
    }

    public void GetCountriesList() {
        AtomDemoApplicationController.getInstance().getAtomManager().getCountries(new CollectionCallback<Country>() {
            @Override
            public void onSuccess(List<Country> list) {
                Log.d("shani", "success list " + list.size());
                for (int i = 0; i < list.size(); i++) {
                    //Log.d("shani","Names..... "+list.get(i).getName());
                    if (list.get(i).getName().equals("United States") || list.get(i).getName().equals("United Kingdom") || list.get(i).getName().equals("Canada")) {
                        arrayList.add(list.get(i));
                    }
                }
                Log.d("shani", "list size ..... " + arrayList.size());
            }
            @Override
            public void onError(AtomException e) {
                Log.d("shani", "AtomException onError " + e.getMessage());
                Log.d("shani", "AtomException onError " + e.getErrorMessage());
                Log.d("shani", "AtomException onError " + e.getCode());
                Log.d("shani", "AtomException onError " + e.getApiError());
                Log.d("shani", "AtomException onError " + e.getException());
            }
            @Override
            public void onNetworkError(AtomException e) {
                Log.d("shani", "AtomException onNetworkError " + e.getMessage());
            }
        });
    }

    public void GetProtocols() {

        AtomDemoApplicationController.getInstance().getAtomManager().getProtocols(new CollectionCallback<Protocol>() {
            @Override
            public void onSuccess(List<Protocol> protocols) {

                for (int i = 0; i < protocols.size(); i++) {
                    if (protocols.get(i).getName().equals("TCP")) {
                        c_Protocol_tcp = protocols.get(i);
                    }
                    if (protocols.get(i).getName().equals("UDP")) {
                        c_Protocol_udp = protocols.get(i);
                    }
                }
            }

            @SuppressLint("LogNotTimber")
            @Override
            public void onError(AtomException atomException) {
                Log.d("shani", atomException.getMessage() + " : " + atomException.getCode());
            }

            @SuppressLint("LogNotTimber")
            @Override
            public void onNetworkError(AtomException atomException) {
                Log.d("shani", atomException.getMessage() + " : " + atomException.getCode());
            }
        });
    }

    public void SetToolBarAndDrawer() {

        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.drawer_menu, getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        //toggle.setHomeAsUpIndicator(R.drawable.drawer_menu);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setElevation(0);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActionBar().setElevation(0);
        }*/

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setActionView(R.layout.action_icon_layout);
        navigationView.getMenu().getItem(1).setActionView(R.layout.action_icon_layout);
        navigationView.getMenu().getItem(2).setActionView(R.layout.action_icon_layout);
        navigationView.getMenu().getItem(3).setActionView(R.layout.action_icon_layout);
        version = (TextView) navigationView.findViewById(R.id.version);
        SetVersion();
    }

    public void MakeConnection() {

        if (!haveInternet) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        } else {
            if (AtomDemoApplicationController.getInstance().getAtomManager().getCurrentVpnStatus(MainActivity.this).equals(AtomManager.VPNStatus.DISCONNECTED)) {

                VPNProperties.Builder vpnProperties = null;
                boolean possible = false;
                SharedPreferences pref = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
                if (pref != null) {

                    try {
                        possible = true;
                        if (GetSelectedCountry() != null) {
                            Country country = GetSelectedCountry();
                            vpnProperties = new VPNProperties.Builder(country, c_Protocol_tcp);
                        }
                    } catch (AtomValidationException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, getResources().getString(R.string.choose_country), Toast.LENGTH_SHORT).show();
                }


                if (possible) {
                    // check for app list

                    SharedPreferences prefsplit = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
                    if (prefsplit != null) {
                        String split_onoff = prefsplit.getString("split_onoff", null);
                        if (split_onoff != null) {
                            if (split_onoff.equals("ON")) {
                                ArrayList<PInfo> list = databaseHelper.getAllAPPs();
                                String[] app_list = new String[list.size()];
                                boolean contains = false;
//                            Log.d("shani","list size.........."+list.size());
                                for (int i = 0; i < list.size(); i++) {
                                    app_list[i] = list.get(i).getPname();
//                                Log.d("shani", "app_list ..... " + app_list[i]);
                                    contains = true;
                                }
                                if (contains) {
                                    vpnProperties.withSplitTunneling(app_list);
                                }
                            }
                        }
                    }

                    vpnProperties.withSecondaryProtocol(c_Protocol_udp);

                    ConnectingLayoutSetting();
                    AtomDemoApplicationController.getInstance().getAtomManager().connect(MainActivity.this, vpnProperties.build());
                } else {
                    Toast.makeText(this, getResources().getString(R.string.choose_country), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void ShowLogoutDialog() {

        String message = null;
        if (getVpnState().equals(CONNECTED)) {
            message = getResources().getString(R.string.disconnect_logout_confirmation);
        } else {
            message = getResources().getString(R.string.logout_confirmation);
        }
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getResources().getString(R.string.logout))
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (getVpnState().equals(CONNECTING)) {
                            cancelVPNConnection();
                        } else if (getVpnState().equals(CONNECTED)) {
                            disconnectVPNConnection();
                        }

                        SharedPreferences sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("login", false);
                        editor.apply();

                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public String getVpnState() {
        if (AtomDemoApplicationController.getInstance().getAtomManager() != null)
            return AtomDemoApplicationController.getInstance().getAtomManager().getCurrentVpnStatus(MainActivity.this);
        else
            return null;
    }

    public void disconnectVPNConnection() {
        AtomDemoApplicationController.getInstance().getAtomManager().disconnect(MainActivity.this);
        DisconnectedLayoutSetting();
    }

    public void cancelVPNConnection() {
        AtomDemoApplicationController.getInstance().getAtomManager().cancel(MainActivity.this);
        DisconnectedLayoutSetting();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            Uri data_uri = data.getData();
            Log.d("shani", "3data_uri ..... " + data_uri);

            if (getVpnState().equals(CONNECTED)){
                disconnectVPNConnection();
            }else if (getVpnState().equals(CONNECTING)){
                cancelVPNConnection();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MakeConnection();
                        }
                    }, 1000);
                }
            }, 500);

        } else {
            Log.d("shani", "mull data..... ");
        }

    }

    public void DisconnectedLayoutSetting() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                secure_not_secure.setText(getResources().getString(R.string.not_secure));
                toggle.setImageDrawable(getResources().getDrawable(R.drawable.toggle_disconnected));
                shield.setImageDrawable(getResources().getDrawable(R.drawable.vpn_disconnected));
                disconnected_layout.setVisibility(View.VISIBLE);
                connected_layout.setVisibility(View.GONE);
                connecting_layout.setVisibility(View.GONE);
                ViewClickable(true);
            }
        });

    }

    public void ConnectingLayoutSetting() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewClickable(false);
                secure_not_secure.setText(getResources().getString(R.string.not_secure));
                toggle.setImageDrawable(getResources().getDrawable(R.drawable.toggle_connecting));
                shield.setImageDrawable(getResources().getDrawable(R.drawable.vpn_connecting));
                disconnected_layout.setVisibility(View.GONE);
                connected_layout.setVisibility(View.GONE);
                connecting_layout.setVisibility(View.VISIBLE);
                ShowConnectingCountry(GetSelectedCountry());
            }
        });

    }

    public void ConnectedLayoutSetting() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("shani","connectedLayoutSetting");
                secure_not_secure.setText(getResources().getString(R.string.secure));
                toggle.setImageDrawable(getResources().getDrawable(R.drawable.toggle_connected));
                shield.setImageDrawable(getResources().getDrawable(R.drawable.vpn_connected));
                disconnected_layout.setVisibility(View.GONE);
                connecting_layout.setVisibility(View.GONE);
                connected_layout.setVisibility(View.VISIBLE);
                ShowConnectedCountry(GetSelectedCountry());
                ViewClickable(true);
                Log.d("shani","8connectedLayoutSetting end2");
            }
        });

    }

    public void ShowConnectedCountry(Country country) {
        connected_flag.setImageDrawable(getResources().getDrawable(CountryImageMatcher.GetImageId(country.getName())));
        connected_country.setText(country.getName());
    }

    public void ShowConnectingCountry(Country country) {
        connecting_flag.setImageDrawable(getResources().getDrawable(CountryImageMatcher.GetImageId(country.getName())));
        connecting_country.setText(country.getName());
    }

    public Country GetSelectedCountry() {
        Country country = null;
        Gson gson_c = new Gson();
        SharedPreferences pref_c = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
        if (pref_c != null) {
            String st_country = pref_c.getString("country_detail", null);
            if (st_country != null) {
                country = gson_c.fromJson(st_country, Country.class);
            }
        }
        return country;
    }

    public void ViewClickable(boolean clickable) {

        if (clickable) {
            canada_layout.setOnClickListener(this);
            uk_layout.setOnClickListener(this);
            us_layout.setOnClickListener(this);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            canada_layout.setOnClickListener(null);
            uk_layout.setOnClickListener(null);
            us_layout.setOnClickListener(null);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.split_tunnel) {
            startActivity(new Intent(MainActivity.this, SplitTunnelingActivity.class));
        } else if (id == R.id.protocol) {
            startActivity(new Intent(MainActivity.this, ProtocolActivity.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        } else if (id == R.id.logout) {
            ShowLogoutDialog();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void SetVersion() {
        try {
            String versionName = this.getPackageManager().getPackageInfo(MainActivity.this.getPackageName(), 0).versionName;
            version.setText(getResources().getString(R.string.app_version) + "  " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.toggle:

                if (getVpnState().equals(CONNECTED)) {
                    disconnectVPNConnection();
                    Toast.makeText(this, getResources().getString(R.string.disconnected), Toast.LENGTH_SHORT).show();
                } else if (getVpnState().equals(DISCONNECTED)) {
                    if (haveInternet) {
                        MakeConnection();
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                } else if (getVpnState().equals(CONNECTING)) {
                    cancelVPNConnection();
                    Toast.makeText(this, getResources().getString(R.string.disconnected), Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.goto_location_layout:
                startActivityForResult(new Intent(MainActivity.this, LocationActivity.class), GOTOLOCATION);

                break;

            case R.id.canada_layout:
                SaveCountryAndConnect("Canada");
                break;
            case R.id.uk_layout:
                SaveCountryAndConnect("United Kingdom");
                break;
            case R.id.us_layout:
                SaveCountryAndConnect("United States");
                break;


            default:
                break;
        }
    }

    public void SaveCountryAndConnect(String country){
        Gson gson = new Gson();
        Country model = null;
        for (int i = 0; i <arrayList.size() ; i++) {
            if (arrayList.get(i).getName().equals(country)){
                model = arrayList.get(i);
            }
        }
        String countryToGson = gson.toJson(model);
        SharedPreferences sharedPref = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("country_detail", countryToGson);
        editor.apply();

        if (getVpnState().equals(CONNECTING)){
            cancelVPNConnection();
        }else if (getVpnState().equals(CONNECTED)){
            disconnectVPNConnection();
        }
        MakeConnection();
    }

    @Override
    public void onConnected() {
        ConnectedLayoutSetting();
    }

    @Override
    public void onConnected(ConnectionDetails connectionDetails) {
        ConnectedLayoutSetting();
    }

    @Override
    public void onConnecting() {
        ConnectingLayoutSetting();
    }

    @Override
    public void onRedialing(AtomException e, ConnectionDetails connectionDetails) {
        Toast.makeText(this, "Redialing", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDialError(AtomException e, ConnectionDetails connectionDetails) {
        Toast.makeText(this, "Dial Error", Toast.LENGTH_LONG).show();
        DisconnectedLayoutSetting();
    }

    @Override
    public void onStateChange(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnected(boolean b) {
        DisconnectedLayoutSetting();
    }

    @Override
    public void onDisconnected(ConnectionDetails connectionDetails) {
        DisconnectedLayoutSetting();
    }

    @Override
    public void onUnableToAccessInternet(AtomException e, ConnectionDetails connectionDetails) {
        Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        DisconnectedLayoutSetting();
    }

    @Override
    public void onPacketsTransmitted(String s, String s1) {}

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            int status = NetworkUtil.getConnectivityStatusString(context);
            Log.d("shani", "network receiver......" + status);
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                    haveInternet = false;
                } else {
                    haveInternet = true;
                }
            }
        }
    }

}
