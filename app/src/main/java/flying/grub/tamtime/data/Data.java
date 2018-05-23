package flying.grub.tamtime.data;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import flying.grub.tamtime.data.dirsruption.DisruptEventHandler;
import flying.grub.tamtime.data.mark.MarkEvent;
import flying.grub.tamtime.data.real_time.RealTimeToUpdate;
import flying.grub.tamtime.data.real_time.RealTimes;
import flying.grub.tamtime.data.report.ReportEvent;
import flying.grub.tamtime.data.map.TamMap;
import flying.grub.tamtime.data.stopzone_location.StopZoneLocationListener;
import flying.grub.tamtime.data.weather.Weather;
import flying.grub.tamtime.data.weather.WeatherEvent;

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
    private WeatherEvent weatherEvent;

    private LocationManager locationManager;
    private StopZoneLocationListener stopZoneLocationListener;

    private static final int LOCATION_INTERVAL = 10000;
    private static final float LOCATION_DISTANCE = 10f;

    private static Data data;

    private Data() {
        data = this;
    }

    @SuppressLint("MissingPermission")
    public void init(Context context) {
        reportEvent = new ReportEvent(context);
        disruptEventHandler = new DisruptEventHandler(context);
        realTimes = new RealTimes(context);
        map = new TamMap(context);

        markEvent = new MarkEvent(context);
        weatherEvent = new WeatherEvent(context);

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        stopZoneLocationListener = new StopZoneLocationListener(context);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, stopZoneLocationListener);
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

    public WeatherEvent getWeatherEvent() {
        return weatherEvent;
    }
}
