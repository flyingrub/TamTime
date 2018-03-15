package flying.grub.tamtime.data.update;

/**
 * Created by fly on 9/25/15.
 */
public class MessageUpdate {
    public enum Type {
        TIMES_UPDATE,
        LINES_UPDATE,
        EVENT_UPDATE,
        REPORT_UPDATE
    }

    public final Type type;

    public MessageUpdate(Type type) {
        this.type = type;
    }
}

