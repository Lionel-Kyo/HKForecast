package com.example.hkforecast;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class HKO_API{

    enum DataType
    {
        LocalWeatherForecast, nineDayWeatherForecast, CurrentWeatherReport, WeatherWarningSummary, WeatherWarningInformation, SpecialWeatherTips;
    }

    public static JSONObject request(DataType type, String language) throws JSONException, IOException {
        String dataType = null;
        switch(type){
            case LocalWeatherForecast: dataType = "flw"; break;
            case nineDayWeatherForecast: dataType = "fnd"; break;
            case CurrentWeatherReport: dataType = "rhrread"; break;
            case WeatherWarningSummary: dataType = "warnsum"; break;
            case WeatherWarningInformation: dataType = "warningInfo"; break;
            case SpecialWeatherTips: dataType = "swt"; break;
        }
        String url = "https://data.weather.gov.hk/weatherAPI/opendata/weather.php?dataType=" + dataType + "&lang=" + language;

        URL hko = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(hko.openStream()));

        String input;
        StringBuffer stringBuffer = new StringBuffer();
        while ((input = in.readLine()) != null)
        {
            stringBuffer.append(input);
        }
        in.close();
        String htmlData = stringBuffer.toString();
        return new JSONObject(htmlData);
    }

    public static void notEmptyAddLine(StringBuilder strbdr, String topic, String text){
        if(text == null) return;
        if(text.equals("")) return;
        strbdr.append(String.format("%s: %s\n", topic, text));
    }
}
