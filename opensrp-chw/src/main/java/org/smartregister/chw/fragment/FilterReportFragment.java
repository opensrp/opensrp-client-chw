package org.smartregister.chw.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.FragmentBaseActivity;
import org.smartregister.chw.contract.FindReportContract;
import org.smartregister.chw.interactor.FindReportInteractor;
import org.smartregister.chw.model.FilterReportFragmentModel;
import org.smartregister.chw.presenter.FilterReportFragmentPresenter;
import org.smartregister.chw.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FilterReportFragment extends Fragment implements FindReportContract.View {
    public static final String TAG = "FilterReportFragment";
    public static final String REPORT_NAME = "REPORT_NAME";

    private FindReportContract.Presenter presenter;
    private View view;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
    private Calendar myCalendar = Calendar.getInstance();
    private String titleName;
    private EditText editTextDate;
    private Spinner spinnerCommunity;
    private ProgressBar progressBar;

    private List<String> communityList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.filter_report_fragment, container, false);
        bindLayout();
        loadPresenter();
        presenter.initializeViews();

        Bundle bundle = getArguments();
        if (bundle != null) {
            titleName = bundle.getString(FilterReportFragment.REPORT_NAME);
        }
        return view;
    }

    @Override
    public void setLoadingState(boolean loadingState) {
        if (progressBar != null)
            progressBar.setVisibility(loadingState ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void bindLayout() {
        Button buttonSave = view.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v -> runReport());
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        editTextDate = view.findViewById(R.id.editTextDate);
        spinnerCommunity = view.findViewById(R.id.spinnerCommunity);

        communityList.add("All communities");

        bindSpinner();
        bindDatePicker();
        updateLabel();
    }

    @Override
    public void onLocationDataLoaded(Map<String, String> locationData) {
        communityList.addAll(locationData.values());
        bindSpinner();
    }

    @Override
    public void runReport() {
        Map<String, String> map = new HashMap<>();
        map.put(Constants.ReportParameters.COMMUNITY, communityList.get(0));
        map.put(Constants.ReportParameters.REPORT_DATE, dateFormat.format(myCalendar.getTime()));
        presenter.runReport(map);
    }

    private void bindSpinner() {
        Context context = getContext();
        if (context == null) return;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, communityList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCommunity.setAdapter(adapter);
    }

    private void bindDatePicker() {
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        editTextDate.setOnClickListener(v -> new DatePickerDialog(getContext(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void updateLabel() {
        editTextDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    @NonNull
    @Override
    public void loadPresenter() {
        presenter = new FilterReportFragmentPresenter()
                .with(this)
                .withModel(new FilterReportFragmentModel())
                .withInteractor(new FindReportInteractor());
    }

    @Override
    public void startResultsView(Bundle bundle) {
        if (titleName == null) return;

        if (titleName.equalsIgnoreCase(getString(R.string.eligible_children))) {
            FragmentBaseActivity.startMe(getActivity(), EligibleChildrenReportFragment.TAG, getString(R.string.eligible_children), bundle);
        } else if (titleName.equalsIgnoreCase(getString(R.string.doses_needed))) {
            FragmentBaseActivity.startMe(getActivity(), VillageDoseReportFragment.TAG, getString(R.string.doses_needed), bundle);
        }
    }
}
