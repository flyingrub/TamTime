package flying.grub.tamtime.data.real_time;

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
        return waitingTime + " (" + hour + ":" + minute + ")";
    }
}
