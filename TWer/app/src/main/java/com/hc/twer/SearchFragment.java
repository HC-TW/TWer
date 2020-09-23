package com.hc.twer;


import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.LocalTime;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.Places;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements PlaceAutoCompleteAdapter.PlaceAutoCompleteInterface, View.OnClickListener{

    private TextView title;
    private AlphaAnimation alphaAnimation = new AlphaAnimation(1F, 0.5F);
    private RecyclerView recyclerView;
    private PlaceAutoCompleteAdapter placeAutoCompleteAdapter;
    private PlacesClient placesClient;

    //List<SavedAddress> mSavedAddressList;
    //PlaceSavedAdapter mSavedAdapter;

    private static final LatLngBounds BOUNDS_TAIWAN = new LatLngBounds(
            new LatLng(22, 120), new LatLng(25, 122));

    private EditText search_text;
    private ImageView search_clear;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.search_result_list);
        search_text = view.findViewById(R.id.search_text);
        search_clear = view.findViewById(R.id.search_clear);

        init();
        setSearchBox();
        setSearchResult();
        getTextFromTravels();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            ((EditScheduleActivity)getActivity()).setBackButtonEnabled(false);
        } catch (ClassCastException e)
        {

        }
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(alphaAnimation);
        switch (v.getId())
        {
            case R.id.search_clear:
                search_text.setText("");
                break;
        }
    }

    @Override
    public void onPlaceClick(ArrayList<PlaceAutoCompleteAdapter.PlaceAutocomplete> mResultList, int position) {
        if(mResultList != null)
        {
            String placeId = String.valueOf(mResultList.get(position).placeId);
            getPlace(placeId);
        }
    }

    private void init()
    {
        try {
            ((EditScheduleActivity)getActivity()).setBackButtonEnabled(true);
        } catch (ClassCastException e)
        {

        }
        title = getActivity().findViewById(R.id.toolbar_title);
        title.setText("搜尋景點");

        if (!Places.isInitialized())
        {
            // Initialize Places.
            Places.initialize(getActivity(), getString(R.string.google_maps_key));
        }
        // Create a new Places client instance.
        placesClient = Places.createClient(getActivity());
    }

    // get text if there is a text from travels
    private void getTextFromTravels()
    {
        Bundle bundle = getArguments();
        if ( bundle != null )
        {
            search_text.setText(bundle.getString("keyword"));
            placeAutoCompleteAdapter.getAutocomplete(search_text.getText().toString());
        }
    }
    // hide keyboard
    private void hideKeyboard(View view)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    // set search box
    private void setSearchBox()
    {
        // detect if focus changed
        search_text.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
            {
                if (search_text.getText().length() > 0)
                {
                    placeAutoCompleteAdapter.getAutocomplete(search_text.getText().toString());
                }
            }
            else
            {
                hideKeyboard(v);
            }
        });
        // detect if text changed
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    search_clear.setVisibility(View.VISIBLE);
                    /*if (placeAutoCompleteAdapter != null) {
                        recyclerView.setAdapter(placeAutoCompleteAdapter);
                    }*/
                } else {// history
                    /*search_clear.setVisibility(View.GONE);
                    if (mSavedAdapter != null && mSavedAddressList.size() > 0) {
                        recyclerView.setAdapter(mSavedAdapter);
                    }*/
                }
                if (!s.toString().equals("")) {
                    placeAutoCompleteAdapter.getAutocomplete(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // clear text button
        search_clear.setOnClickListener(this);
    }
    // set search result
    private void setSearchResult()
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        placeAutoCompleteAdapter = new PlaceAutoCompleteAdapter(getActivity(), placesClient, BOUNDS_TAIWAN, this);
        recyclerView.setAdapter(placeAutoCompleteAdapter);

        recyclerView.setOnTouchListener((v, event) -> {
            hideKeyboard(v);
            return false;
        });
    }

    private void getPlace(String placeId)
    {
        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG,
                                                     Place.Field.WEBSITE_URI, Place.Field.PHONE_NUMBER, Place.Field.RATING, Place.Field.PRICE_LEVEL,
                                                     Place.Field.OPENING_HOURS, Place.Field.TYPES);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            Log.i("Place", "Place found: " + place.getName());

            Bundle bundle = new Bundle();
            DetailFragment detailFragment = new DetailFragment();
            bundle.putString("Name", place.getName());
            if ( place.getAddress() != null )
            {
                bundle.putString("Address", place.getAddress());
            }
            if ( place.getPhoneNumber() != null )
            {
                if ( place.getPhoneNumber().length() > 0 )
                {
                    bundle.putString("Phone", place.getPhoneNumber());
                }
            }
            if ( place.getWebsiteUri() != null )
            {
                bundle.putString("Website", place.getWebsiteUri().toString());
            }
            if (place.getPriceLevel() != null)
            {
                bundle.putInt("Price", place.getPriceLevel());
            }
            if (place.getRating() != null)
            {
                bundle.putDouble("Rating", place.getRating());
            }
            if (place.getLatLng() != null)
            {
                bundle.putDouble("Lat", place.getLatLng().latitude);
                bundle.putDouble("Lng", place.getLatLng().longitude);
            }
            if (place.getOpeningHours() != null)
            {
                OpeningHours openingHours = place.getOpeningHours();
                bundle.putStringArrayList("OpeningHours", new ArrayList<>(openingHours.getWeekdayText()));
                ArrayList<Integer> closedTime = new ArrayList<>();
                List<Period> periods = openingHours.getPeriods();
                for (int i = 0; i < periods.size(); i++)
                {
                    if (periods.get(i).getClose() != null)
                    {
                        LocalTime localTime = periods.get(i).getClose().getTime();
                        closedTime.add(localTime.getHours());
                        closedTime.add(localTime.getMinutes());
                    }
                }
                bundle.putIntegerArrayList("ClosedTime", closedTime);
            }
            if (place.getTypes() != null)
            {
                Log.d("Types", place.getTypes().get(0).name());
                bundle.putParcelableArrayList("Types", new ArrayList<>(place.getTypes()));
            }
            bundle.putString("Id", place.getId());

            // check if transition is from schedule
            try {
                Bundle args = getArguments();
                bundle.putInt("schedulePos", args.getInt("schedulePos"));
                bundle.putInt("datePos", args.getInt("datePos"));
            } catch (NullPointerException e)
            {

            }

            detailFragment.setArguments(bundle);
            if (getActivity().findViewById(R.id.fragment_container) != null)
            {
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.fragment_container, detailFragment).addToBackStack("Search").commit();
            }
            else
            {
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.edit_fragment_container, detailFragment).addToBackStack("Search").commit();
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e("Place", "Place not found: " + exception.getMessage());
            }
        });
    }


}
