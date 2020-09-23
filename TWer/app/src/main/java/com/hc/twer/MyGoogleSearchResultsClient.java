package com.hc.twer;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.style.DoubleBounce;

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

public class MyGoogleSearchResultsClient extends AsyncTask<Void, Void, Void>{

    private ProgressBar progressBar;
    public static String BACKEND = "www.googleapis.com";
    private Map<String, String> parameter;
    private Context context;
    public JSONArray items = new JSONArray();

    public MyGoogleSearchResultsClient(Context context, Map<String, String> parameter) {
        this.parameter = parameter;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        AppCompatActivity appCompatActivity = (AppCompatActivity) context;
        progressBar = appCompatActivity.findViewById(R.id.travels_progress);
        progressBar.setIndeterminateDrawable(new DoubleBounce());
    }

    @Override
    protected Void doInBackground(Void... voids) {
        HttpURLConnection con;
        try {
            for (int i = 0; i < 2; i++ )
            {
                String fetchData = "";
                String query = ParameterStringBuilder.getParamsString(parameter);
                String startParameter = "&start=" + (i*10+1);
                URL url = new URL("https://" + BACKEND + "/customsearch/v1?" + query + startParameter);
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
                // get page
                JSONArray page = jsonObject.getJSONArray("items");
                for (int j = 0; j < page.length(); j++ )
                {
                    items.put(page.get(j));
                }
            }
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
        TravelsSecondFragment.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        TravelsSecondFragment.recyclerView.setHasFixedSize(true);
        TravelsSecondFragment.travelsListAdapter = new TravelsListAdapter(context, items);
        TravelsSecondFragment.recyclerView.setAdapter(TravelsSecondFragment.travelsListAdapter);
        progressBar.setVisibility(View.GONE);
    }
}


