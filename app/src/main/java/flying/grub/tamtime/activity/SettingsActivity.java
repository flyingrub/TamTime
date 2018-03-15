package flying.grub.tamtime.activity;


import android.os.Bundle;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import flying.grub.tamtime.BuildConfig;
import flying.grub.tamtime.R;
import flying.grub.tamtime.data.Data;

/**
 * Created by fly on 9/23/15.
 */
public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
        toolbar.setTitle(R.string.settings);

        // Display the fragment as the main content.
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new SettingsFragment())
                .commit();
    }

    @Override
    public void onPause(){
        super.onPause();
        if (isFinishing()) overridePendingTransition(R.anim.fade_scale_in, R.anim.slide_to_right);
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            findPreference("version").setSummary(String.format("v%s (%d)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
            findPreference("reload").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //Data.getData().getTheoricTimes().downloadAllTheo();
                    return true;
                }
            });
        }
    }
}
