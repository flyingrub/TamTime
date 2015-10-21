package flying.grub.tamtime.data;

import java.util.Calendar;

public class DisruptEvent {
    private DataParser data;
    private Line line;
    private Calendar beginDate;
    private Calendar endDate;
    private String title;

    public DisruptEvent(DataParser data, Line line, Calendar begD, Calendar endD, String title) {
        this.data = data;
        this.data.addDisruptEvent(this);
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
        this.data.removeDisruptEvent(this);
    }

    // Tests & Bullshit
    public String toString() {
        return this.title;
    }
}
