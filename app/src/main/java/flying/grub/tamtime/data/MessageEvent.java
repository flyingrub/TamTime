package flying.grub.tamtime.data;

/**
 * Created by fly on 9/25/15.
 */
public class MessageEvent {
    public enum Type {
        TIMESUPDATE,
        LINESUPDATE,
    }

    public final Type type;

    public MessageEvent(Type type) {
        this.type = type;
    }
}

