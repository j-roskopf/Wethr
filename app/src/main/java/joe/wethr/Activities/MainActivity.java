package joe.wethr.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import joe.wethr.Adapters.DrawerAdapter;
import joe.wethr.Adapters.MainWeatherListAdapter;
import joe.wethr.ControllableAppBarLayout;
import joe.wethr.DividerItemDecoration;
import joe.wethr.Objects.DrawerItem;
import joe.wethr.Objects.Weather;
import joe.wethr.R;

public class MainActivity extends AppCompatActivity implements LocationListener {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ControllableAppBarLayout controllableAppBarLayout;

    //MainActivity context
    Context mContext;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;

    CoordinatorLayout rootLayout;
    FloatingActionButton fabBtn;


    // Location objects
    LocationManager lm;
    Location location;
    double longitude;
    double latitude;
    Geocoder gcd;
    String locationToSend;
    LocationListener ll;

    //was there a location error
    boolean locationError = false;

    // Holds whether or not the user just updated the settings
    public static boolean updatedSettings;

    // Weather object to hold temporary weather objects
    Weather rightNowWeatherObject;

    // Zip code that is used if the user doesn't have location enabled
    String zipToUseIfLocationFailed;

    // UI References
    //TextView descriptionTextView;
    ImageView weatherIconImageView;
    TextView rightNowTextView;

    String city = "";
    String date = "";

    //Navigation drawer listview
    ListView navListView;

    String actionbarTitle = "";

    //actionbar menu
    Menu mMenu;

    //container on main page that has icon / degrees
    LinearLayout container;

    //Drawer
    android.support.design.widget.NavigationView navView;

    // Used to determine weather direction from degrees
    String[] dirTable = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
            "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};

    // Current view
    View mFragmentView;

    // UI References
    MainWeatherListAdapter mAdapter;

    // Used to make network calls
    private AQuery mAQuery;

    // Displays progress
    private ProgressDialog mPrgDialog;

    // Holds the posts
    private ArrayList<Weather> mWeatherList;

    // holds if GPS found the location
    boolean foundLocation;

    //Listview for display dates
    RecyclerView recyclerView;

    //Colors
    String[] colors = {"#F44336","#E91E63","#9C27B0","#673AB7","#3F51B5","#2196F3","#03A9F4","#00BCD4","#009688","#4CAF50","#8BC34A","#FFC107","#FF9800","#FF5722","#607D8B"};
    String[] accent = {"#D32F2F","#C2185B","#7B1FA2","#512DA8","#303F9F","#1976D2","#0288D1","#0097A7","#00796B","#388E3C","#689F38","#FFA000","#F57C00","#E64A19","#455A64"};

    int randomNumber;
    String selectedColor;
    String selectedAccentColor;

    int iconTag = 0;
    String rightNowTemp = "";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
            // Serialize and persist the activated item position.
        Log.d("D", "123456789 putting away w with temp = " + rightNowWeatherObject.getTemp());
        Log.d("D", "123456789 right now text is = " + rightNowTextView.getText().toString());
        Log.d("D", "123456789 right now icon is = " + weatherIconImageView.getTag());
        Log.d("D", "123456789 THE TITLE IS = " + actionbarTitle);
        outState.putParcelable("currentWeather", rightNowWeatherObject);
        outState.putParcelableArrayList("posts", mWeatherList);
        outState.putString("zip", zipToUseIfLocationFailed);
        outState.putString("title", actionbarTitle);
        outState.putString("rightNowIconTag", weatherIconImageView.getTag().toString());
        outState.putString("rightNowTemp", rightNowTextView.getText().toString());
        if(locationError){
            outState.putString("locationError", "true");
        }else{
            outState.putString("locationError", "false");
        }






    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get random color

        randomNumber = new Random().nextInt(colors.length);
        selectedColor = colors[randomNumber];
        selectedAccentColor = accent[randomNumber];

        if ((Build.VERSION.SDK_INT) >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor(selectedAccentColor));
        }

        setContentView(R.layout.activity_main);

        initToolbar();
        initInstances();
        if (savedInstanceState == null) {
            getWeather();
        }else{
            if(savedInstanceState.getString("locationError").equals("true")){
                locationError = true;
            }else{
                locationError = false;
            }
            mWeatherList = savedInstanceState
                    .getParcelableArrayList("posts");
            rightNowTemp = (savedInstanceState.getString("rightNowTemp").split(" ")[0]);
            iconTag = Integer.parseInt(savedInstanceState.getString("rightNowIconTag"));
            rightNowWeatherObject = savedInstanceState.getParcelable("currentWeather");
            actionbarTitle = savedInstanceState.getString("title");
            collapsingToolbarLayout.setTitle(savedInstanceState.getString("title"));
            Log.d("D", "123456789 setting title to " + savedInstanceState.getString("title"));
            MainWeatherListAdapter mAdapter = new MainWeatherListAdapter(this, mWeatherList);
            recyclerView.setAdapter(mAdapter);
            setRightNowText();
            setTempAndIcon();
            collapseThenExpand();


        }

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    private void initInstances() {
        mContext = this;
        mAQuery = new AQuery(this);
        rightNowTextView = (TextView) findViewById(R.id.tempRightNow);
        weatherIconImageView = (ImageView) findViewById(R.id.weatherIcon);
        navView = (NavigationView) findViewById(R.id.navigation);
        controllableAppBarLayout = (ControllableAppBarLayout)findViewById(R.id.appBarLayout);
        container = (LinearLayout) findViewById(R.id.middleContainer);
        container.setBackgroundColor(Color.parseColor(selectedColor));
        navListView = (ListView) findViewById(R.id.navListView);
        lm = (LocationManager) this.getSystemService(
                Context.LOCATION_SERVICE);



        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setContentScrimColor(Color.parseColor(selectedColor));


        recyclerView = (RecyclerView) findViewById(R.id.list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        // Use the default animator
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // you could add item decorators
        //RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        //recyclerView.addItemDecoration(itemDecoration);

        initAndPopulateDrawer();







    }

    public void initAndPopulateDrawer() {
        Menu m = navView.getMenu();

        boolean foundOne = false;
        //used to retreive object
        final ArrayList<DrawerItem> drawerItems = new ArrayList<>();
        Gson gson = new Gson();
        for (int i = 0; i < 20; i++) {
            //get the first one that is blank
            if (!PreferenceManager.getDefaultSharedPreferences(mContext).getString("city" + i, "").equals("")) {
                Log.d("D","564123312 GSON RESPONSE IS = " +  PreferenceManager.getDefaultSharedPreferences(mContext).getString("city" + i, ""));
                DrawerItem cityAndZip = gson.fromJson(PreferenceManager.getDefaultSharedPreferences(mContext).getString("city" + i, ""), DrawerItem.class);
                drawerItems.add(cityAndZip);
                foundOne = true;
            }

        }
        SubMenu temp = m.addSubMenu("");
        temp.add("");

        //ugh oh, no cities in shared preference. Must be the first time you're starting up the app. Add default zip
        if (!foundOne) {
            zipToUseIfLocationFailed = PreferenceManager
                    .getDefaultSharedPreferences(this).getString(
                            "zip", "94114");
            DrawerItem tempDrawerItem = new DrawerItem();
            tempDrawerItem = checkIfValidZipCode(zipToUseIfLocationFailed);
            tempDrawerItem.setZip(zipToUseIfLocationFailed);
            drawerItems.add(tempDrawerItem);
        }

        final DrawerAdapter adapter = new DrawerAdapter(this, drawerItems);
        navListView.setAdapter(adapter);

        navListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PreferenceManager
                        .getDefaultSharedPreferences(mContext).edit().putString("zip", adapter.getItem(i).getZip()).commit();
                drawerLayout.closeDrawer(Gravity.LEFT);
                refreshData();
            }
        });

        navListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

                // set title
                alertDialogBuilder.setTitle("Confirm");

                // set dialog message
                alertDialogBuilder
                        .setMessage(
                                "Remove City "
                                        + adapter.getItem(i).getZip() + "?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {

                                        drawerItems.remove(i);
                                        adapter.notifyDataSetChanged();
                                        PreferenceManager.getDefaultSharedPreferences(mContext).edit().remove("city" + i).commit();
                                        Log.d("D", "123546879 removing " + "city" + i);
                                        //drawerLayout.closeDrawer(Gravity.LEFT);

                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

                return true;
            }
        });

    }


    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;

        if (locationError) {
            //Show error icon in menu
            MenuItem item = mMenu.findItem(R.id.action_error);
            item.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            refreshData();
            return true;
        } else if (id == R.id.action_error) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            // set title
            alertDialogBuilder.setTitle("Alert: Location Disabled");

            // set dialog message
            alertDialogBuilder
                    .setMessage(
                            "collecting weather for location stored in settings: "
                                    + zipToUseIfLocationFailed)
                    .setCancelable(false)
                    .setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {

                                }
                            })
                    .setNegativeButton("Settings",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    Intent dialogIntent = new Intent(
                                            android.provider.Settings.ACTION_SETTINGS);
                                    dialogIntent
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(dialogIntent);
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            return true;
        }
        if (id == R.id.action_add) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Add city");
            alert.setMessage("Enter a valid zip code or city");

            // Set an EditText view to get user input
            final EditText input = new EditText(this);
            alert.setView(input);

            //used to store drawer item in shared preference
            final Gson gson = new Gson();

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    DrawerItem drawerItem = checkIfValidZipCode(input.getText().toString());
                    if (!drawerItem.getCity().equals("no")) {
                        drawerItem.setZip(input.getText().toString());
                        //find first spot in shared preference
                        //only allow twenty cities
                        boolean added = false;
                        for (int i = 0; i < 20; i++) {
                            //get the first one that is blank
                            if (PreferenceManager.getDefaultSharedPreferences(mContext).getString("city" + i, "").equals("")) {
                                //put string in shared pref like his key => value (city+number) => (zip:cityname)
                                PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("city" + i, gson.toJson(drawerItem)).commit();
                                Log.d("D", "456489 adding key=>value to sharedpref " + "city" + i + " => " + gson.toJson(drawerItem));
                                Log.d("D","123546879 adding " + "city"+i);
                                added = true;
                                break;
                            }

                        }

                        if (!added) {
                            Snackbar.make(rootLayout, "Failure. More than 20 cities. Delete some first", Snackbar.LENGTH_SHORT).show();
                        } else {
                            if(!drawerItem.getCity().equals("Can't find city")){
                                initAndPopulateDrawer();
                                PreferenceManager
                                        .getDefaultSharedPreferences(mContext).edit().putString("zip", drawerItem.getZip()).commit();
                                refreshData();
                                Snackbar.make(rootLayout, "Success! Added " + drawerItem.getCity(), Snackbar.LENGTH_SHORT)
                                        .show();
                            }else{
                                Snackbar.make(rootLayout, "Success! Added " + drawerItem.getState(), Snackbar.LENGTH_SHORT)
                                        .show();
                            }

                        }


                    } else {
                        Snackbar.make(rootLayout, "Failure. Not a valid zip", Snackbar.LENGTH_SHORT)
                                .show();
                    }

                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openSettingsActivity(View v){
        Intent i = new Intent(mContext,SettingsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }


    public DrawerItem checkIfValidZipCode(String zipToCheck) {
        DrawerItem toReturn = new DrawerItem();

        final Geocoder geocoder = new Geocoder(this);
        Address address = null;
        try {
            List<Address> addresses = geocoder.getFromLocationName(zipToCheck, 1);
            if (addresses != null && !addresses.isEmpty()) {
                address = addresses.get(0);

                // If there is a network connection, try and get the current
                // forecast
                if (haveNetworkConnection()) {
                    String locality = "Can't find city";
                    if (address.getLocality() != null) {

                        Log.d("D", "123654879 checking if zip is valid with locality 0 = " + locality);
                        locality = address.getLocality();
                        toReturn.setCity(locality);
                    }else{
                        toReturn.setCity("no");
                    }
                    String state = "Can't find state";
                    if (address.getAdminArea() != null) {
                        state = address.getAdminArea();
                        toReturn.setState(state);

                        Log.d("D", "123654879 checking if zip is valid with state 1 = " + state);

                    } else if (address.getCountryName() != null) {
                        state = address.getCountryName();
                        toReturn.setState(state);
                        Log.d("D", "123654879 checking if zip is valid with state 2 = " + state);
                    }
                } else {
                    collapsingToolbarLayout.setTitle("No internet :(");
                    rightNowTextView.setText("");
                    weatherIconImageView.setVisibility(View.INVISIBLE);

                }

            } else {
                // Display appropriate message when Geocoder services are
                // not available
                toReturn.setCity("no");
            }
        } catch (IOException e) {
            // handle exception
            toReturn.setCity("no");

        }

        return toReturn;
    }

    public void getWeather() {
        Log.d("D", "123654 in get weather");


        foundLocation = false;
        updatedSettings = false;
        mWeatherList = new ArrayList<Weather>();


        // Get the stored zip code
        zipToUseIfLocationFailed = PreferenceManager
                .getDefaultSharedPreferences(this).getString(
                        "zip", "94114");


        // If the location is enabled either through GPS or network, try
        // and get the location
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            location = lm
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // If there was a location found and the user has an
            // internet connection, try and get an address from the lat
            // and lon
            if (location != null && haveNetworkConnection()) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                gcd = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(latitude,
                            longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses.size() > 0) {
                    locationToSend = addresses.get(0).getLocality()
                            + ", " + addresses.get(0).getAdminArea();
                    if (addresses.get(0).getPostalCode() != null) {
                        PreferenceManager
                                .getDefaultSharedPreferences(
                                        this)
                                .edit()
                                .putString(
                                        "zip",
                                        addresses.get(0)
                                                .getPostalCode())
                                .commit();
                    }

                } else {
                    locationToSend = "No address associated with coordinates: "
                            + latitude + " " + longitude;
                }
                lm.removeUpdates(this);
                Log.d("heeere2", "yeaaa");
                foundLocation = true;
                city = locationToSend;
                Log.d("D", "123654789 setting current weather here 2");
                collapsingToolbarLayout.setTitle(city + " " + date);
                actionbarTitle = city + " " + date;


                //collapsingToolbarLayout.setTitle("TITLE 1");


                if (haveNetworkConnection()) {
                    Log.d("D", "123654 calling set weather with city = " + city + " " + latitude + " " + longitude);
                    setWeather(latitude + "", longitude + "",
                            locationToSend);
                } else {
                    collapsingToolbarLayout.setTitle("No internet connection :(");

                    rightNowTextView.setText("");
                    weatherIconImageView.setVisibility(View.INVISIBLE);
                }

            }

        } else {
            // The user doesn't have location enabled.
            // Prompt them with an alert that they can click to go to
            // their
            // settings

            locationError = true;


            if (haveNetworkConnection()) {
                Log.d("D", "123654 in get weather here because location is diabled with zip = " + zipToUseIfLocationFailed);
                setWeather("", "", zipToUseIfLocationFailed);
            } else {
                collapsingToolbarLayout.setTitle("No internet connection :(");

                rightNowTextView.setText("");
                weatherIconImageView.setVisibility(View.INVISIBLE);
            }

        }

    }

    public void hideErrorMenuItem() {
        //Show error icon in menu
        MenuItem item = mMenu.findItem(R.id.action_error);
        item.setVisible(false);
    }

    @Override
    public void onLocationChanged(Location arg0) {
        mWeatherList = new ArrayList<Weather>();

        hideErrorMenuItem();

        locationError = false;

        // Clear listview
        recyclerView.setAdapter(null);

        // If there is a network connection, try and get the address from lat
        // and lon
        if (haveNetworkConnection()) {
            gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            latitude = arg0.getLatitude();
            longitude = arg0.getLongitude();
            try {
                addresses = gcd.getFromLocation(arg0.getLatitude(),
                        arg0.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses.size() > 0) {
                if (addresses.get(0).getPostalCode() != null) {
                    PreferenceManager
                            .getDefaultSharedPreferences(this)
                            .edit()
                            .putString("zip", addresses.get(0).getPostalCode())
                            .commit();
                }
                locationToSend = addresses.get(0).getLocality() + ", "
                        + addresses.get(0).getAdminArea();
            } else {
                locationToSend = "No address associated with coordinates: "
                        + latitude + " " + longitude;
            }
            city = locationToSend;
            //collapsingToolbarLayout.setTitle("TITLE 2");


            //collapsingToolbarLayout.setTitle(locationToSend + " " + date);


            // Stop location manager
            lm.removeUpdates(this);
            Log.d("heeere1", "yeaaa");
            foundLocation = true;

            if (haveNetworkConnection()) {
                setWeather(latitude + "", longitude + "", locationToSend);
            } else {
                collapsingToolbarLayout.setTitle("No internet :(");

                rightNowTextView.setText("");
                weatherIconImageView.setVisibility(View.INVISIBLE);
            }
        } else {
            collapsingToolbarLayout.setTitle("No internet :(");
            rightNowTextView.setText("");
            weatherIconImageView.setVisibility(View.INVISIBLE);
        }

        initAndPopulateDrawer();

    }

    public void setWeather(String lat, String lon, String city) {
        String url = null;

        // If lat and lon are blank, that means we have to get the location from
        // the zip in the location
        if (lat.equals("") && lon.equals("")) {

            final Geocoder geocoder = new Geocoder(this);
            Address address = null;
            try {
                List<Address> addresses = geocoder.getFromLocationName(city, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    address = addresses.get(0);

                    if(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.celcius_or_fahrenheit), getResources().getString(R.string.fahrenheit)).equals(getResources().getString(R.string.fahrenheit))){
                        url = "http://api.openweathermap.org/data/2.5/forecast/daily?lat="
                                + address.getLatitude()
                                + "&lon="
                                + address.getLongitude()
                                + "&cnt=14&mode=json&units=imperial";
                    }else{
                        url = "http://api.openweathermap.org/data/2.5/forecast/daily?lat="
                                + address.getLatitude()
                                + "&lon="
                                + address.getLongitude()
                                + "&cnt=14&mode=json&units=metric";
                    }

                    Log.d("D", "123654 setting url to = " + url);

                    // If there is a network connection, try and get the current
                    // forecast
                    if (haveNetworkConnection()) {
                        String locality = "Can't find city";
                        if (address.getLocality() != null) {
                            locality = address.getLocality();
                        }

                        String state = "Can't find state";
                        if (address.getAdminArea() != null) {
                            state = address.getAdminArea();
                        } else if (address.getCountryName() != null) {
                            state = address.getCountryName();
                        }
                        Log.d("D", "123654 calling get current weather with lat = " + address.getLatitude() + " " +
                                "and long = " + address.getLongitude() + " " + " and locality = " + locality + " state = " + state);
                        getCurrentWeather(address.getLatitude() + "",
                                address.getLongitude() + "", locality + ", "
                                        + state);
                    } else {
                        collapsingToolbarLayout.setTitle("No internet :(");
                        rightNowTextView.setText("");
                        weatherIconImageView.setVisibility(View.INVISIBLE);

                    }

                } else {
                    // Display appropriate message when Geocoder services are
                    // not available
                    collapsingToolbarLayout.setTitle("Unable to retrieve location from zip code");
                }
            } catch (IOException e) {
                // handle exception
            }

        } else {

            if(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.celcius_or_fahrenheit), getResources().getString(R.string.fahrenheit)).equals(getResources().getString(R.string.fahrenheit))){
                url = "http://api.openweathermap.org/data/2.5/forecast/daily?lat="
                        + lat + "&lon=" + lon + "&cnt=14&mode=json&units=imperial";
            }else{
                url = "http://api.openweathermap.org/data/2.5/forecast/daily?lat="
                        + lat + "&lon=" + lon + "&cnt=14&mode=json&units=metric";
            }

            Log.d("D", "123654 setting url to 2 = " + url);
            Log.d("D", "123654 and city 1 = " + city);

            if (haveNetworkConnection()) {
                getCurrentWeather(lat, lon, city);
            } else {
                collapsingToolbarLayout.setTitle("No internet connection");
                rightNowTextView.setText("");
                weatherIconImageView.setVisibility(View.INVISIBLE);
            }

        }

        // Send call to get user information
        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
        cb.url(url).type(JSONObject.class).weakHandler(this, "jsonCallback");

        mPrgDialog = new ProgressDialog(this);
        mPrgDialog.setCancelable(false);
        mPrgDialog.setTitle("Gathering the ever import weather data");
        // Display Progress Dialog Bar by invoking progress method
        mAQuery.progress(mPrgDialog).ajax(cb);

        //mAQuery.ajax(url, JSONObject.class, this, "jsonCallback");
    }

    /**
     * Method to get the forecast for the current day
     *
     * @param lat
     * @param lon
     * @param city
     */
    private void getCurrentWeather(String lat, String lon, String city) {

        String url;
        String[] temp = new String[2];
        temp = city.split(",");
        this.city = temp[0];

        //collapsingToolbarLayout.setTitle(city + " " + date);
        //collapsingToolbarLayout.setTitle("TITLE 3");


        if (lat.equals("") && lon.equals("")) {
            if(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.celcius_or_fahrenheit), getResources().getString(R.string.fahrenheit)).equals(getResources().getString(R.string.fahrenheit))){
                url = "http://api.openweathermap.org/data/2.5/weather?lat="
                        + latitude + "&lon=" + longitude
                        + "&mode=json&units=imperial";
            }else{
                url = "http://api.openweathermap.org/data/2.5/weather?lat="
                        + latitude + "&lon=" + longitude
                        + "&mode=json&units=metric";
            }


        } else {
            if(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.celcius_or_fahrenheit), getResources().getString(R.string.fahrenheit)).equals(getResources().getString(R.string.fahrenheit))){
                url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat
                        + "&lon=" + lon + "&mode=json&units=imperial";
            }else{
                url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat
                        + "&lon=" + lon + "&mode=json&units=metric";
            }

        }

        // Send call to get user information
        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
        cb.url(url).type(JSONObject.class)
                .weakHandler(this, "currentWeatherCallback");

        mPrgDialog = new ProgressDialog(this);
        mPrgDialog.setCancelable(false);
        mPrgDialog.setTitle("Gathering the ever import weather data");
        // Display Progress Dialog Bar by invoking progress method
        mAQuery.progress(mPrgDialog).ajax(cb);

    }

    /**
     * called when the internet call is finished to getting the current forecast
     *
     * @param url
     * @param json
     * @param status
     * @throws JSONException
     * @throws ParseException
     */

    public void currentWeatherCallback(String url, JSONObject json,
                                       AjaxStatus status) throws JSONException, ParseException {

        try {
            if (json != null) {

                Log.d("D", "123456789 IN CALLBACK WITH JSON = " + json.toString());

                rightNowWeatherObject = new Weather();
                JSONArray weather = json.getJSONArray("weather");

                JSONObject main = json.getJSONObject("main");
                JSONObject currentWeather = weather.getJSONObject(0);

                rightNowWeatherObject.setIcon(currentWeather.getString("icon"));

                rightNowWeatherObject.setWeatherConditionDescription(currentWeather
                        .getString("description"));


                rightNowWeatherObject.setTemp(Math.round(Double.parseDouble(main.getString("temp")))
                        + "");
                rightNowWeatherObject.setTempMax(Math.round(Double.parseDouble(main
                        .getString("temp_max"))) + "");
                rightNowWeatherObject.setTempMin(Math.round(Double.parseDouble(main
                        .getString("temp_min"))) + "");

                // Get and convert date
                Long dt = json.getLong("dt") * 1000;
                Date date = new java.util.Date(dt);
                SimpleDateFormat fmt = new SimpleDateFormat("MM-dd-yyyy");
                String dateToUse = fmt.format(date);
                rightNowWeatherObject.setDt(dateToUse);
                this.date = rightNowWeatherObject.getDt();
                Log.d("D", "123654789 setting current weather here 1");
                collapsingToolbarLayout.setTitle(city + " " + rightNowWeatherObject.getDt());
                getSupportActionBar().setTitle(city + " " + rightNowWeatherObject.getDt());
                actionbarTitle = city + " " + rightNowWeatherObject.getDt();
                collapseThenExpand();


                Log.d("D", "123456789 w object created and w.gettemp = " + rightNowWeatherObject.getTemp());


                //collapsingToolbarLayout.setTitle("TITLE 4");


            }
        } catch (Exception e) {
            Log.d("D", "123654 EXCEPTION = " + e.getMessage());
        }


        setRightNowText();


    }

    @Override
    public void onResume() {
        super.onResume();
        if ((lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                && !foundLocation) {


            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    this);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        if (updatedSettings) {
            updatedSettings = false;
            refreshData();
        }

    }

    /**
     * Called when the internet call to get the forecast is done
     *
     * @param url
     * @param json
     * @param status
     * @throws JSONException
     * @throws ParseException
     */
    public void jsonCallback(String url, JSONObject json, AjaxStatus status)
            throws JSONException, ParseException {
        try {
            if (json != null) {
                JSONArray list = json.getJSONArray("list");
                Log.d("D", "123654987 IN JSON CALLBACK WITH LIST LENGTH = " + list.length());
                for (int i = 0; i < list.length(); i++) {
                    rightNowWeatherObject = new Weather();

                    // Get and convert date
                    Long dt = list.getJSONObject(i).getLong("dt") * 1000;
                    Date date = new java.util.Date(dt);
                    SimpleDateFormat fmt = new SimpleDateFormat("E, MMM dd");
                    String dateToUse = fmt.format(date);
                    rightNowWeatherObject.setDt(dateToUse);

                    rightNowWeatherObject.setPressure(list.getJSONObject(i).getLong("pressure") + "");
                    rightNowWeatherObject.setHumidity(list.getJSONObject(i).getInt("humidity") + "");
                    rightNowWeatherObject.setWindSpeed(list.getJSONObject(i).getLong("speed") + "");
                    rightNowWeatherObject.setWindDirection(list.getJSONObject(i).getInt("deg") + "");

                    // Get low
                    JSONObject temp = list.getJSONObject(i).getJSONObject("temp");
                    rightNowWeatherObject.setTempMin(Math.round(temp.getDouble("min")) + "");
                    // Get high
                    rightNowWeatherObject.setTempMax(Math.round(temp.getDouble("max")) + "");

                    // Get Other weather
                    JSONArray weather = list.getJSONObject(i).getJSONArray(
                            "weather");
                    for (int j = 0; j < weather.length(); j++) {
                        rightNowWeatherObject.setIcon(weather.getJSONObject(j).getString("icon"));
                        rightNowWeatherObject.setWeatherConditionDescription(weather.getJSONObject(j)
                                .getString("description"));
                        rightNowWeatherObject.setId(weather.getJSONObject(j).getString("id"));
                        rightNowWeatherObject.setMain(weather.getJSONObject(j).getString("main"));
                    }
                    if (rightNowWeatherObject != null) {
                        mWeatherList.add(rightNowWeatherObject);
                    }

                }


                // specify an adapter (see also next example)
                MainWeatherListAdapter mAdapter = new MainWeatherListAdapter(this, mWeatherList);
                recyclerView.setAdapter(mAdapter);



/*            // Set up list
            MyAdapter ad = new MyAdapter(this, mWeatherList);
            mPostListView.setAdapter(ad);
            ad.notifyDataSetChanged();
            mPostListView.invalidateViews();*/

            }
        } catch (Exception e) {
            Log.d("D", "123654 IN ANOTHER EXCEPTION WITH E = " + e.getMessage());
        }


    }

    @Override
    public void onProviderDisabled(String arg0) {

    }

    @Override
    public void onProviderEnabled(String arg0) {

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

    }

    /**
     * Checks to see what the weather object's icon code is and sets the correct
     * recourse
     *
     * @param currentIcon
     * @return
     */
    private int getResourceIcon(String currentIcon) {
        int iconResource = 0;
        // Clear sky day and night
        if (currentIcon.equals("01d")) {
            iconResource = R.drawable.clear_sky_day;
        } else if (currentIcon.equals("01n")) {
            iconResource = R.drawable.clear_sky_night;
        }
        // Few clouds day and night
        else if (currentIcon.equals("02d")) {
            iconResource = R.drawable.few_clouds_day;
        } else if (currentIcon.equals("02n")) {
            iconResource = R.drawable.few_clouds_night;
        }
        // Scattered clouds day and night
        else if (currentIcon.equals("03d")) {
            iconResource = R.drawable.cloudy_day;
        } else if (currentIcon.equals("03n")) {
            iconResource = R.drawable.cloudy_night;
        }
        // Broken clouds day and night
        else if (currentIcon.equals("04d")) {
            iconResource = R.drawable.broken_clouds_night_and_day;
        } else if (currentIcon.equals("04n")) {
            iconResource = R.drawable.broken_clouds_night_and_day;
        }
        // Shower rain day and night
        else if (currentIcon.equals("09d")) {
            iconResource = R.drawable.shower_rain_day;
        } else if (currentIcon.equals("09n")) {
            iconResource = R.drawable.shower_rain_night;
        }
        // Rain day and night
        else if (currentIcon.equals("10d")) {
            iconResource = R.drawable.rain_day;
        } else if (currentIcon.equals("10n")) {
            iconResource = R.drawable.rain_night;
        }
        // Thunderstorm day and night
        else if (currentIcon.equals("11d")) {
            iconResource = R.drawable.thunder_day;
        } else if (currentIcon.equals("11n")) {
            iconResource = R.drawable.thunder_night;
        }
        // Snow day and night
        else if (currentIcon.equals("13d")) {
            iconResource = R.drawable.snow_day;
        } else if (currentIcon.equals("13n")) {
            iconResource = R.drawable.snow_night;
        }
        // Mist day and night
        else if (currentIcon.equals("50d")) {
            iconResource = R.drawable.mist_day;
        } else if (currentIcon.equals("50n")) {
            iconResource = R.drawable.mist_night;
        }

        return iconResource;
    }

    /**
     * Called when the user hits the refresh button
     */
    public void refreshData() {

        mWeatherList = new ArrayList<Weather>();

        // Clear listview
        recyclerView.setAdapter(null);

        String url = null;

        // If lat and lon are blank, that means we have to get the location from
        // the zip in the location
        if (latitude == 0.0 && longitude == 0.0) {
            zipToUseIfLocationFailed = PreferenceManager
                    .getDefaultSharedPreferences(this).getString(
                            "zip", "94114");
            Log.d("latitude", zipToUseIfLocationFailed + "");
            final Geocoder geocoder = new Geocoder(this);
            Address address = null;
            try {
                if (haveNetworkConnection()) {
                    List<Address> addresses = geocoder.getFromLocationName(
                            zipToUseIfLocationFailed, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        address = addresses.get(0);

                        if(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.celcius_or_fahrenheit), getResources().getString(R.string.fahrenheit)).equals(getResources().getString(R.string.fahrenheit))){
                            // Set up URL
                            url = "http://api.openweathermap.org/data/2.5/forecast/daily?lat="
                                    + address.getLatitude()
                                    + "&lon="
                                    + address.getLongitude()
                                    + "&cnt=14&mode=json&units=imperial";
                        }else{
                            // Set up URL
                            url = "http://api.openweathermap.org/data/2.5/forecast/daily?lat="
                                    + address.getLatitude()
                                    + "&lon="
                                    + address.getLongitude()
                                    + "&cnt=14&mode=json&units=metric";
                        }





                        String locality = "Can't find city";
                        if (address.getLocality() != null) {
                            locality = address.getLocality();
                        }

                        String state = "Can't find state";
                        if (address.getAdminArea() != null) {
                            state = address.getAdminArea();
                        } else if (address.getCountryName() != null) {
                            state = address.getCountryName();
                        }

                        getCurrentWeather(address.getLatitude() + "",
                                address.getLongitude() + "", locality + ", "
                                        + state);
                    } else {
                        // Display appropriate message when Geocoder services
                        // are
                        // not available
                        collapsingToolbarLayout.setTitle("Unable to retrieve location from zip code");

                        rightNowTextView.setText("");
                        weatherIconImageView.setVisibility(View.INVISIBLE);
                    }

                } else {
                    if (!haveNetworkConnection()) {
                        collapsingToolbarLayout.setTitle("No internet connection :(");

                        rightNowTextView.setText("");
                        weatherIconImageView.setVisibility(View.INVISIBLE);
                    }

                }
            } catch (IOException e) {
                // Display appropriate message when Geocoder services are
                // not available
                collapsingToolbarLayout.setTitle("Unable to retrieve location from zip code");

                rightNowTextView.setText("");
                weatherIconImageView.setVisibility(View.INVISIBLE);
            }

        } else {


            if(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.celcius_or_fahrenheit), getResources().getString(R.string.fahrenheit)).equals(getResources().getString(R.string.fahrenheit))){
                url = "http://api.openweathermap.org/data/2.5/forecast/daily?lat="
                        + latitude + "&lon=" + longitude
                        + "&cnt=14&mode=json&units=imperial";
            }else{
                // Set up URL
                url = "http://api.openweathermap.org/data/2.5/forecast/daily?lat="
                        + latitude + "&lon=" + longitude
                        + "&cnt=14&mode=json&units=metric";
            }
            if (haveNetworkConnection()) {
                getCurrentWeather(latitude + "", longitude + "", locationToSend);
            } else {
                collapsingToolbarLayout.setTitle("No internet connection :(");

                rightNowTextView.setText("");
                weatherIconImageView.setVisibility(View.INVISIBLE);
            }

        }

        // Send call to get user information
        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
        cb.url(url).type(JSONObject.class).weakHandler(this, "jsonCallback");

        mPrgDialog = new ProgressDialog(this);
        mPrgDialog.setCancelable(false);
        mPrgDialog.setTitle("Gathering the ever import weather data");
        // Display Progress Dialog Bar by invoking progress method
        mAQuery.progress(mPrgDialog).ajax(cb);

        //mAQuery.ajax(url, JSONObject.class, this, "jsonCallback");

    }

    /**
     * Determines if the user has a network connection
     *
     * @return
     */
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void collapseThenExpand() {
        controllableAppBarLayout.collapseToolbar();

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                controllableAppBarLayout.expandToolbar(true);
            }
        }, 800);
    }

    private void setTempAndIcon() {
        if(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.celcius_or_fahrenheit), getResources().getString(R.string.fahrenheit)).equals(getResources().getString(R.string.fahrenheit))){
            rightNowTextView.setText(rightNowTemp + " " + (char) 0x00B0 + "F");
        }else{
            rightNowTextView.setText(rightNowTemp + " " + (char) 0x00B0 + "C");
        }

        weatherIconImageView.setVisibility(View.VISIBLE);
        weatherIconImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        try{
            SVG svg = SVG.getFromResource(this, iconTag);
            Drawable drawable = new PictureDrawable(svg.renderToPicture());
            weatherIconImageView.setImageDrawable(drawable);
        }catch (Exception e){
            weatherIconImageView.setImageResource(getResourceIcon("01d"));

        }

        weatherIconImageView.setTag(iconTag);


    }



    public void setRightNowText(){
        Log.d("D", "123456789 setting right now text and w.getTemp = " + rightNowWeatherObject.getTemp());

        if(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.celcius_or_fahrenheit), getResources().getString(R.string.fahrenheit)).equals(getResources().getString(R.string.fahrenheit))){
            rightNowTextView.setText(rightNowWeatherObject.getTemp() + " " + (char) 0x00B0 + "F");
        }else{
            rightNowTextView.setText(rightNowWeatherObject.getTemp() + " " + (char) 0x00B0 + "C");
        }

        weatherIconImageView.setVisibility(View.VISIBLE);
        weatherIconImageView.setImageResource(getResourceIcon(rightNowWeatherObject.getIcon()));

        weatherIconImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // Read an SVG from the assets folder
        try {
            int iconResource = 0;
            // Clear sky day and night
            if (rightNowWeatherObject.getIcon().equals("01d")) {
                iconResource = R.raw.clear_sky_day_svg;
            } else if (rightNowWeatherObject.getIcon().equals("01n")) {
                iconResource = R.raw.clear_sky_night_svg;
            }
            // Few clouds day and night
            else if (rightNowWeatherObject.getIcon().equals("02d")) {
                iconResource = R.raw.few_clouds_day_svg;
            } else if (rightNowWeatherObject.getIcon().equals("02n")) {
                iconResource = R.raw.few_clouds_night_svg;
            }
            // Scattered clouds day and night
            else if (rightNowWeatherObject.getIcon().equals("03d")) {
                iconResource = R.raw.cloudy_day_svg;
            } else if (rightNowWeatherObject.getIcon().equals("03n")) {
                iconResource = R.raw.cloudy_night_svg;
            }
            // Broken clouds day and night
            else if (rightNowWeatherObject.getIcon().equals("04d")) {
                iconResource = R.raw.broken_clouds_night_and_day_svg;
            } else if (rightNowWeatherObject.getIcon().equals("04n")) {
                iconResource = R.raw.broken_clouds_night_and_day_svg;
            }
            // Shower rain day and night
            else if (rightNowWeatherObject.getIcon().equals("09d")) {
                iconResource = R.raw.shower_rain_day_svg;
            } else if (rightNowWeatherObject.getIcon().equals("09n")) {
                iconResource = R.raw.shower_rain_night_svg;
            }
            // Rain day and night
            else if (rightNowWeatherObject.getIcon().equals("10d")) {
                iconResource = R.raw.rain_day_svg;
            } else if (rightNowWeatherObject.getIcon().equals("10n")) {
                iconResource = R.raw.rain_night_svg;
            }
            // Thunderstorm day and night
            else if (rightNowWeatherObject.getIcon().equals("11d")) {
                iconResource = R.raw.thunder_day_svg;
            } else if (rightNowWeatherObject.getIcon().equals("11n")) {
                iconResource = R.raw.thunder_night_svg;
            }
            // Snow day and night
            else if (rightNowWeatherObject.getIcon().equals("13d")) {
                iconResource = R.raw.snow_day_svg;
            } else if (rightNowWeatherObject.getIcon().equals("13n")) {
                iconResource = R.raw.snow_night_svg;
            }
            // Mist day and night
            else if (rightNowWeatherObject.getIcon().equals("50d")) {
                iconResource = R.raw.mist_day_svg;
            } else if (rightNowWeatherObject.getIcon().equals("50n")) {
                iconResource = R.raw.mist_night_svg;
            }
            SVG svg = SVG.getFromResource(this, iconResource);
            Drawable drawable = new PictureDrawable(svg.renderToPicture());
            weatherIconImageView.setImageDrawable(drawable);
            weatherIconImageView.setTag(iconResource);
        } catch (SVGParseException e) {
        }
    }




}
