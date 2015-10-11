package flying.grub.tamtime.data;

import java.util.Calendar;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Report {
    private Stop stop;
    private ReportType type;
    private String message;
    private Calendar date;
    private static final String SEND_REPORT = "http://tam.flyingrub.me/report.php?r=newReport";

    private Report(Stop stop, ReportType type, String message, Calendar date) {
        this.stop = stop;
        this.type = type;
        if (message != null) this.message = message;
        this.date = date;
        stop.addReport(this);
    }

    public static Report factory(DataParser data, JSONObject jsonReport) {
        String msg = jsonReport.has("msg") ? jsonReport.getString("msg") : null;

        Calendar date = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date.setTime(sdf.parse(jsonReport.getString("date")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Stop stop = data.getStopByOurId(jsonReport.getInt("our_id"));

        if (stop == null) return null;

        return new Report(stop, numType(jsonReport.getInt("type")), msg, date);
    }

    // Adapt this method for android with Voley or whatever
    public static String sendPost(Stop stop, ReportType type, String message) {
        String res;
        String ourId = stop.getOurId() + "";
        String typeNum = type.getValue() + "";

        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(SEND_REPORT);

        // Request parameters and other properties.
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("our_id", ourId));
        params.add(new BasicNameValuePair("type", typeNum));
        params.add(new BasicNameValuePair("msg", message));

        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            res = EntityUtils.toString(entity);
        } catch (Exception e) {
            e.printStackTrace();
            res = null;
        }

        return res;
    }

    public static ReportType numType(int num) {
        ReportType res;
        switch (num) {
            case 1:
                res = ReportType.AUTRE;
                break;
            case 2:
                res = ReportType.CONTROLE_Q;
                break;
            case 3:
                res = ReportType.CONTROLE_T;
                break;
            case 4:
                res = ReportType.INCIDENT;
                break;
            case 5:
                res = ReportType.RETARD;
                break;
            default:
                res = null;
                break;
        }
        return res;
    }

    // Add
    public void removeFromStop() {
        this.stop.removeReport(this);
    }

    // Get
    public ReportType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public boolean equals(Object o) {
        return (((Report)o).stop == this.stop && ((Report)o).type == this.type && ((Report)o).message.equals(this.message) && ((Report)o).date.equals(this.date));
    }

    public boolean isValid(Calendar now) {
        return this.date.compareTo(now) > -5400000;
    }
