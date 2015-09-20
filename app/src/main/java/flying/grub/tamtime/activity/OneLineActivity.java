package flying.grub.tamtime.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import flying.grub.tamtime.fragment.LineRouteFragment;
import flying.grub.tamtime.R;
import flying.grub.tamtime.slidingTab.SlidingTabLayout;


public class OneLineActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;

    private int linePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_sliding_tabs);

        Bundle bundle = getIntent().getExtras();
        linePosition = bundle.getInt("id");

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
        public int count = MainActivity.getData().getLine(linePosition).getRouteCount();
        public OneLinePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int routePosition) {
            return MainActivity.getData().getLine(linePosition).getRoute(routePosition).getDirection();
        }

        @Override
        public int getCount() {
            return this.count;
        }

        @Override
        public Fragment getItem(int position) {
            return LineRouteFragment.newInstance(linePosition, position);
        }
    }
}