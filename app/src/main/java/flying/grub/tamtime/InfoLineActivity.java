package flying.grub.tamtime;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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


public class InfoLineActivity extends ActionBarActivity {

    private SlidingTabLayout mSlidingTabLayout;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private int lineId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_line_with_tab);

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

            mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext());
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