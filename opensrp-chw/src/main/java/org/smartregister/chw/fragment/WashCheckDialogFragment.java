package org.smartregister.chw.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.dao.WashCheckDao;
import org.smartregister.util.JsonFormUtils;

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


    private String jsonData;
    private Long washCheckDate;
    private String baseEntityID;
    private RadioButton handwashingYes, handwashingNo, drinkingYes, drinkingNo;
    private RadioButton latrineYes, latrineNo;
    private Activity activity;

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
        activity = (Activity) context;
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
            String washData = WashCheckDao.getWashCheckDetails(washCheckDate, baseEntityID);
            e.onNext(washData);
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
                jsonData = s;
                parseData();
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

    private void parseData() {
        String handwashingValue = "";
        String drinkingValue = "";
        String latrineValue = "";

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray field = JsonFormUtils.fields(jsonObject);
            JSONObject handwashing_facilities = JsonFormUtils.getFieldJSONObject(field, "handwashing_facilities");
            handwashingValue = handwashing_facilities.optString(JsonFormUtils.VALUE);
            JSONObject drinking_water = JsonFormUtils.getFieldJSONObject(field, "drinking_water");
            drinkingValue = drinking_water.optString(JsonFormUtils.VALUE);
            JSONObject hygienic_latrine = JsonFormUtils.getFieldJSONObject(field, "hygienic_latrine");
            latrineValue = hygienic_latrine.optString(JsonFormUtils.VALUE);

        } catch (Exception e) {
            Timber.e(e);
        }

        // there is a logical bug when parsing legacy data
        // some values where recorded in french while others in english
        // to rectify validate that the value is true

        if (!TextUtils.isEmpty(handwashingValue)) {

            if (isYes(handwashingValue)) {
                handwashingYes.setChecked(true);
                handwashingNo.setEnabled(false);
            } else {
                handwashingNo.setChecked(true);
                handwashingYes.setEnabled(false);
            }
        }
        if (!TextUtils.isEmpty(drinkingValue)) {

            if (isYes(drinkingValue)) {
                drinkingYes.setChecked(true);
                drinkingNo.setEnabled(false);
            } else {
                drinkingNo.setChecked(true);
                drinkingYes.setEnabled(false);
            }
        }
        if (!TextUtils.isEmpty(latrineValue)) {

            if (isYes(latrineValue)) {
                latrineYes.setChecked(true);
                latrineNo.setEnabled(false);
            } else {
                latrineNo.setChecked(true);
                latrineYes.setEnabled(false);
            }
        }
    }

    private boolean isYes(String value) {
        return "Yes".equalsIgnoreCase(value) || "Oui".equalsIgnoreCase(value);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close) {
            dismiss();
        }
    }
}