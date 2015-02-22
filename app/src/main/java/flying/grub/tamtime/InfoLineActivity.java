package flying.grub.tamtime;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import flying.grub.tamtime.Adapter.DividerItemDecoration;
import flying.grub.tamtime.Adapter.InfoLineAdapter;
import flying.grub.tamtime.SlidingTab.SlidingTabLayout;


public class InfoLineActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout refreshLayout;
    private SlidingTabLayout mSlidingTabLayout;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private int lineId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.info_line_with_tab);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);

        Bundle bundle = getIntent().getExtras();
        lineId = bundle.getInt("id");

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Ligne " + MainActivity.getData().getLine(lineId).getLineId());

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new InfoLinePageAdapter());

        mSlidingTabLayout = new SlidingTabLayout(MainActivity.getAppContext());
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.myTextPrimaryColor));
        mSlidingTabLayout.setDividerColors(getResources().getColor(R.color.myPrimaryColor));
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void onResume(){
        super.onResume();  // Always call the superclass method first
    }


    @Override public void setContentView(int layoutResID) {
        View v = getLayoutInflater().inflate(layoutResID, refreshLayout, false);
        setContentView(v);
    }

    @Override public void setContentView(View view) {
        setContentView(view, view.getLayoutParams());
    }

    @Override public void setContentView(View view, ViewGroup.LayoutParams params) {
        refreshLayout.addView(view, params);
        initSwipeOptions();
    }

    private void initSwipeOptions() {
        refreshLayout.setOnRefreshListener(this);
        setAppearance();
        disableSwipe();
    }

    private void setAppearance() {
        refreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    /**
     * It shows the SwipeRefreshLayout progress
     */
    public void showSwipeProgress() {
        refreshLayout.setRefreshing(true);
    }

    /**
     * It shows the SwipeRefreshLayout progress
     */
    public void hideSwipeProgress() {
        refreshLayout.setRefreshing(false);
    }

    /**
     * Enables swipe gesture
     */
    public void enableSwipe() {
        refreshLayout.setEnabled(true);
    }

    /**
     * Disables swipe gesture. It prevents manual gestures but keeps the option tu show
     * refreshing programatically.
     */
    public void disableSwipe() {
        refreshLayout.setEnabled(false);
    }

    /**
     * It must be overriden by parent classes if manual swipe is enabled.
     */
    @Override public void onRefresh() {
        MainActivity.getData().getAll();
    }

    class InfoLinePageAdapter extends PagerAdapter {

        private RecyclerView mRecyclerView;
        private InfoLineAdapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;

        @Override
        public int getCount() {
            return MainActivity.getData().getLine(lineId).getRouteCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return MainActivity.getData().getLine(lineId).getRoute(position).getDirection();

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate a new layout from our resources
            View view = getLayoutInflater().inflate(R.layout.recycler_view,
                    container, false);
            // Add the newly created View to the ViewPager
            container.addView(view);

            mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            mLayoutManager = new LinearLayoutManager(MainActivity.getAppContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(MainActivity.getAppContext());
            mRecyclerView.addItemDecoration(itemDecoration);
            // specify an adapter (see also next example)

            mAdapter = new InfoLineAdapter(lineId, position);

            mRecyclerView.setAdapter(mAdapter);
            mAdapter.SetOnItemClickListener(new InfoLineAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(View v , int position) {
                    //selectitem(position);
                }
            });

            return view;
        }

        public void selectitem(int i){
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }
}