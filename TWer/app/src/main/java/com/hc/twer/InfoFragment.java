package com.hc.twer;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment implements View.OnClickListener{

    private AlphaAnimation alphaAnimation = new AlphaAnimation(1F, 0.8F);
    private Bundle args;
    private MyPlace myPlace;
    private String placeId;
    private String placeName;
    private String placeAddress;
    private LatLng placeLatLng;
    private String placeWebsiteUri;
    private String placePhoneNumber;
    private float placeRating;
    private int placePriceLevel;
    private ArrayList<String> placeOpeningHours;
    private ArrayList<Integer> placeClosedTime;
    private ArrayList<Place.Type> placeTypes;

    private ImageView favorite;
    private ImageView scheduleAdd;
    private ImageView searchImage;
    private ProgressBar progressBar;
    private TextView noPhoto;
    private TextView searchName;
    private TextView searchAddress;
    private TextView searchPhoneNumber;
    private TextView searchWebsite;
    private TextView searchOpeningHours;
    private RatingBar ratingBar;
    private RatingBar priceRatingBar;
    private TextView noPrice;

    private PlacesClient placesClient;
    private SharedPreference sharedPreference;

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        favorite = view.findViewById(R.id.favorite);
        scheduleAdd = view.findViewById(R.id.schedule_add);
        searchImage = view.findViewById(R.id.search_image);
        searchName = view.findViewById(R.id.search_result_name);
        searchAddress = view.findViewById(R.id.search_result_address);
        searchPhoneNumber = view.findViewById(R.id.search_result_phone);
        searchWebsite = view.findViewById(R.id.search_result_website);
        searchOpeningHours = view.findViewById(R.id.search_result_opening_hours);
        ratingBar = view.findViewById(R.id.ratingBar);
        priceRatingBar = view.findViewById(R.id.priceRatingBar);
        noPrice = view.findViewById(R.id.no_price);
        noPhoto = view.findViewById(R.id.no_photo);
        progressBar = view.findViewById(R.id.search_image_progress);

        // init
        init();

        // Result
        setSearchResult();

        // Favorite
        setFavorite();

        // Add Schedule
        setScheduleAdd();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.favorite:
                if ( (Integer) favorite.getTag() == R.drawable.ic_favorite_border )
                {
                    favorite.setImageResource(R.drawable.ic_favorite);
                    favorite.setTag(R.drawable.ic_favorite);
                    sharedPreference.addFavoritePlace(getActivity(), myPlace);
                    Toast.makeText(getActivity(), "已加入收藏景點", Toast.LENGTH_SHORT).show();
                }
                else if ( (Integer) favorite.getTag() == R.drawable.ic_favorite )
                {
                    favorite.setImageResource(R.drawable.ic_favorite_border);
                    favorite.setTag(R.drawable.ic_favorite_border);
                    sharedPreference.removeFavoritePlace(getActivity(), myPlace);
                    Toast.makeText(getActivity(), "已從收藏景點移除", Toast.LENGTH_LONG).show();
                }
                Log.d("PlaceId", myPlace.getId());
                break;

            case R.id.schedule_add:
                v.startAnimation(alphaAnimation);
                int schedulePos = args.getInt("schedulePos");
                int datePos = args.getInt("datePos");
                if (schedulePos != -1 && datePos != -1)
                {
                    ArrayList<MyScheduleContent> scheduleContents = sharedPreference.getMyScheduleContentList(getActivity());
                    MyScheduleContent scheduleContent = scheduleContents.get(schedulePos);
                    scheduleContent.getSchedulePlaces().get(datePos).add(myPlace);
                    scheduleContent.getSchedulePlaceTransportWays().get(datePos).add(0);

                    ArrayList<ArrayList<Integer>> datePlaceTimes = scheduleContent.getSchedulePlaceTimes().get(datePos);
                    ArrayList<Integer> placeTimes = new ArrayList<>();
                    int initHour = 8;
                    int initMinute = 0;
                    int placeSize = datePlaceTimes.size();
                    if (placeSize > 0)
                    {
                        initHour = datePlaceTimes.get(placeSize-1).get(2);
                        initMinute = datePlaceTimes.get(placeSize-1).get(3);
                    }
                    placeTimes.add(initHour);
                    placeTimes.add(initMinute);
                    placeTimes.add(initHour+1);
                    placeTimes.add(initMinute);

                    datePlaceTimes.add(placeTimes);
                    sharedPreference.saveMyScheduleContentList(getActivity(), scheduleContents);
                    Bundle bundle = new Bundle();
                    bundle.putInt("schedulePos", schedulePos);
                    bundle.putInt("datePos", datePos);
                    EditHomeFragment editHomeFragment = new EditHomeFragment();
                    editHomeFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.edit_fragment_container, editHomeFragment).commit();
                }
                else
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("placeId", placeId);
                    ScheduleChoiceFragment scheduleChoiceFragment = new ScheduleChoiceFragment();
                    scheduleChoiceFragment.setArguments(bundle);
                    if (getActivity().findViewById(R.id.fragment_container) != null)
                    {
                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.fragment_container, scheduleChoiceFragment).addToBackStack("Info").commit();
                    }
                    else
                    {
                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.edit_fragment_container, scheduleChoiceFragment).addToBackStack("Info").commit();
                    }
                }
                break;
        }
    }

    private void init()
    {
        if (!Places.isInitialized())
        {
            // Initialize Places.
            Places.initialize(getActivity(), getString(R.string.google_maps_key));
        }
        // Create a new Places client instance.
        placesClient = Places.createClient(getActivity());
        sharedPreference = new SharedPreference();
        args = getArguments();

        placeId = args.getString("Id");
        placeName = args.getString("Name");
        placeAddress = args.getString("Address");
        placePhoneNumber = args.getString("Phone");
        placeWebsiteUri = args.getString("Website");
        placeRating = (float) args.getDouble("Rating");
        placePriceLevel = args.getInt("Price");
        placeLatLng = new LatLng(args.getDouble("Lat"), args.getDouble("Lng"));
        placeOpeningHours = args.getStringArrayList("OpeningHours");
        placeClosedTime = args.getIntegerArrayList("ClosedTime");

        placeTypes = args.getParcelableArrayList("Types");

        // create MyPlace
        myPlace = new MyPlace(placeId, placeName, placeAddress, placeLatLng, placeWebsiteUri, placePhoneNumber, placeRating, placePriceLevel, placeOpeningHours, placeClosedTime, placeTypes);
    }
    private void setSearchResult() {

        progressBar.setIndeterminateDrawable(new DoubleBounce());

        // Info
        searchName.setText(placeName);
        searchAddress.setText(placeAddress);
        searchAddress.setAutoLinkMask(Linkify.MAP_ADDRESSES);
        searchAddress.setMovementMethod(LinkMovementMethod.getInstance());
        searchPhoneNumber.setText(placePhoneNumber);
        searchPhoneNumber.setAutoLinkMask(Linkify.PHONE_NUMBERS);
        searchPhoneNumber.setMovementMethod(LinkMovementMethod.getInstance());
        searchWebsite.setText(placeWebsiteUri);
        searchWebsite.setAutoLinkMask(Linkify.WEB_URLS);
        searchWebsite.setMovementMethod(LinkMovementMethod.getInstance());
        searchOpeningHours.setText(getOpeningHoursString());

        // Rating
        ratingBar.setRating(placeRating);

        // Price Rating
        setPriceRatingBar();

        // Photo
        getPhotos();
    }
    // get photos by using google places api
    private void getPhotos() {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        // Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
        List<Place.Field> fields = Arrays.asList(Place.Field.PHOTO_METADATAS);

        // Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(placeId, fields).build();

        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            List<PhotoMetadata> photoMetadataList = place.getPhotoMetadatas();
            if (photoMetadataList != null)
            {
                for (PhotoMetadata photoMetadata : photoMetadataList)
                {
                    Log.d("photo", "photo found");
                    // Create a FetchPhotoRequest.
                    FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                            .setMaxWidth(400) // Optional.
                            .setMaxHeight(400) // Optional.
                            .build();
                    placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                        Bitmap bitmap = fetchPhotoResponse.getBitmap();
                        bitmaps.add(bitmap);

                        if (bitmaps.size() == photoMetadataList.size())
                        {
                            searchImage.invalidate();
                            searchImage.setImageBitmap(bitmaps.get(0));
                            //Glide.with(this).load(bitmaps.get(0)).into(searchImage);
                            searchImage.setOnClickListener(v -> {
                                MediaApplication.getInstance().setPhotoList(bitmaps);
                                Intent intent = new Intent(getActivity(), GalleryActivity.class);
                                startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.noslide);
                            });
                            progressBar.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(exception -> {
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            int statusCode = apiException.getStatusCode();
                            // Handle error with given status code.
                            Log.e("Place", "Place not found: " + exception.getMessage());
                        }
                    });
                }
            }
            else
            {
                noPhoto.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // convert Bitmap to File
    private File bitmaptoFile(Context context, Bitmap bitmap) throws IOException {
        //create a file to write bitmap data

        try
        {
            File f = new File(context.getCacheDir(), "bitmap");
            f.createNewFile();

            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            return f;
        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    // Favorite
    private void setFavorite() {
        if ( FavoriteChecked(myPlace) )
        {
            favorite.setImageResource(R.drawable.ic_favorite);
            favorite.setTag(R.drawable.ic_favorite);
        }
        else
        {
            favorite.setImageResource(R.drawable.ic_favorite_border);
            favorite.setTag(R.drawable.ic_favorite_border);
        }
        favorite.setOnClickListener(this);
    }
    // Add Schedule
    private void setScheduleAdd()
    {
        scheduleAdd.setOnClickListener(this);
    }
    // Favorite Check
    private boolean FavoriteChecked(MyPlace place)
    {
        boolean checked = false;
        ArrayList<MyPlace> favoritePlaces = sharedPreference.getFavoritePlaces(getActivity());
        if ( favoritePlaces != null )
        {
            for ( MyPlace mPlace : favoritePlaces )
            {
                if (mPlace.getId().equals(place.getId()))
                {
                    checked = true;
                    break;
                }
            }
        }
        return checked;
    }

    private void setPriceRatingBar()
    {
        if ( placePriceLevel != -1 )
        {
            priceRatingBar.setRating(placePriceLevel + 1);
        }
        else
        {
            priceRatingBar.setVisibility(View.GONE);
            noPrice.setVisibility(View.VISIBLE);
        }
    }

    private String getOpeningHoursString()
    {
        if (placeOpeningHours == null)
        {
            return "查無資料";
        }
        else
        {
            String openingHours = "";

            for (int i = 0; i < placeOpeningHours.size(); i++)
            {
                openingHours += placeOpeningHours.get(i) + "\n";
            }
            return openingHours;
        }
    }
}



