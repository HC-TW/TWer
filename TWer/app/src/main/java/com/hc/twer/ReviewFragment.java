package com.hc.twer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends Fragment {

    public static RecyclerView recyclerView;
    public static ReviewListAdapter reviewListAdapter;
    private Bundle bundle;

    public ReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        recyclerView = view.findViewById(R.id.review_list);

        displayReviewList();

        return view;
    }

    private void displayReviewList()
    {
        bundle = getArguments();
        Map<String, String> parameter = new HashMap<>();

        parameter.put("placeid", bundle.getString("Id"));
        parameter.put("language", "zh-TW");
        parameter.put("fields", "review");
        parameter.put("key", getString(R.string.google_maps_key));

        MyGooglePlaceReviewClient myGooglePlaceReviewClient = new MyGooglePlaceReviewClient(getActivity(), parameter);
        myGooglePlaceReviewClient.execute();
    }

}
