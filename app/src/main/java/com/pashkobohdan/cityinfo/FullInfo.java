package com.pashkobohdan.cityinfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pashkobohdan.cityinfo.data.fullInfoJson.CityInfo;
import com.pashkobohdan.cityinfo.data.fullInfoJson.Response;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FullInfo extends AppCompatActivity {

    Response response;

    private class TryDownloadFullInfoAboutCity extends AsyncTask<Void, Void, Boolean> {

        private static final String USER_API_NAME = "pashkobohdan@gmail.com";
        private static final String DATA_URL_PART_1 = " http://api.geonames.org/wikipediaSearchJSON?q=";
        private static final String DATA_URL_PART_2 = "&title=";
        private static final String DATA_URL_PART_3 = "&maxRows=10&username=" + USER_API_NAME;

        String resultJson = "";

        private ProgressDialog progressDialog;

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(FullInfo.this);
            progressDialog.setMessage("Downloading full data about " + cityName);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                URL url = new URL(DATA_URL_PART_1 + cityName + "%20" + countryName + DATA_URL_PART_2 + cityName + DATA_URL_PART_3);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

                if (resultJson.length() < 1) {
                    return false;
                } else {

                    response = new Gson().fromJson(resultJson, Response.class);

                }

            } catch (Exception e) {
                e.printStackTrace();

                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean strJson) {
            super.onPostExecute(strJson);

            progressDialog.dismiss();

            if (strJson) {
                if (response != null && response.getGeonames().size() > 0) {
//                    for (CityInfo c : response.getGeonames()) {
//                        if (c.getFeature().equals("city")) {
//                            refreshData(c);
//                            break;
//                        }
//                    }
                    refreshData(response.getGeonames().get(0));
                } else {
                    Toast.makeText(FullInfo.this, "Wrong response", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(FullInfo.this, "Reading error", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    private String cityName;
    private String countryName;

    private TextView cityNameTextView, citySummary, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_info);

        cityNameTextView = (TextView)findViewById(R.id.city_name);
        citySummary = (TextView)findViewById(R.id.city_summary);
        url = (TextView)findViewById(R.id.url);

        Intent intent = getIntent();
        cityName = intent.getStringExtra("city");
        countryName = intent.getStringExtra("country");

        if (!Application.isOnline(this)) {
            Toast.makeText(this, "Please, turn on the Internet connection and try later", Toast.LENGTH_SHORT).show();
            finish();
        }

        new TryDownloadFullInfoAboutCity().execute();

    }

    private void refreshData(CityInfo cityInfo) {
        cityNameTextView.setText(cityInfo.getTitle());
        citySummary.setText(cityInfo.getSummary());
        url.setText(cityInfo.getWikipediaUrl());
    }
}
