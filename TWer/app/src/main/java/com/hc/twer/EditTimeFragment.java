package com.hc.twer;


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditTimeFragment extends Fragment implements View.OnClickListener{

    private TextView title;
    private MenuItem confirm;
    private Button startTimeButton;
    private Button endTimeButton;
    private Calendar calendar;
    private SharedPreference sharedPreference;
    private ArrayList<MyScheduleContent> scheduleContents;
    private ArrayList<ArrayList<ArrayList<Integer>>> schedulePlaceTime;
    private ArrayList<Integer> placeTime;
    private Bundle args;
    private int schedulePos;
    private int datePos;
    private int order;

    private int startTimeHour;
    private int startTimeMinute;
    private int endTimeHour;
    private int endTimeMinute;

    public EditTimeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_time, container, false);

        startTimeButton = view. findViewById(R.id.input_start_time);
        endTimeButton = view.findViewById(R.id.input_end_time);

        init();
        setTimeButton();

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        confirm = menu.findItem(R.id.action_time_confirm);
        confirm.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == confirm)
        {
            if (startTimeButton.getText() != "" && endTimeButton.getText() != "" && (startTimeHour < endTimeHour || (startTimeHour == endTimeHour && startTimeMinute <= endTimeMinute)))
            {
                // Time
                if (placeTime.size() > 0)
                {
                    placeTime.set(0, startTimeHour);
                    placeTime.set(1, startTimeMinute);
                    placeTime.set(2, endTimeHour);
                    placeTime.set(3, endTimeMinute);
                }
                else
                {
                    placeTime.add(startTimeHour);
                    placeTime.add(startTimeMinute);
                    placeTime.add(endTimeHour);
                    placeTime.add(endTimeMinute);
                }
                ArrayList<ArrayList<Integer>> datePlaceTimes = schedulePlaceTime.get(datePos);
                datePlaceTimes.remove(order);
                int insertPos = 0;
                for (int i = 0; i < datePlaceTimes.size(); i++)
                {
                    if (startTimeHour > datePlaceTimes.get(i).get(0) || (startTimeHour == datePlaceTimes.get(i).get(0) && startTimeMinute > datePlaceTimes.get(i).get(1)))
                    {
                        insertPos++;
                    }
                }
                datePlaceTimes.add(insertPos, placeTime);
                sharedPreference.saveMyScheduleContentList(getActivity(), scheduleContents);
                // Place
                ArrayList<MyPlace> places = scheduleContents.get(schedulePos).getSchedulePlaces().get(datePos);
                MyPlace myPlace = places.get(order);
                places.remove(order);
                places.add(insertPos, myPlace);
                sharedPreference.saveMyScheduleContentList(getActivity(), scheduleContents);
                // EditHome
                //getActivity().getSupportFragmentManager().popBackStack();
                Bundle bundle = new Bundle();
                bundle.putBoolean("EDIT_MODE", true);
                bundle.putInt("schedulePos", schedulePos);
                bundle.putInt("datePos", EditHomeFragment.datePos);
                EditHomeFragment editHomeFragment = new EditHomeFragment();
                editHomeFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.edit_fragment_container, editHomeFragment).commit();
            }
            else
            {
                Toast.makeText(getActivity(), "結束時間必須晚於開始時間", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((EditScheduleActivity)getActivity()).setBackButtonEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.input_start_time:
                new TimePickerDialog(getActivity(), (view, hourOfDay, minute) -> {
                    startTimeButton.setText(getFormatTime(hourOfDay, minute));
                    startTimeHour = hourOfDay;
                    startTimeMinute = minute;
                }, startTimeHour, startTimeMinute, false).show();
                break;

            case R.id.input_end_time:
                new TimePickerDialog(getActivity(), (view, hourOfDay, minute) -> {
                    endTimeButton.setText(getFormatTime(hourOfDay, minute));
                    endTimeHour = hourOfDay;
                    endTimeMinute = minute;
                }, endTimeHour, endTimeMinute, false).show();
                break;
        }
    }

    private void init()
    {
        ((EditScheduleActivity)getActivity()).setBackButtonEnabled(true);

        setHasOptionsMenu(true);
        title = getActivity().findViewById(R.id.toolbar_title);
        title.setText("修改時間");
        calendar = Calendar.getInstance();
        startTimeButton.setOnClickListener(this);
        endTimeButton.setOnClickListener(this);

        startTimeHour = calendar.get(Calendar.HOUR_OF_DAY);
        startTimeMinute = calendar.get(Calendar.MINUTE);
        endTimeHour = calendar.get(Calendar.HOUR_OF_DAY);
        endTimeMinute = calendar.get(Calendar.MINUTE);

        sharedPreference = new SharedPreference();
        args = getArguments();
        schedulePos = args.getInt("schedulePos");
        datePos = args.getInt("datePos");
        order = args.getInt("order");

        scheduleContents = sharedPreference.getMyScheduleContentList(getActivity());
        schedulePlaceTime = scheduleContents.get(schedulePos).getSchedulePlaceTimes();
        placeTime = schedulePlaceTime.get(datePos).get(order);
    }

    private void setTimeButton()
    {
        if (placeTime.size() > 0)
        {
            startTimeHour = placeTime.get(0);
            startTimeMinute = placeTime.get(1);
            endTimeHour = placeTime.get(2);
            endTimeMinute = placeTime.get(3);
            startTimeButton.setText(getFormatTime(startTimeHour, startTimeMinute));
            endTimeButton.setText(getFormatTime(endTimeHour, endTimeMinute));
        }
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



}
