package com.pashkobohdan.cityinfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.pashkobohdan.cityinfo.data.model.CityModel;
import com.pashkobohdan.cityinfo.data.model.CountryModel;
import com.pashkobohdan.cityinfo.data.ormLite.HelperFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AllCities extends AppCompatActivity {


    private class DownloadAndWriteDataTask extends AsyncTask<Void, Void, Boolean> {

        private static final String DATA_URL = "https://raw.githubusercontent.com/David-Haim/CountriesToCitiesJSON/master/countriesToCities.json";

        String resultJson = "";

        private ProgressDialog progressDialog;

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(AllCities.this);
            progressDialog.setMessage("Data downloading");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {


            try {
                URL url = new URL(DATA_URL);

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

            } catch (Exception e) {
                e.printStackTrace();

                return false;
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    progressDialog = new ProgressDialog(AllCities.this);
                    progressDialog.setMessage("Saving data into DB");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });


            try {
                JSONObject object = new JSONObject(resultJson);

                List<CityModel> allCities = new LinkedList<>();

                Iterator<String> keys = object.keys();
                while (keys.hasNext()) {
                    String country = keys.next();

                    CountryModel newCountry = new CountryModel();
                    newCountry.setName(country);

                    HelperFactory.getHelper().getSourceDAO().create(newCountry);

                    JSONArray arrayJson = object.getJSONArray(country);

                    for (int i = 0; i < arrayJson.length(); i++) {
                        CityModel newCity = new CityModel();
                        newCity.setName(arrayJson.getString(i));

                        newCity.setCountry(newCountry);
                        allCities.add(newCity);

                    }
                }


                HelperFactory.getHelper().getPostDAO().bulkInsertDataByCallBatchTasks(allCities);
            } catch (JSONException | SQLException e) {
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
                new TryReadDataFromDBTask().execute();
            } else {
                Toast.makeText(AllCities.this, "Data error", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    private class TryReadDataFromDBTask extends AsyncTask<Void, Void, List<CountryModel>> {

        private ProgressDialog progressDialog;

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(AllCities.this);
            progressDialog.setMessage("Reading data from DB ...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected List<CountryModel> doInBackground(Void... params) {
            try {
                return HelperFactory.getHelper().getSourceDAO().getAllSources();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<CountryModel> sources) {
            super.onPostExecute(sources);

            progressDialog.dismiss();

            if (sources == null) {
                Toast.makeText(AllCities.this, "Data reading error", Toast.LENGTH_SHORT).show();
                finish();
            } else if (sources.size() == 0) {
                // need load data !
                new DownloadAndWriteDataTask().execute();
            } else {
                Collections.sort(sources, new Comparator<CountryModel>() {
                    @Override
                    public int compare(CountryModel o1, CountryModel o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                refreshSpinnersData(sources);
            }

        }
    }

    private Spinner countries;
    private Spinner cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_cities);

        countries = (Spinner) findViewById(R.id.countries);
        cities = (Spinner) findViewById(R.id.cities);

        new TryReadDataFromDBTask().execute();
    }

    private void refreshSpinnersData(final List<CountryModel> countriesList) {
        countries.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, countriesList));
        countries.setSelection(0);

        countries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(AllCities.this, "cities: " + countriesList.get(position).getCitiesList().size(), Toast.LENGTH_SHORT).show();

                final List<CityModel> citiesList = new LinkedList<>(countriesList.get(position).getCitiesList());
                cities.setAdapter(new ArrayAdapter<>(AllCities.this, R.layout.support_simple_spinner_dropdown_item, citiesList));
                cities.setSelection(0);

                cities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(AllCities.this, FullInfo.class);
                        intent.putExtra("city", citiesList.get(position).getName());
                        intent.putExtra("country", citiesList.get(position).getCountry().getName());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}
