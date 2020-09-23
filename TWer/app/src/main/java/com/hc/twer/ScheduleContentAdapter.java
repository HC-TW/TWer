package com.hc.twer;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ScheduleContentAdapter extends RecyclerView.Adapter<ScheduleContentAdapter.ContentViewHolder> implements DragHelper.ActionCompletionContract{

    private SharedPreference sharedPreference = new SharedPreference();
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private Context context;
    private ArrayList<MyScheduleContent> scheduleContents;

    private MyScheduleContent scheduleContent;
    private ArrayList<Integer> placeTransportWays;

    private ItemTouchHelper itemTouchHelper;
    private View draggingView;

    private int schedulePos;
    private int datePos;

    String TRAVEL_MODE = "";
    int TRANSPORTWAY_RESID = 0;

    public ScheduleContentAdapter(Context context, ArrayList<MyScheduleContent> scheduleContents, int schedulePos, int datePos)
    {
        this.context = context;
        this.scheduleContents = scheduleContents;
        this.schedulePos = schedulePos;
        this.datePos = datePos;

        scheduleContent = scheduleContents.get(schedulePos);
        placeTransportWays = scheduleContent.getSchedulePlaceTransportWays().get(datePos);
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {

        private SwipeRevealLayout swipeRevealLayout;
        private ImageView place_delete;
        private ImageView place_duplicate;

        private RelativeLayout timeBlock;
        private TextView startTime;
        private TextView endTime;
        private ImageView place;
        private TextView placeName;

        private RelativeLayout transportation;
        private ImageView transportWay;
        private TextView transportTime;

        public ContentViewHolder(View itemView) {
            super(itemView);

            swipeRevealLayout = itemView.findViewById(R.id.place_swipe_reveal_layout);
            place_delete = itemView.findViewById(R.id.place_delete);
            place_duplicate = itemView.findViewById(R.id.place_duplicate);

            timeBlock = itemView.findViewById(R.id.content_time_block);
            startTime = itemView.findViewById(R.id.content_start_time);
            endTime = itemView.findViewById(R.id.content_end_time);
            place = itemView.findViewById(R.id.content_place_background);
            placeName = itemView.findViewById(R.id.content_place_name);

            transportation = itemView.findViewById(R.id.content_transportation);
            transportWay = itemView.findViewById(R.id.content_transport_image);
            transportTime = itemView.findViewById(R.id.content_transport_time);
        }
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.schedule_content_layout, parent, false);

        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        if (position == getItemCount()-1)
        {
            holder.transportation.setVisibility(View.GONE);
        }
        // get Place
        final MyPlace myPlace = scheduleContent.getSchedulePlaces().get(datePos).get(position);
        // get PlaceTimes
        ArrayList<ArrayList<Integer>> placeTimes = scheduleContent.getSchedulePlaceTimes().get(datePos);

        // Bind Swipe Reveal Layout
        viewBinderHelper.bind(holder.swipeRevealLayout, myPlace.getId());
        // Swipe Reveal Delete Button
        holder.place_delete.setOnClickListener(v -> {
            // temp change
            scheduleContent.getSchedulePlaces().get(datePos).remove(position);
            placeTimes.remove(placeTimes.size()-1);
            scheduleContent.getSchedulePlaceTransportWays().get(datePos).remove(position);
            holder.swipeRevealLayout.close(true);
            notifyDataSetChanged();
            // backup
            sharedPreference.saveMyScheduleContentList(context, scheduleContents);

            Toast.makeText(context, "已刪除景點", Toast.LENGTH_LONG).show();
        });
        // Swipe Reveal Duplicate Button
        holder.place_duplicate.setOnClickListener(v -> {

            // create a last Place time
            int placeSize = placeTimes.size();
            int lastHour = placeTimes.get(placeSize-1).get(2);
            int lastMinute = placeTimes.get(placeSize-1).get(3);
            ArrayList<Integer> lastPlaceTimes = new ArrayList<>();
            lastPlaceTimes.add(lastHour);
            lastPlaceTimes.add(lastMinute);
            lastPlaceTimes.add(lastHour+1);
            lastPlaceTimes.add(lastMinute);

            // temp change
            scheduleContent.getSchedulePlaces().get(datePos).add(position, myPlace);
            placeTimes.add(lastPlaceTimes);
            placeTransportWays.add(position, placeTransportWays.get(position));

            notifyItemInserted(position);
            notifyItemRangeChanged(position, getItemCount() - position);

            // backup
            sharedPreference.saveMyScheduleContentList(context, scheduleContents);

            holder.swipeRevealLayout.close(true);
            Toast.makeText(context, "已複製景點", Toast.LENGTH_LONG).show();
        });

        // set Time
        ArrayList<Integer> placeTime = placeTimes.get(position);
        holder.startTime.setText(getFormatTime(placeTime.get(0), placeTime.get(1)));
        holder.endTime.setText(getFormatTime(placeTime.get(2), placeTime.get(3)));

        // set EditTime event
        final AppCompatActivity appCompatActivity = (AppCompatActivity) context;
        final Bundle bundle = new Bundle();
        bundle.putInt("schedulePos", schedulePos);
        bundle.putInt("datePos", datePos);
        bundle.putInt("order", position);
        holder.timeBlock.setOnClickListener(v -> {
            if (EditHomeFragment.EDIT_MODE)
            {
                EditTimeFragment editTimeFragment = new EditTimeFragment();
                editTimeFragment.setArguments(bundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.edit_fragment_container, editTimeFragment).commit();
            }
            else
            {
                Toast.makeText(context,"如果想要更改時間，請先進入編輯模式！\n按下右上角的鉛筆即可進入編輯模式。", Toast.LENGTH_LONG).show();
            }
        });
        // get & set Name
        holder.placeName.setText(myPlace.getName());
        // Place Detail
        holder.place.setOnClickListener(v -> {
            DetailFragment detailFragment = new DetailFragment();
            // set up place Information
            bundle.putString("Name", myPlace.getName());
            if (myPlace.getAddress() != null) {
                bundle.putString("Address", myPlace.getAddress());
            }
            if (myPlace.getPhoneNumber() != null) {
                if (myPlace.getPhoneNumber().length() > 0) {
                    bundle.putString("Phone", myPlace.getPhoneNumber());
                }
            }
            if (myPlace.getWebsiteUri() != null) {
                bundle.putString("Website", myPlace.getWebsiteUri());
            }
            bundle.putInt("Price", myPlace.getPriceLevel());
            bundle.putDouble("Rating", myPlace.getRating());
            bundle.putString("Id", myPlace.getId());
            bundle.putDouble("Lat", myPlace.getLatLng().latitude);
            bundle.putDouble("Lng", myPlace.getLatLng().longitude);
            bundle.putStringArrayList("OpeningHours", myPlace.getOpeningHours());
            bundle.putIntegerArrayList("ClosedTime", myPlace.getClosedTime());

            detailFragment.setArguments(bundle);
            appCompatActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.edit_fragment_container, detailFragment).commit();
        });
        holder.place.setOnLongClickListener(v -> {
            draggingView = holder.itemView;
            AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.schedule_content_animation_raise);
            set.setTarget(draggingView);
            set.start();
            itemTouchHelper.startDrag(holder);
            return true;
        });
        // Transportation
        setTransportation(holder.transportation, holder.transportWay, holder.transportTime, myPlace,position);
    }

    @Override
    public int getItemCount() {
        return placeTransportWays.size();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        // temp change
        notifyItemMoved(oldPosition, newPosition);
        //notifyItemRangeChanged(oldPosition, Math.abs(newPosition - oldPosition));
    }

    @Override
    public void reallyMoved(int oldPosition, int newPosition) {
        // backup
        ArrayList<MyPlace> places = scheduleContent.getSchedulePlaces().get(datePos);
        MyPlace myPlace = places.remove(oldPosition);
        ArrayList<Integer> placeTransportWays = scheduleContent.getSchedulePlaceTransportWays().get(datePos);
        int placeTransportWay = placeTransportWays.remove(oldPosition);

        places.add(newPosition, myPlace);
        placeTransportWays.add(newPosition, placeTransportWay);

        sharedPreference.saveMyScheduleContentList(context, scheduleContents);

        notifyDataSetChanged();
    }

    @Override
    public void drop() {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.schedule_content_animation_drop);
        set.setTarget(draggingView);
        set.start();
    }

    private String getFormatTime(int hourOfDay, int minute)
    {
        String hourString;
        String minuteString;
        if (hourOfDay < 10)
        {
            hourString = "0" + hourOfDay;
        }
        else
        {
            hourString = Integer.toString(hourOfDay);
        }
        if (minute < 10)
        {
            minuteString = "0" + minute;
        }
        else
        {
            minuteString = Integer.toString(minute);
        }
        return hourString + ":" + minuteString;
    }

    private void setTransportWayParameter(int transportWay)
    {
        switch (transportWay)
        {
            case 0:
                TRAVEL_MODE = "driving";
                TRANSPORTWAY_RESID = R.drawable.ic_car;
                break;
            case 1:
                TRAVEL_MODE = "walking";
                TRANSPORTWAY_RESID = R.drawable.ic_walk;
                break;
            case 2:
                TRAVEL_MODE = "transit";
                TRANSPORTWAY_RESID = R.drawable.ic_train;
                break;
        }
    }

    private void setTransportation(RelativeLayout transportation, ImageView transportWay, TextView transportTime, MyPlace myPlace, int position)
    {
        if (position < getItemCount()-1)
        {
            final MyPlace nextPlace = scheduleContent.getSchedulePlaces().get(datePos).get(position+1);
            transportation.setVisibility(View.VISIBLE);
            setTransportWayParameter(placeTransportWays.get(position));

            // TransportWay
            Glide.with(context).load("").apply(RequestOptions.placeholderOf(TRANSPORTWAY_RESID)).into(transportWay);
            transportWay.setOnClickListener(v -> {
                showTransportWayMenu(v, transportTime, myPlace, nextPlace, position);
            });

            // TransportTime
            setTransportTime(transportTime, myPlace, nextPlace, TRAVEL_MODE);

            // Navigation
            setNavigation(transportTime, myPlace, nextPlace, placeTransportWays.get(position));
        }
    }

    private void showTransportWayMenu(View view, TextView transportTime, MyPlace myPlace, MyPlace nextPlace, int position)
    {
        PopupMenu menu = new PopupMenu(context, view);
        try {
            // Reflection apis to enforce show icon
            Field[] fields = menu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals("mPopup")) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(menu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId())
            {
                case R.id.driving:
                    Glide.with(context).load(R.drawable.ic_car).into((ImageView) view);
                    setTransportTime(transportTime, myPlace, nextPlace, "driving");
                    setNavigation(transportTime, myPlace, nextPlace, 0);
                    placeTransportWays.set(position, 0);
                    break;
                case R.id.walking:
                    Glide.with(context).load(R.drawable.ic_walk).into((ImageView) view);
                    setTransportTime(transportTime, myPlace, nextPlace, "walking");
                    setNavigation(transportTime, myPlace, nextPlace, 1);
                    placeTransportWays.set(position, 1);
                    break;
                case R.id.transit:
                    Glide.with(context).load(R.drawable.ic_train).into((ImageView) view);
                    setTransportTime(transportTime, myPlace, nextPlace, "transit");
                    setNavigation(transportTime, myPlace, nextPlace, 2);
                    placeTransportWays.set(position, 2);
                    break;
            }
            sharedPreference.saveMyScheduleContentList(context, scheduleContents);
            return true;
        });
        menu.inflate(R.menu.transport_way_menu);
        menu.show();
    }

    private void setTransportTime(TextView transportTime, MyPlace myPlace, MyPlace nextPlace, String travelMode)
    {
        Map<String, String> parameter = new HashMap<>();

        parameter.put("origins", "place_id:" + myPlace.getId());
        parameter.put("destinations", "place_id:" + nextPlace.getId());
        parameter.put("mode", travelMode);
        parameter.put("language", "zh-TW");
        parameter.put("departure_time", "now");
        parameter.put("key", context.getString(R.string.google_maps_key));

        MyGoogleDistanceMatrixClient myGoogleDistanceMatrixClient = new MyGoogleDistanceMatrixClient(new MyGoogleDistanceMatrixClient.OnDistanceMatrixListener() {
            @Override
            public void processFinish(String duration) {
                if (duration != null)
                {
                    String traffic_time = "約 " + duration;
                    transportTime.setText(traffic_time);
                }
                else
                {
                    transportTime.setText("無法計算抵達時間");
                }
            }
        }, parameter);
        myGoogleDistanceMatrixClient.execute();
    }

    private void setNavigation(TextView transportTime, MyPlace myPlace, MyPlace nextPlace, int travelMode)
    {
        transportTime.setOnClickListener(null);
        transportTime.setOnClickListener(view -> {
            Bundle latlngArgs = new Bundle();
            latlngArgs.putDouble("originLat", myPlace.getLatLng().latitude);
            latlngArgs.putDouble("originLng", myPlace.getLatLng().longitude);
            latlngArgs.putDouble("destinationLat", nextPlace.getLatLng().latitude);
            latlngArgs.putDouble("destinationLng", nextPlace.getLatLng().longitude);
            latlngArgs.putString("originName", myPlace.getName());
            latlngArgs.putString("destinationName", nextPlace.getName());
            latlngArgs.putInt("travelMode", travelMode);
            NavigationFragment navigationFragment = new NavigationFragment();
            navigationFragment.setArguments(latlngArgs);
            ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.edit_fragment_container, navigationFragment).commit();
        });
    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper)
    {
        this.itemTouchHelper = itemTouchHelper;
    }
}
