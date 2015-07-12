package joe.wethr.Activities;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

import joe.wethr.Objects.IconDescription;
import joe.wethr.Objects.Weather;
import joe.wethr.R;


public class WeatherDetailActivity extends ActionBarActivity {

    String[] colors = {"#F44336","#E91E63","#9C27B0","#673AB7","#3F51B5","#2196F3","#03A9F4","#00BCD4","#009688","#4CAF50","#8BC34A","#FFC107","#FF9800","#FF5722","#607D8B"};
    String[] accent = {"#D32F2F","#C2185B","#7B1FA2","#512DA8","#303F9F","#1976D2","#0288D1","#0097A7","#00796B","#388E3C","#689F38","#FFA000","#F57C00","#E64A19","#455A64"};
    String selectedAccentColor;
    int randomNumber;
    String selectedColor;

    //shared between both simple and adv layout
    RelativeLayout baseLayout;
    ImageView weatherIconDetail;

    //simple layout
    TextView lo;
    TextView high;
    TextView desc;


    Weather w;
    ArrayList<Weather> temp;
    IconDescription iconDesc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iconDesc = new IconDescription();
        Random r = new Random();
        randomNumber = r.nextInt(colors.length);
        selectedAccentColor = accent[randomNumber];
        if((Build.VERSION.SDK_INT) >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor(selectedAccentColor));
        }
        setContentView(R.layout.activity_weather_detail);
        initViews();
        setRandomBackgroundColor();

        Bundle extras = getIntent().getExtras();
        temp = extras.getParcelableArrayList("weater");
        w = temp.get(0);


        setWeatherDetails();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initViews(){
        baseLayout = (RelativeLayout)findViewById(R.id.baseLayout);
        weatherIconDetail = (ImageView)findViewById(R.id.weatherDetailIcon);
        lo = (TextView)findViewById(R.id.lowText);
        high = (TextView)findViewById(R.id.hiText);
        desc = (TextView)findViewById(R.id.desc);
        temp = new ArrayList<>();
    }

    public void setRandomBackgroundColor(){
        selectedColor = colors[randomNumber];
        baseLayout.setBackgroundColor(Color.parseColor(selectedColor));
    }


    public void setWeatherDetails(){
        Log.d("D","weather object id " + w.getId());
        Log.d("D","weather object dt " + w.getDt());
        Log.d("D","weather object humidity " + w.getHumidity());
        Log.d("D","weather object getPressure " + w.getPressure());
        Log.d("D","weather object getTemp " + w.getTemp());
        Log.d("D","weather object getTempMax " + w.getTempMax());
        Log.d("D","weather object getTempMin " + w.getTempMin());
        Log.d("D","weather object getWeatherCondition " + w.getWeatherCondition());
        Log.d("D","weather object getWeatherConditionDescription " + w.getWeatherConditionDescription());
        Log.d("D","weather object getWindDirection " + w.getWindDirection());
        Log.d("D","weather object getWindSpeed " + w.getWindSpeed());

        String stringDesc = iconDesc.getDescription(Integer.parseInt(w.getId()));
        Log.d("D","weather object " + desc);

        boolean containsRainWord = false;
        for(String s: iconDesc.getRainWords()){
            if(stringDesc.contains(s)){
                containsRainWord = true;
            }
        }

        boolean containsSnowWord = false;
        for(String s: iconDesc.getSnowWords()){
            if(stringDesc.contains(s)){
                containsSnowWord = true;
            }
        }

        //already have an additional word in the desc for these ids
        if(w.getId() == "503" ||w.getId() == "511"||w.getId() == "504"){
            desc.setText(stringDesc );
        }else if(containsRainWord && !containsSnowWord ){
            desc.setText(stringDesc + " Bring an umrella!");
        }else if(!containsRainWord && containsSnowWord ){
            desc.setText(stringDesc + " Wear snow boots!");
        }else if(containsRainWord && containsSnowWord ){
            desc.setText(stringDesc + " Bring an umrella and wear snow boots!");
        }else{
            desc.setText(stringDesc);
        }


        if(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.celcius_or_fahrenheit), getResources().getString(R.string.fahrenheit)).equals(getResources().getString(R.string.fahrenheit))){
            lo.setText(w.getTempMin() + " " + (char) 0x00B0 +"F");
            high.setText(w.getTempMax() + " " + (char) 0x00B0 +"F");
        }else{
            lo.setText(w.getTempMin() + " " + (char) 0x00B0 +"C");
            high.setText(w.getTempMax() + " " + (char) 0x00B0 +"C");
        }



        int iconResource = 0;
        // Clear sky day and night
        if (w.getIcon().equals("01d")) {
            iconResource = R.raw.clear_sky_day_svg;
        } else if (w.getIcon().equals("01n")) {
            iconResource = R.raw.clear_sky_night_svg;
        }
        // Few clouds day and night
        else if (w.getIcon().equals("02d")) {
            iconResource = R.raw.few_clouds_day_svg;
        } else if (w.getIcon().equals("02n")) {
            iconResource = R.raw.few_clouds_night_svg;
        }
        // Scattered clouds day and night
        else if (w.getIcon().equals("03d")) {
            iconResource = R.raw.cloudy_day_svg;
        } else if (w.getIcon().equals("03n")) {
            iconResource = R.raw.cloudy_night_svg;
        }
        // Broken clouds day and night
        else if (w.getIcon().equals("04d")) {
            iconResource = R.raw.broken_clouds_night_and_day_svg;
        } else if (w.getIcon().equals("04n")) {
            iconResource = R.raw.broken_clouds_night_and_day_svg;
        }
        // Shower rain day and night
        else if (w.getIcon().equals("09d")) {
            iconResource = R.raw.shower_rain_day_svg;
        } else if (w.getIcon().equals("09n")) {
            iconResource = R.raw.shower_rain_night_svg;
        }
        // Rain day and night
        else if (w.getIcon().equals("10d")) {
            iconResource = R.raw.rain_day_svg;
        } else if (w.getIcon().equals("10n")) {
            iconResource = R.raw.rain_night_svg;
        }
        // Thunderstorm day and night
        else if (w.getIcon().equals("11d")) {
            iconResource = R.raw.thunder_day_svg;
        } else if (w.getIcon().equals("11n")) {
            iconResource = R.raw.thunder_night_svg;
        }
        // Snow day and night
        else if (w.getIcon().equals("13d")) {
            iconResource = R.raw.snow_day_svg;
        } else if (w.getIcon().equals("13n")) {
            iconResource = R.raw.snow_night_svg;
        }
        // Mist day and night
        else if (w.getIcon().equals("50d")) {
            iconResource = R.raw.mist_day_svg;
        } else if (w.getIcon().equals("50n")) {
            iconResource = R.raw.mist_night_svg;
        }

        ImageView root = (ImageView) findViewById(R.id.weatherDetailIcon);
        root.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // Read an SVG from the assets folder
        try
        {
            SVG svg = SVG.getFromResource(this, iconResource);
            Drawable drawable = new PictureDrawable(svg.renderToPicture());
            root.setImageDrawable(drawable);
        }
        catch(SVGParseException e)
        {}

        LinearLayout advancedContainer = (LinearLayout)findViewById(R.id.advancedContainer);


        if(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.pref_title_sync_frequency), getResources().getString(R.string.advanced)).equals(getResources().getString(R.string.advanced))){
            advancedContainer.setVisibility(View.VISIBLE);

            setWindInformation();
            setPressureInformation();
            setPrecipitationInformation();

        }else{
            advancedContainer.setVisibility(View.GONE);
            LinearLayout descContainer = (LinearLayout)findViewById(R.id.descContainer);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) descContainer.getLayoutParams();
            params.weight = 1.0f;
            descContainer.setLayoutParams(params);

        }



    }


    public void setPressureInformation(){
        ImageView pressureIcon = (ImageView)findViewById(R.id.pressureIcon);
        pressureIcon.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // Read an SVG from the assets folder
        try
        {
            SVG svg = SVG.getFromResource(this, R.raw.pressure_icon);
            Drawable drawable = new PictureDrawable(svg.renderToPicture());
            pressureIcon.setImageDrawable(drawable);
        }
        catch(SVGParseException e)
        {}
        TextView pressureText = (TextView)findViewById(R.id.pressureText);
        pressureText.setText(w.getPressure() + " hPa");
    }


    public void setPrecipitationInformation(){

        ImageView humidityIcon = (ImageView)findViewById(R.id.humidityIcon);
        humidityIcon.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // Read an SVG from the assets folder
        try
        {
            SVG svg = SVG.getFromResource(this, R.raw.rain_icon);
            Drawable drawable = new PictureDrawable(svg.renderToPicture());
            humidityIcon.setImageDrawable(drawable);
        }
        catch(SVGParseException e)
        {}

        TextView humidityText = (TextView)findViewById(R.id.humidityText);
        humidityText.setText(w.getHumidity() + " % Hum.");

    }

    public void setWindInformation(){

        ImageView windSpeedIcon = (ImageView)findViewById(R.id.windSpeedIcon);
        windSpeedIcon.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        int dir = Integer.parseInt(w.getWindDirection());
        int toUse = R.raw.north_svg;
        if(dir >= 0 && dir < 23){
            //north
            toUse = R.raw.north_svg;
        }else if(dir >= 23 && dir < 68){
            //north east
            toUse = R.raw.north_east_svg;
        }else if(dir >= 68 && dir < 113){
            //east
            toUse = R.raw.east_svg;
        }else if(dir >= 113 && dir < 158){
            //south east
            toUse = R.raw.south_east_svg;
        }else if(dir >= 158 && dir < 203){
            //south
            toUse = R.raw.south_svg;
        }else if(dir >= 203 && dir < 247){
            //south west
            toUse = R.raw.south_west_svg;
        }else if(dir >= 247 && dir < 293){
            //west
            toUse = R.raw.west_svg;
        }else if(dir >= 293 && dir < 337){
            //north west
            toUse = R.raw.north_west_svg;
        }else if(dir >= 337){
            //north
            toUse = R.raw.north_svg;
        }
        // Read an SVG from the assets folder
        try
        {
            SVG svg = SVG.getFromResource(this, toUse);
            Drawable drawable = new PictureDrawable(svg.renderToPicture());
            windSpeedIcon.setImageDrawable(drawable);
        }
        catch(SVGParseException e)
        {}

        TextView windSpeed = (TextView)findViewById(R.id.windSpeedText);
        windSpeed.setText(w.getWindSpeed() + " M/S");

    }
}
