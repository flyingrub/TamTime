package flying.grub.tamtime.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import flying.grub.tamtime.R;

/**
 * Created by fly on 10/12/15.
 */
public class Indicator extends View implements ViewPager.OnPageChangeListener  {
    private Context context;
    private float pageNumber;
    private int selected;
    private static final int SIZE_LARGE = 10;
    private static final int SIZE_SMALL = 7;
    private static final float PADDING = 40;

    public Indicator(Context context) {
        super(context);
        this.context = context;
    }

    public Indicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selected = position;
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float center = getWidth()/2;
        float height = getHeight();
        float first = center - ((int)pageNumber / 2 ) * PADDING;
        if (pageNumber % 2 == 0) {
            first += PADDING /2;
        }

        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(context.getResources().getColor(R.color.windowBackground));
        for (int i = 0; i < pageNumber; i++) {
            if (i == selected) {
                canvas.drawCircle(first + i * PADDING, height/2, SIZE_LARGE, paint);
            } else {
                canvas.drawCircle(first + i * PADDING, height/2, SIZE_SMALL, paint);
            }
        }
    }
}
