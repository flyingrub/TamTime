package flying.grub.tamtime.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import flying.grub.tamtime.R;
import flying.grub.tamtime.activity.MainActivity;
import flying.grub.tamtime.activity.OneStopActivity;
import flying.grub.tamtime.adapter.AllStopAdapter;
import flying.grub.tamtime.adapter.DividerItemDecoration;
import flying.grub.tamtime.adapter.FavoriteAdapter;
import flying.grub.tamtime.adapter.NavigationDrawerAdapter;
import flying.grub.tamtime.data.FavoriteStops;
import flying.grub.tamtime.data.Stop;

/**
 * Created by fly on 9/21/15.
 */
public class FavoriteStopsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoriteAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FavoriteStops favoriteStops;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.view_recycler, container, false);
        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setBackgroundColor(getResources().getColor(R.color.windowBackgroundCard));

        favoriteStops = new FavoriteStops(getActivity());
        getActivity().setTitle(getString(R.string.all_stops_favs));

        adapter = new FavoriteAdapter(favoriteStops.getFavoriteStop());
        recyclerView.setAdapter(adapter);
        adapter.SetOnItemClickListener(new FavoriteAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                selectitem(position);
            }
        });

        adapter.SetOnMenuClickListener(new FavoriteAdapter.OnMenuClickListener() {
            @Override
            public void onItemClick(View v, final int position) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getActivity(), v);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.delete_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        favoriteStops.remove(position);
                        recyclerView.swapAdapter(new FavoriteAdapter(favoriteStops.getFavoriteStop()), false);
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

        return view;
    }

    public void selectitem(int i){
        Stop s = favoriteStops.getFavoriteStop().get(i);
        if (MainActivity.getData().asData()) {
            Intent intent = new Intent(getActivity(), OneStopActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("stopName", s.getName());
            intent.putExtras(bundle);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.fade_scale_out);
        } else {
            Toast.makeText(getActivity(), getString(R.string.waiting_for_network), Toast.LENGTH_SHORT).show();
        }
    }
}
