package flying.grub.tamtime.data;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import flying.grub.tamtime.R;

/**
 * Created by fly on 10/13/15.
 */
public class DownloadService extends IntentService {

    public static final int ID = 8344;

    public DownloadService() {
        super("DownloadService");
    }

    public NotificationCompat.Builder getNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext())
                .setContentTitle(getString(R.string.download_title))
                .setSmallIcon(R.drawable.ic_launcher);
        return builder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String urlToDownload = intent.getStringExtra("url");

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        startForeground(ID, getNotification().build());
        try {
            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();
            // this will be useful so that you can show a typical 0-100% progress bar
            int fileLength = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(connection.getInputStream());
            FileOutputStream output = openFileOutput("theo.json", Context.MODE_PRIVATE);


            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                int percent = (int) (total * 100 / fileLength);
                NotificationCompat.Builder builder = getNotification();
                builder.setProgress(100, percent, false);
                builder.setContentText(percent + "%");
                mNotificationManager.notify(ID, builder.build());
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();

            // test
            FileInputStream fis = openFileInput("theo.json");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            Log.d("FILE", sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }
}