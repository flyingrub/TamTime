package flying.grub.tamtime.data;

import java.util.ArrayList;


public class Stop {
    private String name;
    private ArrayList<Line> linesList;

    public Stop(String name, Line line){
        this.linesList = new ArrayList<Line>();
        this.name = name;
        this.linesList.add(line);
    }

    // Get
    public String getName() {
        return name;
    }

    public ArrayList<Line> getLines() {
        return this.linesList;
    }

    // Add
    public void addLine(Line line){
        if (!this.linesList.contains(line)) this.linesList.add(line);
    }

    // Test & Bullshit
    public String toString() {
        String res = this.name + "\n";
        for (Line line : linesList) {
            res += "--- " + line + "\n";
        }
        return res;
    }

}
