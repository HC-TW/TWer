package com.hc.twer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPlaceChoiceFragment extends Fragment implements View.OnClickListener {

    private TextView title;
    private TextView addPlaceCollect;
    private TextView addPlaceSearch;

    private Bundle args;
    private int schedulePos;
    private int datePos;

    public AddPlaceChoiceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_place_choice, container, false);

        addPlaceCollect = view.findViewById(R.id.add_place_collect);
        addPlaceSearch = view.findViewById(R.id.add_place_search);

        init();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((EditScheduleActivity)getActivity()).setBackButtonEnabled(false);
    }

    private void init()
    {
        ((EditScheduleActivity)getActivity()).setBackButtonEnabled(true);
        title = getActivity().findViewById(R.id.toolbar_title);
        title.setText("新增景點");

        args = getArguments();
        schedulePos = args.getInt("schedulePos");
        datePos = args.getInt("datePos");

        addPlaceCollect.setOnClickListener(this);
        addPlaceSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putInt("schedulePos", schedulePos);
        bundle.putInt("datePos", datePos);
        switch (v.getId())
        {
            case R.id.add_place_collect:
                AddPlaceCollectFragment addPlaceCollectFragment = new AddPlaceCollectFragment();
                addPlaceCollectFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.edit_fragment_container, addPlaceCollectFragment).addToBackStack("AddPlaceChoice").commit();
                break;

            case R.id.add_place_search:
                SearchFragment searchFragment = new SearchFragment();
                searchFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.edit_fragment_container, searchFragment).addToBackStack("EditHome").commit();
                break;
        }
    }
}
