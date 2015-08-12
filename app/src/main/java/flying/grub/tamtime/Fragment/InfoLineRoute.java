package flying.grub.tamtime.Fragment;


import android.os.AsyncTask;
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

import flying.grub.tamtime.Adapter.DividerItemDecoration;
import flying.grub.tamtime.Adapter.InfoLineAdapter;
import flying.grub.tamtime.Data.WaitForData;
import flying.grub.tamtime.MainActivity;
import flying.grub.tamtime.R;

/**
 * Created by fly on 25/03/15.
 */
public class InfoLineRoute extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private InfoLineAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBarCircularIndeterminate circularIndeterminate;

    private int linePosition;
    private int routePosition;
    private boolean isfirstime = true;

    private static final String TAG = InfoLineRoute.class.getSimpleName();

    /**
     * Create a new instance of CountingFragment, providing "num"
     * as an argument.
     */
    public static Fragment newInstance(int linePosition, int routePosition) {
        InfoLineRoute f = new InfoLineRoute();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("linePosition", linePosition);
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
        linePosition = getArguments().getInt("linePosition");
        routePosition = getArguments().getInt("routePosition");
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swype_refresh_view,
                container, false);
        container.addView(view);

        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        circularIndeterminate = (ProgressBarCircularIndeterminate) view.findViewById(R.id.progressBarCircularIndeterminate);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity());
        recyclerView.addItemDecoration(itemDecoration);


        if (MainActivity.getData().asData()) {
            adapter = new InfoLineAdapter(linePosition, routePosition);
        } else {
            new WaitForData(asNewDataAdapter());
        }


        recyclerView.setAdapter(adapter);
        adapter.SetOnItemClickListener(new InfoLineAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                selectitem(position);
            }
        });
        new WaitForData(asNewData()).execute();

        return view;
    }

    public void selectitem(int i){

    }

    private Runnable asNewDataAdapter (){
        return new Runnable(){
            @Override
            public void run() {
                adapter = new InfoLineAdapter(linePosition, routePosition);
            }
        };
    }

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
    }

}
