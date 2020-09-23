package com.hc.twer;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class MyGooglePlaceReviewClient extends AsyncTask<Void, Void, Void> {

    private Map<String, String> parameter;
    private Context context;
    public JSONArray reviews = new JSONArray();

    public MyGooglePlaceReviewClient(Context context, Map<String, String> parameter) {
        this.parameter = parameter;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        HttpURLConnection con;
        try {
                String fetchData = "";
                String query = ParameterStringBuilder.getParamsString(parameter);
                URL url = new URL("https://maps.googleapis.com/maps/api/place/details/json?" + query);
                Log.d("URL", url.toString());
                con = (HttpURLConnection) url.openConnection();

                InputStream inputStream = con.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while (line != null)
                {
                    line = bufferedReader.readLine();
                    fetchData += line;
                }
                JSONObject jsonObject = new JSONObject(fetchData);
                // get result
                JSONObject result = jsonObject.getJSONObject("result");
                reviews = result.getJSONArray("reviews");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute (Void aVoid){
        super.onPostExecute(aVoid);
        ReviewFragment.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        ReviewFragment.recyclerView.setHasFixedSize(true);
        ReviewFragment.reviewListAdapter = new ReviewListAdapter(context, reviews);
        ReviewFragment.recyclerView.setAdapter(ReviewFragment.reviewListAdapter);
    }
}
