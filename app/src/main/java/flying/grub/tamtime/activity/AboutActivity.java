package flying.grub.tamtime.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import flying.grub.tamtime.R;
import flying.grub.tamtime.layout.Indicator;

/**
 * Created by fly on 10/12/15.
 */
public class AboutActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Indicator indicator;
    private static final String TAG = AboutActivity.class.getSimpleName();

    String[] titles;
    String[] contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        titles = getResources().getStringArray(R.array.about_title);
        contents = getResources().getStringArray(R.array.about_content);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        indicator = (Indicator) findViewById(R.id.indicator);
        indicator.setPageNumber(titles.length);
        viewPager.setOnPageChangeListener(indicator);
        viewPager.setAdapter(new AboutPageAdapter());
    }

    class AboutPageAdapter extends PagerAdapter {

        LayoutInflater mLayoutInflater;

        public AboutPageAdapter() {
            mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.item_about, container, false);

            TextView title = (TextView) itemView.findViewById(R.id.title);
            TextView content = (TextView) itemView.findViewById(R.id.content);
            title.setText(titles[position]);
            content.setText(contents[position]);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}
