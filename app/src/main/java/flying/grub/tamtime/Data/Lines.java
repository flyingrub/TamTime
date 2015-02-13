package flying.grub.tamtime.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by fly on 12/02/15.
 */
public class Lines {
    int lineId;
    ArrayList<Route> routeArrayList;
    ArrayList<Stop> stopArrayList;

    public Lines(JSONObject line){
        registerStop(line);
        JSONArray routes;
        JSONObject routeJson;
        Route route;
        this.routeArrayList = new ArrayList<>();
        try {
            this.lineId = line.getInt("route_number");
            routes = line.getJSONArray("routes");
            routeJson = routes.getJSONObject(1);
            route = new Route(routeJson, lineId);
            for (int i = 0; i < routes.length(); i++) {
                if (getRoute(routes.getJSONObject(i).getString("direction")) == null){
                    routeJson = routes.getJSONObject(i);
                    route = new Route(routeJson, lineId);
                    this.routeArrayList.add(route);
                }
                route.addStep(getStop(routeJson.getInt("stop_id")));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void registerStop(JSONObject line){
        JSONArray stops;
        this.stopArrayList = new ArrayList<>();
        try {
            stops = line.getJSONArray("stops");
            for (int i = 0; i < stops.length(); i++) {
                this.stopArrayList.add(new Stop(stops.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Stop getStop(int id){
        for (int i = 0; i< stopArrayList.size(); i++){
            if (stopArrayList.get(i).getId() == id ){
                return stopArrayList.get(i);
            }
        }
        return null;
    }

    public Route getRoute(String direction){
        for (int i = 0; i< routeArrayList.size(); i++){
            if (routeArrayList.get(i).getDirection().equals(direction)){
                return routeArrayList.get(i);
            }
        }
        return null;
    }

    public Route getRoute(int i){
        return routeArrayList.get(i);
    }

    public int getRouteCount(){
        return routeArrayList.size();
    }


}
