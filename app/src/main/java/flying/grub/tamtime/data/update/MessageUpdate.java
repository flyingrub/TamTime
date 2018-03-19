package flying.grub.tamtime.data.update;

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

