package flying.grub.tamtime.data.map;

import android.location.Location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import flying.grub.tamtime.data.report.Report;
import flying.grub.tamtime.data.weather.Weather;

public class StopZone {
    private int _id;
    private int cityway_id;
    private int tam_id;
    private String name;
    private String search_name;
    private ArrayList<Line> lines;
    private ArrayList<Stop> stops;

    private Location location;
    private ArrayList<Report> reportList;
    private float distanceFromUser;

    /*
    School project
     */
    private double mark;
    private Weather weather;

    public StopZone(String name, int _id, int tam_id, int cityway_id, String search_name, double lat, double lon){
        this.lines = new ArrayList<>();
        this.reportList = new ArrayList<>();
        this.name = name;
        this.cityway_id = cityway_id;
        this._id = _id;
        this.tam_id = tam_id;
        this.search_name = search_name;
        this.location = new Location(name);
        this.location.setLongitude(lon);
        this.location.setLatitude(lat);
        this.stops = new ArrayList<>();

        this.mark = -1;
        weather = null;
    }

    public void addLine(Line line) {
        if (!this.lines.contains(line)) {
            this.lines.add(line);
        }
    }

    public void addStop(Stop stop) {
        this.stops.add(stop);
    }

    public String getName() {
        return name;
    }

    public ArrayList<Line> getLines() {
        return this.lines;
    }

    public int getID() {
        return this._id;
    }

    public void addReport(Report report) {
        this.reportList.add(report);
    }

    public ArrayList<Report> getReports() {
        ArrayList<Report> res = new ArrayList<>();
        Calendar date = Calendar.getInstance();
        for (Report r : this.reportList) {
            if (r.isValid(date)) res.add(r);
        }
        Collections.reverse(res); // the last is the oldest now.
        return res;
    }

    public void removeReport(Report rep) {
        this.reportList.remove(rep);
    }

    public void calcDistanceFromUser(Location user) {
        distanceFromUser = user.distanceTo(this.location);
    }

    public String getNormalisedName() {
        return search_name;
    }

    public float getDistanceFromUser() {
        return distanceFromUser;
    }

    public ArrayList<Stop> getStops(Line line) {
        ArrayList<Stop> res = new ArrayList<>();
        for (Stop stop : stops) {
            if (stop.getDirection().getLine().getCityway_id() == line.getCityway_id()) {
                res.add(stop);
            }
        }
        return res;
    }

    public Location getLocation(){
        return location;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }

    public double getMark()
    {
        return mark;
    }

    public void setMark(double mark)
    {
        this.mark = mark;
    }

    public void setWeather(Weather weather)
    {
        this.weather = weather;
    }

    public Weather getWeather()
    {
        return weather;
    }

    @Override
    public String toString() {
        return "StopZone{" +
                "name='" + name + '\'' +
                ", lines=" + lines +
                ", stops=" + stops +
                '}';
    }
}
