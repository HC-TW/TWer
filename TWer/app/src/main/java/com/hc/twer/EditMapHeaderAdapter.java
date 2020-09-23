package com.hc.twer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class EditMapHeaderAdapter extends RecyclerView.Adapter<EditMapHeaderAdapter.EditMapHeaderViewHolder> {

    private Context context;
    private ArrayList<ArrayList<MyPlace>> places;
    private GoogleMap map;
    private ArrayList<Marker> markers;
    private String[] nums = {"", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
    private String[] base = {"", "十"};

    public EditMapHeaderAdapter(Context context, ArrayList<ArrayList<MyPlace>> places, GoogleMap map, ArrayList<Marker> markers)
    {
        this.context = context;
        this.places = places;
        this.map = map;
        this.markers = markers;
    }

    public static class EditMapHeaderViewHolder extends RecyclerView.ViewHolder {

        private TextView dateTitle;
        private RelativeLayout location;
        private TextView location_order;
        private ImageView locationBackground;

        public EditMapHeaderViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTitle = itemView.findViewById(R.id.edit_map_header_date);
            location = itemView.findViewById(R.id.edit_map_header_location);
            location_order = itemView.findViewById(R.id.edit_map_header_location_order);
            locationBackground = itemView.findViewById(R.id.edit_map_header_location_background);
        }
    }

    @NonNull
    @Override
    public EditMapHeaderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.edit_map_header, viewGroup, false);

        return new EditMapHeaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditMapHeaderViewHolder holder, int position) {
        Pair<Integer, Integer> pair = getLocationOrdinalNumber(position);
        int color = Color.HSVToColor(new float[]{30*((pair.first)%12), 1f, 0.85f});
        // date title
        if (pair.second == 0)
        {
            String date = "第" + getDateOrdinalNumber(pair.first+1) + "天";
            // get hsv color value and replace title background
            GradientDrawable textViewBackground = (GradientDrawable) holder.dateTitle.getBackground().mutate();
            textViewBackground.setColor(color);
            textViewBackground.invalidateSelf();
            // set date title
            holder.dateTitle.setText(date);
            holder.dateTitle.setVisibility(View.VISIBLE);
        }
        // location title
        if (places.get(pair.first).size() > 0)
        {
            holder.location.setVisibility(View.VISIBLE);
            VectorChildFinder vectorChildFinder = new VectorChildFinder(context, R.drawable.ic_location, holder.locationBackground);
            VectorDrawableCompat.VFullPath path = vectorChildFinder.findPathByName("location");
            path.setFillColor(color);
            holder.locationBackground.invalidate();
            // location order
            holder.location_order.setText("" + (pair.second+1));
            // location click event
            holder.location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int markerPos = 0;
                    for (int i = 0; i < pair.first; i++)
                    {
                        markerPos += places.get(i).size();
                    }
                    Marker marker = markers.get(markerPos+pair.second);
                    marker.showInfoWindow();
                    map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 250, null);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        for (int i = 0; i < places.size(); i++)
        {
            int dateItemCount = places.get(i).size();
            if (dateItemCount == 0)
            {
                itemCount += 1;
            }
            else
            {
                itemCount += dateItemCount;
            }
        }
        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // get ordinal number
    private String getDateOrdinalNumber(int n)
    {
        String ordinalNum = "";

        if (n < 20)
        {
            ordinalNum += base[n/10] + nums[n%10];
        }
        else
        {
            ordinalNum += nums[n/10] + "十" + nums[n%10];
        }

        return ordinalNum;
    }

    private Pair<Integer, Integer> getLocationOrdinalNumber(int position)
    {
        int result = position;
        int date = 0;
        for (int i = 0; i < places.size(); i++)
        {
            int dateItemCount = places.get(i).size();
            if (dateItemCount == 0)
            {
                dateItemCount = 1;
            }
            if (result < dateItemCount)
            {
                date = i;
                break;
            }
            else
            {
                result -= dateItemCount;
            }
        }
        return new Pair<>(date, result);
    }
}
