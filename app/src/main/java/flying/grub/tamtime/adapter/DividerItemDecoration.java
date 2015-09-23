package flying.grub.tamtime.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import flying.grub.tamtime.R;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;
    private Paint paint;

    public DividerItemDecoration(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.separator);
        paint = new Paint();
        paint.setColor(context.getResources().getColor(R.color.separator));
        paint.setStrokeWidth(1);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int startX = parent.getPaddingLeft();
        int stopX = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount -1; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int y = child.getBottom() + params.bottomMargin;

            c.drawLine(startX, y, stopX, y, paint);
        }
    }
}