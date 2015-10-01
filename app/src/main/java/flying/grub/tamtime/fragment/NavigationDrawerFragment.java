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
import flying.grub.tamtime.navigation.DrawerCallback;
import flying.grub.tamtime.navigation.ItemWithDrawable;

public class NavigationDrawerFragment extends Fragment {
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREFERENCES_FILE = "app_settings";

    private RecyclerView recyclerView;
    private View containerView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerCallback drawerCallback;
    private NavigationDrawerAdapter drawerAdapter;

    private boolean userLearnedDrawer;
    private boolean fromSavedInstanceState;
    private int currentSelectedPosition;


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

        drawerAdapter = new NavigationDrawerAdapter(getItems());
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
        if (savedInstanceState != null) {
            currentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            fromSavedInstanceState = true;
        } else {
            currentSelectedPosition = 1;
        }
        selectItem(currentSelectedPosition);
    }

    private ArrayList<ItemWithDrawable> getItems() {
        ArrayList<ItemWithDrawable> itemWithDrawables = new ArrayList<>();
        itemWithDrawables.add(new ItemWithDrawable(getString(R.string.all_lines), getResources().getDrawable(R.drawable.ic_directions_subway_black_36dp), false));
        itemWithDrawables.add(new ItemWithDrawable(getString(R.string.all_stops), getResources().getDrawable(R.drawable.ic_place_black_36dp), false));
        itemWithDrawables.add(new ItemWithDrawable(getString(R.string.all_stops_favs), getResources().getDrawable(R.drawable.ic_favorite_black_36dp), false));
        itemWithDrawables.add(new ItemWithDrawable(getString(R.string.maps), getResources().getDrawable(R.drawable.ic_map_black_36dp), false));
        itemWithDrawables.add(new ItemWithDrawable(null, null, true));
        itemWithDrawables.add(new ItemWithDrawable(getString(R.string.settings), getResources().getDrawable(R.drawable.ic_settings_black_36dp), false));
        return itemWithDrawables;
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

        if (!userLearnedDrawer && !fromSavedInstanceState)
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

    void selectItem(int position) {
        currentSelectedPosition = position;
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(containerView);
        }
        if (drawerCallback != null) {
            drawerCallback.onDrawerClick(position);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, currentSelectedPosition);
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
