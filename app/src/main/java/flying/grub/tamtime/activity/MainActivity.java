package flying.grub.tamtime.activity;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import flying.grub.tamtime.data.DataParser;
import flying.grub.tamtime.fragment.AllLinesFragment;
import flying.grub.tamtime.fragment.NavigationDrawerFragment;
import flying.grub.tamtime.navigation.NavigationDrawerCallbacks;
import flying.grub.tamtime.R;

public class MainActivity extends AppCompatActivity implements NavigationDrawerCallbacks {

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
        dataParser = new DataParser(getApplicationContext());


        MainActivity.context = getApplicationContext();

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
    }

    @Override
    public void onResume(){
        super.onResume();  // Always call the superclass method first
        dataParser.getAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
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
                Intent intent = new Intent(this, InfoLineActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", 1);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case 2:
                fragment = new AllLinesFragment();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack("");
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

    public static DataParser getData(){
        return dataParser;
    }
}
