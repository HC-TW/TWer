package com.hc.twer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<MyPlace> places;
    private PlacesClient placesClient;
    //private GeoDataClient mGeoDataClient;
    private int schedulePos;
    private int datePos;

    public ArrayList<MyPlace> pickedList = new ArrayList<>();
    public ArrayList<Integer> transportWays = new ArrayList<>();

    public FavoriteListAdapter(Context context, ArrayList<MyPlace> places, int schedulePos, int datePos)
    {
        this.context = context;
        this.places = places;
        this.schedulePos = schedulePos;
        this.datePos = datePos;

        if (!Places.isInitialized())
        {
            // Initialize Places.
            Places.initialize(context, context.getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(context);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView place_album;
        private TextView place_name;
        private CheckBox checkBox;

        private MyViewHolder(View itemView) {
            super(itemView);

            place_album = itemView.findViewById(R.id.place_album);
            place_name = itemView.findViewById(R.id.place_name);
            checkBox = itemView.findViewById(R.id.collect_checkbox);
        }
    }

    @NonNull
    @Override
    public FavoriteListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.place_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        // In Pick Mode displays CheckBox
        if (AddPlaceCollectFragment.PICK_MODE || CollectFragment.PICK_MODE)
        {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(false);
        }
        else
        {
            holder.checkBox.setVisibility(View.GONE);
        }

        holder.checkBox.setOnClickListener(v -> {
            if (holder.checkBox.isChecked())
            {
                pickedList.add(places.get(position));
                transportWays.add(0);
            }
            else
            {
                pickedList.remove(places.get(position));
                transportWays.remove(0);
            }
        });

        // get Place
        MyPlace place = places.get(position);
        // get & set Name
        holder.place_name.setText(place.getName());
        // get & set Photo
        getAndSetPhoto(place.getId(), holder.place_album);
        // Replace CollectFragment into InfoFragment
        holder.itemView.setOnClickListener(v -> {
            if (!AddPlaceCollectFragment.PICK_MODE && !CollectFragment.PICK_MODE)
            {
                AppCompatActivity appCompatActivity = (AppCompatActivity) context;
                Bundle bundle = new Bundle();
                DetailFragment detailFragment = new DetailFragment();
                // set up place Information
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
                    bundle.putString("Website", place.getWebsiteUri());
                }
                bundle.putInt("Price", place.getPriceLevel());
                bundle.putDouble("Rating", place.getRating());
                bundle.putString("Id", place.getId());
                bundle.putDouble("Lat", place.getLatLng().latitude);
                bundle.putDouble("Lng", place.getLatLng().longitude);
                bundle.putStringArrayList("OpeningHours", place.getOpeningHours());
                bundle.putIntegerArrayList("ClosedTime", place.getClosedTime());

                // check if transition is from schedule
                try {
                    bundle.putInt("schedulePos", schedulePos);
                    bundle.putInt("datePos", datePos);
                } catch (NullPointerException e)
                {

                }

                detailFragment.setArguments(bundle);
                if (appCompatActivity.findViewById(R.id.fragment_container) != null)
                {
                    appCompatActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.fragment_container, detailFragment).addToBackStack("favorite").commit();
                }
                else
                {
                    appCompatActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.edit_fragment_container, detailFragment).addToBackStack("favorite").commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    private void getAndSetPhoto(String placeId, ImageView place_album)
    {
        // Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
        List<Place.Field> fields = Arrays.asList(Place.Field.PHOTO_METADATAS);

        // Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(placeId, fields).build();

        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            // Get the photo metadata.
            if (place.getPhotoMetadatas() == null)
            {
                return;
            }
            PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);

            // Get the attribution text.
            String attributions = photoMetadata.getAttributions();

            // Create a FetchPhotoRequest.
            FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                Glide.with(context).load(bitmap).into(place_album);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                    Log.e("Place", "Place not found: " + exception.getMessage());
                }
            });
        });
    }
}
