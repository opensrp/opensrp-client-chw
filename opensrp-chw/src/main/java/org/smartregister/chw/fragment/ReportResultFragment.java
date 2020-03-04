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
import org.smartregister.chw.adapter.ListableAdapter;
import org.smartregister.chw.contract.ListContract;
import org.smartregister.chw.presenter.ListPresenter;
import org.smartregister.chw.viewholder.ListableViewHolder;

import java.util.List;

import timber.log.Timber;

public abstract class ReportResultFragment<T extends ListContract.Identifiable> extends Fragment implements ListContract.View<T> {

    private View view;
    private ListableAdapter<T, ListableViewHolder<T>> mAdapter;
    private ProgressBar progressBar;
    protected ListContract.Presenter<T> presenter;
    protected List<T> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.report_result_fragment, container, false);
        bindLayout();
        loadPresenter();
        executeFetch();
        return view;
    }

    protected abstract void executeFetch();

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
    public void renderData(List<T> identifiables) {
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
    public void onListItemClicked(T t, int layoutID) {
        Timber.v("Clicked " + t.getID());
    }

    @NonNull
    @Override
    public ListContract.Presenter<T> loadPresenter() {
        if (presenter == null) {
            presenter = new ListPresenter<T>()
                    .with(this);
        }
        return presenter;
    }
}
