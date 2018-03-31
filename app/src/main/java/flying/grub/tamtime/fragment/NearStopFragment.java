package flying.grub.tamtime.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import flying.grub.tamtime.R;
import flying.grub.tamtime.activity.OneStopActivity;
import flying.grub.tamtime.adapter.NearStopAdapter;
import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.map.StopZone;


public class NearStopFragment extends Fragment {

    private static final String TAG = NearStopFragment.class.getSimpleName();
    private static final int PERMISSION = 1;

    private RecyclerView recyclerView;
    private NearStopAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<StopZone> nearStops;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Location lastKnownLocation;
    private boolean gpsActivationCalled;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.view_recycler_load, container, false);
        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setBackgroundColor(getResources().getColor(R.color.windowBackgroundCard));

        getActivity().setTitle(getString(R.string.nearby_stop));
        gpsActivationCalled = false;


        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        // Remove the listener you previously added
        if (hasPermission() && locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d(TAG, "new location");
                lastKnownLocation = location;
                new getAllDistance().execute(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "Provider: "+ provider+ " || status: " + status);
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        if (nearStops != null) {
            setupAdapter();
        }
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION);
        } else {
            getLocation();
        }
    }

    private void setupAdapter() {
        LinearLayout progress = (LinearLayout) getActivity().findViewById(R.id.progress);
        TextView textView = (TextView) getActivity().findViewById(R.id.empty_view);
        if (progress != null) {
            progress.setVisibility(View.GONE);
        }
        if (nearStops.size() == 0) {
            textView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        } else {
            textView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        adapter = new NearStopAdapter(nearStops);
        recyclerView.swapAdapter(adapter, true);
        adapter.SetOnItemClickListener(new NearStopAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                selectitem(position);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment;
                    fragment = new AllLinesFragment();
                    transaction.replace(R.id.container, fragment);
                    transaction.addToBackStack("");
                    transaction.commit();
                }
            }
        }
    }


    private boolean hasPermission() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void getLocation() {


        if (locationManager.getAllProviders().indexOf(LocationManager.GPS_PROVIDER) >= 0) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, locationListener);
            Log.d(TAG, "GPS UPDATE ASKED");
        } else {
            Log.w("MainActivity", "No GPS location provider found. GPS data display will not be available.");
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (!gpsActivationCalled) {
                showGpsDisabledDialog();
            }
        }
        Location bestLocation = getBestLocation();
        if (bestLocation != null) {
            new getAllDistance().execute(bestLocation);
        }
    }

    private Location getBestLocation() {
        Location lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location lastKnownLocationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            return lastKnownLocation;
        }else if (lastKnownLocationGps != null) {
            return lastKnownLocationGps;
        } else if (lastKnownLocationNetwork != null) {
            return lastKnownLocationNetwork;
        }
        return null;
    }

    public void showGpsDisabledDialog(){
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.gps_disabled)
                .content(R.string.please_enable_gps)
                .positiveText(R.string.OK)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        gpsActivationCalled = true;
                        NavigationDrawerFragment.currentSelectedPosition.setI(0);
                        getActivity().getSupportFragmentManager().popBackStack();
                        startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                        dialog.dismiss();
                    }
                }).build();
        dialog.show();
    }

    public void selectitem(int i){
        StopZone s = nearStops.get(i);
        Intent intent = new Intent(getActivity(), OneStopActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("stop_zone_id", s.getID());
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.fade_scale_out);
    }

    class getAllDistance extends AsyncTask<Location, Integer, String> {

        @Override
        protected String doInBackground(Location... locations) {
            for (Location location : locations) {
                Log.d(TAG, "Asked for distance calc");
                Data.getData().getMap().setAlldistance(location);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String message) {
            nearStops = Data.getData().getMap().getAllNearStops();
            setupAdapter();
        }
    }
}
