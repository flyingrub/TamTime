package flying.grub.tamtime.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import flying.grub.tamtime.R;
import flying.grub.tamtime.data.map.Stop;
import flying.grub.tamtime.data.real_time.Time;

/**
 * Created by fly on 10/02/15.
 */
public class OneRouteAdapter extends RecyclerView.Adapter<OneRouteAdapter.ViewHolder> {

    private static final String TAG = OneRouteAdapter.class.getSimpleName();
    public OnItemClickListener mItemClickListener;
    private ArrayList<Stop> stops;
    private boolean isTheoritical;

    private Context context;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public OneRouteAdapter(ArrayList<Stop> stops, Context context) {
        this.context = context;
        this.stops = stops;
        this.isTheoritical = false;
    }

    public OneRouteAdapter(ArrayList<Stop> stops, Context context, boolean isTheoritical) {
        this(stops, context);
        this.isTheoritical = isTheoritical;
    }

    @Override
    public OneRouteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_one_line, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Stop stop = stops.get(position);
        holder.stop.setText(stop.getStopZone().getName());

        ArrayList<Time> times = stop.getTimes(); // Request 3 next times
        holder.tps1.setText(times.get(0).getWaitingTime());
        holder.tps2.setText(times.get(1).getWaitingTime());
        holder.tps3.setText(times.get(2).getWaitingTime());

        if (stop.getStopZone().getReports().size() > 0) {
            Drawable draw = context.getResources().getDrawable(R.drawable.ic_warning_black_18dp).getConstantState().newDrawable().mutate();
            draw.setColorFilter(Color.parseColor("#616161"), PorterDuff.Mode.SRC_ATOP);
            holder.icon.setImageDrawable(draw);
        } else {
            holder.icon.setImageDrawable(null);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return stops.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView stop;
        public TextView tps1;
        public TextView tps2;
        public TextView tps3;
        public ImageView icon;
        public RelativeLayout relativeLayout;


        public ViewHolder(View v) {
            super(v);
            stop = (TextView) itemView.findViewById(R.id.title);
            tps1 = (TextView) itemView.findViewById(R.id.tps1);
            tps2 = (TextView) itemView.findViewById(R.id.tps2);
            tps3 = (TextView) itemView.findViewById(R.id.tps3);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.element);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }

    }

}
