package flying.grub.tamtime.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import flying.grub.tamtime.R;
import flying.grub.tamtime.activity.OneStopActivity;
import flying.grub.tamtime.data.map.Line;
import flying.grub.tamtime.data.persistence.LineStop;
import flying.grub.tamtime.data.map.StopZone;
import flying.grub.tamtime.layout.FavHomeView;
import flying.grub.tamtime.layout.SearchView;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = HomeAdapter.class.getSimpleName();

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    public OnItemClickListener mItemClickListener;
    private ArrayList<LineStop> favoriteStopLine;
    private FragmentActivity context;

    private FavHomeView favHomeView;
    private SearchView searchView;

    public HomeAdapter(ArrayList<LineStop> stopLines, FragmentActivity context, FavHomeView favHomeView, SearchView searchView) {
        this.favoriteStopLine = stopLines;
        this.context = context;
        this.favHomeView = favHomeView;
        this.searchView = searchView;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = searchView.getView(parent);
            return new ViewHolderCustom(v);
        } else if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_home_line_stop, parent, false);
            return new ViewHolder(v);
        } else if (viewType == TYPE_FOOTER) {
            View v = favHomeView.getView(parent);
            return new ViewHolderCustom(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderParam, int position) {
        if (holderParam instanceof ViewHolderCustom) {
            return;
        }
        ViewHolder holder = (ViewHolder) holderParam;
        LineStop lineStop = favoriteStopLine.get(position -1);
        StopZone s = lineStop.getStopZone();
        Line l = lineStop.getLine();

        holder.title.setText(s.getName() + " - Ligne " + l.getLineNum());
        holder.recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setItemAnimator(new DefaultItemAnimator());
        AllDirectionStopLine adapter = new AllDirectionStopLine(s.getStops(l), context);
        final StopZone finalS = s;
        adapter.SetOnItemClickListener(new AllDirectionStopLine.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, OneStopActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("stop_zone_id", finalS.getID());
                intent.putExtras(bundle);
                context.startActivity(intent);
                context.overridePendingTransition(R.anim.slide_from_right, R.anim.fade_scale_out);
            }
        });
        holder.recyclerView.setAdapter(adapter);
    }

    @UiThread
    public void dataSetChanged() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (position == getItemCount()-1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return favoriteStopLine.size() + 2;
    }

    public class ViewHolderCustom extends RecyclerView.ViewHolder {

        public ViewHolderCustom(View itemView) {
            super(itemView);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public RecyclerView recyclerView;
        public ImageView menu;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) itemView.findViewById(R.id.title);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.times);
            menu = (ImageView) itemView.findViewById(R.id.menu);
            menu.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }

    }
}
