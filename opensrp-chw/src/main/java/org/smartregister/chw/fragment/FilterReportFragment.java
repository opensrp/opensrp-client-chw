package org.smartregister.chw.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.joda.time.DateTime;
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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private ProgressBar progressBar;

    private List<String> communityList = new ArrayList<>();
    private LinkedHashMap<String, String> communityIDList = new LinkedHashMap<>();
    protected TextView selectedCommunitiesTV;
    private boolean[] checkedCommunities = null;


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
        selectedCommunitiesTV = view.findViewById(R.id.selected_communities);
        selectedCommunitiesTV.setOnClickListener(view -> showCommunitiesSelectDialog());

        communityList.add("All communities");

        bindDatePicker();
        updateLabel();
    }

    @Override
    public void onLocationDataLoaded(Map<String, String> locationData) {
        communityIDList = new LinkedHashMap<>(locationData);
        communityList.addAll(communityIDList.values());
        checkedCommunities = new boolean[communityList.size()];
    }

    @Override
    public void runReport() {
        List<String> communityIds = new ArrayList<>();
        List<String> communities = new ArrayList<>();

        if (checkedCommunities != null && checkedCommunities.length > 0) {
            for (int i = 0; i < checkedCommunities.length; i++) {
                boolean checked = checkedCommunities[i];
                if (checked) {
                    communities.add(communityList.get(i));
                    if (i == 0) {
                        communityIds.add("");
                        break;
                    } else
                        communityIds.add(new ArrayList<>(communityIDList.keySet()).get(i - 1));
                }
            }
        }
        Map<String, String> map = new HashMap<>();
        Gson gson = new Gson();
        map.put(Constants.ReportParameters.COMMUNITY, gson.toJson(communities));
        map.put(Constants.ReportParameters.COMMUNITY_ID, gson.toJson(communityIds));
        map.put(Constants.ReportParameters.REPORT_DATE, dateFormat.format(myCalendar.getTime()));
        presenter.runReport(map);

    }

    private void bindDatePicker() {
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        editTextDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(getContext(), date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));

            dialog.getDatePicker().setSpinnersShown(true);
            dialog.getDatePicker().setMinDate(new Date().getTime());
            dialog.getDatePicker().setMaxDate(new DateTime().plusMonths(6).toDate().getTime());

            dialog.show();
        });
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

    private void showCommunitiesSelectDialog() {
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getActivity().getResources().getString(R.string.select_communities));
            String[] itemsArray = new String[communityList.size()];
            builder.setMultiChoiceItems(communityList.toArray(itemsArray), checkedCommunities, (dialog, which, isChecked) -> {
                checkedCommunities[which] = isChecked;
                if (which == 0 && isChecked) {
                    int index = 1;
                    while (index < communityList.size()) {
                        ((AlertDialog) dialog).getListView().setItemChecked(index, false);
                        checkedCommunities[index] = false;
                        index++;
                    }
                } else if (isChecked) {
                    ((AlertDialog) dialog).getListView().setItemChecked(0, false);
                    checkedCommunities[0] = false;
                }
            });
            builder.setPositiveButton("OK", (dialog, which) -> {
                updateSelectedCommunitiesView();
            });

            builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    protected void updateSelectedCommunitiesView() {
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < checkedCommunities.length; i++) {
            boolean checked = checkedCommunities[i];
            if (checked) {
                stringBuffer.append(communityList.get(i)).append("\n");
            }
        }
        selectedCommunitiesTV.setText(stringBuffer.toString());
    }
}
