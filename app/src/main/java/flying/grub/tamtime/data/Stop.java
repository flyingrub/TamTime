package flying.grub.tamtime.data;

import java.util.ArrayList;


public class Stop {
    private String name;
    private ArrayList<Line> linesList;

    private ArrayList<StopTimes> stopTimeList;

    public Stop(String name, Line line){
        this.linesList = new ArrayList<>();
        this.stopTimeList = new ArrayList<>();
        this.name = name;
        this.linesList.add(line);
    }

    // Get
    public String getName() {
        return name;
    }

    public ArrayList<Line> getLines() {
        return this.linesList;
    }

    public ArrayList<StopTimes> getStopTimeForLine(int lineId) {
        ArrayList<StopTimes> res = new ArrayList<>();
        for (StopTimes s : stopTimeList) {
            int stopLineId = s.getRoute().getLine().getLineId();
            if (stopLineId == lineId) {
                res.add(s);
            }
        }
        return res;
    }

    // Add
    public void addLine(Line line){
        if (!this.linesList.contains(line)) this.linesList.add(line);
    }

    public void addStopTime(StopTimes stpTim) {
        this.stopTimeList.add(stpTim);
    }

    // Test & Bullshit
    public String toString() {
        String res = this.name + "\n";
        for (Line line : linesList) {
            res += "--- " + line + "\n";
        }
        return res;
    }

}
