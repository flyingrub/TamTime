package flying.grub.tamtime.data;

import android.location.Location;

import java.util.ArrayList;


public class Stop {
    private int ourId;
    private String name;
    private ArrayList<Line> linesList;

    public Location location;
    public float distanceFromUser;

    private ArrayList<StopTimes> stpTimList;


    public Stop(String name, int ourId, double lat, double lon){
        this.linesList = new ArrayList<>();
        this.stpTimList = new ArrayList<>();
        this.name = name;
        this.ourId = ourId;
        this.location = new Location(name);
        this.location.setLongitude(lon);
        this.location.setLatitude(lat);
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

    public ArrayList<StopTimes> getStopTimeForLine(String lineId) {
        ArrayList<StopTimes> res = new ArrayList<>();
        for (StopTimes s : stpTimList) {

            if (s.getRoute().getLine().getLineId().equals(lineId)) {
                res.add(s);
            }
        }
        return res;
    }

    public ArrayList<StopTimes> getStpTimList() {
        return stpTimList;
    }

    // Add
    public void addLine(Line line){
        if (!this.linesList.contains(line)) this.linesList.add(line);
    }

    public void addStpTim(StopTimes stpTim) {
        this.stpTimList.add(stpTim);
    }

    public float distanceToStop(Stop dest) {
        return this.location.distanceTo(dest.location);
    }

    public void calcDistanceFromUser(Location user) {
        distanceFromUser = user.distanceTo(this.location);
    }

    public float getDistanceFromUser() {
        return distanceFromUser;
    }

    @Override
    public String toString() {
        return "Stop{" +
                "ourId=" + ourId +
                ", name='" + name + '\'' +
                '}';
    }
}
