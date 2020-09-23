package com.hc.twer;

import java.util.ArrayList;
import java.util.Date;

public class MyScheduleContent {

    private ArrayList<Date> scheduleDates;
    private ArrayList<ArrayList<MyPlace>> schedulePlaces;
    private ArrayList<ArrayList<ArrayList<Integer>>> schedulePlaceTimes;
    private ArrayList<ArrayList<Integer>> schedulePlaceTransportWays;

    public MyScheduleContent(ArrayList<Date> scheduleDates, ArrayList<ArrayList<MyPlace>> schedulePlaces, ArrayList<ArrayList<ArrayList<Integer>>> schedulePlaceTimes, ArrayList<ArrayList<Integer>> schedulePlaceTransportWays)
    {
        this.scheduleDates = scheduleDates;
        this.schedulePlaces = schedulePlaces;
        this.schedulePlaceTimes = schedulePlaceTimes;
        this.schedulePlaceTransportWays = schedulePlaceTransportWays;
    }

    public void setScheduleDates(ArrayList<Date> scheduleDates) {
        this.scheduleDates = scheduleDates;
    }

    public void setSchedulePlaces(ArrayList<ArrayList<MyPlace>> schedulePlaces) {
        this.schedulePlaces = schedulePlaces;
    }

    public void setSchedulePlaceTimes(ArrayList<ArrayList<ArrayList<Integer>>> schedulePlaceTimes) {
        this.schedulePlaceTimes = schedulePlaceTimes;
    }

    public void setSchedulePlaceTransportWays(ArrayList<ArrayList<Integer>> schedulePlaceTransportWays) {
        this.schedulePlaceTransportWays = schedulePlaceTransportWays;
    }

    public ArrayList<Date> getScheduleDates() {
        return scheduleDates;
    }

    public ArrayList<ArrayList<MyPlace>> getSchedulePlaces() {
        return schedulePlaces;
    }

    public ArrayList<ArrayList<ArrayList<Integer>>> getSchedulePlaceTimes() {
        return schedulePlaceTimes;
    }

    public ArrayList<ArrayList<Integer>> getSchedulePlaceTransportWays() {
        return schedulePlaceTransportWays;
    }
}
