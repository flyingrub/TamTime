package flying.grub.tamtime.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import flying.grub.tamtime.R;
import flying.grub.tamtime.data.FavoriteStops;
import flying.grub.tamtime.fragment.FavoriteStopsFragment;
import flying.grub.tamtime.fragment.LineRouteFragment;
import flying.grub.tamtime.slidingTab.SlidingTabLayout;

/**
 * Created by fly on 9/21/15.
 */
public class FavoriteActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_sliding_tabs);

        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new OneLinePageAdapter(getSupportFragmentManager()));


        slidingTabLayout = new SlidingTabLayout(getApplicationContext());
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.textClearColor));
        slidingTabLayout.setDividerColors(getResources().getColor(R.color.primaryColor));
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public void onResume(){
        super.onResume();
    }


    public class OneLinePageAdapter extends FragmentStatePagerAdapter {
        public OneLinePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int pos) {
            if (pos == 0) {
                return getString(R.string.all_stops);
            } else {
                return getString(R.string.all_itinary);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            return new FavoriteStopsFragment();
        }
    }
}