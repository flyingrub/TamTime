package flying.grub.tamtime;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fly on 09/02/15.
 */
public class HttpRequest {

    DataParser dataParser;
    RequestQueue queue;
    //this.dataParser = new DataParser();

    public HttpRequest(Context context, String url){

        queue = Volley.newRequestQueue(context);


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener() {

                    public void onResponse(JSONObject response) throws JSONException {
                        setDataParser(response.toString());
                    }

                    @Override
                    public void onResponse(Object response) throws JSONException {
                        setDataParser(response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) throws JSONException {
                        setDataParser("error");

                    }
                });

        queue.add(jsObjRequest);

    }

    public void setDataParser(String data) throws JSONException {
        this.dataParser = new DataParser(data);
    }

}
