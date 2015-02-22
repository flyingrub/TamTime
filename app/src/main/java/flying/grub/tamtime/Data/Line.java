package flying.grub.tamtime.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import flying.grub.tamtime.MainActivity;

/**
 * Created by fly on 12/02/15.
 */
public class Line {
    int lineId;
    ArrayList<Route> routeArrayList;
    ArrayList<Stop> stopArrayList;

    public Line(JSONObject line){
        registerStop(line);
        JSONArray routesJson;
        JSONObject routeJson;
        Route route;
        this.routeArrayList = new ArrayList<>();

        try {
            this.lineId = line.getInt("route_number");
            routesJson = line.getJSONArray("routes");
            routeJson = routesJson.getJSONObject(0);
            route = new Route(routeJson, lineId);
            for (int i = 0; i < routesJson.length(); i++) {
                routeJson = routesJson.getJSONObject(i);
                if (getRoute(routesJson.getJSONObject(i).getString("direction")) == null){
                    route = new Route(routeJson, lineId);
                    this.routeArrayList.add(route);
                }
                route.addStep(MainActivity.getData().getStop(routeJson.getInt("stop_id")));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void registerStop(JSONObject line){
        JSONArray stops;
        Stop stop;
        this.stopArrayList = new ArrayList<>();
        try {
            stops = line.getJSONArray("stops");
            for (int i = 0; i < stops.length(); i++) {
                if (MainActivity.getData().getStop(stops.getJSONObject(i).getInt("stop_id")) == null){
                    stop = new Stop(stops.getJSONObject(i));
                }else{
                    stop = MainActivity.getData().getStop(stops.getJSONObject(i).getInt("stop_id"));
                }
                stop.addLine(this);
                this.stopArrayList.add(stop);
                MainActivity.getData().addStop(stop);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public int getLineId(){
        return lineId;
    }
}
