package com.hc.twer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class AddScheduleActivity extends AppCompatActivity implements CalendarDialogFragment.OnCalendarDialogListener{

    private SharedPreference sharedPreference;
    private Toolbar toolbar;
    private EditText scheduleName;
    private Button scheduleTime;
    private CalendarDialogFragment calendarDialogFragment = new CalendarDialogFragment();
    private ArrayList<Date> dates = new ArrayList<>();
    private ArrayList<ArrayList<MyPlace>> datePlaces;
    private ArrayList<ArrayList<Integer>> placeTransportWays;
    private ArrayList<ArrayList<ArrayList<Integer>>> placeTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        init();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.noslide, R.anim.slide_out_bottom);
    }

    // set up menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_schedule_menu, menu);

        return true;
    }

    // set menu item event
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home:
                setResult(RESULT_CANCELED, getIntent());
                finish();
                return true;

            case R.id.action_addschedule:
                if (scheduleName.getText().length() == 0 || scheduleTime.getText().length() == 0)
                {
                    Toast.makeText(this, "行程名稱或旅遊日期不得為空白", Toast.LENGTH_LONG).show();
                    break;
                }
                saveScheduleInfo();
                setResult(RESULT_OK, getIntent());
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void DialogConfirm(List<Date> dates) {
        this.dates = (ArrayList<Date>) dates;
        String dateStart = transformDate(dates.get(0));
        String dateEnd = transformDate(dates.get(dates.size()-1));

        scheduleTime.setText(dateStart + " - " + dateEnd);
    }

    // Initialize
    private void init() {
        toolbar = findViewById(R.id.toolBar);
        scheduleName = findViewById(R.id.input_schedule_name);
        scheduleTime = findViewById(R.id.input_schedule_time);

        // set up ActionBar
        toolbar.setNavigationContentDescription("取消");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText("建立行程");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // set up calendar dialog
        scheduleTime.setOnClickListener(v -> calendarDialogFragment.show(getSupportFragmentManager(), "calendar"));
    }

    // transform date into real world date
    private String transformDate(Date date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String dateString = simpleDateFormat.format(date);

        return dateString;
    }

    private void saveScheduleInfo()
    {
        datePlaces = new ArrayList<>();
        placeTimes = new ArrayList<>();
        placeTransportWays = new ArrayList<>();
        for (int i = 0; i < dates.size(); i++)
        {
            datePlaces.add(new ArrayList<>());
            placeTimes.add(new ArrayList<>());
            placeTransportWays.add(new ArrayList<>());
        }
        MySchedule mySchedule = new MySchedule(getRandomCover(), scheduleName.getText().toString(), scheduleTime.getText().toString());
        MyScheduleContent myScheduleContent = new MyScheduleContent(dates, datePlaces, placeTimes, placeTransportWays);

        sharedPreference = new SharedPreference();
        sharedPreference.addMySchedule(this, mySchedule);
        sharedPreference.addMyScheduleContent(this, myScheduleContent);

        /*sharedPreference.addScheduleDates(this, dates);
        sharedPreference.addSchedulePlaceDates(this, datePlaces);
        sharedPreference.addSchedulePlaceTimes(this, placeTimes);
        sharedPreference.addScheduleTransportWayDates(this, placeTransportWays);*/
    }

    private String getRandomCover()
    {
        // generate random schedule cover
        Random random = new Random();
        int tcNumber = random.nextInt(30) + 1;
        String image = "tc" + (tcNumber);
        int imageId = getResources().getIdentifier(image, "drawable", getPackageName());

        return Integer.toString(imageId);
    }
}
