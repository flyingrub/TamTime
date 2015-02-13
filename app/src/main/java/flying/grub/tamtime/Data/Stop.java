package flying.grub.tamtime.Data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by fly on 12/02/15.
 */
public class Stop {
    private int id;
    private String name;
    private ArrayList<Lines> linesArrayList;

    public Stop(JSONObject stop){
        try {
            this.id = stop.getInt("stop_id");
            this.name = stop.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
