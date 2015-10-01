package flying.grub.tamtime.adapter;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import flying.grub.tamtime.R;
import flying.grub.tamtime.navigation.ItemWithDrawable;

/**
 * Created by fly on 9/20/15.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public OnItemClickListener itemClickListener;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private ArrayList<ItemWithDrawable> itemWithDrawables;

    private int currentPos = 1;


    public NavigationDrawerAdapter(ArrayList<ItemWithDrawable> items) {
        this.itemWithDrawables = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.drawer_item_infos, parent, false);
            return new ViewHolderItem(v);
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.drawer_item_separator, parent, false);
            return new ViewHolderHeader(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setSelected(currentPos == position && position != 5); // don't select the favorites & Settings
        ItemWithDrawable item = itemWithDrawables.get(position);
        if (holder instanceof ViewHolderItem && !item.isHeader()) {
            ViewHolderItem hold = (ViewHolderItem) holder;
            hold.textView.setText(item.getText());
            Drawable draw = item.getDrawable().getConstantState().newDrawable().mutate();
            if (position == currentPos) {
                draw.setColorFilter(Color.parseColor("#1e88e5"), PorterDuff.Mode.SRC_ATOP);
            } else {
                draw.setColorFilter(Color.parseColor("#616161"), PorterDuff.Mode.SRC_ATOP);
            }
            hold.imageView.setImageDrawable(draw);
        } else if (holder instanceof ViewHolderHeader && item.isHeader()) {
            ViewHolderHeader hold = (ViewHolderHeader) holder;
            //hold.textView.setText(item.getText());
        }
    }

    @Override
    public int getItemCount() {
        return itemWithDrawables.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (itemWithDrawables.get(position).isHeader()) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }


    public class ViewHolderItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textView;
        public ImageView imageView;
        public LinearLayout linearLayout;

        public ViewHolderItem(View v) {
            super(v);
            textView = (TextView) itemView.findViewById(R.id.title);
            imageView = (ImageView) itemView.findViewById(R.id.icon);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.element);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            notifyItemChanged(currentPos);
            currentPos = getLayoutPosition();
            notifyItemChanged(currentPos);
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public class ViewHolderHeader extends RecyclerView.ViewHolder {

        public TextView textView;
        public LinearLayout linearLayout;

        public ViewHolderHeader(View v) {
            super(v);
            textView = (TextView) itemView.findViewById(R.id.title);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.element);
        }

    }
}
