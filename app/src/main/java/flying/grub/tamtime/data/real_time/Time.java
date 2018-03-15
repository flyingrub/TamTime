package flying.grub.tamtime.data.real_time;

/**
 * Created by fly on 3/13/18.
 */

public class Time {
    String waitingTime;
    int hour;
    int minute;

    public Time(String waitingTime, int hour, int minute) {
        this.waitingTime = waitingTime;
        this.hour = hour;
        this.minute = minute;
    }

    public String getWaitingTime() {
        return waitingTime;
    }
}
