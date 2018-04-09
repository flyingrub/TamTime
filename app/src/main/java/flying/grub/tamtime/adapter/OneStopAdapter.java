package flying.grub.tamtime.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import flying.grub.tamtime.R;
import flying.grub.tamtime.data.map.Stop;
import flying.grub.tamtime.data.real_time.Time;


public class OneStopAdapter extends RecyclerView.Adapter<OneStopAdapter.ViewHolder> {

    public OnItemClickListener mItemClickListener;
    public ArrayList<Stop> stops;

    public OneStopAdapter(ArrayList<Stop> stops) {
        this.stops = stops;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_stop, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Stop stop = stops.get(position);
        holder.direction.setText(stop.getDirection().getName());

        ArrayList<Time> times = stop.getTimes(); // Request 3 next times
        holder.tps1.setText(times.get(0).getWaitingTime());
        holder.tps2.setText(times.get(1).getWaitingTime());
        holder.tps3.setText(times.get(2).getWaitingTime());
    }

    @Override
    public int getItemCount() {
        return stops.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView direction;
        public TextView tps1;
        public TextView tps2;
        public TextView tps3;
        public LinearLayout linearLayout;

        public ViewHolder(View v) {
            super(v);
            direction = (TextView) itemView.findViewById(R.id.direction);
            tps1 = (TextView) itemView.findViewById(R.id.tps1);
            tps2 = (TextView) itemView.findViewById(R.id.tps2);
            tps3 = (TextView) itemView.findViewById(R.id.tps3);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.element);
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
