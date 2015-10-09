package flying.grub.tamtime.data;

import java.util.Calendar;

/**
 * Created by fly on 10/9/15.
 */
public class Report {

    public enum Type {
        INSOLITE,
        CONTROLE,
    }

    private Type type;
    private String message;
    private Calendar calendar;

    public Report(Type type, String message, Calendar calendar) {
        this.type = type;
        this.message = message;
        this.calendar= calendar;
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
