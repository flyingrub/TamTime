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

    public Report(Stop stop, ReportType type, String message, Calendar date) {
        this.stop = stop;
        this.type = type;
        this.message = message;
        this.date = date;
        stop.addReport(this);
    }

    public Report(Stop stop, ReportType type, String message) {
        this.stop = stop;
        this.type = type;
        this.message = message;
    }

    // Add
    public void removeFromStop() {
        this.stop.removeReport(this);
    }

    // Get
    public ReportType getType() {
        return type;
    }

    public String getTime() {
        Calendar now = Calendar.getInstance();
        int inSec = now.compareTo(this.date);
        return Utils.toTimeString(inSec);
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
