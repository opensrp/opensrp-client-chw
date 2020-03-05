package org.smartregister.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.R;
import org.smartregister.chw.adapter.ListableAdapter;
import org.smartregister.chw.contract.ListContract;
import org.smartregister.chw.presenter.ListPresenter;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.viewholder.ListableViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public abstract class ReportResultFragment<T extends ListContract.Identifiable> extends Fragment implements ListContract.View<T> {

    private View view;
    private ListableAdapter<T, ListableViewHolder<T>> mAdapter;
    private ProgressBar progressBar;
    protected ListContract.Presenter<T> presenter;
    protected List<T> list;

    protected String communityID;
    protected Date reportDate = null;
    protected String communityName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.report_result_fragment, container, false);

        TextView tvDate = view.findViewById(R.id.tvDate);
        TextView tvCommunity = view.findViewById(R.id.tvCommunity);

        Bundle bundle = getArguments();
        if (bundle != null) {
            communityID = bundle.getString(Constants.ReportParameters.COMMUNITY_ID);
            communityName = bundle.getString(Constants.ReportParameters.COMMUNITY);

            String date = bundle.getString(Constants.ReportParameters.REPORT_DATE);

            if (date != null) {
                try {
                    reportDate = new SimpleDateFormat("dd MMM yyyy", Locale.US).parse(date);
                } catch (ParseException e) {
                    Timber.e(e);
                }
            }

            tvDate.setText(date);
            tvCommunity.setText(communityName);
        }
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
