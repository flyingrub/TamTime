package flying.grub.tamtime.data;

public class Utils {

    public static String toTimeString(int timeInt) {
        String timeStr;
        int min = (timeInt / 60);

        if (timeInt >= 10800) return "+ de 3h";

        if (min >= 60) {
            int hour = min /60;
            min = min % 60;
            timeStr = hour + "h" + min + "min";
        } else if (min < 0 ) {
            timeStr = "A quai";
        } else if (min == 0 ) {
            timeStr = "Proche";
        } else {
            timeStr = min + "min";
        }
        return timeStr;
    }
}
