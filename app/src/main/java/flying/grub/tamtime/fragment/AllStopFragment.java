package flying.grub.tamtime.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import flying.grub.tamtime.R;
import flying.grub.tamtime.activity.MainActivity;
import flying.grub.tamtime.activity.OneLineActivity;
import flying.grub.tamtime.adapter.AllStopAdapter;
import flying.grub.tamtime.adapter.DividerItemDecoration;
import flying.grub.tamtime.data.Stop;
import flying.grub.tamtime.data.WaitForData;

/**
 * Created by fly on 09/02/15.
 */
public class AllStopFragment extends Fragment {

    private RecyclerView recyclerView;
    private AllStopAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

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


        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity());
        recyclerView.addItemDecoration(itemDecoration);

        adapter = new AllStopAdapter(MainActivity.getData().getStopArrayList());
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
                ArrayList<Stop> stops = MainActivity.getData().searchInStops(query);
                recyclerView.swapAdapter(new AllStopAdapter(stops), false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Stop> stops = MainActivity.getData().searchInStops(newText);
                recyclerView.swapAdapter(new AllStopAdapter(stops), false);
                return false;
            }
        });
        item.setActionView(sv);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void selectitem(int i){
        Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
    }

}
