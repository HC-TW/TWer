package com.hc.twer;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.Locale;

public class MyPlace {

    private String Id;
    private String Name;
    private String Address;
    private LatLng latLng;
    private String WebsiteUri;
    private String PhoneNumber;
    private double Rating;
    private int PriceLevel;
    private ArrayList<String> OpeningHours;
    private ArrayList<Integer> ClosedTime;
    private ArrayList<Place.Type> Types;

    public MyPlace(String Id, String Name, String Address, LatLng latLng, String WebsiteUri, String PhoneNumber, double Rating, int PriceLevel, ArrayList<String> OpeningHours, ArrayList<Integer> ClosedTime, ArrayList<Place.Type> Types)
    {
        this.Id = Id;
        this.Name = Name;
        this.Address = Address;
        this.latLng = latLng;
        this.WebsiteUri = WebsiteUri;
        this.PhoneNumber = PhoneNumber;
        this.Rating = Rating;
        this.PriceLevel = PriceLevel;
        this.OpeningHours = OpeningHours;
        this.ClosedTime = ClosedTime;
        this.Types = Types;
    }

    public String getId() {
        return Id;
    }

    public String getAddress() {
        return Address;
    }

    public Locale getLocale() {
        return null;
    }

    public String getName() {
        return Name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public LatLngBounds getViewport() {
        return null;
    }

    public String getWebsiteUri() {
        return WebsiteUri;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public double getRating() {
        return Rating;
    }

    public int getPriceLevel() {
        return PriceLevel;
    }

    public ArrayList<String> getOpeningHours() {
        return OpeningHours;
    }

    public void setOpeningHours(ArrayList<String> openingHours) {
        OpeningHours = openingHours;
    }

    public ArrayList<Integer> getClosedTime() {
        return ClosedTime;
    }

    public void setClosedTime(ArrayList<Integer> closedTime) {
        ClosedTime = closedTime;
    }

    public ArrayList<Place.Type> getTypes() {
        return Types;
    }

    public void setTypes(ArrayList<Place.Type> types) {
        Types = types;
    }
}
