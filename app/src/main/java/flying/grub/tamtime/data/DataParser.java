package flying.grub.tamtime.data;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import de.greenrobot.event.EventBus;
import flying.grub.tamtime.R;

public class DataParser {

    private static final String TAG = DataParser.class.getSimpleName();
    private final String JSON_PLAN = "http://bl00m.science/TamTimeData/map.json";
    private final String JSON_THEOTIME = "http://www.bl00m.science/TamTimeData/timesTest.json";
    private final String JSON_REALTIME = "http://www.tam-direct.com/webservice/data.php?pattern=getDetails";
    private final String JSON_REPORT = "http://tam.flyingrub.me/report.php?r=getJson";
    private final String POST_REPORT = "http://tam.flyingrub.me/report.php?r=newReport";
    private ArrayList<Line> linesList;
    private ArrayList<Stop> stopList;
    private ArrayList<StopTimes> stpTimesList;
    private ArrayList<Report> reportList;

    private static DataParser data;

    private DataParser() {
        this.stopList = new ArrayList<>();
        this.linesList = new ArrayList<>();
        this.stpTimesList = new ArrayList<>();
        this.reportList = new ArrayList<>();
        this.data = this;
    }

    public void init(Context context) {
        setupMap(context);
        setupRealTimes(context);
        setupReport(context);
        //setupTheoTimes(context);
    }

    public static synchronized DataParser getDataParser() {
        if (data == null) {
            return new DataParser();
        } else {
            return data;
        }
    }

    public void update(Context context) {
        this.setupRealTimes(context);
        this.setupReport(context);
    }

//////////////////////
// Map Construction //
//////////////////////

    public void setupMap(Context context) {
        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.map);
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
                int e=0;
                while (e < this.linesList.size() && this.linesList.get(e).getLineNum() < curntLine.getLineNum()) e++;
                this.linesList.add(e, curntLine);
            }
        } catch (Exception e) {
              e.printStackTrace();
        }
    }

////////////////
// Real Times //
////////////////

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

///////////////////
// Theoric Times //
///////////////////

    public void downloadAllTheo(Context context) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("url", JSON_THEOTIME);
        context.startService(intent);
    }

    public boolean asTheo(Context context) {
        File file = context.getFileStreamPath("theo.json");
        return file.isFile();
    }

    public boolean needTheoUpdate(Context context) {
        Log.d(TAG, asTheo(context) + "");
        return !asTheo(context); //TO DO (MD5)
    }

    public void setupTheoTimes(Context context) { // Theoric times
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                JSON_THEOTIME, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        setTheoTimes(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
            }
        });
        VolleyApp.getInstance(context).addToRequestQueue(jsonObjReq);
    }

    public void setTheoTimes(JSONObject theoTimesJson) {
        StopTimes curntStpTim;
        JSONObject curntDay;
        String curntStopKey, curntDayKey;
        try {
            JSONObject stpTimJson = theoTimesJson.getJSONObject("stops");
            String date = theoTimesJson.getString("date");

            Iterator stopKey = stpTimJson.keys();

            while (stopKey.hasNext()) {
                curntStopKey = (String)stopKey.next();
                curntStpTim = this.getStopTimesByOurId(curntStopKey);
                curntStpTim.resetTheoTimes();

                curntDay = stpTimJson.getJSONObject(curntStopKey);
                Iterator stopDay = curntDay.keys();
                while (stopDay.hasNext()) {
                    curntDayKey = (String)stopDay.next();
                    curntStpTim.addTheoTimes(curntDay.getJSONArray(curntDayKey), date, Integer.parseInt(curntDayKey));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/////////////////
// User Report //
/////////////////

    // Adapt this method for android with Voley or whatever
    public void sendReport(final Context context, final Report report) {
        Map<String,String> params = new HashMap<String, String>();
        params.put("our_id", report.getStop().getOurId() + "");
        params.put("type", report.getType().getValue() + "");
        params.put("msg", report.getMessage());

        StringRequest sr = new StringRequest(
                Request.Method.POST,
                POST_REPORT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Integer result = Integer.parseInt(response);
                        Log.d(TAG, "REPONSE:" + response + "|");
                        Toast.makeText(context, context.getResources().getStringArray(R.array.post_status)[result], Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: " + error.getMessage());
                    }
                }) {
        };
        sr.setParams(params);
        VolleyApp.getInstance(context).addToRequestQueue(sr);
    }

    public void setupReport(Context context) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                JSON_REPORT, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        setReport(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
            }
        });
        VolleyApp.getInstance(context).addToRequestQueue(jsonObjReq);
    }

    public void setReport(JSONObject reportJson) {
        for (Report r : this.reportList) {
            r.removeFromStop();
        }
        this.reportList = new ArrayList<>();

        try {
            JSONArray reportListJson = reportJson.getJSONArray("report");

            for (int i=0; i< reportListJson.length(); i++) {
                JSONObject reportObjectJson = reportListJson.getJSONObject(i);
                String msg = reportObjectJson.has("msg") ? reportObjectJson.getString("msg") : null;

                Calendar date = Calendar.getInstance();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    date.setTime(sdf.parse(reportObjectJson.getString("date")));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Stop stop = this.getStopByOurId(reportObjectJson.getInt("our_id"));

                if (stop != null) {
                    Report report = new Report(stop, ReportType.reportFromNum(reportObjectJson.getInt("type")), msg, date);
                    this.reportList.add(report);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

///////////////////////////
// CityWay Distupt Event //
///////////////////////////

    public ArrayList<JSONObject> getDisruptEventList() throws Exception {
        ArrayList<JSONObject> res = new ArrayList<>();
        URL request = new URL(JSON_ALL_DISRUPT);
        Scanner scanner = new Scanner(request.openStream());
        String response = scanner.useDelimiter("\\Z").next();
        scanner.close();
        JSONArray list = (new JSONObject(response)).getJSONObject("DisruptionServiceObj").getJSONArray("Line");
        for (int i=0; i<list.length(); i++) {
            try {
                res.addAll(this.getLineDisruptEvent(list.getJSONObject(i).getInt("id")));
            } catch (Exception e) {
                System.err.println(JSON_LINE_DISRUPT + list.getJSONObject(i).getInt("id"));
                e.printStackTrace();
            }
        }
        return res;
    }

    public ArrayList<JSONObject> getLineDisruptEvent(int linkId) throws Exception {
        URL request = new URL(JSON_LINE_DISRUPT + linkId);
        Scanner scanner = new Scanner(request.openStream());
        String response = scanner.useDelimiter("\\Z").next();
        scanner.close();
        JSONObject all = new JSONObject(response);
        JSONArray resJson = all.getJSONObject("DisruptionServiceObj").optJSONArray("Disruption");
        ArrayList<JSONObject> res = new ArrayList<JSONObject>();
        if (resJson != null) {
            for (int i=0; i<resJson.length(); i++) res.add(resJson.getJSONObject(i));
        } else {
            res.add(all.getJSONObject("DisruptionServiceObj").getJSONObject("Disruption"));
        }
        return res;
    }

    public void setDisruptEvent(ArrayList<JSONObject> jsonEventList) {
        Line line;
        String title;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        while (this.disruptList.size() > 0) this.disruptList.get(0).destroy(); // Clear all the disrupt event
        for (JSONObject eventJson : jsonEventList) {
            line = getLineByNum(eventJson.getJSONObject("DisruptedLine").getInt("number"));
            Calendar beginDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            try {
                beginDate.setTime(sdf.parse(eventJson.getString("beginValidityDate").replace("T", " ")));
                endDate.setTime(sdf.parse(eventJson.getString("endValidityDate").replace("T", " ")));
                title = eventJson.getString("title");
                new DisruptEvent(this, line, beginDate, endDate, title); //The event put himself in Data & Line ArrayList
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setAlldistance(Location user) {
        for (Stop stop : stopList) {
            stop.calcDistanceFromUser(user);
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
        int linum = line.contains("L") ? Integer.parseInt(line.replace("L", "")) : Integer.parseInt(line);

        int i=0;
        while (i<this.linesList.size() && this.linesList.get(i).getLineNum() != linum) i++;
        if (i>=this.linesList.size()) return null;
        Line curntLine = this.linesList.get(i);

        i=0;
        ArrayList<Route> routesTab = curntLine.getRoutes();
        while (i<routesTab.size() && !routesTab.get(i).getDirection().toLowerCase().contains(direction.toLowerCase())) i++;
        if (i>=routesTab.size()) return null;
        Route curntRoute = routesTab.get(i);

        i=0;
        ArrayList<StopTimes> stpTimesTab = curntRoute.getStpTimes();
        while (i<stpTimesTab.size() && stpTimesTab.get(i).getStopId() != stopId) i++;
        if (i>=stpTimesTab.size()) return null;

        return stpTimesTab.get(i);
    }
    
    // Reset the real times for all StopTimes
    public void resetAllRealTimes() {
        for (StopTimes stp : stpTimesList) {
            stp.resetRealTimes();
        }
    }

    public ArrayList<Stop> getAllNearStops() {
        ArrayList<Stop> res = new ArrayList<>();
        for (Stop s : stopList) {
            if (s.getDistanceFromUser() <= 500) { // in meter
                res.add(s);
            }
        }
        return res;
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
