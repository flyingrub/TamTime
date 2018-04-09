package flying.grub.tamtime.data;

import android.content.Context;

import flying.grub.tamtime.data.dirsruption.DisruptEventHandler;
import flying.grub.tamtime.data.mark.MarkEvent;
import flying.grub.tamtime.data.real_time.RealTimeToUpdate;
import flying.grub.tamtime.data.real_time.RealTimes;
import flying.grub.tamtime.data.report.ReportEvent;
import flying.grub.tamtime.data.map.TamMap;

public class Data {

    private static final String TAG = Data.class.getSimpleName();

    private DisruptEventHandler disruptEventHandler;
    private ReportEvent reportEvent;
    private RealTimes realTimes;
    private TamMap map;
    private RealTimeToUpdate toUpdate;

    /*
    School project
     */

    private MarkEvent markEvent;

    private static Data data;

    private Data() {
        data = this;
    }

    public void init(Context context) {
        reportEvent = new ReportEvent(context);
        disruptEventHandler = new DisruptEventHandler(context);
        realTimes = new RealTimes(context);
        map = new TamMap(context);

        markEvent = new MarkEvent(context);
    }

    public static synchronized Data getData() {
        if (data == null) {
            return new Data();
        } else {
            return data;
        }
    }

    public void update() {
        if (toUpdate != null) {
            if (toUpdate.isLine()) {
                realTimes.updateLine(toUpdate.getLine());
            } else {
                realTimes.updateStops(toUpdate.getStops());
            }
        }
        reportEvent.getReports();
        markEvent.getMarks();
        //disruptEventHandler.getReports();
    }

    public DisruptEventHandler getDisruptEventHandler() {
        return disruptEventHandler;
    }

    public TamMap getMap() {
        return map;
    }

    public void setToUpdate(RealTimeToUpdate toUpdate) {
        this.toUpdate = toUpdate;
        update();
    }

    public RealTimes getRealTimes() {
        return realTimes;
    }

    public ReportEvent getReportEvent() {
        return reportEvent;
    }

    public MarkEvent getMarkEvent()
    {
        return markEvent;
    }
}
