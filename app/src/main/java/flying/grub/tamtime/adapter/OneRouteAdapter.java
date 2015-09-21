package flying.grub.tamtime.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import flying.grub.tamtime.data.Stop;
import flying.grub.tamtime.activity.MainActivity;
import flying.grub.tamtime.R;
import flying.grub.tamtime.data.StopTimes;

/**
 * Created by fly on 10/02/15.
 */
public class OneRouteAdapter extends RecyclerView.Adapter<OneRouteAdapter.ViewHolder> {
    public OnItemClickListener mItemClickListener;
    private ArrayList<StopTimes> stops;

    public void refresh(){
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public OneRouteAdapter(ArrayList<StopTimes> stops) {
        this.stops = stops;
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
        StopTimes s = stops.get(position);
        holder.mStop.setText(s.getStop().getName());
        holder.tps1.setText(s.getTimes(1));
        holder.tps2.setText(s.getTimes(2));
        holder.tps3.setText(s.getTimes(3));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return stops.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mStop;
        public TextView tps1;
        public TextView tps2;
        public TextView tps3;
        public LinearLayout linearLayout;


        public ViewHolder(View v) {
            super(v);
            mStop = (TextView) itemView.findViewById(R.id.title);
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
