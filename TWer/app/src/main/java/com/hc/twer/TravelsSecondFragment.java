package com.hc.twer;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class TravelsSecondFragment extends Fragment {

    private TextView toolbar_title;
    public static RecyclerView recyclerView;
    public static TravelsListAdapter travelsListAdapter;

    private Bundle args;
    private String title;

    public TravelsSecondFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travels_second, container, false);
        recyclerView = view.findViewById(R.id.travels_list);

        init();
        displayTravelsList();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Map<String, String> parameter = new HashMap<>();

        parameter.put("q", title + "遊記2019");
        parameter.put("cx", "015898240288933304819:q_zvn3wrfxc");
        parameter.put("key", getString(R.string.google_maps_key));

        MyGoogleSearchResultsClient myGoogleSearchResultsClient = new MyGoogleSearchResultsClient(getActivity(), parameter);
        myGoogleSearchResultsClient.execute();
    }

    @Override
    public void onDestroyView() {
        ((MainActivity)getActivity()).setTbackButtonEnabled(false);
        toolbar_title.setText("遊記");
        super.onDestroyView();
    }

    private void init()
    {
        ((MainActivity)getActivity()).setTbackButtonEnabled(true);
    }

    private void displayTravelsList()
    {
        args = getArguments();
        title = args.getString("keyword");
        toolbar_title = getActivity().findViewById(R.id.toolbar_title);
        toolbar_title.setText(title);
    }
}
