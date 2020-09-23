package com.hc.twer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
public class AddPlaceCollectFragment extends Fragment {

    private TextView title;
    private RecyclerView recyclerView;
    private FavoriteListAdapter favoriteListAdapter;
    private SharedPreference sharedPreference;
    private MenuItem menu_pick;
    private MenuItem menu_confirm;
    private ArrayList<MyPlace> favoritePlaces;
    private Bundle args;
    private int schedulePos;
    private int datePos;

    public static boolean PICK_MODE = false;

    public AddPlaceCollectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect, container, false);
        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.favorite_list);

        init();
        setFavoriteList();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((EditScheduleActivity)getActivity()).setBackButtonEnabled(false);
        PICK_MODE = false;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu_pick = menu.findItem(R.id.action_pick);
        menu_confirm = menu.findItem(R.id.action_pick_confirm);
        menu_pick.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_pick:
                if (PICK_MODE)
                {
                    menu_confirm.setVisible(false);
                }
                else
                {
                    menu_confirm.setVisible(true);
                }
                PICK_MODE = !PICK_MODE;
                favoriteListAdapter.pickedList.clear();
                favoriteListAdapter.transportWays.clear();
                favoriteListAdapter.notifyDataSetChanged();
                return true;
            case R.id.action_pick_confirm:
                ArrayList<MyScheduleContent> scheduleContents = sharedPreference.getMyScheduleContentList(getActivity());
                MyScheduleContent scheduleContent = scheduleContents.get(schedulePos);
                scheduleContent.getSchedulePlaces().get(datePos).addAll(favoriteListAdapter.pickedList);
                scheduleContent.getSchedulePlaceTransportWays().get(datePos).addAll(favoriteListAdapter.transportWays);
                ArrayList<ArrayList<Integer>> datePlaceTimes = scheduleContent.getSchedulePlaceTimes().get(datePos);
                int initHour = 8;
                int initMinute = 0;
                int placeSize = datePlaceTimes.size();
                if (placeSize > 0)
                {
                    initHour = datePlaceTimes.get(placeSize-1).get(2);
                    initMinute = datePlaceTimes.get(placeSize-1).get(3);
                }
                for (int i = 0; i < favoriteListAdapter.pickedList.size(); i++)
                {
                    ArrayList<Integer> placeTimes = new ArrayList<>();

                    placeTimes.add(initHour+i);
                    placeTimes.add(initMinute);
                    placeTimes.add(initHour+i+1);
                    placeTimes.add(initMinute);

                    datePlaceTimes.add(placeTimes);
                }
                sharedPreference.saveMyScheduleContentList(getActivity(), scheduleContents);

                Bundle bundle = new Bundle();
                bundle.putInt("schedulePos", schedulePos);
                bundle.putInt("datePos", datePos);
                EditHomeFragment editHomeFragment = new EditHomeFragment();
                editHomeFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.edit_fragment_container, editHomeFragment).commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init()
    {
        ((EditScheduleActivity)getActivity()).setBackButtonEnabled(true);
        PICK_MODE = false;
        title = getActivity().findViewById(R.id.toolbar_title);
        title.setText("收藏景點");

        args = getArguments();
        schedulePos = args.getInt("schedulePos");
        datePos = args.getInt("datePos");
    }
    // set up recyclerView
    private void setFavoriteList() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        sharedPreference = new SharedPreference();
        favoritePlaces = sharedPreference.getFavoritePlaces(getActivity());
        if ( favoritePlaces == null )
        {
            favoritePlaces = new ArrayList<>();
        }
        favoriteListAdapter = new FavoriteListAdapter(getActivity(), favoritePlaces, schedulePos, datePos);
        recyclerView.setAdapter(favoriteListAdapter);
    }

}
