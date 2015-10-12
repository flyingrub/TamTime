package flying.grub.tamtime.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import flying.grub.tamtime.R;
import flying.grub.tamtime.data.DataParser;
import flying.grub.tamtime.data.FavoriteStops;
import flying.grub.tamtime.data.Stop;
import flying.grub.tamtime.fragment.StopRouteFragment;
import flying.grub.tamtime.slidingTab.SlidingTabLayout;

/**
 * Created by fly on 9/19/15.
 */
public class OneStopActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;

    private FavoriteStops favoriteStops;

    private int stopId;
    private Stop stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slidingtabs);
        DataParser.getDataParser().update(getBaseContext());

        Bundle bundle = getIntent().getExtras();
        stopId = bundle.getInt("stopId");
        stop = DataParser.getDataParser().getStopByOurId(stopId);

        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(stop.getName());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setAdapter(new OneStopPageAdapter(getSupportFragmentManager()));

        favoriteStops = new FavoriteStops(getApplicationContext());

        slidingTabLayout = new SlidingTabLayout(getApplicationContext());
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.textClearColor));
        slidingTabLayout.setDividerColors(getResources().getColor(R.color.primaryColor));
        slidingTabLayout.setViewPager(viewPager);

    }

    @Override
    public void onPause(){
        super.onPause();
        if (isFinishing()) overridePendingTransition(R.anim.fade_scale_in, R.anim.slide_to_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stop_menu, menu);
        MenuItem item = menu.findItem(R.id.action_favorite);
        if (favoriteStops.isInFav(stop)) {
            item.setIcon(R.drawable.ic_star_white_24dp);
        } else {
            item.setIcon(R.drawable.ic_star_border_white_24dp);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                if (favoriteStops.isInFav(stop)) {
                    favoriteStops.remove(stop);
                    item.setIcon(R.drawable.ic_star_border_white_24dp);
                } else {
                    favoriteStops.add(stop);
                    item.setIcon(R.drawable.ic_star_white_24dp);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class OneStopPageAdapter extends FragmentStatePagerAdapter {
        public OneStopPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int linePosition) {
            return getString(R.string.one_line) + " " + stop.getLines().get(linePosition).getLineId();
        }

        @Override
        public int getCount() {
            return stop.getLines().size();
        }

        @Override
        public Fragment getItem(int position) {
            return StopRouteFragment.newInstance(stop.getOurId(), position);
        }
    }
}
