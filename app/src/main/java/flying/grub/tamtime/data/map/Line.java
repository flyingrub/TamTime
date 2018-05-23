package flying.grub.tamtime.data.map;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import flying.grub.tamtime.data.dirsruption.Disrupt;

public class Line {
    private String shortName;
    private int id;
    private int tam_id;
    private int cityway_id;
    private int urbanLine;
    private int color;
    private ArrayList<Direction> directions;

    private ArrayList<GeoPoint> polyline_A;
    private ArrayList<GeoPoint> polyline_R;

    private ArrayList<Disrupt> disruptList;

    public Line(String shortName, int id, int tamID, int citywayID, int urbanLine) {
        this.shortName = shortName;
        this.id = id;
        this.tam_id = tamID;
        this.cityway_id = citywayID;
        this.urbanLine = urbanLine;
        this.directions = new ArrayList<>();
        this.disruptList = new ArrayList<>();
        this.polyline_A = new ArrayList<>();
        this.polyline_R = new ArrayList<>();
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
                ", disruptList=" + disruptList +
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

    public ArrayList<Disrupt> getDisruptList() {
        return disruptList;
    }

    public void removeDisruptEvent(Disrupt event) {
        this.disruptList.remove(event);
    }

    public void addDisruptEvent(Disrupt event) {
        this.disruptList.add(event);
    }

    public ArrayList<GeoPoint> getPolyline_A(){
        return polyline_A;
    }

    public void setPolyline_A(ArrayList<GeoPoint> p){
        polyline_A = p;
    }

    public ArrayList<GeoPoint> getPolyline_R(){
        return polyline_R;
    }

    public void setPolyline_R(ArrayList<GeoPoint> p){
        polyline_R = p;
    }

    public int getColor(){
        return color;
    }

    public void setColor(int c){
        color = c;
    }
}
