package flying.grub.tamtime.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;

public class StopTimes {
    private Stop stop;
    private Route route;
    private Line line;
    private int stopId;
    private ArrayList<Calendar> timesList; // Real Times
    private ArrayList<Integer> realTimesList; // Theoric Times

    public StopTimes(Route route, Stop stop, Line line, int stopId) {
        this.route = route;
        this.stop = stop;
        this.line = line;
        this.stopId = stopId;
        this.timesList = new ArrayList<Calendar>();
        this.realTimesList = new ArrayList<Integer>();
        stop.addStpTim(this);
    }

    public StopTimes(Route route, Stop stop, Line line) {
        this(route, stop, line, 0);
    }

    // end must be on the same route as this
    public int howManyTimesTo(StopTimes end) {
        int res = this.timesList.get(1).compareTo(end.timesList.get(1));
        return res / 1000 / 60;
    }

    // Return true if line/direction/stopId correspond to this
    public boolean isTheOne(String line, String direction, int stopId) {
        int linum = line.contains("L") ? Integer.parseInt(line.replace("L", "")) : Integer.parseInt(line);
        if (linum == this.line.getLineNum() && this.route.getDirection().toLowerCase().contains(direction.toLowerCase()) && this.stopId == stopId) {
            return true;
        }
        return false;
    }

    public void resetRealTimes() {
        this.realTimesList.clear();
    }

    // Add
    private void addTheoTime(String time, String date, int day) throws Exception {
        Calendar res = Calendar.getInstance();

        if (time.contains("*")) time.replace("*", ""); // Handle * (This line may need reservation)

        if (!time.equals("|")) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
            res.setTime(sdf.parse(date + " " + time));
            res.add(Calendar.DAY_OF_MONTH, day);
        } else res = null; // | or null means that the tram/bus don't stop this time

        this.timesList.add(res);
    }

    public void addRealTime(int time){
        if (time >= 86400) time -= 86400;
        this.realTimesList.add(time);
        Collections.sort(this.realTimesList);
    }

    public void setTheoTimes(JSONArray timesJson, String date) throws Exception {
        this.timesList.clear(); // reset the theoric times List
        JSONArray curnTab;

        for (int i=0; i<timesJson.length(); i++) {
            curnTab = timesJson.getJSONArray(i);

            for (int j=0; j<curnTab.length(); j++) {
                this.addTheoTime(curnTab.getString(j), date, i);
            }
            
        }
    }

    // Get
    public static String toTimeString(int timeInt) {
        String timeStr;
        int min = (timeInt / 60);
        if (min >= 60) {
            int hour = min /60;
            min = min % 60;
            timeStr = hour + "h" + min + "min";
        } else if (min < 0 ) {
            timeStr = "A quai";
        } else if (min == 0 ) {
            timeStr = "Proche";
        } else {
            timeStr = min + "min";
        }
        return timeStr;
    }

    public String getTimes(int i) {
        try {
            return toTimeString(this.realTimesList.get(i));
        } catch (IndexOutOfBoundsException e) {
            return "---";
        }
    }

    public int getOurId() {
        return Integer.parseInt("" + this.line.getLineNum() + this.route.getDirNum() + this.stop.getOurId());
    }

    //Return the 'nbr' next passage of the tram/bus as an String ArrayList with an * for theoric times
    public ArrayList<String> getStrNextTimes(int nbr) {
        ArrayList<String> res = new ArrayList<>();
        ArrayList<Integer> times = this.getNextTimes(nbr);
        boolean theo = false;
        for (Integer t : times) {
            if (t == null) {
                theo = true;
            } else {
                if (!theo) {
                    res.add(toTimeString(t));
                } else {
                    res.add(toTimeString(t) + "*");
                }
            }


        }
        return res;
    }

    // Return an nteger ArrayLIst with null to separate real & theoric times
    public ArrayList<Integer> getNextTimes(int nbr) {
        ArrayList<Integer> res = new ArrayList<Integer>();
        int i=0;

        while (i<nbr && i<this.realTimesList.size()) {
            res.add(this.realTimesList.get(i));
            i++;
        }
        res.add(null);

        if (i<nbr) {
            i++;
            this.getNextTheoricTimes(nbr-i, i, res);
        }
        return res;
    }

        // Work with getNextTimes (theoric part)
    private void getNextTheoricTimes(int nbr, int jumpN, ArrayList<Integer> res) {
        Calendar curntDate = Calendar.getInstance();
        int i=0, j=0;
        int inMsec;

        while (i<this.timesList.size() && !curntDate.before(this.timesList.get(i))) {
            i++;
        }

        int k = i+jumpN;

        while (k<this.timesList.size() && j<=nbr) {
            if (this.timesList.get(k) != null) {
                inMsec = (int)(this.timesList.get(k).getTimeInMillis() - curntDate.getTimeInMillis());
                res.add(inMsec/1000); // Add times left in second       
                j++;
            }
            k++;
        }
    }

    public ArrayList<String> getAllRealTimes() {
        ArrayList<String> res = new ArrayList<String>();
        for (int i=0; i<this.realTimesList.size(); i++) {
            res.add(this.getTimes(1));
        }
        return res;
    }

    public int getStopId() {
        return this.stopId;
    }

    public Stop getStop() {
        return this.stop;
    }

    public Route getRoute() {
        return this.route;
    }

    public Line getLine() {
        return  this.line;
    }
}
