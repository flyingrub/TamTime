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
    private int confirm;
    private int reportId;
    private static final String CONFIRM_REPORT = "http://tam.flyingrub.me/report.php?r=confirmReport";


    public Report(Stop stop, ReportType type, String message, Calendar date, int confirm, int reportId) {
        this.stop = stop;
        this.type = type;
        this.message = message;
        this.date = date;
        this.confirm = confirm;
        this.reportId = reportId;
        stop.addReport(this);
    }

    public String sendConfirm() {
        String res;

        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(CONFIRM_REPORT);

        // Request parameters and other properties.
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("report_id", this.reportId + ""));

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

    // Add
    public void removeFromStop() {
        this.stop.removeReport(this);
    }

    // Get
    public String getTime() {
        String timeStr;
        Calendar now = Calendar.getInstance();
        int min = (int)((now.getTimeInMillis() - this.date.getTimeInMillis()) / 60000);

        if (min >= 180) return "+ de 3h";

        if (min >= 60) {
            int hour = min /60;
            min = min % 60;
            timeStr = hour + "h" + min + "min";
        } else {
            timeStr = min + "min";
        }
        return timeStr;
    }

    public int getConfirm() {
        return this.confirm;
    }

    public ReportType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Stop getStop() {
        return stop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Report report = (Report) o;

        if (stop != null ? !stop.equals(report.stop) : report.stop != null) return false;
        if (type != report.type) return false;
        if (message != null ? !message.equals(report.message) : report.message != null)
            return false;
        return !(date != null ? !date.equals(report.date) : report.date != null);

    }

    @Override
    public int hashCode() {
        int result = stop != null ? stop.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    public boolean isValid(Calendar now) {
        return this.date.compareTo(now) > -5400000;
    }

    @Override
    public String toString() {
        return "Report{" +
                "message='" + message + '\'' +
                ", type=" + type +
                ", tps=" + getTime() +
                '}';
    }
}
