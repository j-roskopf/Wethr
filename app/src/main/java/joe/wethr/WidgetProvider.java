package joe.wethr;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import joe.wethr.Activities.MainActivity;
import joe.wethr.Objects.Weather;

public class WidgetProvider extends AppWidgetProvider {
    DateFormat df = new SimpleDateFormat("hh:mm:ss");
    Weather rightNowWeatherObject;
    AQuery aq;
    Context c;
    int widgetLength;
    int[] appWidgetIds;
    AppWidgetManager awm;


    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        widgetLength = appWidgetIds.length;
        c = context;
        aq = new AQuery(context);
        awm = appWidgetManager;
        this.appWidgetIds = appWidgetIds;

        String zipToUseIfLocationFailed = PreferenceManager
                .getDefaultSharedPreferences(context).getString("zip", "94114");
        if((haveNetworkConnection())){
            setWeather("","",zipToUseIfLocationFailed);
        }else{
            setError();
        }


        Log.i("ExampleWidget",  "Updating widgets " + Arrays.asList(appWidgetIds));
        Log.d("D","213546879 in onupdate");


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
                Log.d("D", "123654789 setting current weather here 1");
/*                collapsingToolbarLayout.setTitle(city + " " + rightNowWeatherObject.getDt());
                getSupportActionBar().setTitle(city + " " + rightNowWeatherObject.getDt());
                actionbarTitle = city + " " + rightNowWeatherObject.getDt();
                collapseThenExpand();*/


                Log.d("D", "123456789 w object created and w.gettemp = " + rightNowWeatherObject.getTemp());


                //collapsingToolbarLayout.setTitle("TITLE 4");


            }
        } catch (Exception e) {
            Log.d("D", "123654 EXCEPTION = " + e.getMessage());
        }


        setRightNowText();


    }

    public void setRightNowText(){
        // Perform this loop procedure for each App Widget that belongs to this
        // provider
        for (int i = 0; i < widgetLength; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(c, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(c.getPackageName(), R.layout.widget_layout);
            //views.setOnClickPendingIntent(R.id.button, pendingIntent);

            // To update a label
            views.setTextViewText(R.id.hiWidget, rightNowWeatherObject.getTempMax());
            // To update a label
            views.setTextViewText(R.id.lowWidget, rightNowWeatherObject.getTempMin());

            int rescIcon = getResourceIcon(rightNowWeatherObject.getIcon());

            views.setImageViewResource(R.id.descImageWidgetSmall, rescIcon);

            // Tell the AppWidgetManager to perform an update on the current app
            // widget
            awm.updateAppWidget(appWidgetId, views);
        }
    }


    public void setWeather(String lat, String lon, String city) {
        String url = null;

        // If lat and lon are blank, that means we have to get the location from
        // the zip in the location
        if (lat.equals("") && lon.equals("")) {

            final Geocoder geocoder = new Geocoder(c);
            Address address = null;
            try {
                List<Address> addresses = geocoder.getFromLocationName(city, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    address = addresses.get(0);

                    if(PreferenceManager.getDefaultSharedPreferences(c).getString(c.getResources().getString(R.string.celcius_or_fahrenheit), c.getResources().getString(R.string.fahrenheit)).equals(c.getResources().getString(R.string.fahrenheit))){
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
                        setError();

                    }

                } else {
                    // Display appropriate message when Geocoder services are
                    // not available
                    setError();
                }
            } catch (IOException e) {
                // handle exception
            }

            // Send call to get user information
            AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
            cb.url(url).type(JSONObject.class).weakHandler(this, "c");

            // Display Progress Dialog Bar by invoking progress method
            aq.ajax(cb);

        }


        //mAQuery.ajax(url, JSONObject.class, this, "jsonCallback");
    }

    private void setError(){
        Log.d("D","IN SET ERROR FOR WIDGET!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        for (int i = 0; i < widgetLength; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(c, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(c.getPackageName(), R.layout.widget_layout);
            //views.setOnClickPendingIntent(R.id.button, pendingIntent);

            views.setViewVisibility(R.id.weatherContainer,View.GONE);
            views.setViewVisibility(R.id.errorMessage,View.VISIBLE);

            // Tell the AppWidgetManager to perform an update on the current app
            // widget
            awm.updateAppWidget(appWidgetId, views);
        }
    }


    /**
     * Determines if the user has a network connection
     *
     * @return
     */
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) c
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

    /**
     * Method to get the forecast for the current day
     *
     * @param lat
     * @param lon
     * @param city
     */
    private void getCurrentWeather(String lat, String lon, String city) {

        String url;




            if(PreferenceManager.getDefaultSharedPreferences(c).getString(c.getResources().getString(R.string.celcius_or_fahrenheit), c.getResources().getString(R.string.fahrenheit)).equals(c.getResources().getString(R.string.fahrenheit))){
                url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat
                        + "&lon=" + lon + "&mode=json&units=imperial";
            }else{
                url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat
                        + "&lon=" + lon + "&mode=json&units=metric";
            }


        // Send call to get user information
        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
        cb.url(url).type(JSONObject.class)
                .weakHandler(this, "currentWeatherCallback");
        // Display Progress Dialog Bar by invoking progress method
        aq.ajax(cb);

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





}