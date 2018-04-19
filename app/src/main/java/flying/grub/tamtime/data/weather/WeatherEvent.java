package flying.grub.tamtime.data.weather;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

import flying.grub.tamtime.R;
import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.connection.JsonArrayReq;
import flying.grub.tamtime.data.connection.NetworkResponseRequest;
import flying.grub.tamtime.data.connection.VolleyApp;
import flying.grub.tamtime.data.map.StopZone;

import static android.os.Build.VERSION_CODES.M;


public class WeatherEvent {
    private static final String TAG = WeatherEvent.class.getSimpleName();
    private static final String GET_WEATHER = "http://api.wunderground.com/api/03733854eaaa89d1/conditions/forecast/alert/q/"; //<latitude>,<longitude>.json

    private Context context;

    public WeatherEvent(Context context) {
        this.context = context;
    }

    public void getWeather(int stopZoneID) {
        StopZone stopZone = Data.getData().getMap().getStopZoneById(stopZoneID);

        if(stopZone.getWeather() != null) //This condition is here, to limit API's call, because of free and very limited offer(10 calls/min, 500 calls/day)
            return;

        /*
        To avoid API's aproximation problems, we make our own aproximation (2 decimal after .)
         */
        double latitude = stopZone.getLocation().getLatitude() * 100;
        latitude = Math.floor(latitude);
        latitude /= 100;

        double longitude = stopZone.getLocation().getLongitude() * 100;
        longitude = Math.floor(longitude);
        longitude /= 100;

        Log.d(TAG, "getWeather : " + stopZone.getName() + " location : " + latitude+ ", "+ longitude);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                GET_WEATHER + latitude + "," + longitude + ".json",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setWeather(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
            }
        });
        VolleyApp.getInstance(context).addToRequestQueue(req);
    }

    public void setWeather(JSONObject reportJson) {
        try
        {
            JSONObject currentObservation =  reportJson.getJSONObject("current_observation");

            JSONObject displayLocation = currentObservation.getJSONObject("display_location");

            double latitude = displayLocation.getDouble("latitude");
            double longitude = displayLocation.getDouble("longitude");

            Log.d(TAG, "setWeather : " + latitude + ", " + longitude);

            Collection<StopZone> stops = Data.getData().getMap().getStopsByLocation(latitude, longitude);

            if(stops.size() == 0)
                return;

            int temperature = currentObservation.getInt("temp_c");
            String weatherString = currentObservation.getString("weather");
            String humidityString = currentObservation.getString("relative_humidity");
            double wind = currentObservation.getDouble("wind_gust_kph");

            for(StopZone stop : stops) {
                Log.d(TAG, "stop name : " + stop.getName());
                stop.setWeather(new Weather(temperature, weatherString, humidityString, wind));
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }
}
