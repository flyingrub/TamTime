package flying.grub.tamtime.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import flying.grub.tamtime.R;
import flying.grub.tamtime.adapter.NavigationDrawerAdapter;
import flying.grub.tamtime.data.IntRef;
import flying.grub.tamtime.navigation.DrawerCallback;
import flying.grub.tamtime.navigation.ItemWithDrawable;

public class NavigationDrawerFragment extends Fragment {

    public static final String TAG = NavigationDrawerFragment.class.getSimpleName();
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREFERENCES_FILE = "app_settings";

    private RecyclerView recyclerView;
    private View containerView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerCallback drawerCallback;
    private NavigationDrawerAdapter drawerAdapter;
    private ArrayList<ItemWithDrawable> drawerElements;

    private boolean userLearnedDrawer;
    public static IntRef currentSelectedPosition = new IntRef(0);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.view_recycler, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);

        getActivity().setTitle(getString(R.string.app_name));

        setItems();

        drawerAdapter = new NavigationDrawerAdapter(drawerElements, currentSelectedPosition);
        recyclerView.setAdapter(drawerAdapter);
        drawerAdapter.SetOnItemClickListener(new NavigationDrawerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                selectItem(position);
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userLearnedDrawer = Boolean.valueOf(readSharedSetting(getActivity(), PREF_USER_LEARNED_DRAWER, "false"));
    }

    @Override
    public void onResume() {
        super.onResume();
        selectItem(currentSelectedPosition.getI());
    }

    private ArrayList<ItemWithDrawable> setItems() {
        drawerElements = new ArrayList<>();
        drawerElements.add(new ItemWithDrawable(0, getString(R.string.home), getResources().getDrawable(R.drawable.ic_home_black_24dp), false));
        drawerElements.add(new ItemWithDrawable(1, getString(R.string.all_lines), getResources().getDrawable(R.drawable.ic_directions_subway_black_24dp), false));
        drawerElements.add(new ItemWithDrawable(2, getString(R.string.all_stops), getResources().getDrawable(R.drawable.ic_place_black_24dp), false));
        drawerElements.add(new ItemWithDrawable(3, getString(R.string.all_stops_favs), getResources().getDrawable(R.drawable.ic_favorite_black_24dp), false));
        drawerElements.add(new ItemWithDrawable(-1, null, null, true));
        drawerElements.add(new ItemWithDrawable(4, getString(R.string.nearby_stop), getResources().getDrawable(R.drawable.ic_my_location_black_24dp), false));
        drawerElements.add(new ItemWithDrawable(5, getString(R.string.maps), getResources().getDrawable(R.drawable.ic_map_black_24dp), false));
        drawerElements.add(new ItemWithDrawable(6, getString(R.string.reports), getResources().getDrawable(R.drawable.ic_warning_black_24dp), false));
        drawerElements.add(new ItemWithDrawable(-1, null, null, true));
        drawerElements.add(new ItemWithDrawable(7, getString(R.string.settings), getResources().getDrawable(R.drawable.ic_settings_black_24dp), false));
        drawerElements.add(new ItemWithDrawable(8, getString(R.string.app_info), getResources().getDrawable(R.drawable.ic_info_black_24dp), false));
        return drawerElements;
    }

    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        this.drawerLayout = drawerLayout;
        this.drawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.primaryColor));

        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;
                if (!userLearnedDrawer) {
                    userLearnedDrawer = true;
                    saveSharedSetting(getActivity(), PREF_USER_LEARNED_DRAWER, "true");
                }
                getActivity().invalidateOptionsMenu();
            }
        };

        if (!userLearnedDrawer)
            this.drawerLayout.openDrawer(containerView);

        this.drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                actionBarDrawerToggle.syncState();
            }
        });

        this.drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    public void openDrawer() {
        drawerLayout.openDrawer(containerView);
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(containerView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        drawerCallback = null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            drawerCallback = (DrawerCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    public void selectItem(int position) {
        currentSelectedPosition.setI(position);
        drawerAdapter.notifyDataSetChanged();
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(containerView);
        }
        if (drawerCallback != null) {
            drawerCallback.onDrawerClick(drawerElements.get(position));
        }
    }

    public boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(containerView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }
}
