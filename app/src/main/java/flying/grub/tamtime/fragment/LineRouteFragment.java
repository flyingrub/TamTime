package flying.grub.tamtime.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;


import android.os.Handler;

import de.greenrobot.event.EventBus;
import flying.grub.tamtime.activity.OneStopActivity;
import flying.grub.tamtime.activity.TheoriticalActivity;
import flying.grub.tamtime.adapter.DividerItemDecoration;
import flying.grub.tamtime.activity.MainActivity;
import flying.grub.tamtime.R;
import flying.grub.tamtime.adapter.OneRouteAdapter;
import flying.grub.tamtime.data.DataParser;
import flying.grub.tamtime.data.MessageEvent;
import flying.grub.tamtime.data.Route;
import flying.grub.tamtime.data.UpdateRunnable;

/**
 * Created by fly on 25/03/15.
 */
public class LineRouteFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private OneRouteAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBarCircularIndeterminate circularIndeterminate;

    private int linePosition;
    private int routePosition;
    private Route route;

    private boolean isTheoritical;

    private UpdateRunnable updateRunnable;

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
        updateRunnable = new UpdateRunnable(getContext());
        updateRunnable.run();
    }

    @Override
    public void onPause() {
        super.onPause();
        updateRunnable.stop();
    }

    @Override
    public void onStop() {
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

        route = DataParser.getDataParser().getLine(linePosition).getRoutes().get(routePosition);

        adapter = new OneRouteAdapter(route.getStpTimes(), isTheoritical);

        recyclerView.setAdapter(adapter);
        adapter.SetOnItemClickListener(new OneRouteAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                selectitem(position);
            }
        });

        if (isTheoritical) {
            getActivity().setTitle("Thèorique : Ligne " + DataParser.getDataParser().getLine(linePosition).getLineId());
        } else {
            getActivity().setTitle("Ligne " + DataParser.getDataParser().getLine(linePosition).getLineId());
        }

        circularIndeterminate.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DataParser.getDataParser().setupRealTimes(getActivity());
                DataParser.getDataParser().setupReport(getActivity());
            }
        });
        refreshLayout.setColorSchemeResources(R.color.primaryColor);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!isTheoritical) {
            inflater.inflate(R.menu.line_menu, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.go_theoritical:
                if (!isTheoritical) {
                    Intent intent = new Intent(getActivity(), TheoriticalActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("linePosition", linePosition);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.fade_scale_out);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void selectitem(int i){
        Intent intent = new Intent(getActivity(), OneStopActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("stopId", route.getStpTimes().get(i).getStop().getOurId());
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.fade_scale_out);
    }

    public void onEvent(MessageEvent event){
        if (event.type == MessageEvent.Type.TIMESUPDATE) {
            getActivity().invalidateOptionsMenu();
            route = DataParser.getDataParser().getLine(linePosition).getRoutes().get(routePosition);
            adapter = new OneRouteAdapter(route.getStpTimes(), isTheoritical);
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
