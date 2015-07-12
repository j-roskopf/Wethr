package joe.wethr.Adapters;

import java.util.List;

import com.androidquery.AQuery;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import joe.wethr.Objects.DrawerItem;
import joe.wethr.R;

public class DrawerAdapter extends ArrayAdapter<DrawerItem> {
    private final Context context;
    private final List<DrawerItem> values;

    AQuery aq;

    LruCache<String, Bitmap> bitmapCache;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @SuppressLint("NewApi")
    public DrawerAdapter(Context context, List<DrawerItem> values) {
        super(context, R.layout.drawer_item, values);
        this.context = context;
        this.values = values;

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

    public int getCount() {
        return values.size();
    }

    public DrawerItem getItem(int position) {
        return values.get(position);
    }

    public long getItemId(int position) {
        return position;
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

    // private view holder class
    private class ViewHolder {
        TextView city;
        TextView state;
        TextView zip;
        ImageView icon;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.drawer_item, parent, false);
            holder = new ViewHolder();
            holder.zip = (TextView) convertView.findViewById(R.id.drawerZip);
            holder.city = (TextView) convertView.findViewById(R.id.drawerCity);
            holder.state = (TextView) convertView.findViewById(R.id.drawerState);
            holder.icon = (ImageView) convertView.findViewById(R.id.drawerIcon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_maps_map));

        holder.city.setText(values.get(position).getCity() + ", ");
        holder.state.setText(values.get(position).getState());
        Log.d("D","3636 zip = " + values.get(position).getZip());
        holder.zip.setText(values.get(position).getZip());

        return convertView;
    }
}