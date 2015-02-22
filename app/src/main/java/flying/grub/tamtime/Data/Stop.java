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
    private ArrayList<Line> linesArrayList;
    private ArrayList<Integer> times;

    public Stop(JSONObject stop){
        times =  new ArrayList<>();
        linesArrayList = new ArrayList<>();
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

    public String getTimes(int i){
        String time;
        try {
            int min = (times.get(i) / 60);
            if (min >= 60){
                int hour = min /60;
                min = min % 60;
                time = hour + " h " + min + " min";
            }else if (min < 0 ){
                time = "A quai";
            }else if (min == 0 ){
                time = "Proche";
            }else{
                time = min + " min";
            }
            return time;
        }catch (IndexOutOfBoundsException e){
            return "-";
        }
    }

    public void addLine(Line line){
        linesArrayList.add(line);
    }

    public void addTime(int aTimes){
        times.add(aTimes);
    }
}
