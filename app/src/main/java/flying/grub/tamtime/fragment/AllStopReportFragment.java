package flying.grub.tamtime.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import flying.grub.tamtime.R;
import flying.grub.tamtime.activity.OneStopActivity;
import flying.grub.tamtime.adapter.AllStopReportAdapter;
import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.map.StopZone;

public class AllStopReportFragment extends Fragment {

    private RecyclerView recyclerView;
    private AllStopReportAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<StopZone> stops;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.view_recycler_load, container, false);
        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.progress);
        linearLayout.setVisibility(View.GONE);
        TextView textView = (TextView) view.findViewById(R.id.empty_view);
        textView.setText(getString(R.string.no_report));

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setBackgroundColor(getResources().getColor(R.color.windowBackgroundCard));

        stops = Data.getData().getMap().getAllReportStop();
        getActivity().setTitle(getString(R.string.reports));

        if (stops.size() == 0) {
            textView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return view;
        } else {
            adapter = new AllStopReportAdapter(stops);
            recyclerView.setAdapter(adapter);
            adapter.SetOnItemClickListener(new AllStopReportAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    selectitem(position);
                }
            });
            return view;
        }
    }

    public void selectitem(int i) {
        StopZone s = stops.get(i);
        Intent intent = new Intent(getActivity(), OneStopActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("stop_zone_id", s.getID());
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.fade_scale_out);
    }
}