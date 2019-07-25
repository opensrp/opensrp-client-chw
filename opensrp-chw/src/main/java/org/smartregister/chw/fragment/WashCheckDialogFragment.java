package org.smartregister.chw.fragment;

import android.app.DialogFragment;
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
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.Obs;

import java.util.List;

public class WashCheckDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String DIALOG_TAG = "WashCheckDialogFragment";
    private static final String EXTRA_DETAILS = "wash_details";


    private String jsonData;
    private RadioButton handwashingYes,handwashingNo,drinkingYes,drinkingNo;
    private RadioButton latrineYes,latrineNo;
    private String handwashingValue,drinkingValue,latrineValue;

    public static WashCheckDialogFragment getInstance(String jsonString){
        WashCheckDialogFragment washCheckDialogFragment = new WashCheckDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_DETAILS,jsonString);
        washCheckDialogFragment.setArguments(bundle);
        return washCheckDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar);
    }

    @Override
    public void onStart() {
        super.onStart();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (getDialog() != null && getDialog().getWindow() != null) {
                    getDialog().getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                }
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
        jsonData = getArguments().getString(EXTRA_DETAILS);
        handwashingYes = view.findViewById(R.id.choice_1_handwashing);
        handwashingNo = view.findViewById(R.id.choice_2_handwashing);
        drinkingYes = view.findViewById(R.id.choice_1_drinking);
        drinkingNo = view.findViewById(R.id.choice_2_drinking);
        latrineYes = view.findViewById(R.id.choice_1_latrine);
        latrineNo = view.findViewById(R.id.choice_2_latrine);
        view.findViewById(R.id.close).setOnClickListener(this);
        parseData();
    }
    private void parseData(){
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            org.smartregister.domain.db.Event event = ChildUtils.gsonConverter.fromJson(jsonObject.toString(), new TypeToken<Event>() {
            }.getType());
            List<Obs> observations = event.getObs();
            for (org.smartregister.domain.db.Obs obs : observations) {
                if(obs.getFormSubmissionField().equalsIgnoreCase("handwashing_facilities")){
                    handwashingValue = getObservationValue(obs);
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase("drinking_water")) {
                    drinkingValue = getObservationValue(obs);
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase("hygienic_latrine")) {
                    latrineValue = getObservationValue(obs);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(!TextUtils.isEmpty(handwashingValue)){
            if ((handwashingValue.equalsIgnoreCase(getString(R.string.yes)))) {
                handwashingYes.setChecked(true);
            } else {
                handwashingNo.setChecked(true);
            }
        }
        if(!TextUtils.isEmpty(drinkingValue)){
            if ((drinkingValue.equalsIgnoreCase(getString(R.string.yes)))) {
                drinkingYes.setChecked(true);
            } else {
                drinkingNo.setChecked(true);
            }
        }
        if(!TextUtils.isEmpty(latrineValue)){
            if ((latrineValue.equalsIgnoreCase(getString(R.string.yes)))) {
                latrineYes.setChecked(true);
            } else {
                latrineNo.setChecked(true);
            }
        }
    }

    private String getObservationValue(org.smartregister.domain.db.Obs obs){
            List<Object> hu = obs.getHumanReadableValues();
            String value = "";
            for (Object object : hu) {
                value = (String) object;
            }
            if(!value.isEmpty()){
                return value;
            }else{
                List<Object> values = obs.getValues();
                for (Object object : hu) {
                    value = (String) object;
                }
                return value;
            }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close:
                dismiss();
                break;
            default:
                break;
        }
    }
}