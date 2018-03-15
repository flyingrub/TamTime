package flying.grub.tamtime.data.datahandler;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import flying.grub.tamtime.R;
import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.map.StopZone;
import flying.grub.tamtime.data.update.MessageUpdate;
import flying.grub.tamtime.data.report.Report;
import flying.grub.tamtime.data.report.ReportType;
import flying.grub.tamtime.data.connection.VolleyApp;

/**
 * Created by fly on 11/9/15.
 */
public class ReportEvent {

    private static final String TAG = ReportEvent.class.getSimpleName();
    private static final String JSON_REPORT = "https://tam.daze.space/report.php?r=getJson";
    private static final String POST_REPORT = "https://tam.daze.space/report.php?r=newReport";
    private static final String CONFIRM_REPORT = "https://tam.daze.space/report.php?r=confirmReport";

    private ArrayList<Report> reportList;
    private Context context;

    public ReportEvent(Context context) {
        this.reportList = new ArrayList<>();
        this.context = context;
    }

    public void update() {
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

    // Adapt this method for android with Voley or whatever
    public void sendReport(final Context context, final Report report) {
        Map<String,String> params = new HashMap<String, String>();
        params.put("our_id", report.getStop().getID() + "");
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
                        update();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: " + error.getMessage());
                    }
                }) {
        };
        //sr.setParams(params);
        VolleyApp.getInstance(context).addToRequestQueue(sr);
    }

    // Adapt this method for android with Voley or whatever
    public void confirmReport(final Context context, final int reportId) {
        Map<String,String> params = new HashMap<String, String>();
        params.put("report_id", reportId + "");

        StringRequest sr = new StringRequest(
                Request.Method.POST,
                CONFIRM_REPORT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Integer result = Integer.parseInt(response);
                        Log.d(TAG, "REPONSE:" + response + "|");
                        Toast.makeText(context, context.getResources().getStringArray(R.array.confirm_status)[result], Toast.LENGTH_SHORT).show();
                        update();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: " + error.getMessage());
                    }
                }) {
        };
        //sr.setParams(params);
        VolleyApp.getInstance(context).addToRequestQueue(sr);
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
                String msg = reportObjectJson.optString("msg");
                int confirm = reportObjectJson.getInt("confirm");
                int reportId = reportObjectJson.getInt("report_id");

                Calendar date = Calendar.getInstance();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    date.setTime(sdf.parse(reportObjectJson.getString("date")));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                StopZone stop = Data.getData().getMap().getStopZoneById(reportObjectJson.getInt("our_id"));

                if (stop != null) {
                    Report report = new Report(stop, ReportType.reportFromId(reportObjectJson.getInt("type")), msg, date, confirm, reportId);
                    this.reportList.add(report);
                }
            }

            EventBus.getDefault().post(new MessageUpdate(MessageUpdate.Type.REPORT_UPDATE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
