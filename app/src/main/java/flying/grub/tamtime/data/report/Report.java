package flying.grub.tamtime.data.report;

import java.util.Calendar;

import flying.grub.tamtime.data.map.StopZone;

public class Report {

    private StopZone stop;
    private ReportType type;
    private String message;
    private Calendar date;
    private int confirm;
    private int reportId;

    public Report(StopZone stop, ReportType type, String message, Calendar date, int confirm, int reportId) {
        this.stop = stop;
        this.type = type;
        this.message = message;
        this.date = date;
        this.confirm = confirm;
        this.reportId = reportId;
        stop.addReport(this);
    }

    public Report(StopZone stop, ReportType type, String message) {
        this.stop = stop;
        this.type = type;
        this.message = message;
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

    public int getReportId() {
        return reportId;
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

    public StopZone getStop() {
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
