package com.hc.twer;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap map;
    private MapView mapView;
    private MyInfoWindowAdapter myInfoWindowAdapter;

    private SharedPreference sharedPreference;
    private RecyclerView recyclerView;
    private EditMapHeaderAdapter editMapHeaderAdapter;
    private ArrayList<ArrayList<MyPlace>> places;
    private ArrayList<Marker> markers;
    private Bundle args;
    private int schedulePos;
    private int datePos;

    private static final int LOCATION_REQUEST = 500;

    public EditMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_map, container, false);

        mapView = view.findViewById(R.id.edit_map);
        recyclerView = view.findViewById(R.id.edit_map_header);

        init();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((EditScheduleActivity)getActivity()).setBackButtonEnabled(false);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);

        double northEastLat = 0;
        double northEastLng = 0;
        double southWestLat = 0;
        double southWestLng = 0;
        for (int i = 0; i < places.size(); i++)
        {
            ArrayList<MyPlace> datePlaces = places.get(i);
            //float color = 30*(i % 12);
            int color = Color.HSVToColor(new float[]{30*(i % 12), 1f, 0.85f});
            for (int j = 0; j < datePlaces.size(); j++)
            {
                MyPlace place = datePlaces.get(j);
                LatLng latLng = place.getLatLng();
                Marker marker = map.addMarker(new MarkerOptions().position(latLng).title(place.getName()).icon(BitmapDescriptorFactory.fromBitmap(createBitmapFromLayoutWithText(j+1, color))));
                //Marker marker = map.addMarker(new MarkerOptions().position(latLng).title(place.getName()).icon(BitmapDescriptorFactory.defaultMarker(color)));
                marker.setTag(new Pair<>(i, j));
                markers.add(marker);
                // update camera bound
                if (latLng.latitude > northEastLat || northEastLat == 0)
                {
                    northEastLat = latLng.latitude;
                }
                if (latLng.longitude > northEastLng || northEastLng == 0)
                {
                    northEastLng = latLng.longitude;
                }
                if (latLng.latitude < southWestLat || southWestLat == 0)
                {
                    southWestLat = latLng.latitude;
                }
                if (latLng.longitude < southWestLng || southWestLng == 0)
                {
                    southWestLng = latLng.longitude;
                }
            }
        }
        setCameraWithCoordinationBounds(new LatLng(southWestLat, southWestLng), new LatLng(northEastLat, northEastLng));

        // Set up MyLocation
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        map.setInfoWindowAdapter(myInfoWindowAdapter);
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        map.setOnInfoWindowClickListener(this);

        setUpHeader();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Pair<Integer, Integer> pair = (Pair<Integer, Integer>) marker.getTag();
        MyPlace place = places.get(pair.first).get(pair.second);

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
        bundle.putParcelableArrayList("Types", place.getTypes());
        bundle.putInt("schedulePos", schedulePos);
        bundle.putInt("datePos", datePos);

        detailFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.edit_fragment_container, detailFragment).addToBackStack("favorite").commit();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    map.setMyLocationEnabled(true);
                    map.setOnMyLocationButtonClickListener(this);
                    map.setOnMyLocationClickListener(this);
                }
                break;
        }
    }

    private void init()
    {
        ((EditScheduleActivity)getActivity()).setBackButtonEnabled(true);
        sharedPreference = new SharedPreference();
        markers = new ArrayList<>();
        args = getArguments();

        schedulePos = args.getInt("schedulePos");
        datePos = args.getInt("datePos");
        places = sharedPreference.getMyScheduleContentList(getActivity()).get(schedulePos).getSchedulePlaces();

        // set up mapView
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
        myInfoWindowAdapter = new MyInfoWindowAdapter(getActivity());
    }

    private void setUpHeader()
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);

        if (places == null)
        {
            places = new ArrayList<>();
        }
        editMapHeaderAdapter = new EditMapHeaderAdapter(getActivity(), places, map, markers);
        recyclerView.setAdapter(editMapHeaderAdapter);
    }

    // set camera bounds
    private void setCameraWithCoordinationBounds(LatLng northEast, LatLng southWest)
    {
        LatLngBounds bounds = new LatLngBounds(northEast, southWest);
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));
    }

    private Bitmap createBitmapFromLayoutWithText(int order, int color)
    {
        LayoutInflater mInflater = getLayoutInflater();

        //Inflate the layout into a view and configure it the way you like
        RelativeLayout view = new RelativeLayout(getActivity());
        mInflater.inflate(R.layout.edit_map_marker_layout, view, true);

        ImageView marker_background = view.findViewById(R.id.edit_map_marker_background);
        TextView marker_order = view.findViewById(R.id.edit_map_marker_order);
        VectorChildFinder vectorChildFinder = new VectorChildFinder(getActivity(), R.drawable.ic_location, marker_background);
        VectorDrawableCompat.VFullPath path = vectorChildFinder.findPathByName("location");
        path.setFillColor(color);
        marker_order.setText("" + order);

        //Provide it with a layout params. It should necessarily be wrapping the
        //content as we not really going to have a parent for it.
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));

        //Pre-measure the view so that height and width don't remain null.
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        //Assign a size and position to the view and all of its descendants
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        //Create the bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        //Create a canvas with the specified bitmap to draw into
        Canvas c = new Canvas(bitmap);

        //Render this view (and all of its children) to the given Canvas
        view.draw(c);
        return bitmap;
    }
}
