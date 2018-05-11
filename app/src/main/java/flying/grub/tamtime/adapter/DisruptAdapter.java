package flying.grub.tamtime.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import flying.grub.tamtime.R;
import flying.grub.tamtime.data.dirsruption.Disrupt;
import flying.grub.tamtime.data.map.Line;

public class DisruptAdapter extends RecyclerView.Adapter<DisruptAdapter.ViewHolder> {
    public OnItemClickListener mItemClickListener;
    private ArrayList<Disrupt> disrupts;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public DisruptAdapter(ArrayList<Disrupt> disrupts) {
        this.disrupts = disrupts;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DisruptAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_disrupt, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.title.setText(disrupts.get(position).component5());
        holder.desc.setText(disrupts.get(position).component4());
    }

                // Return the size of your dataset (invoked by the layout manager)
        @Override
    public int getItemCount() {
        return disrupts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView title;
        public TextView desc;


        public ViewHolder(View v) {
            super(v);
            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView) itemView.findViewById(R.id.description);
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