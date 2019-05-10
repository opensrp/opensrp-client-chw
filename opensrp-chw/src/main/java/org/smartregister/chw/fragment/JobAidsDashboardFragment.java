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

import org.smartregister.chw.R;
import org.smartregister.chw.presenter.JobAidsDashboardFragmentPresenter;
import org.smartregister.chw.util.DashboardUtil;
import org.smartregister.reporting.contract.ReportContract;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.domain.NumericIndicatorVisualization;
import org.smartregister.reporting.view.NumericDisplayFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobAidsDashboardFragment extends Fragment implements ReportContract.View, LoaderManager.LoaderCallbacks<List<Map<String, IndicatorTally>>> {

    private ViewGroup visualizationsViewGroup;
    private View numericIndicatorView;
    private static ReportContract.Presenter presenter;
    private List<Map<String, IndicatorTally>> indicatorTallies;

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
        // Fetch Indicator data
        presenter = new JobAidsDashboardFragmentPresenter(this);
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_aids_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        visualizationsViewGroup = getView().findViewById(R.id.dashboard_content);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void buildVisualisations() {
        if (indicatorTallies == null || indicatorTallies.isEmpty()) {
            return;
        }
        // Aggregate values for display
        Map<String, IndicatorTally> numericIndicatorValue = new HashMap<>();

        for (Map<String, IndicatorTally> indicatorTallyMap : indicatorTallies) {
            if (indicatorTallyMap.containsKey(DashboardUtil.countOfChildrenUnder5)) {
                updateTotalTally(indicatorTallyMap, numericIndicatorValue, DashboardUtil.countOfChildrenUnder5);
            }
        }
        // Generate numeric indicator visualization
        NumericIndicatorVisualization numericIndicatorData = new NumericIndicatorVisualization(getResources().getString(R.string.total_under_5_children_label),
                numericIndicatorValue.get(DashboardUtil.countOfChildrenUnder5).getCount());

        NumericDisplayFactory numericIndicatorFactory = new NumericDisplayFactory();
        numericIndicatorView = numericIndicatorFactory.getIndicatorView(numericIndicatorData, getContext());
        visualizationsViewGroup.addView(numericIndicatorView);
    }

    private void updateTotalTally(Map<String, IndicatorTally> indicatorTallyMap, Map<String, IndicatorTally> currentIndicatorValueMap, String indicatorKey) {
        int count, currentValue;
        count = indicatorTallyMap.get(indicatorKey).getCount();
        if (currentIndicatorValueMap.get(indicatorKey) == null) {
            currentIndicatorValueMap.put(indicatorKey, new IndicatorTally(null, count, indicatorKey, null));
            return;
        }
        currentValue = currentIndicatorValueMap.get(indicatorKey).getCount();
        currentIndicatorValueMap.get(indicatorKey).setCount(count + currentValue);
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

    }

    @Override
    public void refreshUI() {
        buildVisualisations();
    }

    private static class ReportIndicatorsLoader extends AsyncTaskLoader<List<Map<String, IndicatorTally>>> {

        public ReportIndicatorsLoader(Context context) {
            super(context);
        }

        @Nullable
        @Override
        public List<Map<String, IndicatorTally>> loadInBackground() {
            return presenter.fetchIndicatorsDailytallies();
        }
    }

}
