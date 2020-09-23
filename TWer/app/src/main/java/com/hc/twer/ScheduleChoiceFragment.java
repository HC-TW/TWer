package com.hc.twer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleChoiceFragment extends Fragment {

    private TextView title;
    private RecyclerView scheduleChoiceList;
    private ScheduleChoiceAdapter scheduleChoiceAdapter;
    private SharedPreference sharedPreference;
    private ArrayList<MySchedule> schedules;
    private Bundle args;

    public ScheduleChoiceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule_choice, container, false);

        scheduleChoiceList = view.findViewById(R.id.schedule_choice_list);

        init();
        setScheduleChoiceList();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity)getActivity()).setSbackButtonEnabled(false);
    }

    private void init()
    {
        ((MainActivity)getActivity()).setSbackButtonEnabled(true);
        args = getArguments();
        title = getActivity().findViewById(R.id.toolbar_title);
        title.setText("加入行程");
    }

    private void setScheduleChoiceList()
    {
        scheduleChoiceList.setLayoutManager(new LinearLayoutManager(getActivity()));
        scheduleChoiceList.setHasFixedSize(true);

        sharedPreference = new SharedPreference();
        schedules = sharedPreference.getMyScheduleList(getActivity());
        if (schedules == null)
        {
            schedules = new ArrayList<>();
        }
        scheduleChoiceAdapter = new ScheduleChoiceAdapter(getActivity(), schedules, args.getString("placeId"));
        scheduleChoiceList.setAdapter(scheduleChoiceAdapter);
    }
}
