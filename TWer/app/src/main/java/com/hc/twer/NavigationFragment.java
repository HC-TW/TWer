package com.hc.twer;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.config.GoogleDirectionConfiguration;
import com.akexorcist.googledirection.constant.Language;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener{

    private GoogleMap map;
    private MapView mapView;
    private TextView originText;
    private TextView destinationText;
    private Button navigationButton;

    private Bundle args;
    private double originLat;
    private double originLng;
    private String originName;
    private double destinationLat;
    private double destinationLng;
    private String destinationName;
    private int travelMode;
    private String transportMode;

    public NavigationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        mapView = view.findViewById(R.id.navigation_map);
        navigationButton = view.findViewById(R.id.action_navigation);
        originText = view.findViewById(R.id.origin_name);
        destinationText = view.findViewById(R.id.destination_name);

        init();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.action_navigation:
                //Uri gmmIntentUri = Uri.parse("geo:" + destinationLat + "," + destinationLng + "?q=" + destinationName);
                Uri gmmIntentUri;

                if (travelMode == 1)
                {
                    // walk
                    gmmIntentUri = Uri.parse("google.navigation:q=" + destinationName + "&mode=w");
                }
                else if (travelMode == 2)
                {
                    // public transportation
                    gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=" + destinationLat + "," + destinationLng + "&dirflg=r");
                }
                else
                {
                    // drive
                    gmmIntentUri = Uri.parse("google.navigation:q=" + destinationName);
                }
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        getRoute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((EditScheduleActivity)getActivity()).setBackButtonEnabled(false);
    }

    private void init()
    {
        ((EditScheduleActivity)getActivity()).setBackButtonEnabled(true);
        args = getArguments();
        originLat = args.getDouble("originLat");
        originLng = args.getDouble("originLng");
        originName = args.getString("originName");
        destinationLat = args.getDouble("destinationLat");
        destinationLng = args.getDouble("destinationLng");
        destinationName = args.getString("destinationName");
        travelMode = args.getInt("travelMode");
        setTravelMode(travelMode);

        originText.setText(originName);
        destinationText.setText(destinationName);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
        navigationButton.setOnClickListener(this);

    }

    private void getRoute()
    {
        LatLng origin = new LatLng(originLat, originLng);
        LatLng destination = new LatLng(destinationLat, destinationLng);

        GoogleDirection.withServerKey(getString(R.string.google_maps_key))
                .from(origin)
                .to(destination)
                .language(Language.CHINESE_TRADITIONAL)
                .transportMode(transportMode)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK())
                        {
                            map.clear();
                            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                            Route route = direction.getRouteList().get(0);
                            map.addMarker(new MarkerOptions().position(origin).title(originName).icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_origin)).anchor(0.5f, 0.5f));
                            map.addMarker(new MarkerOptions().position(destination).title(destinationName).icon(BitmapDescriptorFactory.defaultMarker(207.3f)));

                            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                            map.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 10, Color.BLUE));
                            setCameraWithCoordinationBounds(route);
                        }
                        else
                        {
                            Toast.makeText(getActivity(), direction.getStatus(), Toast.LENGTH_SHORT).show();
                            Log.d("Test", rawBody);
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // set camera bounds
    private void setCameraWithCoordinationBounds(Route route)
    {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        if (ContextCompat.getDrawable(context, vectorResId) != null)
        {
            Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
            vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        }
        return null;
    }

    private void setTravelMode(int travelMode)
    {
        switch (travelMode)
        {
            case 0:
                transportMode = TransportMode.DRIVING;
                break;
            case 1:
                transportMode = TransportMode.WALKING;
                break;
            case 2:
                transportMode = TransportMode.TRANSIT;
                break;
        }
    }
}
