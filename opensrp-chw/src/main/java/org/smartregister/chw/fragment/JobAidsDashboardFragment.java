package org.smartregister.chw.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.smartregister.chw.R;
import org.smartregister.chw.presenter.JobAidsDashboardFragmentPresenter;
import org.smartregister.chw.util.DashboardUtil;
import org.smartregister.reporting.contract.ReportContract;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.domain.NumericIndicatorVisualization;
import org.smartregister.reporting.domain.PieChartIndicatorVisualization;
import org.smartregister.reporting.domain.PieChartSlice;
import org.smartregister.reporting.domain.ReportingIndicatorVisualization;
import org.smartregister.reporting.listener.PieChartSelectListener;
import org.smartregister.reporting.view.IndicatorVisualisationFactory;
import org.smartregister.reporting.view.NumericDisplayFactory;
import org.smartregister.reporting.view.PieChartFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobAidsDashboardFragment extends Fragment implements ReportContract.View, LoaderManager.LoaderCallbacks<List<Map<String, IndicatorTally>>> {

    private static ReportContract.Presenter presenter;
    private ViewGroup visualizationsViewGroup;
    private List<Map<String, IndicatorTally>> indicatorTallies;
    private ProgressBar progressBar;

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

    private void buildVisualisations() {
        if (indicatorTallies == null || indicatorTallies.isEmpty()) {
            return;
        }
        // Aggregate values for display
        Map<String, IndicatorTally> childrenU5NumericMap = new HashMap<>();
        Map<String, IndicatorTally> deceased0_11_NumericMap = new HashMap<>();
        Map<String, IndicatorTally> deceased12_59_NumericMap = new HashMap<>();
        Map<String, IndicatorTally> children_0_59_WithCertificateMap = new HashMap<>();
        Map<String, IndicatorTally> children_0_59_WithNoCertificateMap = new HashMap<>();

        for (Map<String, IndicatorTally> indicatorTallyMap : indicatorTallies) {
            for (String key : indicatorTallyMap.keySet()) {
                switch (key) {
                    case DashboardUtil.countOfChildrenUnder5:
                        updateTotalTally(indicatorTallyMap, childrenU5NumericMap, DashboardUtil.countOfChildrenUnder5);
                        break;
                    case DashboardUtil.deceasedChildren0_11Months:
                        updateTotalTally(indicatorTallyMap, deceased0_11_NumericMap, DashboardUtil.deceasedChildren0_11Months);
                        break;
                    case DashboardUtil.deceasedChildren12_59Months:
                        updateTotalTally(indicatorTallyMap, deceased12_59_NumericMap, DashboardUtil.deceasedChildren12_59Months);
                        break;
                    case DashboardUtil.countOfChildren0_59WithBirthCert:
                        updateTotalTally(indicatorTallyMap, children_0_59_WithCertificateMap, DashboardUtil.countOfChildren0_59WithBirthCert);
                        break;
                    case DashboardUtil.countOfChildren0_59WithNoBirthCert:
                        updateTotalTally(indicatorTallyMap, children_0_59_WithNoCertificateMap, DashboardUtil.countOfChildren0_59WithNoBirthCert);
                        break;
                    default:
                        Log.e(JobAidsDashboardFragment.class.getCanonicalName(), "The Indicator with the Key " + key + " has not been handled");
                }
            }
        }

        NumericDisplayFactory numericDisplayFactory = new NumericDisplayFactory();
        PieChartFactory pieChartFactory = new PieChartFactory();

        View childrenU5View = getIndicatorView(getNumericVisualization(DashboardUtil.countOfChildrenUnder5, R.string.total_under_5_children_label, childrenU5NumericMap),
                numericDisplayFactory);

        View deceased_0_11_View = getIndicatorView(getNumericVisualization(DashboardUtil.deceasedChildren0_11Months, R.string.deceased_children_0_11_months, deceased0_11_NumericMap),
                numericDisplayFactory);

        View deceased_12_59_View = getIndicatorView(getNumericVisualization(DashboardUtil.deceasedChildren12_59Months, R.string.deceased_children_12_59_months, deceased12_59_NumericMap),
                numericDisplayFactory);

        PieChartIndicatorVisualization pieChartIndicatorVisualizationData = getPieChartVisualization(children_0_59_WithCertificateMap, children_0_59_WithNoCertificateMap, DashboardUtil.countOfChildren0_59WithBirthCert,
                DashboardUtil.countOfChildren0_59WithNoBirthCert, R.string.children_0_59_months_with_birth_certificate);
        View children_0_59_WithBirthCertificateView = getIndicatorView(pieChartIndicatorVisualizationData, pieChartFactory);

        visualizationsViewGroup.addView(childrenU5View);
        visualizationsViewGroup.addView(deceased_0_11_View);
        visualizationsViewGroup.addView(deceased_12_59_View);
        visualizationsViewGroup.addView(children_0_59_WithBirthCertificateView);

        progressBar.setVisibility(View.GONE);
    }

    private View getIndicatorView(ReportingIndicatorVisualization reportingIndicatorVisualization, IndicatorVisualisationFactory visualisationFactory) {
        return visualisationFactory.getIndicatorView(reportingIndicatorVisualization, getContext());
    }

    private NumericIndicatorVisualization getNumericVisualization(String constant, int resource, Map<String, IndicatorTally> indicatorTallyMap) {
        int cnt = 0;
        if (indicatorTallyMap.get(constant) != null) {
            cnt = indicatorTallyMap.get(constant).getCount();
        }
        return new NumericIndicatorVisualization(getResources().getString(resource), cnt);
    }

    private PieChartIndicatorVisualization getPieChartVisualization(Map<String, IndicatorTally> pieChartYesValue, Map<String, IndicatorTally> pieChartNoValue, String yesIndicatorKey,
                                                                    String noIndicatorKey, int stringResourceId) {
        // Define pie chart chartSlices
        List<PieChartSlice> chartSlices = new ArrayList<>();
        int yesCount = 0;
        int noCount = 0;
        if (pieChartYesValue.get(yesIndicatorKey) != null) {
            yesCount = pieChartYesValue.get(yesIndicatorKey).getCount();
        }
        if (pieChartNoValue.get(noIndicatorKey) != null) {
            noCount = pieChartNoValue.get(noIndicatorKey).getCount();
        }

        PieChartSlice yesSlice = new PieChartSlice(yesCount, DashboardUtil.YES_GREEN_SLICE_COLOR);
        PieChartSlice noSlice = new PieChartSlice(noCount, DashboardUtil.NO_RED_SLICE_COLOR);
        chartSlices.add(yesSlice);
        chartSlices.add(noSlice);

        // Build the chart
        PieChartIndicatorVisualization pieChartIndicatorVisualization = new PieChartIndicatorVisualization.PieChartIndicatorVisualizationBuilder()
                .indicatorLabel(getResources().getString(stringResourceId))
                .chartHasLabels(true)
                .chartHasLabelsOutside(true)
                .chartHasCenterCircle(false)
                .chartSlices(chartSlices)
                .chartListener(new ChartListener()).build();

        return pieChartIndicatorVisualization;
    }

    private void updateTotalTally(Map<String, IndicatorTally> indicatorTallyMap, Map<String, IndicatorTally> currentIndicatorValueMap, String indicatorKey) {
        int count;
        int currentValue;
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
        // Clean up or release resources 
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

    private class ChartListener implements PieChartSelectListener {

        @Override
        public void handleOnSelectEvent(PieChartSlice sliceValue) {
            Toast.makeText(getContext(), DashboardUtil.getPieSelectionValue(sliceValue, getContext()), Toast.LENGTH_SHORT).show();
        }
    }

}
