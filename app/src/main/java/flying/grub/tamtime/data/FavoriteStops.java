/*
 * Copyright (C) 2015 - Holy Lobster
 *
 * Nuntius is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Nuntius is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Nuntius. If not, see <http://www.gnu.org/licenses/>.
 */

package flying.grub.tamtime.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import flying.grub.tamtime.activity.MainActivity;

public class FavoriteStops {
    private static final String TAG = FavoriteStops.class.getSimpleName();

    private List<String> favoriteStop;
    private Context context;

    public FavoriteStops(Context c) {
        context = c;
        getFromPref();
    }

    private void getFromPref() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        favoriteStop = new ArrayList<>();
        for (String stopName : defaultSharedPreferences.getStringSet(TAG, new HashSet<String>())) {
            favoriteStop.add(stopName);
        }
        Collections.sort(favoriteStop);
    }

    public ArrayList<Stop> getFavoriteStop() {
        getFromPref();
        ArrayList<Stop> res = new ArrayList<>();
        for (String string : favoriteStop) {
            res.add(MainActivity.getData().getStopByName(string));
        }
        return res;
    }

    public boolean isInFav(String stopName) {
        for (String s : favoriteStop) {
            if (s.equals(stopName)) {
                return true;
            }
        }
        return false;
    }

    public void add(String stopName) {
        favoriteStop.add(stopName);
        sortAndPush();
    }

    public void remove(int i) {
        favoriteStop.remove(i);
        sortAndPush();
    }

    public void remove(String stopName) {
        favoriteStop.remove(stopName);
        sortAndPush();
    }

    private void sortAndPush() {
        Collections.sort(favoriteStop);
        pushToPref();
    }

    public void pushToPref() {
        ArrayList<String> fav = new ArrayList<>();
        for (String stopName : favoriteStop) {
            fav.add(stopName);
        }
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
        editor.putStringSet(TAG, new HashSet<>(fav));
        editor.commit();
    }
}
