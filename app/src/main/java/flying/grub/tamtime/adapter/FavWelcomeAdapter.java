package flying.grub.tamtime.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import flying.grub.tamtime.R;
import flying.grub.tamtime.data.map.StopZone;


public class FavWelcomeAdapter extends RecyclerView.Adapter<FavWelcomeAdapter.ViewHolder> {

    public OnItemClickListener itemClickListener;
    public OnMenuClickListener menuClickListener;
    private ArrayList<StopZone> stops;

    public FavWelcomeAdapter(ArrayList<StopZone> stops) {
        this.stops = stops;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fav_stop, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(stops.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return stops.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnMenuClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }

    public void SetOnMenuClickListener(final OnMenuClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textView;
        public RelativeLayout relative;
        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            textView = (TextView) itemView.findViewById(R.id.title);
            imageView = (ImageView) itemView.findViewById(R.id.menu);
            relative = (RelativeLayout) itemView.findViewById(R.id.fav_stop_item);
            relative.setOnClickListener(this);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v instanceof ImageView && menuClickListener != null) {
                menuClickListener.onItemClick(v, getPosition());
            }
            if (v instanceof RelativeLayout && itemClickListener != null) {
                itemClickListener.onItemClick(v, getPosition());
            }
        }
    }
}
