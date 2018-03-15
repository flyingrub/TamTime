package flying.grub.tamtime.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import flying.grub.tamtime.R;
import flying.grub.tamtime.data.map.StopZone;

/**
 * Created by fly on 10/9/15.
 */
public class NearStopAdapter extends RecyclerView.Adapter<NearStopAdapter.ViewHolder> {

    public OnItemClickListener itemClickListener;
    private ArrayList<StopZone> stops;

    public NearStopAdapter(ArrayList<StopZone> stops) {
        this.stops = stops;
        Collections.sort(stops, new Comparator<StopZone>() {
            @Override
            public int compare(StopZone s1, StopZone s2) {
                return ((Float) s1.getDistanceFromUser()).compareTo(s2.getDistanceFromUser());
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_nearby, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.stopName.setText(stops.get(position).getName());
        String dist= String.format("%.0f", stops.get(position).getDistanceFromUser()) + "m";
        holder.distance.setText(dist);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView stopName;
        public CardView cardView;
        public TextView distance;

        public ViewHolder(View v) {
            super(v);
            stopName = (TextView) itemView.findViewById(R.id.title);
            distance = (TextView) itemView.findViewById(R.id.distance);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getPosition());
            }
        }
    }
}
