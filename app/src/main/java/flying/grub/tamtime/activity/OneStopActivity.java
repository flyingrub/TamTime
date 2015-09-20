package flying.grub.tamtime.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import flying.grub.tamtime.R;
import flying.grub.tamtime.fragment.LineRouteFragment;
import flying.grub.tamtime.fragment.StopRouteFragment;
import flying.grub.tamtime.slidingTab.SlidingTabLayout;

/**
 * Created by fly on 9/19/15.
 */
public class OneStopActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;

    private int stopID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_sliding_tabs);

        Bundle bundle = getIntent().getExtras();
        stopID = bundle.getInt("stopID");
        Log.d("STOP", "" + stopID);

        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setAdapter(new OneStopPageAdapter(getSupportFragmentManager()));


        slidingTabLayout = new SlidingTabLayout(getApplicationContext());
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.textClearColor));
        slidingTabLayout.setDividerColors(getResources().getColor(R.color.primaryColor));
        slidingTabLayout.setViewPager(viewPager);
    }

    public class OneStopPageAdapter extends FragmentStatePagerAdapter {
        public int count = 1;
        public OneStopPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int routePosition) {
            return MainActivity.getData().getStop(stopID).getName();
        }

        @Override
        public int getCount() {
            return this.count;
        }

        @Override
        public Fragment getItem(int position) {
            return StopRouteFragment.newInstance(stopID, position);
        }
    }
}
