package com.hc.twer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private SharedPreference sharedPreference;
    private TextView title;
    private MenuItem menu_add;
    private RecyclerView recyclerView;
    private MyScheduleAdapter myScheduleAdapter;
    private ArrayList<MySchedule> schedules;

    private DragHelper dragHelper;
    private ItemTouchHelper itemTouchHelper;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.mySchedule);

        init();
        setupMySchedule();

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu_add = menu.findItem(R.id.action_add);
        menu_add.setVisible(true);
    }

    private void init()
    {
        setHasOptionsMenu(true);
        title = getActivity().findViewById(R.id.toolbar_title);
        title.setText("我的行程");
    }

    private void setupMySchedule()
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        sharedPreference = new SharedPreference();
        schedules = sharedPreference.getMyScheduleList(getActivity());
        if ( schedules == null )
        {
            schedules = new ArrayList<>();
        }
        myScheduleAdapter = new MyScheduleAdapter(getActivity(), schedules);

        dragHelper = new DragHelper(myScheduleAdapter);
        itemTouchHelper = new ItemTouchHelper(dragHelper);
        myScheduleAdapter.setItemTouchHelper(itemTouchHelper);
        recyclerView.setAdapter(myScheduleAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(myScheduleAdapter);
    }
}
