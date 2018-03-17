package flying.grub.tamtime.data.report;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.greenrobot.event.EventBus;
import flying.grub.tamtime.R;
import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.connection.JsonArrayReq;
import flying.grub.tamtime.data.connection.NetworkResponseRequest;
import flying.grub.tamtime.data.map.StopZone;
import flying.grub.tamtime.data.update.MessageUpdate;
import flying.grub.tamtime.data.connection.VolleyApp;

/**
 * Created by fly on 11/9/15.
 */
public class ReportEvent {

    private static final String TAG = ReportEvent.class.getSimpleName();
    private static final String GET_REPORTS = "https://tam.pulsr.xyz/reports";
    private static final String POST_REPORT = "https://tam.pulsr.xyz/report";
    private static final String CONFIRM_REPORT = "https://tam.pulsr.xyz/confirm";

    private ArrayList<Report> reportList;
    private Context context;

    public ReportEvent(Context context) {
        this.reportList = new ArrayList<>();
        this.context = context;
    }

    public void getReports() {
        JsonArrayReq req = new JsonArrayReq(Request.Method.GET,
                GET_REPORTS, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        setReport(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: " + error.getMessage());
                    }
        });
        VolleyApp.getInstance(context).addToRequestQueue(req);
    }

    // Adapt this method for android with Voley or whatever
    public void sendReport(final Context context, final Report report) {
        Uri.Builder url = Uri.parse(POST_REPORT).buildUpon();
        url.appendQueryParameter("stop", report.getStop().getID() + "");
        url.appendQueryParameter("type", report.getType().getValue() + "");
        url.appendQueryParameter("message", report.getMessage());

        NetworkResponseRequest req = new NetworkResponseRequest(
                Request.Method.POST,
                url.toString(),
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Toast.makeText(context, context.getResources().getText(R.string.request_sucessful), Toast.LENGTH_SHORT).show();
                        getReports();
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

    public void confirmReport(final Context context, final int reportId) {
        Uri.Builder url = Uri.parse(CONFIRM_REPORT).buildUpon();
        url.appendQueryParameter("id", reportId + "");

        NetworkResponseRequest sr = new NetworkResponseRequest(
                Request.Method.POST,
                url.toString(),
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Toast.makeText(context, context.getResources().getText(R.string.request_sucessful), Toast.LENGTH_SHORT).show();
                        getReports();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.statusCode == 403) {
                            Toast.makeText(context, context.getResources().getText(R.string.request_already_confirmed), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, context.getResources().getText(R.string.request_unsucessful), Toast.LENGTH_SHORT).show();
                        }
                    }
                }) {
        };
        VolleyApp.getInstance(context).addToRequestQueue(sr);
    }

    public void setReport(JSONArray reportJson) {
        for (Report r : this.reportList) {
            r.removeFromStop();
        }
        this.reportList = new ArrayList<>();

        try {

            for (int i=0; i< reportJson.length(); i++) {
                JSONObject reportObjectJson = reportJson.getJSONObject(i);
                String msg = reportObjectJson.optString("message");
                int confirm = reportObjectJson.getInt("confirm");
                int reportId = reportObjectJson.getInt("id");

                Calendar date = Calendar.getInstance();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    date.setTime(sdf.parse(reportObjectJson.getString("date")));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                StopZone stop = Data.getData().getMap().getStopZoneById(reportObjectJson.getInt("stop"));

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
