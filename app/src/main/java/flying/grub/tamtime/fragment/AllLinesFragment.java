package flying.grub.tamtime.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import flying.grub.tamtime.activity.OneLineActivity;
import flying.grub.tamtime.adapter.AllLinesAdapter;
import flying.grub.tamtime.adapter.DividerItemDecoration;
import flying.grub.tamtime.data.WaitForData;
import flying.grub.tamtime.activity.MainActivity;
import flying.grub.tamtime.R;

/**
 * Created by fly on 09/02/15.
 */
public class AllLinesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private AllLinesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_recycler, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity());
        mRecyclerView.addItemDecoration(itemDecoration);
        // specify an adapter (see also next example)

        int[] integer = new int[17];
        int count = 0;
        for(int i = 0; i< integer.length; i++){
            count++;
            if (count == 5 || count == 18){
                count++;
            }
            integer[i] = + count;
        }
        mAdapter = new AllLinesAdapter(integer);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener(new AllLinesAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v , int position) {
                selectitem(position);
            }
        });

        return view;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void selectitem(int i){
        if (MainActivity.getData().asData()) {
            Intent intent = new Intent(getActivity(), OneLineActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("id", i);
            intent.putExtras(bundle);
            startActivity(intent);
            //getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            Toast.makeText(getActivity(), getString(R.string.waiting_for_network), Toast.LENGTH_SHORT).show();
            new WaitForData(asNewData()).execute();
        }
    }

    private Runnable asNewData (){
        return new Runnable(){
            public void run(){
                Toast.makeText(getActivity(), getString(R.string.new_data), Toast.LENGTH_SHORT).show();
            }
        };
    }
}
