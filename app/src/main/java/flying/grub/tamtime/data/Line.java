package flying.grub.tamtime.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Line {
    String lineId;
    int num;
    ArrayList<Route> routesList;

    public Line(int num, String lineId) {
        this.num = num;
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

    public String getLineId(){
        return this.lineId;
    }

    public int getLineNum(){
        return this.num;
    }

    // Add
    public void addRoute(Route r) {
        this.routesList.add(r);
    }
}
