package flying.grub.tamtime;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import flying.grub.tamtime.Fragment.AllLinesFragment;
import flying.grub.tamtime.Fragment.NavigationDrawerFragment;
import flying.grub.tamtime.Navigation.NavigationDrawerCallbacks;
import flying.grub.tamtime.SlidingTab.SlidingTabsBasicFragment;


public class MainActivity extends ActionBarActivity implements NavigationDrawerCallbacks {

    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private static Context context;
    private static DataParser dataParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        dataParser = new DataParser();


        MainActivity.context = getApplicationContext();

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);


    }

    @Override
    public void onResume(){
        super.onResume();  // Always call the superclass method first
        dataParser.getAll();
        dataParser.getTime();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment fragment;
        switch (position) {
            case 0:

                fragment = new AllLinesFragment();
                transaction.replace(R.id.container, fragment);
                transaction.commit();
                break;
            case 1:
                android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                SlidingTabsBasicFragment test = new SlidingTabsBasicFragment();
                t.replace(R.id.container, test);
                t.commit();
                break;
            case 2:
                fragment = new AllLinesFragment();
                transaction.replace(R.id.container, fragment);
                transaction.commit();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    public static DataParser getData(){
        return dataParser;
    }
}
