package org.smartregister.chw.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.smartregister.chw.R;
import org.smartregister.chw.presenter.JobAidsDashboardFragmentPresenter;
import org.smartregister.chw.reporting.modules.AncReportingModule;
import org.smartregister.chw.reporting.modules.ChildReportingModule;
import org.smartregister.reporting.contract.ReportContract;
import org.smartregister.reporting.domain.IndicatorTally;

import java.util.List;
import java.util.Map;

public class JobAidsDashboardFragment extends Fragment implements ReportContract.View, LoaderManager.LoaderCallbacks<List<Map<String, IndicatorTally>>> {

    private static ReportContract.Presenter presenter;
    private ViewGroup visualizationsViewGroup;
    private List<Map<String, IndicatorTally>> indicatorTallies;
    private ProgressBar progressBar;
    private ChildReportingModule childReportingModule;
    private AncReportingModule ancReportingModule;

    public JobAidsDashboardFragment() {
        // Required empty public constructor
    }

    public static JobAidsDashboardFragment newInstance() {
        JobAidsDashboardFragment fragment = new JobAidsDashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new JobAidsDashboardFragmentPresenter(this);
        childReportingModule = new ChildReportingModule();
        ancReportingModule = new AncReportingModule();
        loadIndicatorTallies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_job_aids_dashboard, container, false);
        progressBar = rootView.findViewById(R.id.progress_bar);
        visualizationsViewGroup = rootView.findViewById(R.id.dashboard_content);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void loadIndicatorTallies() {
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    private void buildVisualisations() {

        if (indicatorTallies == null || indicatorTallies.isEmpty()) {
            return;
        }

        //Create report for child indicators
        childReportingModule.setIndicatorTallies(indicatorTallies);
        childReportingModule.createReport(visualizationsViewGroup);

        //Create report for anc indicators
        ancReportingModule.setIndicatorTallies(indicatorTallies);
        ancReportingModule.createReport(visualizationsViewGroup);

        progressBar.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public Loader<List<Map<String, IndicatorTally>>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new ReportIndicatorsLoader(getContext());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Map<String, IndicatorTally>>> loader, List<Map<String, IndicatorTally>> indicatorTallies) {
        this.indicatorTallies = indicatorTallies;
        refreshUI();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Map<String, IndicatorTally>>> loader) {
        // Clean up or release resources
    }

    @Override
    public void refreshUI() {
        buildVisualisations();
    }

    private static class ReportIndicatorsLoader extends AsyncTaskLoader<List<Map<String, IndicatorTally>>> {

        private ReportIndicatorsLoader(Context context) {
            super(context);
        }

        @Nullable
        @Override
        public List<Map<String, IndicatorTally>> loadInBackground() {
            return presenter.fetchIndicatorsDailytallies();
        }
    }
}