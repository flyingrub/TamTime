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
    private final String JSON_PLAN = "http://www.tam-direct.com/webservice/data.php?pattern=getAll";
    private final String JSON_THEOTIME = "http://www.bl00m.science/TamTimeData/allLines.json";// "http://www.tam-direct.com/webservice/data.php?pattern=getAll";
    private final String JSON_REALTIME = "http://www.tam-direct.com/webservice/data.php?pattern=getDetails";
    private ArrayList<Line> linesList;
    private ArrayList<Stop> stopList;
    private ArrayList<StopTimes> stpTimesList;

    private Context context;
    private static DataParser data;

    private DataParser(Context context) {
        this.stopList = new ArrayList<>();
        this.linesList = new ArrayList<>();
        this.stpTimesList = new ArrayList<>();
        this.context = context;
        setupLines();
        setupTimes();
    }

    public static synchronized DataParser getDataParser(Context context) {
        if (data == null) {
            return new DataParser(context);
        } else {
            return data;
        }
    }

    public void setupTimes() { // Real times
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                JSON_REALTIME, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        setTimes(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
            }
        });
        VolleyApp.getInstance(context).addToRequestQueue(jsonObjReq);
    }

    public void setupLines() {
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
            setLines(new JSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

	// Setup toutes les instance nécéssaire
	private void setLines(JSONObject planJson) {
		this.linesList = new ArrayList<>();
		this.stopList = new ArrayList<>();

		try {
			JSONArray linesInfoJson = planJson.getJSONArray("lines");
			JSONObject jsonLine;
			for (int i=0; i<linesInfoJson.length(); i++) { // Parcour du Json et construction des lignes
				jsonLine = linesInfoJson.getJSONObject(i);
				Line curntLine = new Line(jsonLine.getInt("route_number"));

				JSONArray jsonRoutesList = jsonLine.getJSONArray("routes"); // On récupère le Json des routes

				Route curntRoute = null;
				Stop curntStop;
				String directionRoute;
				StopTimes stpTimes;
				for (int j=0; j<jsonRoutesList.length(); j++) {
					if (jsonRoutesList.getJSONObject(j).getInt("stop_order") == 1) { // Si c'est un Arret n°1 alors on crée une nouvelle Route
					    // Maybe dangerous d'initialiser dans un if (si le json n'a pas de stop n°1 alors ça plantera grave)
					    curntRoute = new Route(jsonRoutesList.getJSONObject(j).getString("direction"), curntLine);
					    curntLine.addRoute(curntRoute); // Et on l'ajoute a la ligne
					}

					curntStop = this.getStopByName(jsonRoutesList.getJSONObject(j).getString("stop_name")); // On regarde si l'Arret est déjà setup

					if (curntStop == null) { // Si non alors on le crée avec la ligne
					    curntStop = new Stop(jsonRoutesList.getJSONObject(j).getString("stop_name"), curntLine);
					    this.addStop(curntStop); // On le référence au Data
					} else {
				    	curntStop.addLine(curntLine); // Si oui alors on lui ajoute ljuste a ligne
					}
					 // On add le stop a route via StopTimes
					stpTimes = new StopTimes(curntRoute, curntStop, jsonRoutesList.getJSONObject(j).getInt("stop_id"));
					this.addStpTimes(stpTimes);
					curntRoute.addStopTimes(stpTimes);
				}
				this.addLine(curntLine);
			}
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

    private void setTimes(JSONObject timesJson) {
        StopTimes stpTimes;
        try {
            JSONArray aller = timesJson.getJSONArray("aller");
            JSONArray retour = timesJson.getJSONArray("retour");

            for (int i=0; i < aller.length(); i++) { // Pour tout aller
                stpTimes = this.getSrlByStopId(aller.getJSONArray(i).getInt(4)); // On récupère le RouteLinkStop
                if (stpTimes != null) { // Si il existe on lui ajoute le time
                    stpTimes.addRealTime(Integer.parseInt(aller.getJSONArray(i).get(5).toString()));
                }
            }

            for (int i=0; i < retour.length(); i++) {
                stpTimes = this.getSrlByStopId(retour.getJSONArray(i).getInt(4));
                if (stpTimes != null) {
                    stpTimes.addRealTime(Integer.parseInt(retour.getJSONArray(i).get(5).toString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.TIMESUPDATE));
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

    public StopTimes getSrlByStopId(int stopId) {
        for (StopTimes srl : this.stpTimesList) {
            if (srl.getStopId() == stopId) return srl;
        }
        return null;
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
