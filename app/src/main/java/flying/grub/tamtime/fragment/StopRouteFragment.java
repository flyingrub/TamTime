package flying.grub.tamtime.fragment;

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

import flying.grub.tamtime.R;
import flying.grub.tamtime.activity.MainActivity;
import flying.grub.tamtime.adapter.DividerItemDecoration;
import flying.grub.tamtime.adapter.OneLineAdapter;
import flying.grub.tamtime.adapter.OneStopAdapter;

/**
 * Created by fly on 9/19/15.
 */
public class StopRouteFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private OneStopAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBarCircularIndeterminate circularIndeterminate;

    private int stopID;
    private int routePosition;

    public static Fragment newInstance(int stopID, int routePosition) {
        StopRouteFragment f = new StopRouteFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("stopID", stopID);
        args.putInt("routePosition", routePosition);
        f.setArguments(args);
        return f;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopID = getArguments().getInt("stopID");
        routePosition = getArguments().getInt("routePosition");
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_swype_refresh,
                container, false);
        container.addView(view);

        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        circularIndeterminate = (ProgressBarCircularIndeterminate) view.findViewById(R.id.progressBarCircularIndeterminate);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new OneStopAdapter(MainActivity.getData().getStop(stopID));

        recyclerView.setAdapter(adapter);
        adapter.SetOnItemClickListener(new OneStopAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                selectitem(position);
            }
        });

        getActivity().setTitle(MainActivity.getData().getStop(stopID).getName());

        circularIndeterminate.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);

        return view;
    }

    public void selectitem(int i){

    }
}
