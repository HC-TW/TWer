package com.hc.twer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;

public class PlaceAutoCompleteAdapter extends RecyclerView.Adapter<PlaceAutoCompleteAdapter.SearchResultViewHolder> {


    private ArrayList<PlaceAutocomplete> mResultList;
    private Context mContext;
    private PlaceAutoCompleteInterface mListener;

    private PlacesClient placesClient;
    private LatLngBounds mBounds;

    public interface PlaceAutoCompleteInterface
    {
        void onPlaceClick(ArrayList<PlaceAutocomplete> mResultList, int position);
    }

    /*public interface SavedPlaceListener
    {
        public void onSavedPlaceClick(ArrayList<SavedAddress> mResultList, int position);

    }*/
    public PlaceAutoCompleteAdapter(Context context, PlacesClient placesClient, LatLngBounds mBounds, PlaceAutoCompleteInterface mListener) {
        this.mContext = context;
        this.placesClient = placesClient;
        this.mBounds = mBounds;
        this.mListener = mListener;
    }

    public static class SearchResultViewHolder extends RecyclerView.ViewHolder
    {
        private TextView searchResultName;
        private TextView searchResultAddress;

        private SearchResultViewHolder(View itemView) {
            super(itemView);
            searchResultName = itemView.findViewById(R.id.search_result_name);
            searchResultAddress = itemView.findViewById(R.id.search_result_address);
        }
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.search_result_layout, parent, false);

        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, final int position) {

        holder.searchResultName.setText(mResultList.get(position).name);
        holder.searchResultAddress.setText(mResultList.get(position).description);
        holder.itemView.setOnClickListener(v -> mListener.onPlaceClick(mResultList, position));
    }

    @Override
    public int getItemCount() {
        if(mResultList != null)
            return mResultList.size();
        else
            return 0;
    }

    public void getAutocomplete(String query) {
        ArrayList<PlaceAutocomplete> resultList = new ArrayList<>();
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
        // Call either setLocationBias() OR setLocationRestriction().
                //.setLocationBias(bounds)
                //.setLocationRestriction(mBounds)
                .setCountry("TW")
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                Log.i("AutocompletePrediction", prediction.getPlaceId());
                Log.i("AutocompletePrediction", prediction.getPrimaryText(null).toString());

                // Get the details of this prediction and copy it into a new PlaceAutocomplete object.
                resultList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(null),
                        prediction.getSecondaryText(null)));
            }
            mResultList = resultList;
            notifyDataSetChanged();
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("Place", "Place not found: " + apiException.getStatusCode());
            }
        });
    }
    // Place description
    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence name;
        public CharSequence description;

        PlaceAutocomplete(CharSequence placeId, CharSequence name, CharSequence description) {
            this.placeId = placeId;
            this.name = name;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }

    /*public class SavedAddress
    {
        String Latitude, Longitude;

        public String getLatitude() { return Latitude; }
        public void setLatitude(String latitude) { Latitude = latitude; }
        public String getLongitude() { return Longitude; }
        public void setLongitude(String longitude) { Longitude = longitude; }
    }*/

    // Set the bounds for all subsequent queries.
    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
    }
}
