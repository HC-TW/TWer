package com.hc.twer;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    private TextView toolbar_title;
    private String lastTitle;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;

    private String placeName;
    private String placeId;

    private InfoFragment infoFragment;
    private MapFragment mapFragment;
    private ReviewFragment reviewFragment;

    private Bundle bundle;
    private Bundle infoArgs;
    private Bundle mapArgs;
    private Bundle reviewArgs;

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabs);

        init();
        setupViewPager();
        tabHelper();

        return view;
    }

    @Override
    public void onDestroyView() {
        try {
            ((MainActivity)getActivity()).setSbackButtonEnabled(false);
        }catch (ClassCastException e)
        {

        }
        toolbar_title.setText(lastTitle);
        super.onDestroyView();
    }

    private void init()
    {
        try {
            ((MainActivity)getActivity()).setSbackButtonEnabled(true);
        }catch (ClassCastException e)
        {
            ((EditScheduleActivity)getActivity()).setBackButtonEnabled(true);
        }

        bundle = getArguments();
        placeName = bundle.getString("Name");
        placeId = bundle.getString("Id");

        toolbar_title = getActivity().findViewById(R.id.toolbar_title);
        lastTitle = toolbar_title.getText().toString(); // distinguish which is last page
        toolbar_title.setText(placeName);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        tabLayout.setupWithViewPager(viewPager);
    }

    // set up ViewPager
    private void setupViewPager()
    {
        infoFragment = new InfoFragment();
        mapFragment = new MapFragment();
        reviewFragment = new ReviewFragment();

        infoArgs = new Bundle();
        mapArgs = new Bundle();
        reviewArgs = new Bundle();

        // Set up InfoFragment
        infoArgs.putString("Name", placeName);
        infoArgs.putString("Address", bundle.getString("Address", "查無資料"));
        infoArgs.putString("Phone", bundle.getString("Phone", "查無資料"));
        infoArgs.putString("Website", bundle.getString("Website", "查無資料"));
        infoArgs.putDouble("Rating", bundle.getDouble("Rating", 0));
        infoArgs.putInt("Price", bundle.getInt("Price", -1));
        infoArgs.putStringArrayList("OpeningHours", bundle.getStringArrayList("OpeningHours"));
        infoArgs.putIntegerArrayList("ClosedTime", bundle.getIntegerArrayList("ClosedTime"));
        infoArgs.putParcelableArrayList("Types", bundle.getParcelableArrayList("Types"));
        infoArgs.putString("Id", placeId);
        infoArgs.putDouble("Lat", bundle.getDouble("Lat", 0));
        infoArgs.putDouble("Lng", bundle.getDouble("Lng", 0));
        infoArgs.putInt("schedulePos", bundle.getInt("schedulePos", -1));
        infoArgs.putInt("datePos", bundle.getInt("datePos", -1));
        infoFragment.setArguments(infoArgs);
        // Set up MapFragment
        mapArgs.putString("Name", placeName);
        mapArgs.putDouble("Lat", bundle.getDouble("Lat", 0));
        mapArgs.putDouble("Lng", bundle.getDouble("Lng", 0));
        mapFragment.setArguments(mapArgs);
        // Set up ReviewFragment
        reviewArgs.putString("Id", placeId);
        reviewFragment.setArguments(reviewArgs);
        // Set up ViewPager
        viewPagerAdapter.addFragment(infoFragment, "詳細資訊");
        viewPagerAdapter.addFragment(mapFragment, "地圖");
        viewPagerAdapter.addFragment(reviewFragment, "評論");
        viewPager.setAdapter(viewPagerAdapter);
    }

    // set up tabLayout
    private void tabHelper()
    {
        LinearLayout tabInfoLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.detail_tab_layout, null);
        TextView tabInfo = tabInfoLayout.findViewById(R.id.customTab);
        tabInfo.setText(" 詳細資訊");
        tabInfo.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_info, 0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabInfo);

        LinearLayout tabMapLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.detail_tab_layout, null);
        TextView tabMap = tabMapLayout.findViewById(R.id.customTab);
        tabMap.setText(" 地圖");
        tabMap.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_map, 0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabMap);

        LinearLayout tabReviewLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.detail_tab_layout, null);
        TextView tabReview = tabReviewLayout.findViewById(R.id.customTab);
        tabReview.setText(" 評論");
        tabReview.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_review, 0, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabReview);
    }

}
