package flying.grub.tamtime.data;


import java.util.ArrayList;

public class Line {
    int lineId;
    ArrayList<Route> routesList;

    public Line(int lineId) {
        this.lineId = lineId;
        this.routesList = new ArrayList<Route>();
    }

    // Get
    public Route getRoute(String direction){
        for (int i = 0; i< routesList.size(); i++){
            if (routesList.get(i).getDirection().equals(direction)){
                return routesList.get(i);
            }
        }
        return null;
    }

    public ArrayList<Route> getRoutes() {
        return this.routesList;
    }

    public int getRouteCount(){
        return this.routesList.size();
    }

    public int getLineId(){
        return this.lineId;
    }

    // Add
    public void addRoute(Route r) {
        this.routesList.add(r);
    }


    // Test & Bullshit
    public String toString() {
        return "Line " + this.lineId;
    }

}
