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
import flying.grub.tamtime.data.map.Line;

public class AllLinesAdapter extends RecyclerView.Adapter<AllLinesAdapter.ViewHolder> {
    public OnItemClickListener mItemClickListener;
    private ArrayList<Line> lines;

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public AllLinesAdapter(ArrayList<Line> lines) {
        this.lines = lines;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AllLinesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_all_lines, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String text;
        if (position < 4) {
            text = "Tram";
            holder.mImageView.setImageResource(R.drawable.ic_directions_subway_black_24dp);
        } else {
            text = "Bus";
            holder.mImageView.setImageResource(R.drawable.ic_directions_bus_black_24dp);
        }
        text = text + " " + lines.get(position).getShortName();
        holder.mTextView.setText(text);
    }

                // Return the size of your dataset (invoked by the layout manager)
        @Override
    public int getItemCount() {
        return lines.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView mTextView;
        public ImageView mImageView;
        public LinearLayout linearLayout;


        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) itemView.findViewById(R.id.title);
            mImageView = (ImageView) itemView.findViewById(R.id.icon);
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