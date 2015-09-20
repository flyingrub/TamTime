package flying.grub.tamtime.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import flying.grub.tamtime.R;
import flying.grub.tamtime.data.Stop;

/**
 * Created by fly on 9/20/15.
 */
public class OneStopAdapter extends RecyclerView.Adapter<OneStopAdapter.ViewHolder> {

    public OnItemClickListener mItemClickListener;
    public Stop stop;

    public OneStopAdapter(Stop stop) {
        this.stop = stop;
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
        holder.direction.setText(stop.getName());
        holder.tps1.setText(stop.getTimes(1));
        holder.tps2.setText(stop.getTimes(2));
        holder.tps3.setText(stop.getTimes(3));
    }

    @Override
    public int getItemCount() {
        return 1;
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
