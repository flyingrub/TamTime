package flying.grub.tamtime.data.map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import flying.grub.tamtime.R;

public class TamMapDatabaseHelper {

    private static final String NAME = "referential_android.sqlite";
    private SQLiteDatabase database;
    private Context context;

    private static TamMapDatabaseHelper singleton;

    public static TamMapDatabaseHelper getDB(Context c) {
        if (singleton == null)  {
            singleton = new TamMapDatabaseHelper(c.getApplicationContext());
        }
        return singleton;
    }

    private TamMapDatabaseHelper(Context context) {
        this.context = context;
        database = getDataBase();
    }

    private SQLiteDatabase getDataBase() {
        try {
            createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File dbFile = context.getDatabasePath(NAME);
        return  SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);
    }

    private void createDatabase() throws IOException {
        File outputFile = context.getDatabasePath(NAME);
        if (!outputFile.exists()) {
            InputStream input = context.getResources().openRawResource(R.raw.referential_android);
            FileOutputStream output = new FileOutputStream(outputFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();
            output.close();
            input.close();
        }
    }

    public ArrayList<Line> getLines() {
        ArrayList<Line> lines = new ArrayList<>();
        String command = "SELECT * FROM LINE ORDER BY display_order;";
        Cursor cursor = database.rawQuery(command, null);
        if (cursor.moveToFirst()) {
            do {
                int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                int tam_id = cursor.getInt(cursor.getColumnIndex("tam_id"));
                int cityway_id = cursor.getInt(cursor.getColumnIndex("cityway_id"));
                String short_name = cursor.getString(cursor.getColumnIndex("short_name"));
                int urbanLine = cursor.getInt(cursor.getColumnIndex("urban"));
                Line line = new Line(short_name, _id, tam_id, cityway_id, urbanLine);

                String forwardName = cursor.getString(cursor.getColumnIndex("commercial_forward_name"));
                Direction forward = new Direction(forwardName, line, 1);
                forward.setStops(getStopForDirection(forward));
                forward.setArrivalIds(getArrivalIdForDirection(forward));
                line.addRoute(forward);

                String backwardName = cursor.getString(cursor.getColumnIndex("commercial_backward_name"));
                Direction backward = new Direction(backwardName, line, 2);
                backward.setStops(getStopForDirection(backward));
                backward.setArrivalIds(getArrivalIdForDirection(backward));
                line.addRoute(backward);

                lines.add(line);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return lines;
    }

    public ArrayList<Integer> getArrivalIdForDirection(Direction direction) {
        int line_id = direction.getLine().getId();
        int sens = direction.getSens();
        ArrayList<Integer> ids = new ArrayList<>();
        String cityway_sens = Direction.TamDirection.fromCityWaySens(sens).toString();
        String command = "SELECT DISTINCT arrivalCitywayId FROM STOP_ON_LINE WHERE line_id = " + line_id + " AND directionForTam = \"" + cityway_sens +"\";";
        Cursor cursor = database.rawQuery(command, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("arrivalCitywayId"));
                ids.add(id);
            } while(cursor.moveToNext());
            cursor.close();
        }
        return ids;
    }

    public ArrayList<Stop> getStopForDirection(Direction direction) {
        int line_id = direction.getLine().getId();
        int sens = direction.getSens();
        ArrayList<Stop> stops = new ArrayList<>();
        String cityway_sens = Direction.TamDirection.fromCityWaySens(sens).toString();
        String command = "SELECT * FROM STOP_SEQUENCE JOIN STOP ON stop_id = STOP._id WHERE line_id = " + line_id + " AND directionForTam = \"" + cityway_sens +"\";";
        Cursor cursor = database.rawQuery(command, null);
        if (cursor.moveToFirst()) {
            do {
                int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                int tam_id = cursor.getInt(cursor.getColumnIndex("tam_id"));
                int cityway_id = cursor.getInt(cursor.getColumnIndex("cityway_id"));
                int stopzone_id = cursor.getInt(cursor.getColumnIndex("stopzone_id"));
                StopZone stopZone = getStopZoneFromId(stopzone_id);
                Stop stop = new Stop(_id, cityway_id, tam_id, stopZone, direction);
                stopZone.addStop(stop);
                stops.add(stop);
            } while(cursor.moveToNext());
            cursor.close();
        }
        return stops;
    }

    public StopZone getStopZoneFromId(int stopzoneid) {
        String command = "SELECT * FROM STOPZONE WHERE _id = " + stopzoneid + ";";
        Cursor cursor = database.rawQuery(command, null);
        if (cursor.moveToFirst()) {
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            int tam_id = cursor.getInt(cursor.getColumnIndex("tam_id"));
            int cityway_id = cursor.getInt(cursor.getColumnIndex("cityway_id"));
            String stop_name = cursor.getString(cursor.getColumnIndex("stopzone_name"));
            String search_name = cursor.getString(cursor.getColumnIndex("search_name"));
            double lon = cursor.getDouble(cursor.getColumnIndex("longitude"));
            double lat = cursor.getDouble(cursor.getColumnIndex("latitude"));
            StopZone stopZone = new StopZone(stop_name, _id, tam_id, cityway_id, search_name, lat, lon);
            cursor.close();
            return stopZone;
        }
        return null;
    }

    public ArrayList<StopZone> getStopZonesAlt() {
        ArrayList<StopZone> stopZones = new ArrayList<>();
        String command = "SELECT * FROM STOPZONE;";
        Cursor cursor = database.rawQuery(command, null);
        if (cursor.moveToFirst()) {
            do {
                int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                int tam_id = cursor.getInt(cursor.getColumnIndex("tam_id"));
                int cityway_id = cursor.getInt(cursor.getColumnIndex("cityway_id"));
                String stop_name = cursor.getString(cursor.getColumnIndex("stopzone_name"));
                String search_name = cursor.getString(cursor.getColumnIndex("search_name"));
                double lon = cursor.getDouble(cursor.getColumnIndex("longitude"));
                double lat = cursor.getDouble(cursor.getColumnIndex("latitude"));
                StopZone stopZone = new StopZone(stop_name, _id, tam_id, cityway_id, search_name, lat, lon);
                stopZones.add(stopZone);
            } while(cursor.moveToNext());
            cursor.close();
        }
        return stopZones;
    }

    public ArrayList<Stop> getStopsForStopZone(StopZone stopZone) {
        ArrayList<Stop> stops = new ArrayList<>();
        String command = "SELECT * FROM STOP WITH stopzone = " + stopZone.getID() + ";";
        Cursor cursor = database.rawQuery(command, null);
        if (cursor.moveToFirst()) {
            do {
                int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                int tam_id = cursor.getInt(cursor.getColumnIndex("tam_id"));
                int cityway_id = cursor.getInt(cursor.getColumnIndex("cityway_id"));
                Stop stop = new Stop(_id, cityway_id, tam_id, stopZone, null);
                stops.add(stop);
            } while(cursor.moveToNext());
            cursor.close();
        }
        return stops;
    }
}
