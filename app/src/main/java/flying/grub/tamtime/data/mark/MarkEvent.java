package flying.grub.tamtime.data.mark;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import flying.grub.tamtime.R;
import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.connection.JsonArrayReq;
import flying.grub.tamtime.data.connection.NetworkResponseRequest;
import flying.grub.tamtime.data.map.StopZone;
import flying.grub.tamtime.data.connection.VolleyApp;


public class MarkEvent {

    public static final int MARK_LIMIT = 5;

    private static final String TAG = MarkEvent.class.getSimpleName();
    private static final String GET_MARKS = "http://192.168.1.16:5000/marks_average"; //TODO : Use the real server
    private static final String POST_MARK = "http://192.168.1.16:5000/mark"; //TODO : Use the real server

    private ArrayList<StopZone> markList;
    private Context context;

    public MarkEvent(Context context) {
        this.markList = new ArrayList<>();
        this.context = context;
    }

    public void sendMark(final Context context, final int stopZoneId, final int mark) {
        Uri.Builder url = Uri.parse(POST_MARK).buildUpon();
        url.appendQueryParameter("stop_id", stopZoneId + "");
        url.appendQueryParameter("mark", mark + "");
        url.appendQueryParameter("android_id", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));

        NetworkResponseRequest req = new NetworkResponseRequest(
                Request.Method.POST,
                url.toString(),
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Toast.makeText(context, context.getResources().getText(R.string.request_sucessful), Toast.LENGTH_SHORT).show();
                        getMarks();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, context.getResources().getText(R.string.request_unsucessful), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Error: " + error.getMessage());
                    }
                }) {
        };
        VolleyApp.getInstance(context).addToRequestQueue(req);
    }

    public void getMarks() {
        JsonArrayReq req = new JsonArrayReq(Request.Method.GET,
                GET_MARKS, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        setMarks(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
            }
        });
        VolleyApp.getInstance(context).addToRequestQueue(req);
    }

    public void setMarks(JSONArray reportJson) {
        try {

            for (int i=0; i< reportJson.length(); i++) {
                JSONObject reportObjectJson = reportJson.getJSONObject(i);
                Double mark = reportObjectJson.getDouble("mark_average");

                StopZone stop = Data.getData().getMap().getStopZoneById(reportObjectJson.getInt("stop_id"));

                stop.setMark(mark);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
