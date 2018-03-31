package flying.grub.tamtime.data.map;

import java.util.ArrayList;

import flying.grub.tamtime.data.dirsruption.DisruptEvent;

public class Line {
    private String shortName;
    private int id;
    private int tam_id;
    private int cityway_id;
    private int urbanLine;
    private ArrayList<Direction> directions;

    private ArrayList<DisruptEvent> disruptEventList;

    public Line(String shortName, int id, int tamID, int citywayID, int urbanLine) {
        this.shortName = shortName;
        this.id = id;
        this.tam_id = tamID;
        this.cityway_id = citywayID;
        this.urbanLine = urbanLine;
        this.directions = new ArrayList<>();
        this.disruptEventList = new ArrayList<>();
    }

    // Get
    public Direction getRoute(String direction){
        for (int i = 0; i< directions.size(); i++){
            if (directions.get(i).equals(direction)) {
                return directions.get(i);
            }
        }
        return null;
    }

    public int getTam_id() {
        return tam_id;
    }

    public int getCityway_id() {

        return cityway_id;
    }

    public int getUrbanLine() {

        return urbanLine;
    }

    @Override
    public String toString() {
        return "Line{" +
                "shortName='" + shortName + '\'' +
                ", id=" + id +
                ", tam_id=" + tam_id +
                ", cityway_id=" + cityway_id +
                ", directions=" + directions +
                ", disruptEventList=" + disruptEventList +
                '}';
    }

    public int getId() {
        return id;
    }

    public ArrayList<Direction> getDirections() {
        return this.directions;
    }

    public void addRoute(Direction r) {
        this.directions.add(r);
    }

    public int getDirectionsCount(){
        return this.directions.size();
    }

    public String getShortName(){
        return this.shortName;
    }

    public int getLineNum(){
        return this.id;
    }

    public ArrayList<DisruptEvent> getDisruptEventList() {
        return disruptEventList;
    }

    public void removeDisruptEvent(DisruptEvent event) {
        this.disruptEventList.remove(event);
    }

    public void addDisruptEvent(DisruptEvent event) {
        this.disruptEventList.add(event);
    }
}
