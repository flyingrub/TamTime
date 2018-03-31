package flying.grub.tamtime.data.persistence;

import java.util.ArrayList;

import flying.grub.tamtime.data.map.Line;
import flying.grub.tamtime.data.map.Stop;
import flying.grub.tamtime.data.map.StopZone;


public class LineStop {
    private StopZone stop;
    private Line line;

    public LineStop(StopZone stop, Line line) {
        this.stop = stop;
        this.line = line;
    }

    public ArrayList<Stop> getStops() {
        return stop.getStops(line);
    }

    public StopZone getStopZone() {
        return stop;
    }

    public Line getLine() {
        return line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineStop lineStop = (LineStop) o;

        if (stop != null ? !stop.equals(lineStop.stop) : lineStop.stop != null) return false;
        return !(line != null ? !line.equals(lineStop.line) : lineStop.line != null);

    }

    @Override
    public int hashCode() {
        int result = stop != null ? stop.hashCode() : 0;
        result = 31 * result + (line != null ? line.hashCode() : 0);
        return result;
    }
}
