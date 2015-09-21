package flying.grub.tamtime.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;

import flying.grub.tamtime.data.Stop;

public class StopTimes {
    private Stop stop;
    private Route route;
    private int stopId;
    private ArrayList<Calendar> timesList; // Horraires théorique
    private ArrayList<Integer> realTimesList; // Horraires réele

    public StopTimes(Route route, Stop stop, int stopId) {
        this.route = route;
        this.stop = stop;
        this.stopId = stopId;
        this.timesList = new ArrayList<Calendar>();
        this.realTimesList = new ArrayList<Integer>();
        stop.addStpTim(this);
    }

    public StopTimes(Route route, Stop stop) {
        this(route, stop, 0);
    }

    // end dit être un StopTimes situé après this sur la même route
    public int howManyTimesTo(StopTimes end) {
        int res = this.timesList.get(1).compareTo(end.timesList.get(1));
        return res / 1000 / 60;
    }

    public void resetRealTimes() {
        this.realTimesList.clear();
    }

    // Add
    private void addTime(String time) throws Exception { // String to Calendar
        Calendar res = Calendar.getInstance();

        if (time.contains("*")) time.replace("*", ""); // Handle *

        String[] splt = time.split(" ");

        if (!splt[2].equals("|")) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
            res.setTime(sdf.parse(splt[0] + " " + splt[2]));
            res.add(Calendar.DAY_OF_MONTH, Integer.parseInt(splt[1]));
        } else res = null;

        this.timesList.add(res);
    }

    public void addRealTime(int time){
        if (time >= 1440) time -= 1440;
        this.realTimesList.add(time);
        Collections.sort(this.realTimesList);
    }

    public void setTheoTimes(JSONArray timesJson, String date) throws Exception {
        this.timesList.clear(); // Vide les times dèjà set si il y a

        for (int i=0; i<timesJson.length(); i++) {
            this.addTime(date + " " + timesJson.getString(i));
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
            return "-";
        }
    }

    public String getTimes(ArrayList<Integer> intab) {
        String time, res = " ";
        int min, hour;

        for (Integer i : intab) {
            if (i != null) {
                res += toTimeString(i) + " - ";
            } else res += "|";
        }
        return res;
    }
    //Return les nbr prochain passage du tram/bus dans un ArrayList de string avec une * pour les horraires théorique
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

    // Return un ArrayList d'Integer avec un null pour séparer les réel des théorique
    public ArrayList<Integer> getNextTimes(int nbr) {
        ArrayList<Integer> res = new ArrayList<Integer>();// Gros nbr gros tableau
        int i=0;

        while (i<nbr && i<this.realTimesList.size()) {
            res.add(this.realTimesList.get(i));
            i++;
        }
        res.add(null);
        // Arranger bien les times (ordre)
        if (i<nbr) {
            i++;
            this.getNextTheoricTimes(nbr-i, i, res);
        }

        return res;
    }

    // Work with getNextTimes
    private void getNextTheoricTimes(int nbr, int jumpN, ArrayList<Integer> res) {
        Calendar curntDate = Calendar.getInstance();
        int i=0, j=0;
        int inMsec;

        while (i<this.timesList.size() && j<nbr) {
            if (curntDate.before(this.timesList.get(i))) {
                inMsec = (int)(this.timesList.get(i+jumpN).getTimeInMillis() - curntDate.getTimeInMillis());
                res.add(inMsec/1000); //On ajoute le temps restant en seconde
                j++;
            }
            i++;
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

    public boolean verify(String stopName, String direction) { // Return true si la direction et le nom du stop sont juste
        if (this.stop.getName().equals(stopName) && this.route.getDirection().equals(direction)) {
            return true;
        } else return false;
    }

    //Test & Bullshit
    public String toString() {
        return this.route.getLine().getLineId() + " / " + this.route.getDirection() + " / " + this.stop.getName() + " / " + this.stopId;
    }

    public String getStringTimes() {
        String res = "\n";
        for (Calendar cal : this.timesList) {
            res += "        " + cal.toString() + "\n";
        }
        return res;
    }

    public void displayAllCalendar() {
        for (Calendar cal : this.timesList) {
            if (cal != null) {
                System.out.println(cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + " -- " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE));
            } else System.out.println("---");
        }
    }

}
