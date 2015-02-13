package flying.grub.tamtime.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import flying.grub.tamtime.Adapter.DividerItemDecoration;
import flying.grub.tamtime.Adapter.InfoLineAdapter;
import flying.grub.tamtime.MainActivity;
import flying.grub.tamtime.R;
import flying.grub.tamtime.SlidingTab.SlidingTabLayout;


public class InfoLineFragment extends Fragment {

    public static InfoLineFragment newInstance(int line) {
        InfoLineFragment fragmentDemo = new InfoLineFragment();
        Bundle args = new Bundle();
        args.putInt("line", line);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    private SlidingTabLayout mSlidingTabLayout;

    private ViewPager mViewPager;
    private int lineId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.lineId = getArguments().getInt("line", 1);
        return inflater.inflate(R.layout.info_line_with_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new InfoLinePageAdapter());

        mSlidingTabLayout = new SlidingTabLayout(MainActivity.getAppContext());
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
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
            View view = getActivity().getLayoutInflater().inflate(R.layout.recycler_view,
                    container, false);
            // Add the newly created View to the ViewPager
            container.addView(view);

            mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity());
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