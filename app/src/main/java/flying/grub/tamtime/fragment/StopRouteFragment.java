package flying.grub.tamtime.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import flying.grub.tamtime.R;
import flying.grub.tamtime.activity.MainActivity;
import flying.grub.tamtime.activity.TheoriticalActivity;
import flying.grub.tamtime.adapter.OneRouteAdapter;
import flying.grub.tamtime.adapter.OneStopAdapter;
import flying.grub.tamtime.data.DataParser;
import flying.grub.tamtime.data.Line;
import flying.grub.tamtime.data.MessageEvent;
import flying.grub.tamtime.data.Report;
import flying.grub.tamtime.data.ReportType;
import flying.grub.tamtime.data.Stop;
import flying.grub.tamtime.data.StopTimes;

/**
 * Created by fly on 9/19/15.
 */
public class StopRouteFragment extends Fragment {

    private static final String TAG = StopRouteFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private OneStopAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBarCircularIndeterminate circularIndeterminate;

    private Stop stop;
    private Line line;

    public static Fragment newInstance(Integer stopId, int linePosition) {
        StopRouteFragment f = new StopRouteFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("stopId", stopId);
        args.putInt("linePosition", linePosition);
        f.setArguments(args);
        return f;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        stop = DataParser.getDataParser().getStopByOurId(getArguments().getInt("stopId"));
        line = stop.getLines().get(getArguments().getInt("linePosition"));
    }

    @Override
    public void onResume(){
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.report:
                createReportDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        circularIndeterminate = (ProgressBarCircularIndeterminate) view.findViewById(R.id.progressBarCircularIndeterminate);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);

        recyclerView.setHasFixedSize(true);
        recyclerView.setBackgroundColor(getResources().getColor(R.color.windowBackgroundCard));

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new OneStopAdapter(stop.getStopTimeForLine(line.getLineId()));

        recyclerView.setAdapter(adapter);
        adapter.SetOnItemClickListener(new OneStopAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // Unneeded ?
            }
        });

        circularIndeterminate.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DataParser.getDataParser().setupRealTimes(getActivity());
            }
        });
        refreshLayout.setColorSchemeResources(R.color.primaryColor);

        return view;
    }

    private void createReportDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.choose_report_category)
                .items(R.array.report_types)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == 3) {
                            createInputDialog();
                            return;
                        }
                        createConfimationDialog(which);
                        dialog.dismiss();
                    }
                })
                .build();
        dialog.show();
    }

    private void createConfimationDialog(final int position) {
        String content = String.format(getString(R.string.confirm_report), getResources().getStringArray(R.array.report_types)[position], line.getLineId());
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.confirm_report_title)
                .content(content)
                .negativeText(R.string.no)
                .positiveText(R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        DataParser.getDataParser().sendPost(getActivity(), new Report(stop, ReportType.reportFromNum(position), null)); // message == null for now;
                        dialog.dismiss();
                    }
                }).build();
        dialog.show();
    }

    private void createInputDialog() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.input_report)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(R.string.none, R.string.none, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        createConfimationDialog(4);
                    }
                }).show();
    }

    public void onEvent(MessageEvent event){
        if (event.type == MessageEvent.Type.TIMESUPDATE) {
            refreshLayout.setRefreshing(false);
            stop = DataParser.getDataParser().getStopByOurId(getArguments().getInt("stopId"));
            line = stop.getLines().get(getArguments().getInt("linePosition"));

            adapter = new OneStopAdapter(stop.getStopTimeForLine(line.getLineId()));
            recyclerView.swapAdapter(adapter, false);
        }
    }
}
