package com.hc.twer;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.config.GoogleDirectionConfiguration;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.nshmura.recyclertablayout.RecyclerTabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditHomeFragment extends Fragment implements View.OnClickListener {

    private MenuItem menu_edit;
    private MenuItem menu_map;
    private TextView title;

    private SharedPreference sharedPreference = new SharedPreference();
    private ImageView scheduleCover;
    private TextView scheduleName;
    public static TextView scheduleTime;

    private ArrayList<Date> scheduleDates;
    private RecyclerTabLayout tabLayout;
    private DateTabListAdapter dateTabListAdapter;
    private CustomViewPager viewPager;
    private ImageView addDate;
    private ImageView sort;
    private EditContentPagerAdapter editContentPagerAdapter;
    private DragHelper dragHelper;
    private ItemTouchHelper itemTouchHelper;

    public static boolean EDIT_MODE = false;
    private ImageView changeCover;
    private RelativeLayout editNameTimeBlock;
    private ImageView changeNameTime;

    private String[] coverChoices = {"內建封面", "從相片挑選"};
    private String[] nums = {"", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
    private String[] base = {"", "十"};

    private Bundle args;
    private int schedulePos;
    public static int datePos;

    private static final int REQUEST_CODE_INTERNAL_COVER = 0;
    private static final int REQUEST_CODE_PICK_PHOTO = 1;

    public EditHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_home, container, false);

        scheduleCover = view.findViewById(R.id.edit_home_schedule_cover);
        scheduleName = view.findViewById(R.id.edit_home_schedule_name);
        scheduleTime = view.findViewById(R.id.edit_home_schedule_time);
        viewPager = view.findViewById(R.id.dayViewPager);
        tabLayout = view.findViewById(R.id.dayTabs);
        addDate = view.findViewById(R.id.edit_home_add_date);
        sort = view.findViewById(R.id.edit_home_sort);
        changeCover = view.findViewById(R.id.change_cover);
        editNameTimeBlock = view.findViewById(R.id.edit_home_name_time_block);
        changeNameTime = view.findViewById(R.id.change_name_time);

        init();
        setUpViewPager();
        tabHelper();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((EditScheduleActivity)getActivity()).setBackButtonEnabled(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu_edit = menu.findItem(R.id.action_edit);
        menu_map = menu.findItem(R.id.action_map);
        menu_edit.setVisible(true);
        menu_map.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_edit:
                EDIT_MODE = !EDIT_MODE;
                dateTabListAdapter.notifyDataSetChanged();
                if (EDIT_MODE)
                {
                    changeCover.setVisibility(View.VISIBLE);
                    changeNameTime.setVisibility(View.VISIBLE);
                }
                else
                {
                    changeCover.setVisibility(View.GONE);
                    changeNameTime.setVisibility(View.GONE);
                }

                return true;

            case R.id.action_map:
                EditMapFragment editMapFragment = new EditMapFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("schedulePos", schedulePos);
                bundle.putInt("datePos", datePos);
                editMapFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.edit_fragment_container, editMapFragment).commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.edit_home_schedule_cover:
                if (!EDIT_MODE)
                {
                    Toast.makeText(getActivity(),"如果想要更換行程封面，\n請先進入編輯模式！\n按下右上角的鉛筆即可進入編輯模式。", Toast.LENGTH_LONG).show();
                }
                break;
            // change cover
            case R.id.change_cover:
                AlertDialog changeCoverDialog =
                        new AlertDialog.Builder(getActivity())
                        .setTitle("想要更換封面嗎？")
                        .setItems(coverChoices, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which)
                                {
                                    case 0:
                                        Intent intent = new Intent(getActivity(), InternalCoverActivity.class);
                                        intent.putExtra("schedulePos", schedulePos);
                                        startActivityForResult(intent, REQUEST_CODE_INTERNAL_COVER);
                                        getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.noslide);
                                        break;
                                    case 1:
                                        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                        photoPickerIntent.setType("image/*");
                                        startActivityForResult(photoPickerIntent, REQUEST_CODE_PICK_PHOTO);
                                        getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.noslide);
                                        break;
                                }
                            }
                        })
                        .setNegativeButton("取消", null).create();
                TextView textView = new TextView(getActivity());
                textView.setBackground(new ColorDrawable(Color.GRAY));
                textView.setHeight(1);
                ListView listView = changeCoverDialog.getListView();
                listView.addFooterView(textView);
                listView.setDivider(new ColorDrawable(Color.GRAY));
                listView.setDividerHeight(2);
                changeCoverDialog.show();
                break;

            case R.id.edit_home_name_time_block:
                if (EDIT_MODE)
                {
                    EditScheduleNameTimeFragment editScheduleNameTimeFragment = new EditScheduleNameTimeFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("schedulePos", schedulePos);
                    bundle.putString("scheduleName", scheduleName.getText().toString());
                    bundle.putString("scheduleTime", scheduleTime.getText().toString());
                    editScheduleNameTimeFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.edit_fragment_container, editScheduleNameTimeFragment).commit();
                }
                else
                {
                    Toast.makeText(getActivity(),"如果想要更改行程名稱或日期，\n請先進入編輯模式！\n按下右上角的鉛筆即可進入編輯模式。", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.edit_home_add_date:
                // temp change
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(scheduleDates.get(scheduleDates.size()-1));
                calendar.add(Calendar.DATE, 1);
                Date newDate = calendar.getTime();
                scheduleDates.add(newDate);
                String newDateTabTitle = transformDate(newDate) + "\n" + "第" + getOrdinalNumber(scheduleDates.size()) + "天";

                // backup
                ArrayList<MyScheduleContent> scheduleContents = sharedPreference.getMyScheduleContentList(getActivity());
                MyScheduleContent scheduleContent = scheduleContents.get(schedulePos);
                scheduleContent.getScheduleDates().add(newDate);
                scheduleContent.getSchedulePlaces().add(new ArrayList<>());
                scheduleContent.getSchedulePlaceTimes().add(new ArrayList<>());
                scheduleContent.getSchedulePlaceTransportWays().add(new ArrayList<>());
                sharedPreference.saveMyScheduleContentList(getActivity(), scheduleContents);

                editContentPagerAdapter.addFragment(new EditContentFragment(), newDateTabTitle);
                editContentPagerAdapter.notifyDataSetChanged();
                //dateTabListAdapter.notifyDataSetChanged();
                int position = editContentPagerAdapter.getCount()-1;
                dateTabListAdapter.notifyItemInserted(position);
                dateTabListAdapter.notifyItemChanged(position);
                // refresh scheduleTime
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                String scheduleTimeString = simpleDateFormat.format(scheduleDates.get(0)) + " - " + simpleDateFormat.format(scheduleDates.get(scheduleDates.size()-1));
                scheduleTime.setText(scheduleTimeString);
                ArrayList<MySchedule> schedules = sharedPreference.getMyScheduleList(getActivity());
                schedules.get(schedulePos).setScheduleTime(scheduleTimeString);
                sharedPreference.saveMyScheduleList(getActivity(), schedules);

                viewPager.setCurrentItem(position);
                EditHomeFragment.datePos = position;
                Toast.makeText(getActivity(), "已新增 " + transformDate(newDate) + " 行程", Toast.LENGTH_LONG).show();

                break;

            case R.id.edit_home_sort:
                new AlertDialog.Builder(getActivity())
                        .setMessage("最佳排序會依據現在的起終點，\n可能改變現有行程順序喔！")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("確定", (dialog, which) -> {
                            getBestOrder();
                        })
                        .show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case REQUEST_CODE_INTERNAL_COVER:
                if (resultCode == RESULT_OK)
                {
                    Glide.with(this).load("").apply(RequestOptions.placeholderOf(Integer.parseInt(sharedPreference.getMyScheduleList(getActivity()).get(schedulePos).getScheduleCover()))).into(scheduleCover);
                }
                break;

            case REQUEST_CODE_PICK_PHOTO:
                if (resultCode == RESULT_OK)
                {
                    String selectedImage = data.getDataString();
                    Glide.with(this).load(selectedImage).into(scheduleCover);

                    ArrayList<MySchedule> schedules = sharedPreference.getMyScheduleList(getActivity());
                    schedules.get(schedulePos).setScheduleCover(selectedImage);
                    sharedPreference.saveMyScheduleList(getActivity(), schedules);
                }
                break;
        }
    }

    private void init()
    {
        title = getActivity().findViewById(R.id.toolbar_title);
        title.setText("我的行程");
        ((EditScheduleActivity)getActivity()).setBackButtonEnabled(false);
        setHasOptionsMenu(true);

        args = getArguments();
        EDIT_MODE = args.getBoolean("EDIT_MODE", false);
        schedulePos = args.getInt("schedulePos");
        datePos = args.getInt("datePos", 0);
        // check if it's in EDIT MODE
        if (EDIT_MODE)
        {
            changeCover.setVisibility(View.VISIBLE);
            changeNameTime.setVisibility(View.VISIBLE);
        }
        else
        {
            changeCover.setVisibility(View.GONE);
            changeNameTime.setVisibility(View.GONE);
        }

        try {
            Glide.with(this).load("").apply(RequestOptions.placeholderOf(Integer.parseInt(sharedPreference.getMyScheduleList(getActivity()).get(schedulePos).getScheduleCover()))).into(scheduleCover);
        }catch (NumberFormatException e)
        {
            Glide.with(this).load(sharedPreference.getMyScheduleList(getActivity()).get(schedulePos).getScheduleCover()).into(scheduleCover);
        }

        scheduleName.setText(sharedPreference.getMyScheduleList(getActivity()).get(schedulePos).getScheduleName());
        scheduleTime.setText(sharedPreference.getMyScheduleList(getActivity()).get(schedulePos).getScheduleTime());
        scheduleCover.setOnClickListener(this);
        changeCover.setOnClickListener(this);
        editNameTimeBlock.setOnClickListener(this);
        addDate.setOnClickListener(this);
        sort.setOnClickListener(this);
    }
    private void setUpViewPager()
    {
        scheduleDates = sharedPreference.getMyScheduleContentList(getActivity()).get(schedulePos).getScheduleDates();
        editContentPagerAdapter = new EditContentPagerAdapter(getChildFragmentManager(), schedulePos);

        if ( scheduleDates == null )
        {
            scheduleDates = new ArrayList<>();
        }
        for (int i = 0; i < scheduleDates.size(); i++)
        {
            String dateTabTitle = transformDate(scheduleDates.get(i)) + "\n" + "第" + getOrdinalNumber(i+1) + "天";
            editContentPagerAdapter.addFragment(new EditContentFragment(), dateTabTitle);
        }
        viewPager.setAdapter(editContentPagerAdapter);
    }

    private void tabHelper()
    {
        tabLayout.setHasFixedSize(true);
        tabLayout.setItemAnimator(new DefaultItemAnimator());

        dateTabListAdapter = new DateTabListAdapter(viewPager, getActivity(), scheduleDates, schedulePos);
        dragHelper = new DragHelper(dateTabListAdapter);
        itemTouchHelper = new ItemTouchHelper(dragHelper);
        dateTabListAdapter.setItemTouchHelper(itemTouchHelper);
        tabLayout.setUpWithAdapter(dateTabListAdapter);
        itemTouchHelper.attachToRecyclerView(tabLayout);

        tabLayout.setCurrentItem(datePos, false);

        /*View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout)
        {
            ((LinearLayout) root).setShowDividers(RelativeLayout..SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.separator));
            drawable.setSize(2, 1);
            ((LinearLayout) root).setDividerPadding(8);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }*/
    }

    // get real world date
    private String transformDate(Date date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");

        return simpleDateFormat.format(date);
    }

    // get ordinal number
    private String getOrdinalNumber(int n)
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

    private void getBestOrder()
    {
        ArrayList<MyScheduleContent> scheduleContents = sharedPreference.getMyScheduleContentList(getActivity());
        ArrayList<MyPlace> oldPlaces = scheduleContents.get(schedulePos).getSchedulePlaces().get(datePos);
        // check the number of places
        int placesCount = oldPlaces.size();
        if (placesCount <= 2)
        {
            Toast.makeText(getActivity(), "地點不得少於兩個！", Toast.LENGTH_LONG).show();
            return;
        }
        // sort places by distance
        List<LatLng> wayPoints = new ArrayList<>();
        ArrayList<MyPlace> newPlaces = new ArrayList<>();

        for (int i = 1; i < placesCount-1; i++)
        {
            wayPoints.add(oldPlaces.get(i).getLatLng());
        }

        GoogleDirectionConfiguration.getInstance().setLogEnabled(true);
        GoogleDirection.withServerKey(getString(R.string.google_maps_key))
                .from(oldPlaces.get(0).getLatLng())
                .and(wayPoints)
                .to(oldPlaces.get(placesCount-1).getLatLng())
                .optimizeWaypoints(true)
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.getRouteList().size() > 0)
                        {
                            Route route = direction.getRouteList().get(0);
                            int legCount = route.getLegList().size();
                            for (int index = 0; index < legCount; index++) {
                                Leg leg = route.getLegList().get(index);
                                LatLng latLng = leg.getStartLocation().getCoordination();

                                for (int i = 0; i < placesCount; i++) {
                                    LatLng oldLatLng = oldPlaces.get(i).getLatLng();
                                    if (distance(latLng.latitude, latLng.longitude, oldLatLng.latitude, oldLatLng.longitude) < 0.5) {
                                        newPlaces.add(oldPlaces.get(i));
                                        break;
                                    }
                                }
                                if (index == legCount - 1) {
                                    newPlaces.add(oldPlaces.get(placesCount - 1));
                                }
                            }
                            Log.d("test", ""+newPlaces.size());
                            scheduleContents.get(schedulePos).getSchedulePlaces().set(datePos, newPlaces);
                            sharedPreference.saveMyScheduleContentList(getActivity(), scheduleContents);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("EDIT_MODE", EDIT_MODE);
                            bundle.putInt("schedulePos", schedulePos);
                            bundle.putInt("datePos", datePos);
                            EditHomeFragment editHomeFragment = new EditHomeFragment();
                            editHomeFragment.setArguments(bundle);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.edit_fragment_container, editHomeFragment, null).commit();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "有部分景點無法到達，所以無法取得最佳順序！", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {

                    }
                });

        // check closed places
        String remindClosed = "";
        String remindExceeded = "";
        for (int i = 0; i < oldPlaces.size(); i++)
        {
            MyPlace myPlace;
            ArrayList<String> openingHours;
            if (newPlaces.size() > 0)
            {
                myPlace = newPlaces.get(i);
            }
            else
            {
                myPlace = oldPlaces.get(i);
            }
            openingHours = myPlace.getOpeningHours();

            if (openingHours != null)
            {
                int weekOfDay = Integer.valueOf(new SimpleDateFormat("u", Locale.TAIWAN).format(scheduleDates.get(datePos).getTime()));
                if (openingHours.get(weekOfDay-1).contains("休息"))
                {
                    if (remindClosed.length() > 0)
                    {
                        remindClosed += "、";
                    }
                    remindClosed += myPlace.getName();
                }
                else
                {
                    ArrayList<Integer> closedTime = myPlace.getClosedTime();
                    if (closedTime != null)
                    {
                        if (closedTime.size() > 0)
                        {
                            int closeHours = closedTime.get((weekOfDay-1)*2);
                            int closeMinutes = closedTime.get((weekOfDay-1)*2+1);
                            ArrayList<Integer> placeTimes = sharedPreference.getMyScheduleContentList(getActivity()).get(schedulePos).getSchedulePlaceTimes().get(datePos).get(i);
                            if (placeTimes.get(2) > closeHours || (placeTimes.get(2) == closeHours && placeTimes.get(3) >= closeMinutes))
                            {
                                if (remindExceeded.length() > 0)
                                {
                                    remindExceeded += "、";
                                }
                                remindExceeded += myPlace.getName();
                            }
                        }
                    }
                }
            }
        }
        // remind user which places are closed
        if (remindClosed.length() > 0)
        {
            Toast.makeText(getActivity(), remindClosed + "\n今天休息喔，請排在別天吧！", Toast.LENGTH_LONG).show();
        }
        if (remindExceeded.length() > 0)
        {
            Toast.makeText(getActivity(), remindExceeded + "\n超過營業時間了，請調整時間吧！", Toast.LENGTH_LONG).show();
        }
    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }
}
