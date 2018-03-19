package flying.grub.tamtime.fragment;

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

import de.greenrobot.event.EventBus;
import flying.grub.tamtime.R;
import flying.grub.tamtime.adapter.HomeAdapter;
import flying.grub.tamtime.data.persistence.FavoriteStopLine;
import flying.grub.tamtime.data.map.Line;
import flying.grub.tamtime.data.update.MessageUpdate;
import flying.grub.tamtime.data.map.StopZone;
import flying.grub.tamtime.data.update.UpdateRunnable;
import flying.grub.tamtime.layout.FavHomeView;
import flying.grub.tamtime.layout.SearchView;

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    public FavHomeView favHomeView;
    public SearchView searchView;

    private RecyclerView favStopLinesRecycler;
    private HomeAdapter homeAdapter;
    private FavoriteStopLine favoriteStopLine;

    private UpdateRunnable updateRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_recycler, container, false);
        setHasOptionsMenu(true);

        getActivity().setTitle(getString(R.string.home));

        favoriteStopLine = new FavoriteStopLine(getContext());

        favHomeView = new FavHomeView(getActivity());
        favHomeView.setUpdateStopLine(new FavHomeView.AddStopLine() {
            @Override
            public void update(StopZone stop, Line line) {
                favoriteStopLine.addLineStop(line, stop);
                setupFavStopLine();
            }
        });

        searchView = new SearchView(getActivity());

        favStopLinesRecycler = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        favStopLinesRecycler.setLayoutManager(layoutManager);
        favStopLinesRecycler.setItemAnimator(new DefaultItemAnimator());
        favStopLinesRecycler.setHasFixedSize(false);
        favStopLinesRecycler.setBackgroundColor(getResources().getColor(R.color.windowBackgroundCard));
        setupFavStopLine();

        return view;
    }

    public void setupFavStopLine() {
        homeAdapter = new HomeAdapter(favoriteStopLine.getFavStopLines(), getActivity(), favHomeView, searchView);
        homeAdapter.SetOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                PopupMenu popup = new PopupMenu(getActivity(), view);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.remove_from_home, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        favoriteStopLine.removeLineStop(position -1);
                        setupFavStopLine();
                        return true;
                    }
                });

                popup.show();
            }
        });
        favStopLinesRecycler.swapAdapter(homeAdapter, true);
    }

    @Override
    public void onResume(){
        super.onResume();
        EventBus.getDefault().register(this);
        updateRunnable = new UpdateRunnable();
        updateRunnable.run();

    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        updateRunnable.stop();
        super.onPause();
    }

    public void onEvent(MessageUpdate event){
        if (event.type == MessageUpdate.Type.TIMES_UPDATE) {
            setupFavStopLine();
        }
    }
}
