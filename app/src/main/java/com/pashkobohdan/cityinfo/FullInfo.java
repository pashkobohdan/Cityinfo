package com.pashkobohdan.cityinfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
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

                response = new Gson().fromJson(resultJson, Response.class);

                if (response.getGeonames().size() < 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FullInfo.this, "No data about this city. Trying to load country info", Toast.LENGTH_SHORT).show();
                        }
                    });

                    url = new URL(DATA_URL_PART_1 + countryName + DATA_URL_PART_3);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FullInfo.this, "load for country: " + countryName, Toast.LENGTH_SHORT).show();
                        }
                    });

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    inputStream = urlConnection.getInputStream();
                    buffer = new StringBuffer();

                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    resultJson = buffer.toString();

                    if (resultJson.length() < 1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FullInfo.this, "No data about this country :(", Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FullInfo.this, "Showed data about country", Toast.LENGTH_SHORT).show();
                            }
                        });
                        response = new Gson().fromJson(resultJson, Response.class);

                    }


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
                if (response == null) {
                    Toast.makeText(FullInfo.this, "Wrong response", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (response.getGeonames().size() == 0) {
                    Toast.makeText(FullInfo.this, "No data about this city", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    refreshData(response.getGeonames().get(0));
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
    private ImageView picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_info);

        cityNameTextView = (TextView) findViewById(R.id.city_name);
        citySummary = (TextView) findViewById(R.id.city_summary);
        url = (TextView) findViewById(R.id.url);
        picture = (ImageView) findViewById(R.id.picture);

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

        new DownloadImageTask(picture).execute(cityInfo.getThumbnailImg());
//        URL url = null;
//        try {
//            url = new URL(cityInfo.getThumbnailImg());
//            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            picture.setImageBitmap(bmp);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            URL url;
            try {
                url = new URL(urldisplay);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap bmp) {
            //bmp.getScaledHeight(picture.getWidth() / bmp.getWidth());
            try {
                bmImage.setImageBitmap(Bitmap.createScaledBitmap(bmp,
                        picture.getWidth(),
                        bmp.getHeight() * (picture.getWidth() / bmp.getWidth()),
                        false));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
