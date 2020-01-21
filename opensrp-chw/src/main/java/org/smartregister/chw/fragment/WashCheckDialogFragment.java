package org.smartregister.chw.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.dao.WashCheckDao;
import org.smartregister.util.JsonFormUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class WashCheckDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String DIALOG_TAG = "WashCheckDialogFragment";
    private static final String BASE_ENTITY_ID = "base_entity_id";
    private static final String VISIT_DATE = "visit_date";

    private Long washCheckDate;
    private String baseEntityID;
    private RadioButton handwashingYes, handwashingNo, drinkingYes, drinkingNo;
    private RadioButton latrineYes, latrineNo;
    private Map<String, Boolean> selectedOptions = new HashMap<>();

    public static WashCheckDialogFragment getInstance(String familyBaseEntityID, Long visitDate) {
        WashCheckDialogFragment washCheckDialogFragment = new WashCheckDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BASE_ENTITY_ID, familyBaseEntityID);
        bundle.putLong(VISIT_DATE, visitDate);
        washCheckDialogFragment.setArguments(bundle);
        return washCheckDialogFragment;
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
        return inflater.inflate(R.layout.fragment_wash_check, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        baseEntityID = getArguments().getString(BASE_ENTITY_ID);
        washCheckDate = getArguments().getLong(VISIT_DATE);

        handwashingYes = view.findViewById(R.id.choice_1_handwashing);
        handwashingNo = view.findViewById(R.id.choice_2_handwashing);
        drinkingYes = view.findViewById(R.id.choice_1_drinking);
        drinkingNo = view.findViewById(R.id.choice_2_drinking);
        latrineYes = view.findViewById(R.id.choice_1_latrine);
        latrineNo = view.findViewById(R.id.choice_2_latrine);
        view.findViewById(R.id.close).setOnClickListener(this);


        Observable<String> observable = Observable.create(e -> {
            Map<String, VisitDetail> washData = WashCheckDao.getWashCheckDetails(washCheckDate, baseEntityID);
            if (washData.get("details_info") != null) {
                parseOldData(washData.get("details_info").getDetails());
            } else {
                for (Map.Entry<String, VisitDetail> entry : washData.entrySet()) {
                    String value = NCUtils.getText(entry.getValue());
                    selectedOptions.put(entry.getKey(), "Yes".equalsIgnoreCase(value));
                }
            }

            e.onNext("");
            e.onComplete();
        });

        final Disposable[] disposable = new Disposable[1];
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable[0] = d;
            }

            @Override
            public void onNext(String s) {
                refreshUI();
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e);
            }

            @Override
            public void onComplete() {
                disposable[0].dispose();
                disposable[0] = null;
            }
        });
    }

    private void parseOldData(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray field = JsonFormUtils.fields(jsonObject);

            selectedOptions.put("handwashing_facilities", getValueFromJsonFieldNode(field, "handwashing_facilities"));
            selectedOptions.put("drinking_water", getValueFromJsonFieldNode(field, "drinking_water"));
            selectedOptions.put("hygienic_latrine", getValueFromJsonFieldNode(field, "hygienic_latrine"));
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private Boolean getValueFromJsonFieldNode(JSONArray field, String key) {
        JSONObject jsonObject = JsonFormUtils.getFieldJSONObject(field, key);
        if (jsonObject == null)
            return null;

        String value = jsonObject.optString(JsonFormUtils.VALUE);
        if (StringUtils.isBlank(value))
            return null;

        return "Yes".equalsIgnoreCase(value) || "Oui".equalsIgnoreCase(value);
    }

    private void refreshUI() {
        notifyUIValues(handwashingYes, handwashingNo, "handwashing_facilities");
        notifyUIValues(drinkingYes, drinkingNo, "drinking_water");
        notifyUIValues(latrineYes, latrineNo, "hygienic_latrine");
    }

    private void notifyUIValues(RadioButton radioButtonYes, RadioButton radioButtonNo, String optionName) {
        Boolean handWashing = selectedOptions.get(optionName);

        if (handWashing != null) {
            if (handWashing) {
                radioButtonYes.setChecked(true);
                radioButtonNo.setEnabled(false);
            } else {
                radioButtonNo.setChecked(true);
                radioButtonYes.setEnabled(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close) {
            dismiss();
        }
    }
}