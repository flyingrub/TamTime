package flying.grub.tamtime.data;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyApp extends Application {

    private static VolleyApp instanceApp;
    private RequestQueue mRequestQueue;
    private static Context context;

    private VolleyApp(Context context) {
        this.context = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyApp getInstance(Context context) {
        if (instanceApp == null) {
            instanceApp = new VolleyApp(context);
        }
        return instanceApp;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setShouldCache(false);
        getRequestQueue().add(req);
    }
}