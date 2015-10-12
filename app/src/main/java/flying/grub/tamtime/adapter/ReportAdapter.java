package flying.grub.tamtime.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import flying.grub.tamtime.R;
import flying.grub.tamtime.data.Report;
import flying.grub.tamtime.data.ReportType;

/**
 * Created by fly on 10/12/15.
 */
public class ReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public OnItemClickListener itemClickListener;

    private static final int TYPE_EXTENDED = 0;
    private static final int TYPE_NORMAL = 1;

    public ArrayList<Report> reports;
    public Context context;

    public ReportAdapter(ArrayList<Report> reports, Context context) {
        this.reports = reports;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EXTENDED) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_report_extended, parent, false);
            return new ViewHolderExtended(v);
        } else if (viewType == TYPE_NORMAL) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_report, parent, false);
            return new ViewHolderNormal(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Report report = reports.get(position);
        if (holder instanceof ViewHolderExtended && report.getType() == ReportType.AUTRE) {
            ViewHolderExtended hold = (ViewHolderExtended) holder;
            hold.title.setText(context.getResources().getStringArray(R.array.report_types)[report.getType().getValue()]);
            hold.content.setText(report.getMessage());
            hold.time.setText(report.getTime());
        } else if (holder instanceof ViewHolderNormal && report.getType() != ReportType.AUTRE) {
            ViewHolderNormal hold = (ViewHolderNormal) holder;
            hold.title.setText(context.getResources().getStringArray(R.array.report_types)[report.getType().getValue()]);
            hold.time.setText(report.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (reports.get(position).getType() == ReportType.AUTRE) {
            return TYPE_EXTENDED;
        } else {
            return TYPE_NORMAL;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }

    public class ViewHolderNormal extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public TextView time;
        public RelativeLayout relativeLayout;

        public ViewHolderNormal(View v) {
            super(v);
            title = (TextView) itemView.findViewById(R.id.title);
            time = (TextView) itemView.findViewById(R.id.time);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.element);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public class ViewHolderExtended extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public TextView time;
        public TextView content;
        public RelativeLayout relativeLayout;

        public ViewHolderExtended(View v) {
            super(v);
            title = (TextView) itemView.findViewById(R.id.title);
            content = (TextView) itemView.findViewById(R.id.content);
            time = (TextView) itemView.findViewById(R.id.time);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.element);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

}
