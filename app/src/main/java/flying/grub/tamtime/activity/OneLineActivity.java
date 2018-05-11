package flying.grub.tamtime.activity;

import android.app.Dialog;
import android.content.Intent;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import de.greenrobot.event.EventBus;
import flying.grub.tamtime.adapter.DisruptAdapter;
import flying.grub.tamtime.adapter.DividerItemDecoration;
import flying.grub.tamtime.adapter.ReportAdapter;
import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.map.Line;
import flying.grub.tamtime.data.real_time.RealTimeToUpdate;
import flying.grub.tamtime.data.update.MessageUpdate;
import flying.grub.tamtime.data.update.UpdateRunnable;
import flying.grub.tamtime.fragment.LineRouteFragment;
import flying.grub.tamtime.R;
import flying.grub.tamtime.slidingTab.SlidingTabLayout;


public class OneLineActivity extends AppCompatActivity {

    private static final String TAG = OneLineActivity.class.getSimpleName();
    private Toolbar toolbar;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;

    private int linePosition;
    private Line line;

    private UpdateRunnable updateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slidingtabs);

        Bundle bundle = getIntent().getExtras();
        linePosition = bundle.getInt("id");

        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        line = Data.getData().getMap().getLine(linePosition);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setAdapter(new OneLinePageAdapter(getSupportFragmentManager()));

        slidingTabLayout = new SlidingTabLayout(getApplicationContext());
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.textClearColor));
        slidingTabLayout.setDividerColors(getResources().getColor(R.color.primaryColor));
        slidingTabLayout.setViewPager(viewPager);

        Log.d(TAG, line.getDisruptList() + "");
        if (line.getDisruptList().size() > 0) {
            showInfo(line.getDisruptList().get(0).toString());
        }
        if (linePosition > 19) {
            //createAskDialog();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        EventBus.getDefault().register(this);
        updateRunnable = new UpdateRunnable();
        updateRunnable.run();
        Data.getData().setToUpdate(new RealTimeToUpdate(line));
    }

    @Override
    public void onPause(){
        super.onPause();
        updateRunnable.stop();
        EventBus.getDefault().unregister(this);
        if (isFinishing()) overridePendingTransition(R.anim.fade_scale_in, R.anim.slide_to_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.line_menu, menu);
        if (line.getDisruptList().size() > 0) {
            getMenuInflater().inflate(R.menu.disrupt, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.go_theoritical:
                Intent intent = new Intent(getApplicationContext(), TheoriticalActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("linePosition", linePosition);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.fade_scale_out);
                return true;
            case R.id.show_disrupt:
                createDisruptDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showInfo(String text) {
        Snackbar.with(getApplicationContext())
                .text(text)
                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                .actionLabel("Ok")
                .actionListener(new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        Log.d(TAG, "TEST");
                    }
                })
                .actionColor(getResources().getColor(R.color.accentColor))
                .show(this);
    }

    public  void createDisruptDialog() {
        String title = getString(R.string.disrupt);
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(title)
                .customView(R.layout.view_recycler, false)
                .positiveText(R.string.OK)
                .build();

        View view = dialog.getCustomView();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getBaseContext());
        recyclerView.addItemDecoration(itemDecoration);

        DisruptAdapter adapter = new DisruptAdapter(line.getDisruptList());
        recyclerView.setAdapter(adapter);
        dialog.show();
    }

    public void createAskDialog() { // TODO
        //if (Data.getData().getTheoricTimes().needTheoUpdate()) {
            MaterialDialog dialog = new MaterialDialog.Builder(this).title(R.string.ask_download_title)
                    .content(R.string.ask_download_content)
                    .negativeText(R.string.no)
                    .positiveText(R.string.OK)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
          //                  Data.getData().getTheoricTimes().downloadAllTheo();
                            dialog.dismiss();
                        }
                    }).build();
            dialog.show();
        //}
    }

    public class OneLinePageAdapter extends FragmentStatePagerAdapter {
        public OneLinePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int routePosition) {
            return line.getDirections().get(routePosition).getName();
        }

        @Override
        public int getCount() {
            return line.getDirectionsCount();
        }

        @Override
        public Fragment getItem(int position) {
            return LineRouteFragment.newInstance(linePosition, position);
        }
    }

    public void onEvent(MessageUpdate event) {
        if (event.type == MessageUpdate.Type.EVENT_UPDATE) {
            if (line.getDisruptList().size() > 0) {
                showInfo(line.getDisruptList().get(0).toString());
            }
        }
    }
}