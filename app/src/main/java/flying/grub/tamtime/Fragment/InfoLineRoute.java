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
import flying.grub.tamtime.MainActivity;
import flying.grub.tamtime.R;

/**
 * Created by fly on 25/03/15.
 */
public class InfoLineRoute extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private InfoLineAdapter mAdapter;
    private SwipeRefreshLayout refreshLayout;

    private int linePosition;
    private int routePosition;
    private boolean isfirstime = true;

    private static final String TAG = InfoLineRoute.class.getSimpleName();

    /**
     * Create a new instance of CountingFragment, providing "num"
     * as an argument.
     */
    public static Fragment newInstance(int linePosition, int routePosition) {
        Fragment f = new Fragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("linePosition", linePosition);
        args.putInt("routePosition", routePosition);
        f.setArguments(args);
        Log.d(TAG, "lyo");
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
        Log.d(TAG, "yo");
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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(MainActivity.getAppContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(MainActivity.getAppContext());
        mRecyclerView.addItemDecoration(itemDecoration);


        mAdapter = new InfoLineAdapter(linePosition, routePosition);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener(new InfoLineAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v , int position) {
                selectitem(position);
            }
        });

        return view;
    }

    public void selectitem(int i){

    }

    public void asNewData(){
        if (isfirstime){
            firstData();
        }else{
            refreshLayout.setRefreshing(false);
            mAdapter.refresh();
        }
    }

    public void firstData(){
        getActivity().setTitle("Ligne " + MainActivity.getData().getLine(linePosition).getLineId());
        ProgressBarCircularIndeterminate progress = (ProgressBarCircularIndeterminate) getView().findViewById(R.id.progressBarCircularIndeterminate);
        refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

        progress.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity.getData().getAll();
                new WaitForData().execute();
            }
        });
        refreshLayout.setColorSchemeResources(R.color.myPrimaryColor);

        isfirstime = false;
    }

    class WaitForData extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... unused) {
            try {
                while (!MainActivity.getData().asData()) {
                    Thread.sleep(500);
                }
            } catch (InterruptedException t) {
                // Gérer l'exception et terminer le traitement
                return ("The sleep operation failed");
            }
            return ("return object when task is finished");
        }

        // Surcharge de la méthode onPostExecute (s'exécute dans la Thread de l'IHM)
        @Override
        protected void onPostExecute(String message) {
            asNewData();
        }
    }
}
