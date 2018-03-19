package flying.grub.tamtime.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;


import de.greenrobot.event.EventBus;
import flying.grub.tamtime.activity.OneStopActivity;
import flying.grub.tamtime.adapter.DividerItemDecoration;
import flying.grub.tamtime.R;
import flying.grub.tamtime.adapter.OneRouteAdapter;
import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.map.Direction;
import flying.grub.tamtime.data.update.MessageUpdate;

public class LineRouteFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private OneRouteAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBarCircularIndeterminate circularIndeterminate;

    private int linePosition;
    private int routePosition;
    private Direction direction;

    private boolean isTheoritical;

    private static final String TAG = LineRouteFragment.class.getSimpleName();

    public static Fragment newInstance(int linePosition, int routePosition) {
        LineRouteFragment f = new LineRouteFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("linePosition", linePosition);
        args.putInt("routePosition", routePosition);
        args.putBoolean("isTheoritical", false);
        f.setArguments(args);
        return f;
    }

    public static Fragment newInstance(int linePosition, int routePosition, boolean isTheoritical) {
        LineRouteFragment f = new LineRouteFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("linePosition", linePosition);
        args.putInt("routePosition", routePosition);
        args.putBoolean("isTheoritical", isTheoritical);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        linePosition = getArguments().getInt("linePosition");
        routePosition = getArguments().getInt("routePosition");
        isTheoritical = getArguments().getBoolean("isTheoritical");
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

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity());
        recyclerView.addItemDecoration(itemDecoration);

        direction = Data.getData().getMap().getLine(linePosition).getDirections().get(routePosition);

        adapter = new OneRouteAdapter(direction.getStops(), getContext(), isTheoritical);

        recyclerView.setAdapter(adapter);
        adapter.SetOnItemClickListener(new OneRouteAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                selectitem(position);
            }
        });

        if (isTheoritical) {
            getActivity().setTitle("Théorique : Ligne " + Data.getData().getMap().getLine(linePosition).getShortName());
        } else {
            getActivity().setTitle("Ligne " + Data.getData().getMap().getLine(linePosition).getShortName());
        }

        circularIndeterminate.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Data.getData().update();
            }
        });
        refreshLayout.setColorSchemeResources(R.color.primaryColor);

        setHasOptionsMenu(true);
        return view;
    }

    public void selectitem(int i){
        Intent intent = new Intent(getActivity(), OneStopActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("stop_zone_id", direction.getStops().get(i).getStopZone().getID());
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.fade_scale_out);
    }

    public void onEvent(MessageUpdate event){
        if (event.type == MessageUpdate.Type.TIMES_UPDATE) {
            direction = Data.getData().getMap().getLine(linePosition).getDirections().get(routePosition);
            adapter = new OneRouteAdapter(direction.getStops(), getContext(), isTheoritical);
            adapter.SetOnItemClickListener(new OneRouteAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    selectitem(position);
                }
            });
            refreshLayout.setRefreshing(false);
            recyclerView.swapAdapter(adapter, true);
        }
    }

}
