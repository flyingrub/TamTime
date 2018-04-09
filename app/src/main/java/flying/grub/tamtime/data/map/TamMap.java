package flying.grub.tamtime.data.map;

import android.content.Context;
import android.location.Location;

import java.text.Normalizer;
import java.util.ArrayList;

import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.real_time.Time;


public class TamMap {

    private static final String TAG = TamMap.class.getSimpleName();
    private ArrayList<Line> lines;
    private ArrayList<StopZone> stopZones;
    private ArrayList<Stop> stops;
    private TamMapDatabaseHelper dbHelper;

    private Context context;

    public TamMap(Context context) {
        this.stopZones = new ArrayList<>();
        this.stops = new ArrayList<>();
        this.context = context;
        dbHelper = TamMapDatabaseHelper.getDB(context);
        this.lines = dbHelper.getLines();
        setStopZones();
    }

    private void setStopZones() {
        for (Line line : lines) {
            for (Direction direction : line.getDirections()) {
                for (Stop stop : direction.getStops()) {
                    stops.add(stop);
                    boolean exists = false;
                    for (int i = 0; i < stopZones.size(); i++) {
                        if (stopZones.get(i).getID() == stop.getStopZone().getID()) {
                            stopZones.get(i).addStop(stop);
                            stopZones.get(i).addLine(line);
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        stopZones.add(stop.getStopZone());
                    }
                }
            }
        }
    }

    public StopZone getStopByName(String name) {
        for (StopZone stp : this.stopZones) {
            if (stp.getName().equals(name)) return stp;
        }
        return null;
    }

    // SETTER
    public void setAlldistance(Location user) {
        for (StopZone stop : stopZones) {
            stop.calcDistanceFromUser(user);
        }
    }

    // GET
    public Line getLine(int i){
        if (lines != null) return lines.get(i);
        return null;
    }

    public Line getLineByNum(int num) {
        for (Line l : this.lines) {
            if (l.getLineNum() == num) return l;
        }
        return null;
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public ArrayList<StopZone> getStopZones() {
        return stopZones;
    }

    public StopZone getStopZoneById(int id) {
        for (StopZone stp : this.stopZones) {
            if (stp.getID() == id) return stp;
        }
        return null;
    }

    public ArrayList<StopZone> getAllNearStops() {
        ArrayList<StopZone> res = new ArrayList<>();
        for (StopZone s : stopZones) {
            if (s.getDistanceFromUser() <= 500) { // in meter
                res.add(s);
            }
        }
        return res;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }

    public void addTimeToStop(int tam_id, int cityway_id, ArrayList<Time> times) {
        if (tam_id == -1) {
            addTimeToStopByCitywayId(cityway_id, times);
        } else {
            addTimeToStopByTamId(tam_id, times);
        }
    }

    private void addTimeToStopByTamId(int id, ArrayList<Time> times) {
        for (int i = 0; i < Data.getData().getMap().getStops().size(); i++) {
            if (stops.get(i).getTam_id() == id) {
                stops.get(i).setTimes(times);
                return;
            }
        }
    }

    private void addTimeToStopByCitywayId(int id, ArrayList<Time> times) {
        for (int i = 0; i < Data.getData().getMap().getStops().size(); i++) {
            if (stops.get(i).getCityway_id() == id) {
                stops.get(i).setTimes(times);
                return;
            }
        }
    }

    public ArrayList<StopZone> searchInStops(String search, int number) {
        ArrayList<StopZone> res = new ArrayList<>();
        String normalizedSearch = normalize(search);
        for (StopZone s : stopZones) {
            if (number != -1 && number == res.size()) {
                break;
            }
            if (s.getNormalisedName().contains(normalizedSearch)) {
                res.add(s);
            }
        }
        return res;
    }

    public String normalize(String s) {
        String normalized = Normalizer.normalize(s, Normalizer.Form.NFD);
        return normalized.replaceAll("[^\\p{ASCII}]", "").toLowerCase().replaceAll("[^A-Za-z0-9]", "");
    }

    public ArrayList<StopZone> getAllReportStop() {
        ArrayList<StopZone> res = new ArrayList<>();
        for (StopZone s : stopZones) {
            if (s.getReports().size() > 0) {
                res.add(s);
            }
        }
        return res;
    }
}
