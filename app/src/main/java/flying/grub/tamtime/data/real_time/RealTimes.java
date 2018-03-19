package flying.grub.tamtime.data.real_time;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.connection.JsonArrayReq;
import flying.grub.tamtime.data.connection.VolleyApp;
import flying.grub.tamtime.data.map.Direction;
import flying.grub.tamtime.data.map.Line;
import flying.grub.tamtime.data.map.Stop;
import flying.grub.tamtime.data.update.MessageUpdate;

public class RealTimes {

    private static final String TAG = RealTimes.class.getSimpleName();
    private static final String JSON_REALTIME_STOPS = "https://apimobile.tam-voyages.com/api/v1/hours/next/stops";
    private static final String JSON_REALTIME_LINE = "https://apimobile.tam-voyages.com/api/v1/hours/next/line";

    private Context context;

    public RealTimes(Context context) {
        this.context = context;
    }

    public JSONObject stopToJSON(Stop stop) throws JSONException {
        JSONObject result = new JSONObject();
        result.put("sens", stop.getDirection().getSens());
        JSONArray directionArray = new JSONArray();
        for(Integer id : stop.getDirection().getArrivalIds()) {
            directionArray.put(id);
        }
        result.put("directions", directionArray);
        result.put("urbanLine", stop.getDirection().getLine().getUrbanLine());
        result.put("citywayLineId", stop.getDirection().getLine().getCityway_id());
        result.put("lineNumber", stop.getDirection().getLine().getTam_id());
        result.put("citywayStopId", stop.getCityway_id());
        result.put("tamStopId", stop.getTam_id());
        return result;
    }

    public JSONObject stopsToJSON(ArrayList<Stop> stops) throws JSONException {
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        for (Stop stop : stops) {
            array.put(stopToJSON(stop));
        }
        result.put("stopList", array);
        return result;
    }

    public void updateStops(ArrayList<Stop> stops) {
        JSONObject data = null;
        try {
            data = stopsToJSON(stops);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonArrayReq req = new JsonArrayReq(Request.Method.POST, JSON_REALTIME_STOPS, data,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject stop = response.getJSONObject(i);
                                int id = stop.getInt("cityway_stop_id");
                                JSONArray timesJSON = stop.getJSONArray("stop_next_time");
                                ArrayList<Time> times = new ArrayList<>();
                                for (int j = 0; j < timesJSON.length(); j++) {
                                    JSONObject timeJSON = timesJSON.getJSONObject(j);
                                    String waiting_time = timeJSON.getString("waiting_time");
                                    int hour = timeJSON.getInt("passing_hour");
                                    int min = timeJSON.getInt("passing_minute");
                                    times.add(new Time(waiting_time, hour, min));
                                }
                                Data.getData().getMap().addTimeToStop(id, times);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        EventBus.getDefault().post(new MessageUpdate(MessageUpdate.Type.TIMES_UPDATE));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error :" + error);
            }
        });

        VolleyApp.getInstance(context).addToRequestQueue(req);
    }

    public void updateLine(Line line) {
        JSONObject data = null;
        try {
            data = lineToJSON(line);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonArrayReq req = new JsonArrayReq(Request.Method.POST, JSON_REALTIME_LINE, data,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject stop = response.getJSONObject(i);
                                int id = stop.getInt("cityway_stop_id");
                                JSONArray timesJSON = stop.getJSONArray("stop_next_time");
                                ArrayList<Time> times = new ArrayList<>();
                                for (int j = 0; j < timesJSON.length(); j++) {
                                    JSONObject timeJSON = timesJSON.getJSONObject(j);
                                    String waiting_time = timeJSON.getString("waiting_time");
                                    int hour = timeJSON.getInt("passing_hour");
                                    int min = timeJSON.getInt("passing_minute");
                                    times.add(new Time(waiting_time, hour, min));
                                }
                                Data.getData().getMap().addTimeToStop(id, times);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        EventBus.getDefault().post(new MessageUpdate(MessageUpdate.Type.TIMES_UPDATE));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error :" + error.networkResponse);
            }
        });

        VolleyApp.getInstance(context).addToRequestQueue(req);
    }

    public JSONObject lineToJSON(Line line) throws JSONException {
        JSONObject result = new JSONObject();
        JSONArray directions = new JSONArray();
        for (Direction direction : line.getDirections()) {
            for (Integer id : direction.getArrivalIds()) {
                directions.put(id);
            }
        }
        result.put("directions", directions);
        result.put("citywayLineId", line.getCityway_id());
        result.put("lineNumber", line.getTam_id());
        result.put("sens", 1);

        JSONArray stops = new JSONArray();
        for (Direction direction : line.getDirections()) {
            for (Stop stop : direction.getStops()) {
                stops.put(stop.getCityway_id());
            }
        }
        result.put("stops", stops);
        result.put("urbanLine", line.getUrbanLine());
        return result;
    }
}
