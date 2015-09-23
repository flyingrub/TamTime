package flying.grub.tamtime.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import flying.grub.tamtime.R;
import flying.grub.tamtime.data.FavoriteStops;
import flying.grub.tamtime.data.Line;
import flying.grub.tamtime.data.Stop;

/**
 * Created by fly on 9/21/15.
 */
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    public OnItemClickListener itemClickListener;
    public OnMenuClickListener menuClickListener;
    private ArrayList<Stop> stops;

    public FavoriteAdapter(ArrayList<Stop> stops) {
        this.stops = stops;
        Collections.sort(stops, new Comparator<Stop>() {
            @Override
            public int compare(Stop s1, Stop s2) {
                return s1.getName().compareToIgnoreCase(s2.getName());
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_favorite, parent, false);

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
        public CardView cardView;
        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            textView = (TextView) itemView.findViewById(R.id.title);
            imageView = (ImageView) itemView.findViewById(R.id.menu);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v instanceof ImageView && menuClickListener != null) {
                menuClickListener.onItemClick(v, getPosition());
            }
            if (v instanceof CardView && itemClickListener != null) {
                itemClickListener.onItemClick(v, getPosition());
            }
        }
    }
}
