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
import android.widget.Toast;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;


import flying.grub.tamtime.activity.OneStopActivity;
import flying.grub.tamtime.adapter.DividerItemDecoration;
import flying.grub.tamtime.activity.MainActivity;
import flying.grub.tamtime.R;
import flying.grub.tamtime.adapter.OneRouteAdapter;
import flying.grub.tamtime.data.Route;

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

    private static final String TAG = LineRouteFragment.class.getSimpleName();

    public static Fragment newInstance(int linePosition, int routePosition) {
        LineRouteFragment f = new LineRouteFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("linePosition", linePosition);
        args.putInt("routePosition", routePosition);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        linePosition = getArguments().getInt("linePosition");
        routePosition = getArguments().getInt("routePosition");
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

        route = MainActivity.getData().getLine(linePosition).getRoutes().get(routePosition);
        adapter = new OneRouteAdapter(route.getStpTimes());

        recyclerView.setAdapter(adapter);
        adapter.SetOnItemClickListener(new OneRouteAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                selectitem(position);
            }
        });

        getActivity().setTitle("Ligne " + MainActivity.getData().getLine(linePosition).getLineId());

        circularIndeterminate.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);

        return view;
    }

    public void selectitem(int i){
        if (MainActivity.getData().asData()) {
            Intent intent = new Intent(getActivity(), OneStopActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("stopName", route.getStpTimes().get(i).getStop().getName());
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), getString(R.string.waiting_for_network), Toast.LENGTH_SHORT).show();
        }
    }


    /*
    private Runnable asNewData (){
        return new Runnable(){
            public void run(){
                if (isfirstime){
                    firstData();
                }else{
                    refreshLayout.setRefreshing(false);
                    adapter.refresh();
                }
            }
        };
    }

    public void firstData(){
        getActivity().setTitle("Ligne " + MainActivity.getData().getLine(linePosition).getLineId());

        circularIndeterminate.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new WaitForData(asNewData()).execute();
            }
        });
        refreshLayout.setColorSchemeResources(R.color.myPrimaryColor);

        isfirstime = false;
    }*/

}
