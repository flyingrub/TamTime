package flying.grub.tamtime.data.update;

import android.os.Handler;

import flying.grub.tamtime.data.Data;

/**
 * Created by fly on 10/20/15.
 */
public class UpdateRunnable implements Runnable {

    private Handler handler = new Handler();

    private static final int TIME = 30000; // 30 sec

    @Override
    public void run() {
        Data.getData().update();
        handler.postDelayed(this, TIME);
    }

    public void stop() {
        handler.removeCallbacks(this);
    }
}
