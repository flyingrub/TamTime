package flying.grub.tamtime.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import org.json.JSONArray;

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
        this.timesList = new ArrayList<>();
        this.realTimesList = new ArrayList<>();
        this.stop.addStopTime(this);
    }

    public StopTimes(Route route, Stop stop) {
        this(route, stop, 0);
    }

    // Add
    public void addTime(String time) throws Exception { // String to Calendar
        Calendar res = Calendar.getInstance();

        if (time.contains("*")) time.replace("*", ""); // Handle *

        if (!time.equals("|")) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
            res.setTime(sdf.parse(time));
        } else res = null;

        this.timesList.add(res);
    }

    public void addRealTime(int time){
        this.realTimesList.add(time);
        Collections.sort(this.realTimesList);
    }

    public void setTheoricalTimes(JSONArray timesJson) throws Exception {
        this.timesList.clear(); // Vide les times dèjà set si il y a

        for (int i=0; i<timesJson.length(); i++) {
            this.addTime(timesJson.getString(i));
        }
    }

    // Get
    public String getTimes(int i) {
        try {
            return parseTimeInt(this.realTimesList.get(i), false);
        } catch (IndexOutOfBoundsException e) {
            return "-";
        }
    }

    public String parseTimeInt(int timeInt, boolean isTheoritical) {
        String time;
        int min = (timeInt / 60);
        if (min >= 60) {
            int hour = min /60;
            min = min % 60;
            time = hour + "h " + min + "min";
        } else if (min < 0 ) {
            time = "A quai";
        } else if (min == 0 ) {
            time = "Proche";
        } else {
            time = min + "min";
        }
        if (isTheoritical) {
            time += "*";
        }
        return time;
    }

    // Return un ArrayList de string
    public ArrayList<String> getNextTimeString(int nbr) {
        ArrayList<String> res = new ArrayList<>();// Gros nbr gros tableau
        int i=0;

        while (i<nbr && i<this.realTimesList.size()) {
            res.add(parseTimeInt(this.realTimesList.get(i), false));
            i++;
        }
        res.add(null);
        // Arranger bien les times (ordre)
        if (i<nbr) {
            i++;
            for (Integer integer : this.getNextTheoricTimes(nbr - i, i)) {
                res.add(parseTimeInt(integer, true));
            }
        }

        return res;
    }

    // Return un ArrayList d'Integer avec un null pour séparer les réel des théorique
    public ArrayList<Integer> getNextTimes(int nbr) {
        ArrayList<Integer> res = new ArrayList<>();
        int i=0;

        while (i<nbr && i<this.realTimesList.size()) {
            res.add(this.realTimesList.get(i));
            i++;
        }
        res.add(null);
        // Arranger bien les times (ordre)
        if (i<nbr) {
            i++;
            res.addAll(this.getNextTheoricTimes(nbr-i, i));
        }

        return res;
    }

    // Work with getNextTimes
    private ArrayList<Integer> getNextTheoricTimes(int nbr, int jumpN) {
        ArrayList<Integer> res = new ArrayList<>();
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
        return res;
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
