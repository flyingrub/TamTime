package flying.grub.tamtime.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import flying.grub.tamtime.R;
import flying.grub.tamtime.fragment.LineRouteFragment;


public class TheoriticalActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;

    private int linePosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theoritical);

        Bundle bundle = getIntent().getExtras();
        linePosition = bundle.getInt("linePosition");

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
        toolbar.setTitle(toolbar.getTitle() + " Théorique");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TheoriticalPagerAdapter(getSupportFragmentManager()));

        ImageView imageView = (ImageView) findViewById(R.id.previous);
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int pos = viewPager.getCurrentItem();
                if (pos > 0) {
                    viewPager.setCurrentItem(pos - 1);
                }
            }
        });
        imageView = (ImageView)findViewById(R.id.next);
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int pos = viewPager.getCurrentItem();
                if (pos < 2) {
                    viewPager.setCurrentItem(pos + 1);
                }
            }
        });


    }

    class TheoriticalPagerAdapter extends FragmentStatePagerAdapter {

        public TheoriticalPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return LineRouteFragment.newInstance(linePosition, position, true);
        }
    }
}
