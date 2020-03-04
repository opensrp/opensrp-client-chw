package org.smartregister.chw.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.FragmentBaseActivity;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.FindReportContract;
import org.smartregister.chw.model.FilterReportFragmentModel;
import org.smartregister.chw.presenter.FilterReportFragmentPresenter;
import org.smartregister.location.helper.LocationHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FilterReportFragment extends Fragment implements FindReportContract.View {
    public static final String TAG = "FilterReportFragment";
    public static final String REPORT_NAME = "REPORT_NAME";


    private FindReportContract.Presenter presenter;
    private View view;
    private Spinner spinnerDueDate;
    private Spinner spinnerCommunity;
    private Button buttonSave;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.filter_report_fragment, container, false);

        bindLayout();
        loadPresenter();

        return view;
    }

    @Override
    public void bindLayout() {
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v -> runReport());

        List<String> strings = new ArrayList<>();
        strings.add(sdf.format(new Date()));
        spinnerDueDate = view.findViewById(R.id.spinnerDueDate);
        bindSpinner(spinnerDueDate, strings);

        spinnerCommunity = view.findViewById(R.id.spinnerCommunity);
        bindSpinner(spinnerCommunity, LocationHelper.getInstance().generateDefaultLocationHierarchy(ChwApplication.getInstance().getAllowedLocationLevels()));
    }

    @Override
    public void runReport() {
        Map<String, String> map = new HashMap<>();
        presenter.runReport(map);
    }

    private void bindSpinner(Spinner spinner, List<String> list) {
        Context context = getContext();
        if (context == null) return;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @NonNull
    @Override
    public void loadPresenter() {
        presenter = new FilterReportFragmentPresenter()
                .with(this)
                .withModel(new FilterReportFragmentModel());
    }

    @Override
    public void startResultsView(Bundle bundle) {
        FragmentBaseActivity.startMe(getActivity(), EligibleChildrenReportFragment.TAG, "Query Results", bundle);
    }
}
