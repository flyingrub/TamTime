package flying.grub.tamtime.data.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;

import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.map.Line;
import flying.grub.tamtime.data.map.StopZone;

public class FavoriteStopLine {

    private static final String TAG = FavoriteStopLine.class.getSimpleName();

    private ArrayList<LineStop> favoriteStopLine;
    private Context context;

    public FavoriteStopLine(Context c) {
        context = c;
        getFromPref();
    }

    private void getFromPref() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        favoriteStopLine = new ArrayList<>();
        for (String info : defaultSharedPreferences.getStringSet(TAG, new HashSet<String>())) {
            String[] infos = info.split("<>");
            StopZone s = Data.getData().getMap().getStopZoneById(Integer.parseInt(infos[0]));
            Line l = Data.getData().getMap().getLineByNum(Integer.parseInt(infos[1]));
            favoriteStopLine.add(new LineStop(s, l));
        }
    }

    // STOP LINES

    public void addLineStop(Line l, StopZone s) {
        favoriteStopLine.add(new LineStop(s, l));
        sortAndPush();
    }

    public void removeLineStop(int position) {
        favoriteStopLine.remove(position);
        sortAndPush();
    }

    public ArrayList<LineStop> getFavStopLines() {
        getFromPref();
        return new ArrayList<>(favoriteStopLine);
    }

    private void sortAndPush() {
        pushToPref();
    }

    public void pushToPref() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        ArrayList<String> favLineStop = new ArrayList<>();
        for (LineStop lineStop : favoriteStopLine) {
            favLineStop.add(lineStop.getStopZone().getID() + "<>" + lineStop.getLine().getLineNum());
        }

        editor.putStringSet(TAG, new HashSet<>(favLineStop));
        editor.commit();
    }
}
