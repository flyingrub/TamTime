package flying.grub.tamtime.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import flying.grub.tamtime.R;
import flying.grub.tamtime.activity.OneStopActivity;
import flying.grub.tamtime.adapter.AllStopAdapter;
import flying.grub.tamtime.adapter.DividerItemDecoration;
import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.map.StopZone;

public class AllStopFragment extends Fragment {

    private RecyclerView recyclerView;
    private AllStopAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<StopZone> currentDisplayedStop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_recycler, container, false);
        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getActivity().setTitle(getString(R.string.all_stops));

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity());
        recyclerView.addItemDecoration(itemDecoration);

        currentDisplayedStop = Data.getData().getMap().getStopZones();
        adapter = new AllStopAdapter(currentDisplayedStop);
        recyclerView.setAdapter(adapter);
        adapter.SetOnItemClickListener(new AllStopAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                selectitem(position);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);

        SearchView sv = new SearchView(getActivity());
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new SearchAsync().execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                new SearchAsync().execute(newText);
                return false;
            }
        });
        changeSearchViewTextColor(sv);
        item.setActionView(sv);
    }

    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.textClearColor));
                return;
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void selectitem(int i){
        StopZone s = currentDisplayedStop.get(i);
        Intent intent = new Intent(getActivity(), OneStopActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("stop_zone_id", s.getID());
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.fade_scale_out);
    }

    private class SearchAsync extends AsyncTask<String, String, ArrayList<StopZone>> {
        protected ArrayList<StopZone> doInBackground(String... strings) {
            String s = strings[0];
            return Data.getData().getMap().searchInStops(s, -1);
        }

        protected void onPostExecute(ArrayList<StopZone> stops) {
            currentDisplayedStop = stops;
            recyclerView.swapAdapter(new AllStopAdapter(currentDisplayedStop), false);
        }
    }
}
