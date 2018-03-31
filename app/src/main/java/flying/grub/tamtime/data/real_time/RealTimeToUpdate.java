package flying.grub.tamtime.data.real_time;

import java.util.ArrayList;

import flying.grub.tamtime.data.map.Line;
import flying.grub.tamtime.data.map.Stop;

public class RealTimeToUpdate {
    private boolean isLine;
    private Line line;
    private ArrayList<Stop> stops;


    public boolean isLine() {
        return isLine;
    }

    public Line getLine() {
        return line;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }


    public RealTimeToUpdate(ArrayList<Stop> stops) {
        this.stops = stops;
        isLine = false;
    }

    public RealTimeToUpdate(Line line) {
        this.line = line;
        isLine = true;
    }
}
