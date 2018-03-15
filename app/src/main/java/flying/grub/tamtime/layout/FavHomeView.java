package flying.grub.tamtime.layout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import flying.grub.tamtime.R;
import flying.grub.tamtime.activity.OneStopActivity;
import flying.grub.tamtime.adapter.FavWelcomeAdapter;
import flying.grub.tamtime.data.persistence.FavoriteStops;
import flying.grub.tamtime.data.map.Line;
import flying.grub.tamtime.data.map.StopZone;

/**
 * Created by fly on 11/29/15.
 */
public class FavHomeView {

    private LinearLayout favLayout;
    private RecyclerView favStopRecyclerView;
    private FavWelcomeAdapter favStopAdapter;
    private FavoriteStops favoriteStops;

    public FragmentActivity context;

    public interface AddStopLine {
        void update(StopZone stop, Line line);
    }

    public AddStopLine updateStopLine;

    public void setUpdateStopLine(AddStopLine addStopLine) {
        this.updateStopLine = addStopLine;
    }

    public FavHomeView(FragmentActivity context) {
        this.context = context;
        favoriteStops = new FavoriteStops(context);
    }

    public View getView(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.footer_home, parent, false);

        favLayout = (LinearLayout) view.findViewById(R.id.fav_stop_recycler);
        LinearLayout linearLayout = (LinearLayout) favLayout.findViewById(R.id.progress);
        linearLayout.setVisibility(View.GONE);
        TextView textView = (TextView) favLayout.findViewById(R.id.empty_view);
        textView.setText(context.getString(R.string.no_favorite_stop));


        favStopRecyclerView = (RecyclerView) favLayout.findViewById(R.id.recycler_view);
        favStopRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManagerFav = new org.solovyev.android.views.llm.LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        favStopRecyclerView.setLayoutManager(layoutManagerFav);
        favStopRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if (favoriteStops.getFavoriteStop().size() == 0) {
            textView.setVisibility(View.VISIBLE);
            favStopRecyclerView.setVisibility(View.GONE);
        } else {
            favStopAdapter = new FavWelcomeAdapter(favoriteStops.getFavoriteStop());
            favStopRecyclerView.setAdapter(favStopAdapter);
            favStopAdapter.SetOnItemClickListener(new FavWelcomeAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(View v, int position) {
                    selectitem(favoriteStops.getFavoriteStop().get(position));
                }
            });

            favStopAdapter.SetOnMenuClickListener(new FavWelcomeAdapter.OnMenuClickListener() {
                @Override
                public void onItemClick(View v, final int position) {
                    //Creating the instance of PopupMenu
                    PopupMenu popup = new PopupMenu(context, v);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.add_to_home, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            ArrayList<CharSequence> lines = new ArrayList<>();
                            for (Line l : favoriteStops.getFavoriteStop().get(position).getLines()) {
                                lines.add("Ligne " + l.getLineNum());
                            }
                            CharSequence[] lineString = lines.toArray(new CharSequence[lines.size()]);

                            MaterialDialog dialog = new MaterialDialog.Builder(context)
                                    .title(R.string.line_choice)
                                    .items(lineString)
                                    .itemsCallback(new MaterialDialog.ListCallback() {
                                        @Override
                                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                            StopZone s = favoriteStops.getFavoriteStop().get(position);
                                            Line l = s.getLines().get(which);
                                            updateStopLine.update(s, l);
                                            dialog.dismiss();
                                        }
                                    }).build();
                            dialog.show();
                            return true;
                        }
                    });

                    popup.show();
                }
            });
        }
        return view;
    }

    public void selectitem(StopZone s){
        Intent intent = new Intent(context, OneStopActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("stop_zone_id", s.getID());
        intent.putExtras(bundle);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_from_right, R.anim.fade_scale_out);
    }


}
