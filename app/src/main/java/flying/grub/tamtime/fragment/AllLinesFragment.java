package flying.grub.tamtime.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import flying.grub.tamtime.activity.OneLineActivity;
import flying.grub.tamtime.adapter.AllLinesAdapter;
import flying.grub.tamtime.adapter.DividerItemDecoration;
import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.map.Line;
import flying.grub.tamtime.data.update.MessageUpdate;
import flying.grub.tamtime.R;

/**
 * Created by fly on 09/02/15.
 */
public class AllLinesFragment extends Fragment {

    private RecyclerView recyclerView;
    private AllLinesAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Line> lines;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_recycler, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getActivity().setTitle(getString(R.string.all_lines));

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity());
        recyclerView.addItemDecoration(itemDecoration);
        // specify an adapter (see also next example)

        lines = Data.getData().getMap().getLines();
        adapter = new AllLinesAdapter(lines);
        recyclerView.setAdapter(adapter);
        adapter.SetOnItemClickListener(new AllLinesAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v , int position) {
                selectitem(position);
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void selectitem(int i){
        Intent intent = new Intent(getActivity(), OneLineActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id", i);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.fade_scale_out);
    }



    public void onEvent(MessageUpdate event){
        if (event.type == MessageUpdate.Type.LINES_UPDATE) {
            lines = Data.getData().getMap().getLines();
            recyclerView.swapAdapter(new AllLinesAdapter(lines), false);
        }
    }
}
