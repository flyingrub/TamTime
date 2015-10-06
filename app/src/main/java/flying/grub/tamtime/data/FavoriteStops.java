package flying.grub.tamtime.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import flying.grub.tamtime.activity.MainActivity;

public class FavoriteStops {
    private static final String TAG = FavoriteStops.class.getSimpleName();

    private List<Stop> favoriteStop;
    private Context context;

    public FavoriteStops(Context c) {
        context = c;
        getFromPref();
    }

    private void getFromPref() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        favoriteStop = new ArrayList<>();
        for (String stopId : defaultSharedPreferences.getStringSet(TAG, new HashSet<String>())) {
            favoriteStop.add(DataParser.getDataParser().getStopByOurId(Integer.parseInt(stopId)));
        }
        sort();
    }

    public ArrayList<Stop> getFavoriteStop() {
        getFromPref();
        return new ArrayList<>(favoriteStop);
    }

    public boolean isInFav(Stop stop) {
        int stopId = stop.getOurId();
        for (Stop s : favoriteStop) {
            if (s.getOurId() == stopId) {
                return true;
            }
        }
        return false;
    }

    public void add(Stop stop) {
        favoriteStop.add(stop);
        sortAndPush();
    }

    public void remove(int i) {
        favoriteStop.remove(i);
        sortAndPush();
    }

    public void remove(Stop stop) {
        favoriteStop.remove(stop);
        sortAndPush();
    }

    private void sort() {
        Collections.sort(favoriteStop, new Comparator<Stop>() {
            @Override
            public int compare(Stop s1, Stop s2) {
                return s1.getName().compareToIgnoreCase(s2.getName());
            }
        });
    }

    private void sortAndPush() {
        sort();
        pushToPref();
    }

    public void pushToPref() {
        ArrayList<String> fav = new ArrayList<>();
        for (Stop s : favoriteStop) {
            fav.add("" + s.getOurId());
        }
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
        editor.putStringSet(TAG, new HashSet<>(fav));
        editor.commit();
    }
}
