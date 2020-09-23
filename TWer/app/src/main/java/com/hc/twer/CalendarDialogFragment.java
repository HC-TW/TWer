package com.hc.twer;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.squareup.timessquare.CalendarPickerView;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarDialogFragment extends DialogFragment {

    private CalendarPickerView calendarPickerView;
    private Button button;
    private List<Date> saveDates = new ArrayList<>();
    OnCalendarDialogListener calendarDialogListener;


    public CalendarDialogFragment() {
        // Required empty public constructor
    }

    public interface OnCalendarDialogListener
    {
        void DialogConfirm(List<Date> dates);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarPickerView = view.findViewById(R.id.calendar_view);
        button = view.findViewById(R.id.confirm_date);

        init();
        setCalendarPickerView();
        setConfirmButton();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = (Activity) context;
        try
        {
            calendarDialogListener = (OnCalendarDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onCalendarDialogListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        getDialog().setCanceledOnTouchOutside(true);
    }

    private void init()
    {
        if (getArguments() != null)
        {
            saveDates.addAll(transformText(getArguments().getString("dateString")));
        }
    }

    // set up CalendarPickerView
    private void setCalendarPickerView() {
        // set up next year
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        // set up today
        Date today = new Date();

        // choose date on the calendar
        if ( saveDates.size() > 0 )
        {
            if (saveDates.get(0).before(today))
            {
                today = saveDates.get(0);
            }
            calendarPickerView.init(today, nextYear.getTime())
                    .inMode(CalendarPickerView.SelectionMode.RANGE)
                    .withSelectedDates(saveDates);
        }
        else
        {
            calendarPickerView.init(today, nextYear.getTime())
                    .withSelectedDate(today)
                    .inMode(CalendarPickerView.SelectionMode.RANGE);
        }
        calendarPickerView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {

            }
            @Override
            public void onDateUnselected(Date date) {

            }
        });
    }

    // confirm button event
    private void setConfirmButton() {
        button.setOnClickListener(v -> {
            List<Date> dates = calendarPickerView.getSelectedDates();
            saveDates.clear();
            calendarDialogListener.DialogConfirm(dates);
            saveDates.add(dates.get(0));
            saveDates.add(dates.get(dates.size()-1));
            dismiss();
        });
    }

    private List<Date> transformText(String dateString)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        List<Date> dates = new ArrayList<>();
        ParsePosition parsePosition = new ParsePosition(0);
        dates.add(simpleDateFormat.parse(dateString, parsePosition));
        parsePosition.setIndex(parsePosition.getIndex()+3);
        dates.add(simpleDateFormat.parse(dateString, parsePosition));

        return dates;
    }


}
