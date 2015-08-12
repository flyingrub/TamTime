package flying.grub.tamtime.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by fly on 12/02/15.
 */
public class Route {
    int lineId;
    String direction;
    ArrayList<Stop> stopArrayList;

    public Route(JSONObject route, int lineId){
        this.stopArrayList = new ArrayList<>();
        try {
            this.direction = route.getString("direction");
            this.lineId = lineId;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addStep(Stop stop){
        this.stopArrayList.add(stop);
    }

    public String getDirection() {
        return direction;
    }

    public ArrayList<Stop> getStopArrayList() {
        return stopArrayList;
    }

}
