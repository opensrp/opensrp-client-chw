package org.smartregister.chw.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import org.smartregister.chw.R;
import org.smartregister.chw.presenter.JobAidsDashboardFragmentPresenter;
import org.smartregister.chw.reporting.ChwReport;
import org.smartregister.reporting.contract.ReportContract;
import org.smartregister.reporting.domain.IndicatorTally;

import java.util.List;
import java.util.Map;

public class SupervisorIndicatorsFragment extends Fragment implements ReportContract.View, LoaderManager.LoaderCallbacks<List<Map<String, IndicatorTally>>> {
    public static final String TAG = "SupervisorIndicatorsFragment";

    private static ReportContract.Presenter presenter;
    private ViewGroup visualizationsViewGroup;
    private ProgressBar progressBar;
    private List<Map<String, IndicatorTally>> indicatorTallies;

    public SupervisorIndicatorsFragment() {
        // Required empty public constructor
    }

    public static SupervisorIndicatorsFragment newInstance() {
        SupervisorIndicatorsFragment fragment = new SupervisorIndicatorsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new JobAidsDashboardFragmentPresenter(this);
        loadIndicatorTallies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
    public void onDetach() {
        super.onDetach();
    }

    public void loadIndicatorTallies() {
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @NonNull
    @Override
    public Loader<List<Map<String, IndicatorTally>>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new ReportIndicatorsLoader(getContext());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Map<String, IndicatorTally>>> loader, List<Map<String, IndicatorTally>> indicatorTallies) {
        setIndicatorTallies(indicatorTallies);
        refreshUI();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Map<String, IndicatorTally>>> loader) {
        // Clean up or release resources
    }

    @Override
    public void refreshUI() {
        buildVisualization(visualizationsViewGroup);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void buildVisualization(ViewGroup viewGroup) {
        //Refresh view with new indicators
        viewGroup.removeAllViews();
        ChwReport.showSupervisorIndicatorVisualisations(viewGroup, indicatorTallies, getActivity());
    }

    public List<Map<String, IndicatorTally>> getIndicatorTallies() {
        return this.indicatorTallies;
    }

    public void setIndicatorTallies(List<Map<String, IndicatorTally>> indicatorTallies) {
        this.indicatorTallies = indicatorTallies;
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