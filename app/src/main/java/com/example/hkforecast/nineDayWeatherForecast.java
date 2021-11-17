package com.example.hkforecast;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link nineDayWeatherForecast#newInstance} factory method to
 * create an instance of this fragment.
 */
public class nineDayWeatherForecast extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<String> forecastList;
    private TextView tv_LocalWeatherForecast;

    public nineDayWeatherForecast() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment nineDayWeatherForecast.
     */
    // TODO: Rename and change types and number of parameters
    public static nineDayWeatherForecast newInstance(String param1, String param2) {
        nineDayWeatherForecast fragment = new nineDayWeatherForecast();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_LocalWeatherForecast = getView().findViewById(R.id.tv_central);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder strbdr = new StringBuilder();
                ArrayList<String> dayList = new ArrayList<>();
                forecastList = new ArrayList<>();
                try {
                    JSONObject json = HKO_API.request(HKO_API.DataType.nineDayWeatherForecast, getString(R.string.language));
                    JSONArray weatherForecast = json.getJSONArray("weatherForecast");
                    for (int i = 0; i < weatherForecast.length(); i++) {
                        strbdr = new StringBuilder();
                        JSONObject json2 = weatherForecast.getJSONObject(i);
                        String forecastDate = json2.getString("forecastDate");
                        forecastDate = String.format("%s/%s/%s", forecastDate.substring(0,4), forecastDate.substring(4,6), forecastDate.substring(6,8));
                        dayList.add(String.format("%s (%s)", forecastDate, json2.getString("week")));

                        JSONObject forecastMaxtemp = json2.getJSONObject("forecastMaxtemp");
                        JSONObject forecastMintemp = json2.getJSONObject("forecastMintemp");
                        JSONObject forecastMaxrh = json2.getJSONObject("forecastMaxrh");
                        JSONObject forecastMinrh = json2.getJSONObject("forecastMinrh");

                        HKO_API.notEmptyAddLine(strbdr, getString(R.string.forecastWeather), json2.getString("forecastWeather"));
                        HKO_API.notEmptyAddLine(strbdr, getString(R.string.forecastMaxtemp), forecastMaxtemp.getString("value") + "°" + forecastMaxtemp.getString("unit"));
                        HKO_API.notEmptyAddLine(strbdr, getString(R.string.forecastMintemp), forecastMintemp.getString("value") + "°" + forecastMintemp.getString("unit"));
                        HKO_API.notEmptyAddLine(strbdr, getString(R.string.forecastWind), json2.getString("forecastWind"));
                        HKO_API.notEmptyAddLine(strbdr, getString(R.string.forecastMaxrh), forecastMaxrh.getString("value") + forecastMaxrh.getString("unit").replace("percent", "%"));
                        HKO_API.notEmptyAddLine(strbdr, getString(R.string.forecastMinrh), forecastMinrh.getString("value") + forecastMinrh.getString("unit").replace("percent", "%"));

                        //HKO_API.notEmptyAddLine(strbdr, "土壤溫度", json.getString("soilTemp"));
                        //HKO_API.notEmptyAddLine(strbdr, "海面溫度", json.getString("seaTemp"));
                        forecastList.add(strbdr.toString());
                        //Log.d("debug",json2.toString());
                        //Log.d("debug",json.getString("soilTemp").toString());
                        //Log.d("debug",json.getString("seaTemp").toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                StringBuilder finalStrbdr = strbdr;
                tv_LocalWeatherForecast.post(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, dayList);
                        Spinner spinner_week = getView().findViewById(R.id.spinner_week);
                        spinner_week.setAdapter(adapter);
                        spinner_week.setOnItemSelectedListener(itemSelectedListener);
                        tv_LocalWeatherForecast.setText(finalStrbdr.toString());
                    }
                });
            }
        });
        thread.start();
    }

    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(forecastList != null) {
                tv_LocalWeatherForecast.setText(forecastList.get(position));
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nine_day_weather_forecast, container, false);
    }
}