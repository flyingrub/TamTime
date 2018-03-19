package flying.grub.tamtime.data.map;

import java.util.ArrayList;

public class Direction {
    private String name;
    private Line line;
    private int sens;
    private ArrayList<Stop> stops;
    private ArrayList<Integer> arrivalIds;

    public Direction(String name, Line line, int sens) {
        this.name = name;
        this.line = line;
        this.sens = sens;
    }

    public String getName() {
        return name;
    }

    public Line getLine() {
        return line;
    }

    public int getSens() {
        return sens;
    }

    public void setStops(ArrayList<Stop> stops) {
        this.stops = stops;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }

    public ArrayList<Integer> getArrivalIds() {
        return arrivalIds;
    }

    public void setArrivalIds(ArrayList<Integer> arrivalIds) {
        this.arrivalIds = arrivalIds;
    }

    @Override
    public String toString() {
        return "Direction{" +
                "name='" + name + '\'' +
                ", line=" + line.getShortName() +
                ", sens=" + sens +
                ", stops=" + stops +
                '}';
    }



    public enum TamDirection {
        FORWARD(1), BACKWARD(2);
        private int value;

        TamDirection(int i) {
            value = i;
        }

        public static TamDirection fromCityWaySens(int i) {
            switch (i) {
                case 1:
                    return TamDirection.FORWARD;
                case 2:
                    return TamDirection.BACKWARD;
                default:
                    return null;
            }
        }

        @Override
        public String toString() {
            return value == 1 ? "FORWARD" : "BACKWARD";
        }
    }
}
