package com.hc.twer;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

public class MyGoogleDistanceMatrixClient extends AsyncTask<Void, Void, Void> {

    private Map<String, String> parameter;
    private String duration;
    private  OnDistanceMatrixListener distanceMatrixListener;

    public interface OnDistanceMatrixListener
    {
        void processFinish(String duration);
    }

    public MyGoogleDistanceMatrixClient(OnDistanceMatrixListener distanceMatrixListener, Map<String, String> parameter) {
        this.distanceMatrixListener = distanceMatrixListener;
        this.parameter = parameter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        HttpURLConnection con;
        try {
                String fetchData = "";
                String query = ParameterStringBuilder.getParamsString(parameter);
                URL url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?" + query);
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
                duration = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0)
                        .getJSONObject("duration").getString("text");
                Log.d("Duration", duration);

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
        distanceMatrixListener.processFinish(duration);
    }

}
