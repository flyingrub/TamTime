package flying.grub.tamtime.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.widgets.Dialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

import de.greenrobot.event.EventBus;
import flying.grub.tamtime.R;
import flying.grub.tamtime.adapter.DividerItemDecoration;
import flying.grub.tamtime.adapter.ReportAdapter;
import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.mark.MarkEvent;
import flying.grub.tamtime.data.persistence.FavoriteStopLine;
import flying.grub.tamtime.data.persistence.FavoriteStops;
import flying.grub.tamtime.data.map.Line;
import flying.grub.tamtime.data.map.StopZone;
import flying.grub.tamtime.data.real_time.RealTimeToUpdate;
import flying.grub.tamtime.data.update.MessageUpdate;
import flying.grub.tamtime.data.report.Report;
import flying.grub.tamtime.data.report.ReportType;
import flying.grub.tamtime.data.update.UpdateRunnable;
import flying.grub.tamtime.fragment.StopLineFragment;
import flying.grub.tamtime.slidingTab.SlidingTabLayout;


public class OneStopActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;

    private FavoriteStops favoriteStops;
    private FavoriteStopLine favoriteStopLine;

    private int stop_zone_id;
    private StopZone stop;

    private UpdateRunnable updateRunnable;
    private MaterialDialog current_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slidingtabs);

        Bundle bundle = getIntent().getExtras();
        stop_zone_id = bundle.getInt("stop_zone_id");
        stop = Data.getData().getMap().getStopZoneById(stop_zone_id);

        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        StringBuilder title = new StringBuilder();
        title.append(stop.getName());

        if(stop.getMark() != -1) //Default mark, no one have mark this stop
            if(stop.getMark() % 1 == 0)
                title.append(" (" + (int) stop.getMark() + " / 5)");
            else {
                DecimalFormat decimalFormat = new DecimalFormat("#.0");
                title.append(" (" + decimalFormat.format(stop.getMark()) + " / " + MarkEvent.MARK_LIMIT + ")");
            }

        getSupportActionBar().setTitle(title.toString());

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setAdapter(new OneStopPageAdapter(getSupportFragmentManager()));

        favoriteStops = new FavoriteStops(getApplicationContext());
        favoriteStopLine = new FavoriteStopLine(getApplicationContext());

        slidingTabLayout = new SlidingTabLayout(getApplicationContext());
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.textClearColor));
        slidingTabLayout.setDividerColors(getResources().getColor(R.color.primaryColor));
        slidingTabLayout.setViewPager(viewPager);

        /*
        School project - Weather
        Call weather api only if necessary because of very limited calls permission (free offer : 10/min, 500/day)
         */
        Data.getData().getWeatherEvent().getWeather(stop.getID());
    }


    @Override
    public void onResume(){
        super.onResume();
        EventBus.getDefault().register(this);
        updateRunnable = new UpdateRunnable();
        updateRunnable.run();
        Data.getData().setToUpdate(new RealTimeToUpdate(stop.getStops()));
    }

    @Override
    public void onPause(){
        super.onPause();
        EventBus.getDefault().unregister(this);
        updateRunnable.stop();
        if (isFinishing()) overridePendingTransition(R.anim.fade_scale_in, R.anim.slide_to_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stop_menu, menu);
        getMenuInflater().inflate(R.menu.add_to_home, menu);
        MenuItem item = menu.findItem(R.id.action_favorite);
        if (favoriteStops.isInFav(stop)) {
            item.setIcon(R.drawable.ic_star_white_24dp);
        } else {
            item.setIcon(R.drawable.ic_star_border_white_24dp);
        }
        if (stop.getReports().size() > 0) {
            getMenuInflater().inflate(R.menu.alert_report_item, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                if (favoriteStops.isInFav(stop)) {
                    favoriteStops.remove(stop);
                    item.setIcon(R.drawable.ic_star_border_white_24dp);
                } else {
                    favoriteStops.add(stop);
                    item.setIcon(R.drawable.ic_star_white_24dp);
                }
                return true;
            case R.id.report:
                choiceReportDialog();
                return true;
            case R.id.report_warn:
                createAllReportDialog();
                return true;
            case R.id.action_add_line_on_home:
                ArrayList<CharSequence> lines = new ArrayList<>();
                for (Line l : stop.getLines()) {
                    lines.add("Ligne " + l.getShortName());
                }
                CharSequence[] lineString = lines.toArray(new CharSequence[lines.size()]);

                MaterialDialog dialog = new MaterialDialog.Builder(OneStopActivity.this)
                        .title(R.string.line_choice)
                        .items(lineString)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                StopZone s = stop;
                                Line l = s.getLines().get(which);
                                favoriteStopLine.addLineStop(l, s);
                                dialog.dismiss();
                            }
                        }).build();
                dialog.show();
                return true;

            case R.id.action_mark_stop:
                Collection<CharSequence> marks = new ArrayList<>();
                for (int t_index = 0; t_index <= MarkEvent.MARK_LIMIT; t_index++)
                    marks.add(t_index + " / " + MarkEvent.MARK_LIMIT);

                CharSequence[] marks_string = marks.toArray(new CharSequence[marks.size()]);

                MaterialDialog mark_dialog = new MaterialDialog.Builder(OneStopActivity.this)
                        .title(R.string.mark_stop)
                        .items(marks_string)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                Data.getData().getMarkEvent().sendMark(getBaseContext(), stop.getID(), which);
                                Data.getData().update();
                                dialog.dismiss();
                            }
                        }).build();
                mark_dialog.show();
                return true;

            case R.id.action_check_weather:
                if(stop.getWeather() == null)
                    return false;

                /*
                String indent, semi-colon are aligned
                 */
                Collection<CharSequence> weather = new ArrayList<>();
                weather.add("Température : " + stop.getWeather().getTemperature() + "°");
                weather.add("Météo             : " + stop.getWeather().getWeatherString()); //TODO : Trace this string
                weather.add("Humidité        : " + stop.getWeather().getHumidityString());
                weather.add("Vent                 : " + stop.getWeather().getWind() + " km/h");


                CharSequence[] weather_string = weather.toArray(new CharSequence[weather.size()]);

                MaterialDialog weather_dialog = new MaterialDialog.Builder(OneStopActivity.this)
                        .title(R.string.check_weather)
                        .items(weather_string)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                Data.getData().getMarkEvent().sendMark(getBaseContext(), stop.getID(), which);
                                Data.getData().update();

                                dialog.dismiss();
                            }
                        }).build();
                weather_dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void choiceReportDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.choose_report_category)
                .items(R.array.report_types)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (ReportType.reportFromPosition(which) == ReportType.AUTRE) {
                            createInputDialog();
                            return;
                        }
                        createDialog(which, null);
                        dialog.dismiss();
                    }
                })
                .build();
        dialog.show();
    }

    private void createDialog(final int position, final String message) {
        String content = String.format(getString(R.string.create_report), getResources().getStringArray(R.array.report_types)[position], stop.getName());
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.confirm_report_title)
                .content(content)
                .negativeText(R.string.no)
                .positiveText(R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Data.getData().getReportEvent().sendReport(getBaseContext(), new Report(stop, ReportType.reportFromPosition(position), message));
                        Data.getData().update();
                        dialog.dismiss();
                    }
                }).build();
        dialog.show();
    }

    private void confirmDialog(final int position) {
        String content = String.format(getString(R.string.confirm_report), getResources().getStringArray(R.array.report_types)[stop.getReports().get(position).getType().getValueForString()], stop.getName());
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.confirm_report_title)
                .content(content)
                .negativeText(R.string.no)
                .positiveText(R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Report r = stop.getReports().get(position);
                        Data.getData().getReportEvent().confirmReport(getBaseContext(), r.getReportId());
                        dialog.dismiss();
                    }
                }).build();
        dialog.show();
    }

    private void createInputDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.input_report)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(R.string.none, R.string.none, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        createDialog(ReportType.AUTRE.getValueForString(), input.toString());
                    }
                }).show();
    }

    private void updateAllReportDialog() {
        if (current_dialog != null && current_dialog.isShowing()) {
            View view = current_dialog.getCustomView();
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            ReportAdapter adapter = new ReportAdapter(stop.getReports(), getBaseContext());
            recyclerView.setAdapter(adapter);
            adapter.SetOnItemClickListener(new ReportAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    confirmDialog(position);
                }
            });
        }
    }

    private void createAllReportDialog() {

        String title = getBaseContext().getResources().getQuantityString(R.plurals.report, stop.getReports().size());
        current_dialog = new MaterialDialog.Builder(this)
                .title(title)
                .customView(R.layout.view_recycler, false)
                .positiveText(R.string.OK)
                .build();

        View view = current_dialog.getCustomView();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getBaseContext());
        recyclerView.addItemDecoration(itemDecoration);

        ReportAdapter adapter = new ReportAdapter(stop.getReports(), getBaseContext());
        recyclerView.setAdapter(adapter);
        adapter.SetOnItemClickListener(new ReportAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                confirmDialog(position);
            }
        });
        current_dialog.show();
    }

    public void onEvent(MessageUpdate event){
        if (event.type == MessageUpdate.Type.REPORT_UPDATE) {
            invalidateOptionsMenu();
            updateAllReportDialog();
        }
    }

    public class OneStopPageAdapter extends FragmentStatePagerAdapter {
        public OneStopPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int linePosition) {
            return getString(R.string.one_line) + " " + stop.getLines().get(linePosition).getShortName();
        }

        @Override
        public int getCount() {
            return stop.getLines().size();
        }

        @Override
        public Fragment getItem(int position) {
            return StopLineFragment.newInstance(stop.getID(), position);
        }
    }
}
