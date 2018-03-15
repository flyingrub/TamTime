package flying.grub.tamtime.data.real_time;

import java.util.ArrayList;

import flying.grub.tamtime.data.map.Line;
import flying.grub.tamtime.data.map.Stop;

/**
 * Created by fly on 3/15/18.
 */

public class RealTimeToUpdate {
    private boolean isLine;
    private Line line;

    public boolean isLine() {
        return isLine;
    }

    public Line getLine() {
        return line;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }

    private ArrayList<Stop> stops;

    public RealTimeToUpdate(ArrayList<Stop> stops) {
        this.stops = stops;
        isLine = false;
    }

    public RealTimeToUpdate(Line line) {
        this.line = line;
        isLine = true;
    }
}
