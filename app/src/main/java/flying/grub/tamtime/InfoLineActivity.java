package flying.grub.tamtime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import flying.grub.tamtime.Adapter.InfoLineAdapter;
import flying.grub.tamtime.Fragment.InfoLineRoute;
import flying.grub.tamtime.SlidingTab.SlidingTabLayout;


public class InfoLineActivity extends ActionBarActivity {

    private InfoLineAdapter mAdapter;
    private SwipeRefreshLayout refreshLayout;
    private SlidingTabLayout mSlidingTabLayout;
    private Toolbar mToolbar;
    private ViewPager mViewPager;

    private int linePosition;
    private boolean isfirstime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_line_with_tab);

        Bundle bundle = getIntent().getExtras();
        linePosition = bundle.getInt("id");

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new InfoLinePageAdapter(getSupportFragmentManager()));

        mSlidingTabLayout = new SlidingTabLayout(MainActivity.getAppContext());
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.myTextPrimaryColor));
        mSlidingTabLayout.setDividerColors(getResources().getColor(R.color.myPrimaryColor));
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void onResume(){
        super.onResume();
    }


    public class InfoLinePageAdapter extends FragmentStatePagerAdapter {
        public InfoLinePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int routePosition) {
            return MainActivity.getData().getLine(linePosition).getRoute(routePosition).getDirection();
        }

        @Override
        public int getCount() {
            return MainActivity.getData().getLine(linePosition).getRouteCount();
        }

        @Override
        public Fragment getItem(int position) {
            return InfoLineRoute.newInstance(linePosition, position);
        }
    }
}