package flying.grub.tamtime.activity;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import flying.grub.tamtime.data.DataParser;
import flying.grub.tamtime.data.FavoriteStops;
import flying.grub.tamtime.fragment.AllLinesFragment;
import flying.grub.tamtime.fragment.AllStopFragment;
import flying.grub.tamtime.fragment.FavoriteStopsFragment;
import flying.grub.tamtime.fragment.NavigationDrawerFragment;
import flying.grub.tamtime.R;
import flying.grub.tamtime.fragment.WebFragment;
import flying.grub.tamtime.navigation.DrawerCallback;

public class MainActivity extends AppCompatActivity implements DrawerCallback {

    private Toolbar mToolbar;
    private NavigationDrawerFragment navigationDrawerFragment;
    private static final String MAP_URL = "http://tam.cartographie.pro/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DataParser dataParser = DataParser.getDataParser();
        dataParser.init(this);

        navigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        navigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawerFragment.isDrawerOpen()) {
            navigationDrawerFragment.closeDrawer();
        }
    }

    @Override
    public void onDrawerClick(int position) {
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new AllLinesFragment();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack("");
                transaction.commit();
                break;
            case 1:
                fragment = new AllStopFragment();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack("");
                transaction.commit();
                break;
            case 2:
                fragment = new FavoriteStopsFragment();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack("");
                transaction.commit();
                break;
            case 3:
                WebFragment webFragment = WebFragment.newInstance(MAP_URL);
                transaction.replace(R.id.container, webFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case 5:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.fade_scale_out);
                break;
        }
    }
}

