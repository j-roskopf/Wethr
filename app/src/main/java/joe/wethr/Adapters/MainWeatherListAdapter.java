package joe.wethr.Adapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;

import joe.wethr.Objects.IconDescription;
import joe.wethr.R;
import joe.wethr.Objects.Weather;
import joe.wethr.Activities.WeatherDetailActivity;

public class MainWeatherListAdapter extends RecyclerView.Adapter<MainWeatherListAdapter.ViewHolder> {

    private ArrayList<Weather> mDataset;

    Context mContext;

    AQuery aq;

    LruCache<String, Bitmap> bitmapCache;

    IconDescription iconDesc;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView hi;
        TextView lo;
        TextView date;
        TextView description;
        ImageView icon;



        Weather w;
        View v;

        public void setWeather(Weather w){
            this.w = w;
        }





        public ViewHolder(View v) {
            super(v);
            this.v = v;
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, WeatherDetailActivity.class);
                    ArrayList<Weather> temp = new ArrayList<Weather>();
                    temp.add(w);
                    Log.d("D","12354989 putting weather object into extra " + w.getId());
                    i.putParcelableArrayListExtra("weater", temp);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(i);

                }
            });
            hi = (TextView) v.findViewById(R.id.hi);
            lo = (TextView) v.findViewById(R.id.low);
            date = (TextView) v.findViewById(R.id.date);
            description = (TextView) v
                    .findViewById(R.id.description);
            icon = (ImageView) v.findViewById(R.id.icon);
        }




    }

    public void add(int position, Weather item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(String item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainWeatherListAdapter(Context c, ArrayList<Weather> myDataset) {
        mContext = c;
        aq = new AQuery(c);
        mDataset = myDataset;

        iconDesc = new IconDescription();

        // Get memory class of this device, exceeding this amount will throw an
        // OutOfMemory exception.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory());

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        bitmapCache = new LruCache<String, Bitmap>(cacheSize) {
            @SuppressLint("NewApi")
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();

            }
        };
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MainWeatherListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            bitmapCache.put(key, bitmap);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public Bitmap getBitmapFromMemCache(String key) {
        return bitmapCache.get(key);
    }



    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {



        if (getBitmapFromMemCache(Integer.valueOf(position) + "") != null) {
            holder.icon.setImageBitmap(getBitmapFromMemCache(Integer.valueOf(position)
                    + ""));
        } else {

            String currentIcon = mDataset.get(position).getIcon();
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
            Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(),
                    iconResource);
            //holder.icon = iconResource;
            holder.icon.setImageBitmap(bm);
            //aq.id(R.id.icon).image(iconResource);
            bitmapCache.put(Integer.valueOf(position) + "", bm);

        }

        String desc = "";
        try{
            desc = iconDesc.getDescription(Integer.parseInt(mDataset.get(position).getId()));
        }catch (Exception e){
            desc = mDataset.get(position)
                    .getWeatherConditionDescription();
        }

        if(PreferenceManager.getDefaultSharedPreferences(mContext).getString(mContext.getResources().getString(R.string.celcius_or_fahrenheit), mContext.getResources().getString(R.string.fahrenheit)).equals(mContext.getResources().getString(R.string.fahrenheit))){
            holder.lo.setText(mDataset.get(position).getTempMin() + " " + (char) 0x00B0 +"F");
            holder.hi.setText(mDataset.get(position).getTempMax() + " " + (char) 0x00B0 +"F");
        }else{
            holder.lo.setText(mDataset.get(position).getTempMin() + " " + (char) 0x00B0 +"C");
            holder.hi.setText(mDataset.get(position).getTempMax() + " " + (char) 0x00B0 +"C");
        }

        holder.date.setText(mDataset.get(position).getDt());
        holder.description.setText(mDataset.get(position).getMain()+"\n"+mDataset.get(position)
                .getWeatherConditionDescription());

        holder.setWeather(mDataset.get(position));


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }



}