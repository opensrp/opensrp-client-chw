package org.smartregister.chw.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.adapter.FormHistoryAdapter;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.dao.RoutineHouseHoldDao;
import org.smartregister.chw.domain.Choice;
import org.smartregister.chw.domain.FormDetails;
import org.smartregister.chw.domain.Question;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.util.FormUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class FormHistoryDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String DIALOG_TAG = "FormHistoryDialogFragment";
    private static final String FORM_DETAILS = "FORM_DETAILS";

    private FormDetails formDetails;

    protected List<Question> questions = new ArrayList<>();
    private ProgressBar progressBar;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView recyclerView;

    public static FormHistoryDialogFragment getInstance(FormDetails formDetails) {
        FormHistoryDialogFragment FormHistoryDialogFragment = new FormHistoryDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FORM_DETAILS, formDetails);
        FormHistoryDialogFragment.setArguments(bundle);
        return FormHistoryDialogFragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.close).setOnClickListener(this);

        formDetails = (FormDetails) getArguments().getSerializable(FORM_DETAILS);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        progressBar = view.findViewById(R.id.progressBarUpcomingServices);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new FormHistoryAdapter(questions);
        recyclerView.setAdapter(mAdapter);
        tvTitle.setText(formDetails.getTitle());

        getQuestions()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Question>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.v("Subscribed");
                    }

                    @Override
                    public void onSuccess(List<Question> questionList) {
                        questions.clear();
                        questions.addAll(questionList);

                        mAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(mAdapter);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private Single<List<Question>> getQuestions() {
        return Single.create(e -> {
            try {
                Map<String, List<VisitDetail>> visitDetailMap = RoutineHouseHoldDao.getEventDetails(formDetails.getEventDate(), formDetails.getBaseEntityID(), formDetails.getEventType());
                List<Question> questions = new ArrayList<>();

                JSONObject jsonForm = FormUtils.getInstance(getActivity()).getFormJson(formDetails.getFormName());

                List<JSONObject> formSteps = JsonFormUtils.getFormSteps(jsonForm);
                for (JSONObject jsonObject : formSteps) {
                    JSONArray array = jsonObject.getJSONArray(JsonFormConstants.FIELDS);
                    int x = 0;
                    int count = array.length() - 1;
                    while (x < count) {
                        JSONObject field = array.getJSONObject(x);
                        if(field.has(JsonFormConstants.HINT) || field.has(JsonFormConstants.LABEL)){
                            Question question = toQuestion(field, visitDetailMap);
                            if (question != null)
                                questions.add(question);
                        }
                        x++;
                    }
                }

                e.onSuccess(questions);
            } catch (Exception ex) {
                e.onError(ex);
            }
        });
    }

    private @org.jetbrains.annotations.Nullable Question toQuestion(JSONObject field, Map<String, List<VisitDetail>> visitDetailMap) throws JSONException {
        String key = field.getString(JsonFormConstants.KEY);

        List<VisitDetail> visitDetails = visitDetailMap.get(key);
        if (visitDetails == null) return null;

        String hint = field.has(JsonFormConstants.HINT) ? field.getString(JsonFormConstants.HINT) : field.getString(JsonFormConstants.LABEL);
        String type = field.getString(JsonFormConstants.TYPE);

        Question question = new Question();
        question.setName(hint);

        if (type.equalsIgnoreCase(JsonFormConstants.CHECK_BOX)) {
            JSONArray options = field.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
            question.setChoices(getChoicesFromOptions(options, visitDetails));
        } else if (type.equalsIgnoreCase(JsonFormConstants.SPINNER)) {
            JSONArray options = field.getJSONArray(JsonFormConstants.VALUES);
            question.setChoices(getChoicesSpinnerOptions(options, visitDetails));
        } else {
            question.setValue(NCUtils.getText(visitDetails));
        }

        return question;
    }

    private List<Choice> getChoicesSpinnerOptions(JSONArray options, List<VisitDetail> visitDetails) throws JSONException {
        List<Choice> choices = new ArrayList<>();

        int x = 0;
        int option_count = options.length();
        while (x < option_count) {

            Choice choice = new Choice();
            choice.setName(options.getString(x));
            choice.setSelected(visitDetails.size() > 0 && visitDetails.get(0).getHumanReadable().equals(choice.getName()));

            choices.add(choice);
            x++;
        }

        return choices;
    }

    private List<Choice> getChoicesFromOptions(JSONArray options, List<VisitDetail> visitDetails) throws JSONException {
        List<Choice> choices = new ArrayList<>();
        List<String> visitOptions = new ArrayList<>();
        for (VisitDetail d : visitDetails) {
            visitOptions.add(d.getDetails());
        }

        int x = 0;
        int option_count = options.length();
        while (x < option_count) {
            JSONObject option = options.getJSONObject(x);
            String optionKey = option.getString(JsonFormConstants.KEY);
            String optionText = option.getString(JsonFormConstants.TEXT);

            Choice choice = new Choice();
            choice.setName(optionText);
            choice.setSelected(visitOptions.contains(optionKey));

            choices.add(choice);
            x++;
        }

        return choices;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close) {
            dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar);
    }

    @Override
    public void onStart() {
        super.onStart();
        new Handler().post(() -> {
            if (getDialog() != null && getDialog().getWindow() != null) {
                getDialog().getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_routine_visit, container, false);
    }

}
