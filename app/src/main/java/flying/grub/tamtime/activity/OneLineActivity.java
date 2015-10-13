package flying.grub.tamtime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import flying.grub.tamtime.data.DataParser;
import flying.grub.tamtime.data.Line;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slidingtabs);
        DataParser.getDataParser().update(getBaseContext());

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

        line = DataParser.getDataParser().getLine(linePosition);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setAdapter(new OneLinePageAdapter(getSupportFragmentManager()));

        slidingTabLayout = new SlidingTabLayout(getApplicationContext());
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.textClearColor));
        slidingTabLayout.setDividerColors(getResources().getColor(R.color.primaryColor));
        slidingTabLayout.setViewPager(viewPager);

        if (false) { // to be replaced
            showInfo("Preturbation en cours sur la ligne...");
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if (isFinishing()) overridePendingTransition(R.anim.fade_scale_in, R.anim.slide_to_right);
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

    public class OneLinePageAdapter extends FragmentStatePagerAdapter {
        public OneLinePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int routePosition) {
            return line.getRoutes().get(routePosition).getDirection();
        }

        @Override
        public int getCount() {
            return line.getRouteCount();
        }

        @Override
        public Fragment getItem(int position) {
            return LineRouteFragment.newInstance(linePosition, position);
        }
    }
}