package flying.grub.tamtime.data;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;


public class Stop {
    private int ourId;
    private String name;
    private ArrayList<Line> linesList;
    private ArrayList<StopTimes> stpTimList;
    private static ArrayList<String[]> idToName = new ArrayList<>();
    private double lat;
    private double lon;

    public Stop(String name, int ourId, double lat, double lon){
        this.linesList = new ArrayList<Line>();
        this.stpTimList = new ArrayList<StopTimes>();
        this.name = name;
        this.ourId = ourId;
        this.lat = lat;
        this.lon = lon;
    }

    // Get
    public String getName() {
        return name;
    }

    public ArrayList<Line> getLines() {
        return this.linesList;
    }

    public int getOurId() {
        return this.ourId;
    }

    // Add
    public void addLine(Line line){
        if (!this.linesList.contains(line)) this.linesList.add(line);
    }

    public void addStpTim(StopTimes stpTim) {
        this.stpTimList.add(stpTim);
    }
}
