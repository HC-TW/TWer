package com.hc.twer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditScheduleNameTimeFragment extends Fragment {

    private SharedPreference sharedPreference;
    private MenuItem confirm;
    private TextView title;
    private EditText scheduleName;
    private Button scheduleTime;

    private Bundle args;
    private int schedulePos;
    private ArrayList<Date> saveDates;

    public EditScheduleNameTimeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_schedule_name_time, container, false);

        scheduleName = view.findViewById(R.id.edit_schedule_name);
        scheduleTime = view.findViewById(R.id.edit_schedule_time);

        init();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((EditScheduleActivity)getActivity()).setBackButtonEnabled(false);
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
            // save MySchedule
            sharedPreference = new SharedPreference();
            ArrayList<MySchedule> schedules = sharedPreference.getMyScheduleList(getActivity());
            MySchedule schedule = schedules.get(schedulePos);
            schedule.setScheduleName(scheduleName.getText().toString());
            schedule.setScheduleTime(scheduleTime.getText().toString());
            sharedPreference.saveMyScheduleList(getActivity(), schedules);

            if (saveDates != null)
            {
                // save MyScheduleContent
                ArrayList<MyScheduleContent> scheduleContents = sharedPreference.getMyScheduleContentList(getActivity());
                MyScheduleContent scheduleContent = scheduleContents.get(schedulePos);
                ArrayList<Date> scheduleDates = scheduleContent.getScheduleDates();
                ArrayList<ArrayList<MyPlace>> schedulePlaces = scheduleContent.getSchedulePlaces();
                ArrayList<ArrayList<ArrayList<Integer>>> schedulePlaceTimes = scheduleContent.getSchedulePlaceTimes();
                ArrayList<ArrayList<Integer>> schedulePlaceTransportWays = scheduleContent.getSchedulePlaceTransportWays();

                int scheduleDatesCount = scheduleDates.size();
                int saveDatesCount = saveDates.size();

                if (scheduleDatesCount < saveDatesCount)
                {
                    int newDateCount = saveDatesCount - scheduleDatesCount;

                    for (int i = 0; i < newDateCount; i++)
                    {
                        schedulePlaces.add(new ArrayList<>());
                        schedulePlaceTimes.add(new ArrayList<>());
                        schedulePlaceTransportWays.add(new ArrayList<>());
                    }
                }
                else if (scheduleDatesCount > saveDatesCount)
                {
                    int removeDateCount = scheduleDatesCount - saveDatesCount;

                    for (int i = scheduleDatesCount-1; i > scheduleDatesCount-1-removeDateCount; i--)
                    {
                        schedulePlaces.remove(i);
                        schedulePlaceTimes.remove(i);
                        schedulePlaceTransportWays.remove(i);
                    }
                }

                scheduleContent.setScheduleDates(saveDates);
                sharedPreference.saveMyScheduleContentList(getActivity(), scheduleContents);
            }

            //getActivity().getSupportFragmentManager().popBackStack();

            Bundle bundle = new Bundle();
            bundle.putBoolean("EDIT_MODE", true);
            bundle.putInt("schedulePos", schedulePos);
            bundle.putInt("datePos", EditHomeFragment.datePos);
            EditHomeFragment editHomeFragment = new EditHomeFragment();
            editHomeFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.edit_fragment_container, editHomeFragment).commit();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void DialogConfirm(List<Date> dates)
    {
        saveDates = new ArrayList<>(dates);
        String dateStart = transformDate(dates.get(0));
        String dateEnd = transformDate(dates.get(dates.size()-1));

        scheduleTime.setText(dateStart + " - " + dateEnd);
    }

    private void init()
    {
        ((EditScheduleActivity)getActivity()).setBackButtonEnabled(true);
        setHasOptionsMenu(true);
        args = getArguments();
        title = getActivity().findViewById(R.id.toolbar_title);
        title.setText("修改行程");

        schedulePos = args.getInt("schedulePos");
        scheduleName.setText(args.getString("scheduleName"));
        scheduleTime.setText(args.getString("scheduleTime"));

        scheduleTime.setOnClickListener(v -> {
            // set up calendar dialog
            CalendarDialogFragment calendarDialogFragment = new CalendarDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString("dateString", args.getString("scheduleTime"));
            calendarDialogFragment.setArguments(bundle);
            calendarDialogFragment.show(getChildFragmentManager(), "calendar");
        });
    }

    // transform date into real world date
    private String transformDate(Date date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");

        return simpleDateFormat.format(date);
    }
}
