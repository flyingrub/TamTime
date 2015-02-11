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

import flying.grub.tamtime.Adapter.DividerItemDecoration;
import flying.grub.tamtime.Adapter.InfoLineAdapter;
import flying.grub.tamtime.MainActivity;
import flying.grub.tamtime.R;

/**
 * Created by fly on 09/02/15.
 */
public class InfoLineFragment extends Fragment {

    public static InfoLineFragment newInstance(int line) {
        InfoLineFragment fragmentDemo = new InfoLineFragment();
        Bundle args = new Bundle();
        args.putInt("line", line);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }


    private RecyclerView mRecyclerView;
    private InfoLineAdapter mAdapter;
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

        mAdapter = new InfoLineAdapter(MainActivity.getData());

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener(new InfoLineAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v , int position) {
                //selectitem(position);
            }
        });

        return view;

    }

    public void selectitem(int i){
        Fragment newFragment;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        newFragment = InfoLineFragment.newInstance(i);
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
