package com.pashkobohdan.cityinfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private DownloadAndWriteDataTask downloadAndWriteDataTask;
    private TryReadDataFromDBTask tryReadDataFromDBTask;

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
                    if (country.equals("")) {
                        continue;
                    }

                    CountryModel newCountry = new CountryModel();
                    newCountry.setName(country);

                    HelperFactory.getHelper().getCountryDAO().create(newCountry);

                    JSONArray arrayJson = object.getJSONArray(country);

                    for (int i = 0; i < arrayJson.length(); i++) {
                        String cityName = arrayJson.getString(i);
                        if (cityName.equals("")) {
                            continue;
                        }

                        CityModel newCity = new CityModel();
                        newCity.setName(cityName);

                        newCity.setCountry(newCountry);
                        allCities.add(newCity);

                    }
                }


                HelperFactory.getHelper().getCityDAO().bulkInsertDataByCallBatchTasks(allCities);
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
                return HelperFactory.getHelper().getCountryDAO().getAllCountries();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<CountryModel> sources) {
            super.onPostExecute(sources);

            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (sources == null) {
                Toast.makeText(AllCities.this, "Data reading error", Toast.LENGTH_SHORT).show();
                finish();
            } else if (sources.size() == 0) {
                if (Application.isOnline(AllCities.this)) {
                    downloadAndWriteDataTask = new DownloadAndWriteDataTask();
                    downloadAndWriteDataTask.execute();
                } else {
                    Toast.makeText(AllCities.this, "Please, turn on the Internet connection and try later", Toast.LENGTH_SHORT).show();
                    finish();
                }
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

    private Button loadInfOButton;
    private List<CountryModel> globalCountryList;
    private List<CityModel> globalCityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_cities);

        countries = (Spinner) findViewById(R.id.countries);
        cities = (Spinner) findViewById(R.id.cities);

        loadInfOButton = (Button) findViewById(R.id.load_info_button);

        loadInfOButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllCities.this, FullInfo.class);
                intent.putExtra("city", globalCityList.get(cities.getSelectedItemPosition()).getName());
                intent.putExtra("country", globalCountryList.get(countries.getSelectedItemPosition()).getName());

                startActivity(intent);
            }
        });

        if(!Application.isOnline(this)){
            Toast.makeText(this, "Please, turn on the Internet connection and try later", Toast.LENGTH_SHORT).show();
            finish();
        }

        tryReadDataFromDBTask = new TryReadDataFromDBTask();
        tryReadDataFromDBTask.execute();
    }

    private void refreshSpinnersData(final List<CountryModel> countriesList) {
        globalCountryList = countriesList;

        countries.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, countriesList));
        countries.setSelection(0);
        globalCityList = new LinkedList<>(globalCountryList.get(0).getCitiesList());

        countries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                globalCityList = new LinkedList<>(countriesList.get(position).getCitiesList());
                Collections.sort(globalCityList, new Comparator<CityModel>() {
                    @Override
                    public int compare(CityModel o1, CityModel o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });

                cities.setAdapter(new ArrayAdapter<>(AllCities.this, R.layout.support_simple_spinner_dropdown_item, globalCityList));
                cities.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (tryReadDataFromDBTask != null) {
            tryReadDataFromDBTask.cancel(true);
        }

        if (downloadAndWriteDataTask != null) {
            downloadAndWriteDataTask.cancel(true);
        }
    }
}
