package com.hc.twer;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditContentFragment extends Fragment implements View.OnClickListener {

    private SharedPreference sharedPreference;
    private TextView dateTitle;
    private RecyclerView recyclerView;
    private ScheduleContentAdapter scheduleContentAdapter;
    private DragHelper dragHelper;
    private ItemTouchHelper itemTouchHelper;
    private ArrayList<MyScheduleContent> scheduleContents;
    private Button addPlace;
    private Bundle args;
    private int schedulePos;
    private int datePos;

    public EditContentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_content, container, false);

        dateTitle = view.findViewById(R.id.edit_content_header_date);
        recyclerView = view.findViewById(R.id.content_place_list);
        addPlace = view.findViewById(R.id.add_place);

        init();
        setScheduleContent();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.add_place:
                AddPlaceChoiceFragment addPlaceChoiceFragment = new AddPlaceChoiceFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("schedulePos", schedulePos);
                bundle.putInt("datePos", datePos);
                addPlaceChoiceFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.edit_fragment_container, addPlaceChoiceFragment).commit();
                break;
        }
    }

    private void init()
    {
        sharedPreference = new SharedPreference();
        addPlace.setOnClickListener(this);
        args = getArguments();
        schedulePos = args.getInt("schedulePos");
        datePos = args.getInt("datePos");
    }

    private void setScheduleContentHeader()
    {
        Date date = sharedPreference.getMyScheduleContentList(getActivity()).get(schedulePos).getScheduleDates().get(datePos);
        int color = Color.HSVToColor(new float[]{30*((datePos)%12), 1f, 0.85f});
        // get hsv color value and replace title background
        GradientDrawable textViewBackground = (GradientDrawable) dateTitle.getBackground().mutate();
        textViewBackground.setColor(color);
        textViewBackground.invalidateSelf();

        dateTitle.setText(new SimpleDateFormat("EEEE", Locale.TAIWAN).format(date.getTime()));
    }

    private void setScheduleContent()
    {
        setScheduleContentHeader();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        scheduleContents = sharedPreference.getMyScheduleContentList(getActivity());

        if (scheduleContents == null)
        {
            scheduleContents = new ArrayList<>();
        }
        scheduleContentAdapter = new ScheduleContentAdapter(getActivity(), scheduleContents, schedulePos, datePos);

        dragHelper = new DragHelper(scheduleContentAdapter);
        itemTouchHelper = new ItemTouchHelper(dragHelper);
        scheduleContentAdapter.setItemTouchHelper(itemTouchHelper);
        recyclerView.setAdapter(scheduleContentAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
