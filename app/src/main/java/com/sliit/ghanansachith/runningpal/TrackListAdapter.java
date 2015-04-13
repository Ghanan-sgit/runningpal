package com.sliit.ghanansachith.runningpal;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Sachith on 4/10/2015.
 */
public class TrackListAdapter extends ArrayAdapter<Track> {

    private Context context;
    List<Track> tracks;
    SharedPreference sharedPreference;

    public TrackListAdapter(Context context, List<Track> tracks) {
        super(context, R.layout.product_list_item, tracks);
        this.context = context;
        this.tracks = tracks;
        sharedPreference = new SharedPreference();
    }

    private class ViewHolder {
        TextView trackId;
        TextView trackTime;
        TextView trackDistance;
        ImageView trackImage;
        TextView trackSpeed;
    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Track getItem(int position) {
        return tracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.product_list_item, null);
            holder = new ViewHolder();
            holder.trackDistance = (TextView) convertView
                    .findViewById(R.id.txt_distance);
            holder.trackTime = (TextView) convertView
                    .findViewById(R.id.txt_time);
            holder.trackSpeed = (TextView) convertView
                    .findViewById(R.id.txt_avgSpeed);
            holder.trackImage = (ImageView) convertView
                    .findViewById(R.id.imgbtn_favorite);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Track track = (Track) getItem(position);
        holder.trackSpeed.setText(track.getSpeed());
        holder.trackTime.setText(track.getTime());
        holder.trackDistance.setText(track.getDistance() + "");

        /*If a product exists in shared preferences then set heart_red drawable
         * and set a tag*/
      //  if (checkFavoriteItem(product)) {
            holder.trackImage.setImageResource(R.drawable.history_tab_icon);
            holder.trackImage.setTag("red");
     //   } else {
         //   holder.favoriteImg.setImageResource(R.drawable.heart_grey);
         //   holder.favoriteImg.setTag("grey");
      //  }

        return convertView;
    }

    /*Checks whether a particular product exists in SharedPreferences*/
   /* public boolean checkFavoriteItem(Product checkProduct) {
        boolean check = false;
        List<Product> favorites = sharedPreference.getFavorites(context);
        if (favorites != null) {
            for (Product product : favorites) {
                if (product.equals(checkProduct)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }
*/
    @Override
    public void add(Track track) {
        super.add(track);
        tracks.add(track);
        notifyDataSetChanged();
    }

    @Override
    public void remove(Track track) {
        super.remove(track);
        tracks.remove(track);
        notifyDataSetChanged();
    }
}
