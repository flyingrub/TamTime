package flying.grub.tamtime.data;


import java.util.ArrayList;

public class Route {
    private Line line;
    private String direction;
    private ArrayList<StopTimes> stpTimes;

    public Route(String direction, Line line) {
        this.stpTimes = new ArrayList<StopTimes>();
        this.line = line;
        this.direction = direction;
    }

    // Add
    public void addStopTimes(StopTimes lsr) {
        this.stpTimes.add(lsr);
    }

    // Get
    public ArrayList<Stop> getStops() {
        ArrayList<Stop> stopList = new ArrayList<Stop>();
        for (StopTimes lsr : this.stpTimes) {
            stopList.add(lsr.getStop());
        }
        return stopList;
    }

    public int getStopNum(Stop stop) {
        for (int i=0; i<this.stpTimes.size(); i++) {
            Stop curntStop = this.stpTimes.get(i).getStop();
            if (stop == curntStop) return i;
        }
        return -1;
    }

    public Stop getStopByNum(int num){
        if (num <= this.stpTimes.size()) {
            return this.stpTimes.get(num).getStop();
        }
        return null;
    }

    public ArrayList<StopTimes> getStpTimes() {
        return this.stpTimes;
    }

    public Line getLine() {
        return this.line;
    }

    public String getDirection() {
        return this.direction;
    }

    // Test & Bullshit
    public String toString() {
        return "---> " + this.direction + "\n";
    }
}
