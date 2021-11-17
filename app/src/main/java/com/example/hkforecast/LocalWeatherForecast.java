package com.example.hkforecast;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalWeatherForecast#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalWeatherForecast extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LocalWeatherForecast() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocalWeatherForecast.
     */
    // TODO: Rename and change types and number of parameters
    public static LocalWeatherForecast newInstance(String param1, String param2) {
        LocalWeatherForecast fragment = new LocalWeatherForecast();
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
        TextView tv_LocalWeatherForecast = getView().findViewById(R.id.tv_central);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder strbdr = new StringBuilder();
                try {
                    JSONObject json = HKO_API.request(HKO_API.DataType.LocalWeatherForecast, getString(R.string.language));
                    HKO_API.notEmptyAddLine(strbdr, getString(R.string.generalSituation), json.getString("generalSituation"));
                    HKO_API.notEmptyAddLine(strbdr, getString(R.string.tcInfo), json.getString("tcInfo"));
                    HKO_API.notEmptyAddLine(strbdr, getString(R.string.fireDangerWarning), json.getString("fireDangerWarning"));
                    HKO_API.notEmptyAddLine(strbdr, getString(R.string.forecastPeriod), json.getString("forecastPeriod"));
                    HKO_API.notEmptyAddLine(strbdr, getString(R.string.outlook), json.getString("forecastDesc"));
                    HKO_API.notEmptyAddLine(strbdr, getString(R.string.outlook), json.getString("outlook"));
                    String updateTime = json.getString("updateTime");
                    String[] splitedFT = updateTime.split("T");
                    splitedFT[0] = splitedFT[0].replace("-","/");
                    String[] splitedTime = splitedFT[1].split("\\+");
                    String[] splitedGMT = splitedTime[1].split(":");
                    splitedTime[0] = String.format("%s (UTC+%d.%d)", splitedTime[0], Integer.parseInt(splitedGMT[0]), Integer.parseInt(splitedGMT[1]));
                    updateTime = splitedFT[0] + " " + splitedTime[0];
                    HKO_API.notEmptyAddLine(strbdr, getString(R.string.updateTime), updateTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tv_LocalWeatherForecast.post(new Runnable() {
                    @Override
                    public void run() {
                            tv_LocalWeatherForecast.setText(strbdr.toString());
                    }
                });
            }
        });
        thread.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_local_weather_forecast, container, false);
    }
}