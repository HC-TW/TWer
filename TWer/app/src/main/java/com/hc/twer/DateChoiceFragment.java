package com.hc.twer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.LocalTime;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DateChoiceFragment extends Fragment {

    private TextView title;
    private MenuItem menu_confirm;
    private SharedPreference sharedPreference = new SharedPreference();
    private RecyclerView dateChoiceList;
    private DateChoiceAdapter dateChoiceAdapter;
    private ArrayList<Date> dates;
    private Bundle args;
    private int schedulePos;
    private String placeId;
    private PlacesClient placesClient;


    public DateChoiceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_date_choice, container, false);

        dateChoiceList = view.findViewById(R.id.date_choice_list);

        init();
        setDateChoiceList();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity)getActivity()).setSbackButtonEnabled(false);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu_confirm = menu.findItem(R.id.action_date_confirm);
        menu_confirm.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_date_confirm:
                savePlace();
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init()
    {
        ((MainActivity)getActivity()).setSbackButtonEnabled(true);
        setHasOptionsMenu(true);
        title = getActivity().findViewById(R.id.toolbar_title);
        title.setText("選擇日期");
        args = getArguments();
        schedulePos = args.getInt("schedulePos");
        placeId = args.getString("placeId");

        if (!Places.isInitialized())
        {
            // Initialize Places.
            Places.initialize(getActivity(), getString(R.string.google_maps_key));
        }
        // Create a new Places client instance.
        placesClient = Places.createClient(getActivity());
    }

    private void setDateChoiceList()
    {
        dateChoiceList.setLayoutManager(new LinearLayoutManager(getActivity()));
        dateChoiceList.setHasFixedSize(true);

        dates = sharedPreference.getMyScheduleContentList(getActivity()).get(schedulePos).getScheduleDates();
        if (dates == null)
        {
            dates = new ArrayList<>();
        }
        dateChoiceAdapter = new DateChoiceAdapter(getActivity(), dates);
        dateChoiceList.setAdapter(dateChoiceAdapter);
    }

    private void savePlace()
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

            String address = "";
            String website = "";
            String phone = "";
            double rating = 1;
            int priceLevel = 0;
            ArrayList<String> openingHours = new ArrayList<>();
            ArrayList<Integer> closedTime = new ArrayList<>();
            ArrayList<Place.Type> types = new ArrayList<>();
            if (place.getAddress() != null)
            {
                address = place.getAddress();
            }
            if (place.getWebsiteUri() != null)
            {
                website = place.getWebsiteUri().toString();
            }
            if (place.getPhoneNumber() != null)
            {
                phone = place.getPhoneNumber();
            }
            if (place.getRating() != null)
            {
                rating = place.getRating();
            }
            if (place.getPriceLevel() != null)
            {
                priceLevel = place.getPriceLevel();
            }
            if (place.getOpeningHours() != null)
            {
                OpeningHours placeOpeningHours = place.getOpeningHours();
                openingHours = new ArrayList<>(placeOpeningHours.getWeekdayText());
                List<Period> periods = placeOpeningHours.getPeriods();
                for (int i = 0; i < periods.size(); i++)
                {
                    LocalTime localTime = periods.get(i).getClose().getTime();
                    closedTime.add(localTime.getHours());
                    closedTime.add(localTime.getMinutes());
                }
            }
            if (place.getTypes() != null)
            {
                types = new ArrayList<>(place.getTypes());
            }

            MyPlace myPlace = new MyPlace(placeId, place.getName(), address, place.getLatLng(), website, phone, rating, priceLevel, openingHours, closedTime, types);
            ArrayList<MyScheduleContent> scheduleContents = sharedPreference.getMyScheduleContentList(getActivity());
            MyScheduleContent scheduleContent = scheduleContents.get(schedulePos);
            ArrayList<ArrayList<MyPlace>> schedulePlaces = scheduleContent.getSchedulePlaces();
            ArrayList<ArrayList<Integer>> schedulePlaceTransportWays = scheduleContent.getSchedulePlaceTransportWays();
            ArrayList<ArrayList<ArrayList<Integer>>> schedulePlaceTimes = scheduleContent.getSchedulePlaceTimes();
            Log.d("dateIndex", "" + dateChoiceAdapter.dateIndex);
            for (int i = 0; i < dateChoiceAdapter.dateIndex.size(); i++)
            {
                schedulePlaces.get(dateChoiceAdapter.dateIndex.get(i)).add(myPlace);
                schedulePlaceTransportWays.get(dateChoiceAdapter.dateIndex.get(i)).add(0);
                ArrayList<ArrayList<Integer>> datePlaceTimes = schedulePlaceTimes.get(dateChoiceAdapter.dateIndex.get(i));
                ArrayList<Integer> placeTimes = new ArrayList<>();
                int placeSize = datePlaceTimes.size();

                if (placeSize < 1)
                {
                    placeTimes.add(8);
                    placeTimes.add(0);
                    placeTimes.add(9);
                    placeTimes.add(0);
                }
                else
                {
                    int newHour = datePlaceTimes.get(placeSize-1).get(2);
                    int newMinute = datePlaceTimes.get(placeSize-1).get(3);
                    placeTimes.add(newHour);
                    placeTimes.add(newMinute);
                    placeTimes.add(newHour+1);
                    placeTimes.add(newMinute);
                }
                datePlaceTimes.add(placeTimes);
            }
            sharedPreference.saveMyScheduleContentList(getActivity(), scheduleContents);
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