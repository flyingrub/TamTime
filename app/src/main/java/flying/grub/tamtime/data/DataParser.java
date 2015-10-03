package flying.grub.tamtime.data;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

import de.greenrobot.event.EventBus;
import flying.grub.tamtime.R;

public class DataParser {

    private static final String TAG = DataParser.class.getSimpleName();
    private final String JSON_PLAN = "http://bl00m.science/TamTimeData/allData.json"; 
    private final String JSON_THEOTIME = "http://www.bl00m.science/TamTimeData/timesTest.json";
    private final String JSON_REALTIME = "http://www.tam-direct.com/webservice/data.php?pattern=getDetails";
    private ArrayList<Line> linesList;
    private ArrayList<Stop> stopList;
    private ArrayList<StopTimes> stpTimesList;

    private static DataParser data;

    private DataParser() {
        this.stopList = new ArrayList<>();
        this.linesList = new ArrayList<>();
        this.stpTimesList = new ArrayList<>();
        this.data = this;
    }

    public void init(Context context) {
        setupMap(context);
        setupRealTimes(context);
    }

    public static synchronized DataParser getDataParser() {
        if (data == null) {
            return new DataParser();
        } else {
            return data;
        }
    }

    public void setupRealTimes(Context context) { // Real times
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                JSON_REALTIME, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        setRealTimes(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
            }
        });
        VolleyApp.getInstance(context).addToRequestQueue(jsonObjReq);
    }

    public void setupMap(Context context) {
        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.data);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            setMap(new JSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Setup toutes les instance nécéssaire
    public void setMap(JSONObject planJson) throws JSONException {
        this.linesList = new ArrayList<Line>();
        this.stopList = new ArrayList<Stop>();

        Line curntLine;
        Stop curntStop;
        Route curntRoute;
        StopTimes stpTimes;

        try {
            // Create all Stops
            JSONArray stopsDetailsJson = planJson.getJSONArray("stop_details");
            JSONObject curntStpJson;
            for (int i=0; i<stopsDetailsJson.length(); i++) {
                curntStpJson = stopsDetailsJson.getJSONObject(i);
                curntStop = new Stop(curntStpJson.getString("name"), curntStpJson.getInt("our_id"), curntStpJson.getDouble("lat"), curntStpJson.getDouble("lon"));
                this.stopList.add(curntStop);
            }
            curntStop = null; // Need to be null

            // Create all Lines/Routes/StopTimes
            JSONArray linesInfoJson = planJson.getJSONArray("lines");
            JSONObject jsonLine;
            JSONArray jsonRoutesList;
            JSONArray jsonStopsList;
            for (int i=0; i<linesInfoJson.length(); i++) { // Browse the lines JsonArray
                jsonLine = linesInfoJson.getJSONObject(i);
                curntLine = new Line(jsonLine.getInt("num"), jsonLine.getString("id"));

                jsonRoutesList = jsonLine.getJSONArray("routes");
                for (int j=0; j<jsonRoutesList.length(); j++) { // Browse the routes array
                    curntRoute = new Route(jsonRoutesList.getJSONObject(j).getString("direction"), curntLine, j+1);
                    curntLine.addRoute(curntRoute);

                    jsonStopsList = jsonRoutesList.getJSONObject(j).getJSONArray("stops");
                    for (int k=0; k<jsonStopsList.length(); k++) { // Browse the stops (StopTimes)array
                        curntStop = this.getStopByName(jsonStopsList.getJSONObject(k).getString("name")); // Get the stop
                        curntStop.addLine(curntLine);

                        stpTimes = new StopTimes(curntRoute, curntStop, curntLine, jsonStopsList.getJSONObject(k).getInt("stop_id"));
                        this.addStpTimes(stpTimes);
                        curntRoute.addStopTimes(stpTimes);
                    }
                }
                this.linesList.add(curntLine);
            }
        } catch (Exception e) {
              e.printStackTrace();
        }
    }

    public void setRealTimes(JSONObject timesJson) {
        StopTimes stpTimes;

        this.resetAllRealTimes(); // Reset all the real times

        try {
            JSONArray aller = timesJson.getJSONArray("aller");
            JSONArray retour = timesJson.getJSONArray("retour");
            JSONArray curntStop;
            for (int i=0; i < aller.length(); i++) {
                curntStop = aller.getJSONArray(i);
                stpTimes = this.getTheStopTimes(curntStop.getString(0), curntStop.getString(6), curntStop.getInt(4)); // Get the StopTimes
                if (stpTimes != null) {
                    stpTimes.addRealTime(curntStop.getInt(5));
                }
            }

            for (int i=0; i < retour.length(); i++) {
                curntStop = retour.getJSONArray(i);
                stpTimes = this.getTheStopTimes(curntStop.getString(0), curntStop.getString(6), curntStop.getInt(4));
                if (stpTimes != null) {
                    stpTimes.addRealTime(curntStop.getInt(5));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.TIMESUPDATE));
    }

    public void setTheoTimes(JSONObject theoTimesJson) {
        StopTimes curntStpTim;
        try {
            JSONArray stpTimJson = theoTimesJson.getJSONArray("stops");
            String date = theoTimesJson.getString("date");

            for (int i=0; i < stpTimJson.length(); i++) {
                curntStpTim = this.getStopTimesByOurId(stpTimJson.getJSONObject(i).getInt("our_id"));
                curntStpTim.setTheoTimes(stpTimJson.getJSONObject(i).getJSONArray("times"), date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Add
    public void addStop(Stop s){
        this.stopList.add(s);
    }

    public void addLine(Line l) {
        this.linesList.add(l);
    }

    public void addStpTimes(StopTimes srl) {
        this.stpTimesList.add(srl);
    }

    // Get
    public Line getLine(int i){
        if (linesList != null) return linesList.get(i);
        return null;
    }

    public ArrayList<Line> getLinesList() {
        return linesList;
    }

    public ArrayList<Stop> getStopList() {
        return stopList;
    }

    public Stop getStopByName(String name) {
        for (Stop stp : this.stopList) {
            if (stp.getName().equals(name)) return stp;
        }
        return null;
    }

    public Stop getStopByOurId(int ourId) {
        for (Stop stp : this.stopList) {
            if (stp.getOurId() == ourId) return stp;
        }
        return null;
    }

    public StopTimes getStopTimesByOurId(int ourId) {
        for (StopTimes st : this.stpTimesList) {
            if (st.getOurId() == ourId) return st;
        }
        return null;
    }

        // Return the StopTimes which correspond to line/direction/stopId
    public StopTimes getTheStopTimes(String line, String direction, int stopId) {
        for (StopTimes st : this.stpTimesList) {
            if (st.isTheOne(line, direction, stopId)) return st;
        }
        return null;
    }
 
        // Reset the real times for all StopTimes
    public void resetAllRealTimes() {
        for (StopTimes stp : stpTimesList) {
            stp.resetRealTimes();
        }
    }

    public ArrayList<Stop> searchInStops(String search) {
        ArrayList<Stop> res = new ArrayList<>();
        for (Stop s : stopList) {
            if (s.getName().toLowerCase().contains(search.toLowerCase())) {
                res.add(s);
            }
        }
        return res;
    }

}
