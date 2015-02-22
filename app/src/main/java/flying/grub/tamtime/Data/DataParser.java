package flying.grub.tamtime.Data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import flying.grub.tamtime.MainActivity;


/**
 * Created by fly on 09/02/15.
 */
public class DataParser {

    private static final String all = "http://www.tam-direct.com/webservice/data.php?pattern=getAll";

    private JSONObject allInfo;
    private ArrayList<Line> linesArrayList;
    private ArrayList<Stop> stopArrayList;
    private Boolean asData;

    public DataParser() {
        stopArrayList = new ArrayList<>();
        asData = false;
    }

    public void httpRequest(String url){
        RequestQueue mRequestQueue = Volley.newRequestQueue(MainActivity.getAppContext());
        Log.d("Data", "Request");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                setLines(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Data", "Erreur" + error);
            }
        });

        mRequestQueue.add(stringRequest);
    }

    public void setLines(String response) {
        linesArrayList = new ArrayList<>();
        try {
            allInfo = new JSONObject(response);

            for(int i = 0; i< 17; i++) {
                if (i == 16) { i++;} // skip the L18
                linesArrayList.add(new Line(allInfo.getJSONArray("lines").getJSONObject(i)));
            }

            asData = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setTimes();
    }

    public void addStop(Stop s){
        stopArrayList.add(s);
    }

    public Stop getStop(int id){
        for (int i = 0; i< stopArrayList.size(); i++){
            if (stopArrayList.get(i).getId() == id ){
                return stopArrayList.get(i);
            }
        }
        return null;
    }

    public void getAll(){
        httpRequest(all);
    }

    public void setTimes(){
        int stopId;
        try {
            JSONArray aller = allInfo.getJSONObject("details").getJSONArray("aller");
            JSONArray retour = allInfo.getJSONObject("details").getJSONArray("retour");

            for (int i=0; i < aller.length() ;i++){
                stopId =  Integer.parseInt(aller.getJSONArray(i).get(4).toString());
                getStop(stopId).addTime(Integer.parseInt(aller.getJSONArray(i).get(5).toString()));
            }
            for (int i=0; i < retour.length() ;i++){
                stopId =  Integer.parseInt(retour.getJSONArray(i).get(4).toString());
                if (getStop(stopId) != null){
                    getStop(stopId).addTime(Integer.parseInt(retour.getJSONArray(i).get(5).toString()));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public Line getLine(int i){
        if (asData){
            return linesArrayList.get(i);
        }
        return null;
    }




}
