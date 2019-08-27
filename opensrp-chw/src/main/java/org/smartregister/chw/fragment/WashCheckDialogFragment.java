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
import org.smartregister.chw.util.Utils;
import org.smartregister.util.JsonFormUtils;

public class WashCheckDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String DIALOG_TAG = "WashCheckDialogFragment";
    private static final String EXTRA_DETAILS = "wash_details";


    private String jsonData;
    private RadioButton handwashingYes, handwashingNo, drinkingYes, drinkingNo;
    private RadioButton latrineYes, latrineNo;
    private Activity activity;

    public static WashCheckDialogFragment getInstance(String jsonString) {
        WashCheckDialogFragment washCheckDialogFragment = new WashCheckDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_DETAILS, jsonString);
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
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(handwashingValue)) {
            if ((Utils.getYesNoAsLanguageSpecific(activity, handwashingValue).equalsIgnoreCase(getString(R.string.yes)))) {
                handwashingYes.setChecked(true);
                handwashingNo.setEnabled(false);
            } else {
                handwashingNo.setChecked(true);
                handwashingYes.setEnabled(false);
            }
        }
        if (!TextUtils.isEmpty(drinkingValue)) {
            if (((Utils.getYesNoAsLanguageSpecific(activity, drinkingValue).equalsIgnoreCase(getString(R.string.yes))))) {
                drinkingYes.setChecked(true);
                drinkingNo.setEnabled(false);
            } else {
                drinkingNo.setChecked(true);
                drinkingYes.setEnabled(false);
            }
        }
        if (!TextUtils.isEmpty(latrineValue)) {
            if (((Utils.getYesNoAsLanguageSpecific(activity, latrineValue).equalsIgnoreCase(getString(R.string.yes))))) {
                latrineYes.setChecked(true);
                latrineNo.setEnabled(false);
            } else {
                latrineNo.setChecked(true);
                latrineYes.setEnabled(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                dismiss();
                break;
            default:
                break;
        }
    }
}