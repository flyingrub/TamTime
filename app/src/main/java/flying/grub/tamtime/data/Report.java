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
        return "Postée il y a " + StopTimes.toTimeString(inSec);
    }

    public String getMessage() {
        return message;
    }

    public Stop getStop() {
        return stop;
    }

    public boolean equals(Object o) {
        return (((Report) o).stop == this.stop && ((Report) o).type == this.type && ((Report) o).message.equals(this.message) && ((Report) o).date.equals(this.date));
    }

    public boolean isValid(Calendar now) {
        return this.date.compareTo(now) > -5400000;
    }
}
