package flying.grub.tamtime;

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

/**
 * Created by fly on 09/02/15.
 */
public class DataParser {

    MainActivity mainActivity;

    static final String all = "http://www.tam-direct.com/webservice/data.php?pattern=getAll";
    static final String time = "http://www.tam-direct.com/webservice/data.php?pattern=getDetails";

    JSONObject allInfo;
    JSONObject timeInfo;

    public DataParser() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }

    public void httpRequest(String url, final boolean all){
        RequestQueue mRequestQueue = Volley.newRequestQueue(MainActivity.getAppContext());
        Log.d("Data", "Request" + all);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(all){
                    setAll(response);
                }else{
                    setTime(response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Data", "Erreur" + error);
                if(all){
                    setAll("{erreur}");
                }else{
                    setTime("{erreur}");
                }
            }
        });

        mRequestQueue.add(stringRequest);
    }

    public void setAll(String all) {
        try {
            Log.d("Data", "SetAll");
            allInfo = new JSONObject(all);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setTime(String time){
        try {
            Log.d("Data", "SetTime");
            timeInfo = new JSONObject(time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getAll(){
        httpRequest(all, true);
    }


    public void getTime(){
        httpRequest(time, false);
    }

    public void getStop(int Line){

    }




}
