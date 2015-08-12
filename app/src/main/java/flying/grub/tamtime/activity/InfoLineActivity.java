package flying.grub.tamtime.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import flying.grub.tamtime.data.WaitForData;
import flying.grub.tamtime.fragment.InfoLineRoute;
import flying.grub.tamtime.R;
import flying.grub.tamtime.slidingTab.SlidingTabLayout;


public class InfoLineActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;

    private int linePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_line_with_tab);

        Bundle bundle = getIntent().getExtras();
        linePosition = bundle.getInt("id");

        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        if (MainActivity.getData().asData()) {
            viewPager.setAdapter(new InfoLinePageAdapter(getSupportFragmentManager()));
        } else {
            new WaitForData(asNewData());
        }

        slidingTabLayout = new SlidingTabLayout(getApplicationContext());
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.myTextPrimaryColor));
        slidingTabLayout.setDividerColors(getResources().getColor(R.color.myPrimaryColor));
        slidingTabLayout.setViewPager(viewPager);
    }

    private Runnable asNewData (){
        return new Runnable(){
            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        viewPager.setAdapter(new InfoLinePageAdapter(getSupportFragmentManager()));
                    }
                });
            }
        };
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
            //return InfoLineRoute.newInstance(linePosition, position);
            return InfoLineRoute.newInstance(linePosition, position);
        }
    }
}