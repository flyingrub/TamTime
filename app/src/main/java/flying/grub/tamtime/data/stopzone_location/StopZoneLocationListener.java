package flying.grub.tamtime.data.stopzone_location;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import flying.grub.tamtime.R;
import flying.grub.tamtime.activity.MainActivity;
import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.map.StopZone;
import flying.grub.tamtime.data.mark.MarkEvent;

public class StopZoneLocationListener implements LocationListener{
    private static final String TAG = StopZoneLocationListener.class.getSimpleName();
    private Context context;

    public StopZoneLocationListener(Context context) {
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        for(StopZone stopZone : Data.getData().getMap().getStopZones())
        {
            if(stopZone.getLocation().distanceTo(location) < 50 && location.getSpeed() < 5)
            {
                Log.d(TAG, "StopZone close : " + stopZone.getStops().get(0).getTimes().get(0).getWaitingTime());

                if(stopZone.getStops().get(0).getTimes().get(0).getWaitingTime().length() == 1) //Default value : -
                    return;

                StringBuilder notificationContent = new StringBuilder();

                if(stopZone.getStops().size() > 0) {
                    notificationContent.append(stopZone.getStops().get(0).getDirection().getName());
                    notificationContent.append(" (");
                    notificationContent.append(stopZone.getStops().get(0).getTimes().get(0).getWaitingTime());
                    notificationContent.append(" )");
                }

                if(stopZone.getStops().size() > 1) {
                    notificationContent.append(" - ");
                    notificationContent.append(stopZone.getStops().get(1).getDirection().getName());
                    notificationContent.append(" (");
                    notificationContent.append(stopZone.getStops().get(1).getTimes().get(0).getWaitingTime());
                    notificationContent.append(" )");
                }


                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setContentTitle(stopZone.getName())
                        .setContentText(notificationContent.toString())
                        .setSmallIcon(R.drawable.ic_action_tam);

                Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(""));
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(contentIntent);

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if(manager != null)
                    manager.notify(1, builder.build());
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled");
    }
}
