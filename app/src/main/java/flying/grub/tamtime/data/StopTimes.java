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
    private int ourId;
    private OrderedTimes timesList; // Theoric Times
    private ArrayList<Integer> realTimesList; // Real Times

    public StopTimes(Route route, Stop stop, Line line, int stopId) {
        this.route = route;
        this.stop = stop;
        this.line = line;
        this.stopId = stopId;
        this.ourId = Integer.parseInt("" + this.line.getLineNum() + this.route.getDirNum() + this.stop.getOurId());
        this.realTimesList = new ArrayList<Integer>();
        stop.addStpTim(this);
    }

    public StopTimes(Route route, Stop stop, Line line) {
        this(route, stop, line, 0);
    }


    public void resetRealTimes() {
        this.realTimesList = new ArrayList<Integer>();
    }

    // Add
    public void addRealTime(int time){
        if (time >= 86400) time -= 86400;
        this.realTimesList.add(time);
        Collections.sort(this.realTimesList);
    }

    public void addTheoTimes(JSONArray timesJson, String date, int jumpDay) throws Exception {
        if (this.timesList != null) {
            this.timesList.addToEnd(OrderedTimes.getOrderedTimesFromJson(date, jumpDay, timesJson));
        } else {
            this.timesList = OrderedTimes.getOrderedTimesFromJson(date, jumpDay, timesJson);
        }
    }

    public void resetTheoTimes() {
        this.timesList = null;
    }

    // Get
    public static String toTimeString(int timeInt) {
        String timeStr;
        int min = (timeInt / 60);

        if (timeInt >= 10800) return "+ de 3h";

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
        return this.ourId;
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
        while (res.size() < nbr) res.add("-");
        return res;
    }

    // Return an Integer ArrayLIst with null to separate real & theoric times
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
        Calendar now = Calendar.getInstance();
        int i=0, j=0;
        int inMsec;

        OrderedTimes oT;
        if (this.timesList != null) oT = this.timesList.getFirstValidDate();
        else oT = null;

        i=0;
        while (i<jumpN && oT != null) {
            oT = oT.getNext();
            i++;
        }

        i=0;
        while (i<nbr && oT != null) {
            inMsec = (int)(oT.getDate().getTimeInMillis() - now.getTimeInMillis());
            res.add(inMsec/1000); // Add times left in second
            oT = oT.getNext();
            i++;
        }
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
