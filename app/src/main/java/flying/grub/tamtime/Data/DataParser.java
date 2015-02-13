package flying.grub.tamtime.Data;

import android.os.StrictMode;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

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
    private ArrayList<Lines> linesArrayList;
    private Boolean asData;

    public DataParser() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        linesArrayList = new ArrayList<>();
        StrictMode.setThreadPolicy(policy);
        asData = false;
    }

    public void httpRequest(String url){
        RequestQueue mRequestQueue = Volley.newRequestQueue(MainActivity.getAppContext());
        Log.d("Data", "Request");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                setAll(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Data", "Erreur" + error);
                setAll("{erreur}");
            }
        });

        mRequestQueue.add(stringRequest);
    }

    public void setAll(String all) {
        try {
            allInfo = new JSONObject(all);

            for(int i = 0; i< 1; i++) {
                linesArrayList.add(new Lines(allInfo.getJSONArray("lines").getJSONObject(i)));
            }

            asData = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getAll(){
        httpRequest(all);
    }


    public Lines getLine(int i){
        if (asData){
            return linesArrayList.get(i);
        }
        return null;
    }




}
