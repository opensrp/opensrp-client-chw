package org.smartregister.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.FragmentBaseActivity;
import org.smartregister.chw.adapter.ListableAdapter;
import org.smartregister.chw.adapter.ReportsFragmentAdapter;
import org.smartregister.chw.contract.ListContract;
import org.smartregister.chw.domain.ReportType;
import org.smartregister.chw.presenter.ListPresenter;
import org.smartregister.chw.viewholder.ListableViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ReportsFragment extends Fragment implements ListContract.View<ReportType> {

    private View view;
    private ListableAdapter<ReportType, ListableViewHolder<ReportType>> mAdapter;
    private ProgressBar progressBar;
    private ListContract.Presenter<ReportType> presenter;
    private List<ReportType> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.reports_fragment, container, false);
        bindLayout();
        loadPresenter();

        presenter.fetchList(() -> {
            List<ReportType> list = new ArrayList<>();
            list.add(new ReportType(getString(R.string.eligible_children), getString(R.string.eligible_children)));
            list.add(new ReportType(getString(R.string.doses_needed), getString(R.string.doses_needed)));
            list.add(new ReportType(getString(R.string.community_activity), getString(R.string.community_activity)));
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
    }

    @Override
    public void renderData(List<ReportType> identifiables) {
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
    public void onListItemClicked(ReportType reportType, int layoutID) {
        if (reportType.getID().equals(getString(R.string.eligible_children))) {
            Bundle bundle = new Bundle();
            bundle.putString(FilterReportFragment.REPORT_NAME, getString(R.string.eligible_children));
            FragmentBaseActivity.startMe(getActivity(), FilterReportFragment.TAG, getString(R.string.eligible_children), bundle);
        } else if (reportType.getID().equals(getString(R.string.doses_needed))) {
            Bundle bundle = new Bundle();
            bundle.putString(FilterReportFragment.REPORT_NAME, getString(R.string.doses_needed));
            FragmentBaseActivity.startMe(getActivity(), FilterReportFragment.TAG, getString(R.string.doses_needed), bundle);
        } else if (reportType.getID().equals(getString(R.string.community_activity))) {
            FragmentBaseActivity.startMe(getActivity(), JobAidsDashboardFragment.TAG, getString(R.string.community_activity));
        }
    }

    @NonNull
    @Override
    public ListableAdapter<ReportType, ListableViewHolder<ReportType>> adapter() {
        return new ReportsFragmentAdapter(list, this);
    }

    @NonNull
    @Override
    public ListContract.Presenter<ReportType> loadPresenter() {
        if (presenter == null) {
            presenter = new ListPresenter<ReportType>()
                    .with(this);
        }
        return presenter;
    }
}
