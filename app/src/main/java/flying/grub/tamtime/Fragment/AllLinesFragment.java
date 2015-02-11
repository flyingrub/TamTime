package flying.grub.tamtime.Fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import flying.grub.tamtime.Adapter.AllLinesAdapter;
import flying.grub.tamtime.Adapter.DividerItemDecoration;
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
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

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
        int[] img = {R.drawable.l1, R.drawable.l2, R.drawable.l3, R.drawable.l4, R.drawable.l6, R.drawable.l7, R.drawable.l8, R.drawable.l9, R.drawable.l10,R.drawable.l11, R.drawable.l12, R.drawable.l13, R.drawable.l14, R.drawable.l15, R.drawable.l16, R.drawable.l17, R.drawable.l19};
        mAdapter = new AllLinesAdapter(integer, img);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener(new AllLinesAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v , int position) {
                selectitem(position);
            }
        });

        return view;

    }

    public void selectitem(int i){
        Fragment newFragment;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        newFragment = InfoLineFragment.newInstance(i);
        transaction.replace(R.id.container, newFragment);
        transaction.commit();
    }
}
