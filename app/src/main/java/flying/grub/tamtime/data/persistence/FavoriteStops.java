package flying.grub.tamtime.data.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.map.StopZone;

public class FavoriteStops {
    private static final String TAG = FavoriteStops.class.getSimpleName();

    private List<StopZone> favoriteStop;
    private Context context;

    public FavoriteStops(Context c) {
        context = c;
        getFromPref();
    }

    private void getFromPref() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        favoriteStop = new ArrayList<>();
        for (String stop_zone_id : defaultSharedPreferences.getStringSet(TAG, new HashSet<String>())) {
            favoriteStop.add(Data.getData().getMap().getStopZoneById(Integer.parseInt(stop_zone_id)));
        }
        sort();
    }

    public ArrayList<StopZone> getFavoriteStop() {
        getFromPref();
        return new ArrayList<>(favoriteStop);
    }

    public boolean isInFav(StopZone stop) {
        int stop_zone_id = stop.getID();
        for (StopZone s : favoriteStop) {
            if (s.getID() == stop_zone_id) {
                return true;
            }
        }
        return false;
    }

    public void add(StopZone stop) {
        favoriteStop.add(stop);
        sortAndPush();
    }

    public void remove(int i) {
        favoriteStop.remove(i);
        sortAndPush();
    }

    public void remove(StopZone stop) {
        favoriteStop.remove(stop);
        sortAndPush();
    }

    private void sort() {
        Collections.sort(favoriteStop, new Comparator<StopZone>() {
            @Override
            public int compare(StopZone s1, StopZone s2) {
                return s1.getName().compareToIgnoreCase(s2.getName());
            }
        });
    }

    private void sortAndPush() {
        sort();
        pushToPref();
    }

    public void pushToPref() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        ArrayList<String> fav = new ArrayList<>();
        for (StopZone s : favoriteStop) {
            fav.add("" + s.getID());
        }

        editor.putStringSet(TAG, new HashSet<>(fav));
        editor.commit();
    }
}
