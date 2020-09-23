package com.hc.twer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InternalCoverActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView title;
    private RecyclerView recyclerView;
    private CoverListAdapter coverListAdapter;
    private List<String> covers = new ArrayList<>();

    private int schedulePos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_cover);

        init();
        setUpCoverList();
    }

    // set menu item event
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.noslide, R.anim.slide_out_bottom);
    }

    private void init()
    {
        recyclerView = findViewById(R.id.cover_list);
        toolbar = findViewById(R.id.toolBar);
        title = findViewById(R.id.toolbar_title);
        // set up ActionBar
        toolbar.setNavigationContentDescription("取消");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        title.setText("內建封面");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        schedulePos = getIntent().getIntExtra("schedulePos", 0);
    }

    private void setUpCoverList()
    {
        for (int i = 1; i <= 30; i++)
        {
            covers.add("tc"+i);
        }
        String[] county_names = getResources().getStringArray(R.array.county_names);
        Log.d("county", county_names.length+"");
        for (int i = 0; i < county_names.length; i++)
        {
            Log.d("county", county_names[i]);
        }
        covers.addAll(Arrays.asList(getResources().getStringArray(R.array.county_names)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        coverListAdapter = new CoverListAdapter(this, covers, schedulePos);
        recyclerView.setAdapter(coverListAdapter);
    }
}
