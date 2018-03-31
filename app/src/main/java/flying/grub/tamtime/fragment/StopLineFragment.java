package flying.grub.tamtime.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

import de.greenrobot.event.EventBus;
import flying.grub.tamtime.R;
import flying.grub.tamtime.adapter.OneStopAdapter;
import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.map.Line;
import flying.grub.tamtime.data.map.StopZone;
import flying.grub.tamtime.data.update.MessageUpdate;

public class StopLineFragment extends Fragment {

    private static final String TAG = StopLineFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private OneStopAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBarCircularIndeterminate circularIndeterminate;

    private StopZone stopZone;
    private Line line;

    public static Fragment newInstance(Integer stop_zone_id, int linePosition) {
        StopLineFragment f = new StopLineFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("stop_zone_id", stop_zone_id);
        args.putInt("linePosition", linePosition);
        f.setArguments(args);
        return f;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopZone = Data.getData().getMap().getStopZoneById(getArguments().getInt("stop_zone_id"));
        line = stopZone.getLines().get(getArguments().getInt("linePosition"));
    }

    @Override
    public void onResume(){
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_swype_refresh,
                container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        circularIndeterminate = (ProgressBarCircularIndeterminate) view.findViewById(R.id.progressBarCircularIndeterminate);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);

        recyclerView.setHasFixedSize(true);
        recyclerView.setBackgroundColor(getResources().getColor(R.color.windowBackgroundCard));

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new OneStopAdapter(stopZone.getStops(line));

        recyclerView.setAdapter(adapter);
        adapter.SetOnItemClickListener(new OneStopAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // Unneeded ?
            }
        });

        circularIndeterminate.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Data.getData().update();
            }
        });
        refreshLayout.setColorSchemeResources(R.color.primaryColor);

        return view;
    }

    public void onEvent(MessageUpdate event){
        if (event.type == MessageUpdate.Type.TIMES_UPDATE) {

            refreshLayout.setRefreshing(false);
            stopZone = Data.getData().getMap().getStopZoneById(getArguments().getInt("stop_zone_id"));
            line = stopZone.getLines().get(getArguments().getInt("linePosition"));

            adapter = new OneStopAdapter(stopZone.getStops(line));
            recyclerView.swapAdapter(adapter, true);
        }
    }
}
