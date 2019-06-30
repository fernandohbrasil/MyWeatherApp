package com.fernando.myweatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<City> cityList;
    private RecyclerView recyclerView;
    private CityAdapter cityAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ImageView imgAdd;
    private AutoCompleteTextView edtCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        loadData();
        startObjects();
        startEvents();
    }

    private void startObjects() {
        imgAdd = findViewById(R.id.imgCancel);
        edtCity = findViewById(R.id.edtCity);
        String[] countries = getResources().getStringArray(R.array.countries);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, countries);
        edtCity.setAdapter(adapter);

        buildRecyclerView();
    }

    private void startEvents() {
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtCity.getText().clear();
            }
        });

        cityAdapter.setOnItemClickListener(new CityAdapter.OnItemClickListener() {

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                removeItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(this.recyclerView);

        edtCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                findWeather(edtCity.getText().toString());
            }
        });
    }


    public void findWeather(String cityName) {
        String apiId = "&appid=a6c4a51175a81ae6b541bf050dc78715";
        String baseUrl = "http://api.openweathermap.org/data/2.5/weather?q=";
        String url = baseUrl + cityName + apiId;

        final City city = new City();
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_object = response.getJSONObject("main");
                    JSONObject sys_object = response.getJSONObject("sys");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);

                    city.setName(response.getString("name"));
                    city.setCountry(sys_object.getString("country"));
                    city.setTemperature(String.valueOf(main_object.getDouble("temp")));
                    city.setIcon(object.getString("icon"));
                    city.setTimeZone(response.getString("timezone"));

                    insertItem(city);
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cityList);
        editor.putString("task list", json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<City>>() {
        }.getType();
        cityList = gson.fromJson(json, type);

        if (cityList == null) {
            cityList = new ArrayList<>();
        }

        if (cityList.isEmpty()) {
            loadInitList();
        } else {
            refreshAll();
        }
    }

    private void refreshAll() {
        ArrayList<City> tempCities = (ArrayList<City>) cityList.clone();
        cityList.clear();

        for (int i = 0; i < tempCities.size(); i++) {
            findWeather(tempCities.get(i).toString());
        }
    }


    private void loadInitList() {
        ArrayList<String> cities = new ArrayList<>();
        cities.add("Dublin,IE");
        cities.add("London,UK");
        cities.add("Beijing,CN");
        cities.add("Sydney,AU");

        for (int i = 0; i <= 3; i++) {
            findWeather(cities.get(i));
        }
    }

    private void buildRecyclerView() {
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        cityAdapter = new CityAdapter(cityList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cityAdapter);
    }

    private void insertItem(City city) {
        cityList.add(cityList.size(), city);
        cityAdapter.notifyItemInserted(cityList.size());
        saveData();
    }

    public void removeItem(int position) {
        cityList.remove(position);
        cityAdapter.notifyItemRemoved(position);
        saveData();
    }
}