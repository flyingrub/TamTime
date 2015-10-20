package flying.grub.tamtime.data;

import android.content.Context;

import android.os.Handler;

/**
 * Created by fly on 10/20/15.
 */
public class UpdateRunnable implements Runnable {

    private Context context;
    private Handler handler = new Handler();

    private static final int TIME = 30000; // 30 sec

    public UpdateRunnable(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        DataParser.getDataParser().update(context);
        handler.postDelayed(this, TIME);
    }

    public void stop() {
        handler.removeCallbacks(this);
    }
}
