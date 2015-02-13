package flying.grub.tamtime.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import flying.grub.tamtime.MainActivity;
import flying.grub.tamtime.R;

/**
 * Created by fly on 10/02/15.
 */
public class InfoLineAdapter extends RecyclerView.Adapter<InfoLineAdapter.ViewHolder> {
    public int lineId;
    public int routeId;
    public OnItemClickListener mItemClickListener;

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

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public InfoLineAdapter(int mLineId, int mRouteId) {
        this.lineId = mLineId;
        this.routeId = mRouteId;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public InfoLineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.info_line_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Log.d("lol", MainActivity.getData().getLine(lineId).getRoute(routeId).getStopArrayList().get(position).getName());
        holder.mStop.setText(MainActivity.getData().getLine(lineId).getRoute(routeId).getStopArrayList().get(position).getName());
        holder.tps1.setText("tps1");
        holder.tps2.setText("tps2");
        holder.tps3.setText("tps3");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return MainActivity.getData().getLine(lineId).getRoute(routeId).getStopArrayList().size();
    }

}
