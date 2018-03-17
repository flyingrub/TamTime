package flying.grub.tamtime.data.dirsruption;

import java.util.Calendar;

import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.map.Line;

public class DisruptEvent {
    private Line line;
    private Calendar beginDate;
    private Calendar endDate;
    private String title;

    public DisruptEvent(Line line, Calendar begD, Calendar endD, String title) {
        Data.getData().getDisruptEventHandler().addDisruptEvent(this);
        this.line = line;
        this.line.addDisruptEvent(this);
        this.beginDate = begD;
        this.endDate = endD;
        this.title = title;
    }

    public boolean asValidDate() {
        Calendar now = Calendar.getInstance();
        if (now.compareTo(this.endDate) <= 0) return true;
        return false;
    }

    public void destroy() {
        this.line.removeDisruptEvent(this);
        Data.getData().getDisruptEventHandler().removeDisruptEvent(this);
    }

    // Tests & Bullshit
    public String toString() {
        return this.title;
    }
}
