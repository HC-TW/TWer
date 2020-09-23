package com.hc.twer;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;

public class SharedPreference {

    private BackupManager backupManager;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    public SharedPreference()
    {
        super();
    }

    public void saveFavoritePlaces(Context context, ArrayList<MyPlace> places)
    {
        settings = context.getSharedPreferences("places", Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonPlaces = gson.toJson(places);
        editor.putString("places", jsonPlaces);
        editor.apply();
        //backupManager.dataChanged();
    }

    public void addFavoritePlace(Context context, MyPlace place)
    {
        ArrayList<MyPlace> places = getFavoritePlaces(context);

        if ( places == null )
        {
            places = new ArrayList<>();
        }
        places.add(place);
        saveFavoritePlaces(context, places);
    }

    public void removeFavoritePlace(Context context, MyPlace place)
    {
        ArrayList<MyPlace> places = getFavoritePlaces(context);
        if ( places != null )
        {
            for ( MyPlace mPlace : places )
            {
                if (mPlace.getId().equals(place.getId()))
                {
                    places.remove(mPlace);
                    break;
                }
            }
            saveFavoritePlaces(context, places);
        }
    }

    public ArrayList<MyPlace> getFavoritePlaces(Context context)
    {
        ArrayList<MyPlace> places;

        settings = context.getSharedPreferences("places", Context.MODE_PRIVATE);

        if ( settings.contains("places") )
        {
            String jsonPlaces = settings.getString("places", null);

            Gson gson = new Gson();
            places = gson.fromJson(jsonPlaces, new TypeToken<ArrayList<MyPlace>>(){}.getType());
        }
        else
        {
            return null;
        }
        return places;
    }

    /*
    public void saveScheduleDates(Context context, ArrayList<ArrayList<Date>> scheduleDates)
    {
        settings = context.getSharedPreferences("scheduleDates", Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonScheduleDates = gson.toJson(scheduleDates);
        editor.putString("schedule_dates", jsonScheduleDates);
        editor.apply();
        //backupManager.dataChanged();
    }

    public void addScheduleDates(Context context, ArrayList<Date> scheduleDates)
    {
        ArrayList<ArrayList<Date>> datesList = getScheduleDates(context);

        if ( datesList == null )
        {
            datesList = new ArrayList<>();
        }
        datesList.add(scheduleDates);
        saveScheduleDates(context, datesList);
    }

    public void removeScheduleDates(Context context, int position)
    {
        ArrayList<ArrayList<Date>> datesList = getScheduleDates(context);
        if ( datesList != null )
        {
            datesList.remove(position);
            saveScheduleDates(context, datesList);
        }
    }

    public ArrayList<ArrayList<Date>> getScheduleDates(Context context)
    {
        ArrayList<ArrayList<Date>> datesList;

        settings = context.getSharedPreferences("scheduleDates", Context.MODE_PRIVATE);

        if ( settings.contains("schedule_dates") )
        {
            String jsonScheduleDates = settings.getString("schedule_dates", null);

            Gson gson = new Gson();
            datesList = gson.fromJson(jsonScheduleDates, new TypeToken<ArrayList<ArrayList<Date>>>(){}.getType());
        }
        else
        {
            return null;
        }
        return datesList;
    }

    public void saveSchedulePlaces(Context context, ArrayList<ArrayList<ArrayList<MyPlace>>> schedulePlaces)
    {
        settings = context.getSharedPreferences("schedulePlaces", Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonSchedulePlaces = gson.toJson(schedulePlaces);
        editor.putString("schedule_places", jsonSchedulePlaces);
        editor.apply();
        //backupManager.dataChanged();
    }

    public void addSchedulePlaceDates(Context context, ArrayList<ArrayList<MyPlace>> schedulePlaceDates)
    {
        ArrayList<ArrayList<ArrayList<MyPlace>>> placesList = getSchedulePlaces(context);

        if ( placesList == null )
        {
            placesList = new ArrayList<>();
        }
        placesList.add(schedulePlaceDates);
        saveSchedulePlaces(context, placesList);
    }

    public void removeSchedulePlaceDates(Context context, int position)
    {
        ArrayList<ArrayList<ArrayList<MyPlace>>> placesList = getSchedulePlaces(context);
        if ( placesList != null )
        {
            placesList.remove(position);
            saveSchedulePlaces(context, placesList);
        }
    }

    public void addSchedulePlaces(Context context, int schedulePos, int datePos, ArrayList<MyPlace> schedulePlaces)
    {
        ArrayList<ArrayList<ArrayList<MyPlace>>> placesList = getSchedulePlaces(context);

        if ( placesList == null )
        {
            placesList = new ArrayList<>();
        }
        placesList.get(schedulePos).get(datePos).addAll(schedulePlaces);
        saveSchedulePlaces(context, placesList);
    }

    public void removeSchedulePlaces(Context context, int schedulePos, int position)
    {
        ArrayList<ArrayList<ArrayList<MyPlace>>> placesList = getSchedulePlaces(context);
        if ( placesList != null )
        {
            placesList.get(schedulePos).remove(position);
            saveSchedulePlaces(context, placesList);
        }
    }

    public void addSchedulePlace(Context context, int schedulePos, int datePos, MyPlace place)
    {
        ArrayList<ArrayList<ArrayList<MyPlace>>> placesList = getSchedulePlaces(context);

        if ( placesList == null )
        {
            placesList = new ArrayList<>();
        }
        placesList.get(schedulePos).get(datePos).add(place);
        saveSchedulePlaces(context, placesList);
    }

    public void removeSchedulePlace(Context context, int schedulePos, int datePos, int position)
    {
        ArrayList<ArrayList<ArrayList<MyPlace>>> placesList = getSchedulePlaces(context);
        if ( placesList != null )
        {
            placesList.get(schedulePos).get(datePos).remove(position);
            saveSchedulePlaces(context, placesList);
        }
    }

    public ArrayList<ArrayList<ArrayList<MyPlace>>> getSchedulePlaces(Context context)
    {
        ArrayList<ArrayList<ArrayList<MyPlace>>> placesList;

        settings = context.getSharedPreferences("schedulePlaces", Context.MODE_PRIVATE);

        if ( settings.contains("schedule_places") )
        {
            String jsonSchedulePlaces = settings.getString("schedule_places", null);

            Gson gson = new Gson();
            placesList = gson.fromJson(jsonSchedulePlaces, new TypeToken<ArrayList<ArrayList<ArrayList<MyPlace>>>>(){}.getType());
        }
        else
        {
            return null;
        }
        return placesList;
    }

    public void saveSchedulePlaceTimes(Context context, ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> schedulePlaceTimes)
    {
        settings = context.getSharedPreferences("schedulePlaceTimes", Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonSchedulePlaces = gson.toJson(schedulePlaceTimes);
        editor.putString("schedule_place_times", jsonSchedulePlaces);
        editor.apply();
        //backupManager.dataChanged();
    }

    public void addSchedulePlaceTimes(Context context, ArrayList<ArrayList<ArrayList<Integer>>> schedulePlaceTimes)
    {
        ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> placeTimes = getSchedulePlaceTimes(context);

        if ( placeTimes == null )
        {
            placeTimes = new ArrayList<>();
        }
        placeTimes.add(schedulePlaceTimes);
        saveSchedulePlaceTimes(context, placeTimes);
    }

    public void removeSchedulePlaceTimes(Context context, int position)
    {
        ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> placeTimes = getSchedulePlaceTimes(context);
        if ( placeTimes != null )
        {
            placeTimes.remove(position);
            saveSchedulePlaceTimes(context, placeTimes);
        }
    }

    public void addPlaceDateTimes(Context context, int schedulePos, ArrayList<ArrayList<Integer>> placeDateTimes)
    {
        ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> placeTimes = getSchedulePlaceTimes(context);

        if ( placeTimes == null )
        {
            placeTimes = new ArrayList<>();
        }
        placeTimes.get(schedulePos).add(placeDateTimes);
        saveSchedulePlaceTimes(context, placeTimes);
    }

    public void removePlaceDateTimes(Context context, int schedulePos, int position)
    {
        ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> placeTimes = getSchedulePlaceTimes(context);
        if ( placeTimes != null )
        {
            placeTimes.get(schedulePos).remove(position);
            saveSchedulePlaceTimes(context, placeTimes);
        }
    }

    public void addSchedulePlaceDateTimes(Context context, int schedulePos, int datePos, ArrayList<Integer> schedulePlaceDateTimes)
    {
        ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> placeTimes = getSchedulePlaceTimes(context);

        if ( placeTimes == null )
        {
            placeTimes = new ArrayList<>();
        }
        placeTimes.get(schedulePos).get(datePos).add(schedulePlaceDateTimes);
        saveSchedulePlaceTimes(context, placeTimes);
    }

    public void removeSchedulePlacDateTimes(Context context, int schedulePos, int datePos, int position)
    {
        ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> placeTimes = getSchedulePlaceTimes(context);
        if ( placeTimes != null )
        {
            placeTimes.get(schedulePos).get(datePos).remove(position);
            saveSchedulePlaceTimes(context, placeTimes);
        }
    }

    public ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> getSchedulePlaceTimes(Context context)
    {
        ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> placeTimes;

        settings = context.getSharedPreferences("schedulePlaceTimes", Context.MODE_PRIVATE);

        if ( settings.contains("schedule_place_times") )
        {
            String jsonSchedulePlaces = settings.getString("schedule_place_times", null);

            Gson gson = new Gson();
            placeTimes = gson.fromJson(jsonSchedulePlaces, new TypeToken<ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>>>(){}.getType());
        }
        else
        {
            return null;
        }
        return placeTimes;
    }

    public void saveScheduleTransportWays(Context context, ArrayList<ArrayList<ArrayList<Integer>>> transportWays)
    {
        settings = context.getSharedPreferences("transport_ways", Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonString = gson.toJson(transportWays);
        editor.putString("transport_ways", jsonString);
        editor.apply();
        //backupManager.dataChanged();
    }

    public void addScheduleTransportWayDates(Context context, ArrayList<ArrayList<Integer>> transportWayDates)
    {
        ArrayList<ArrayList<ArrayList<Integer>>> transportWays = getScheduleTransportWays(context);

        if ( transportWays == null )
        {
            transportWays = new ArrayList<>();
        }
        transportWays.add(transportWayDates);
        saveScheduleTransportWays(context, transportWays);
    }

    public void removeScheduleTransportWayDates(Context context, int index)
    {
        ArrayList<ArrayList<ArrayList<Integer>>> transportWays = getScheduleTransportWays(context);
        if ( transportWays != null )
        {
            transportWays.remove(index);
            saveScheduleTransportWays(context, transportWays);
        }
    }

    public void addScheduleTransportWays(Context context, int schedulePos, int datePos, ArrayList<Integer> scheduleTransportWays)
    {
        ArrayList<ArrayList<ArrayList<Integer>>> transportWays = getScheduleTransportWays(context);

        if ( transportWays == null )
        {
            transportWays = new ArrayList<>();
        }
        transportWays.get(schedulePos).get(datePos).addAll(scheduleTransportWays);
        saveScheduleTransportWays(context, transportWays);
    }

    public void removeScheduleTransportWays(Context context, int schedulePos, int position)
    {
        ArrayList<ArrayList<ArrayList<Integer>>> transportWays = getScheduleTransportWays(context);
        if ( transportWays != null )
        {
            transportWays.get(schedulePos).remove(position);
            saveScheduleTransportWays(context, transportWays);
        }
    }

    public void addScheduleTransportWay(Context context, int schedulePos, int datePos, int transportWay)
    {
        ArrayList<ArrayList<ArrayList<Integer>>> transportWays = getScheduleTransportWays(context);

        if ( transportWays == null )
        {
            transportWays = new ArrayList<>();
        }
        transportWays.get(schedulePos).get(datePos).add(transportWay);
        saveScheduleTransportWays(context, transportWays);
    }

    public void removeScheduleTransportWay(Context context, int schedulePos, int datePos, int position)
    {
        ArrayList<ArrayList<ArrayList<Integer>>> transportWays = getScheduleTransportWays(context);
        if ( transportWays != null )
        {
            transportWays.get(schedulePos).get(datePos).remove(position);
            saveScheduleTransportWays(context, transportWays);
        }
    }

    public ArrayList<ArrayList<ArrayList<Integer>>> getScheduleTransportWays(Context context)
    {
        ArrayList<ArrayList<ArrayList<Integer>>> transportWays;
        settings = context.getSharedPreferences("transport_ways", Context.MODE_PRIVATE);

        if ( settings.contains("transport_ways") )
        {
            String jsonString = settings.getString("transport_ways", null);

            Gson gson = new Gson();
            transportWays  = gson.fromJson(jsonString, new TypeToken<ArrayList<ArrayList<ArrayList<Integer>>>>(){}.getType());
        }
        else
        {
            return null;
        }
        return transportWays;
    }
    */

    public void saveMyScheduleList(Context context, ArrayList<MySchedule> schedules)
    {
        settings = context.getSharedPreferences("schedule", Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonString = gson.toJson(schedules);
        editor.putString("schedule", jsonString);
        editor.apply();
        //backupManager.dataChanged();
    }

    public void addMySchedule(Context context, MySchedule schedule)
    {
        ArrayList<MySchedule> schedules = getMyScheduleList(context);

        if ( schedules == null )
        {
            schedules = new ArrayList<>();
        }
        schedules.add(schedule);
        saveMyScheduleList(context, schedules);
    }

    public void removeMySchedule(Context context, int position)
    {
        ArrayList<MySchedule> schedules = getMyScheduleList(context);
        if ( schedules != null )
        {
            schedules.remove(position);
            saveMyScheduleList(context, schedules);
        }
    }

    public ArrayList<MySchedule> getMyScheduleList(Context context)
    {
        ArrayList<MySchedule> schedules;
        settings = context.getSharedPreferences("schedule", Context.MODE_PRIVATE);

        if ( settings.contains("schedule") )
        {
            String jsonString = settings.getString("schedule", null);

            Gson gson = new Gson();
            schedules  = gson.fromJson(jsonString, new TypeToken<ArrayList<MySchedule>>(){}.getType());
        }
        else
        {
            return null;
        }
        return schedules;
    }

    public void saveMyScheduleContentList(Context context, ArrayList<MyScheduleContent> scheduleContents)
    {
        settings = context.getSharedPreferences("schedule_content", Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonString = gson.toJson(scheduleContents);
        editor.putString("schedule_content", jsonString);
        editor.apply();
        //backupManager.dataChanged();
    }

    public void addMyScheduleContent(Context context, MyScheduleContent scheduleContent)
    {
        ArrayList<MyScheduleContent> scheduleContents = getMyScheduleContentList(context);

        if ( scheduleContents == null )
        {
            scheduleContents = new ArrayList<>();
        }
        scheduleContents.add(scheduleContent);
        saveMyScheduleContentList(context, scheduleContents);
    }

    public void removeMyScheduleContent(Context context, int position)
    {
        ArrayList<MyScheduleContent> scheduleContents = getMyScheduleContentList(context);
        if ( scheduleContents != null )
        {
            scheduleContents.remove(position);
            saveMyScheduleContentList(context, scheduleContents);
        }
    }

    public ArrayList<MyScheduleContent> getMyScheduleContentList(Context context)
    {
        ArrayList<MyScheduleContent> scheduleContents;
        settings = context.getSharedPreferences("schedule_content", Context.MODE_PRIVATE);

        if ( settings.contains("schedule_content") )
        {
            String jsonString = settings.getString("schedule_content", null);

            Gson gson = new Gson();
            scheduleContents = gson.fromJson(jsonString, new TypeToken<ArrayList<MyScheduleContent>>(){}.getType());
        }
        else
        {
            return null;
        }
        return scheduleContents;
    }

    public void removeSchedule(Context context, int position)
    {
        removeMySchedule(context, position);
        removeMyScheduleContent(context, position);
    }
}

