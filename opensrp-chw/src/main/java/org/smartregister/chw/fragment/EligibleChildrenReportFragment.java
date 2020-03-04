package org.smartregister.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.R;
import org.smartregister.chw.adapter.EligibleChildrenAdapter;
import org.smartregister.chw.adapter.ListableAdapter;
import org.smartregister.chw.contract.ListContract;
import org.smartregister.chw.domain.EligibleChild;
import org.smartregister.chw.presenter.ListPresenter;
import org.smartregister.chw.viewholder.ListableViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EligibleChildrenReportFragment extends Fragment implements ListContract.View<EligibleChild> {
    public static final String TAG = "EligibleChildrenReportFragment";

    private View view;
    private ListableAdapter<EligibleChild, ListableViewHolder<EligibleChild>> mAdapter;
    private ProgressBar progressBar;
    private ListContract.Presenter<EligibleChild> presenter;
    private List<EligibleChild> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.eligible_children_report_fragment, container, false);
        bindLayout();
        loadPresenter();

        presenter.fetchList(() -> {
            List<EligibleChild> list = new ArrayList<>();
            EligibleChild child = new EligibleChild();
            child.setID("12345");
            child.setFullName("Joe Smith");
            child.setFamilyName("Smith Family");
            child.setDueVaccines(new String[]{"OPV", "Penta", "Rota"});

            Thread.sleep(TimeUnit.SECONDS.toMillis(4));

            return list;
        });

        return view;
    }

    @Override
    public void bindLayout() {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        mAdapter = adapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void renderData(List<EligibleChild> identifiables) {
        this.list = identifiables;
    }

    @Override
    public void refreshView() {
        mAdapter.reloadData(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setLoadingState(boolean loadingState) {
        progressBar.setVisibility(loadingState ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onListItemClicked(EligibleChild eligibleChild, int layoutID) {

    }

    @NonNull
    @Override
    public ListableAdapter<EligibleChild, ListableViewHolder<EligibleChild>> adapter() {
        return new EligibleChildrenAdapter(list, this);
    }

    @NonNull
    @Override
    public ListContract.Presenter<EligibleChild> loadPresenter() {
        if (presenter == null) {
            presenter = new ListPresenter<EligibleChild>()
                    .with(this);
        }
        return presenter;
    }
}
