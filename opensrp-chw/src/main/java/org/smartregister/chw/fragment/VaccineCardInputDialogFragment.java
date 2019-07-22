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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.smartregister.chw.R;

public class VaccineCardInputDialogFragment extends DialogFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    public static final String DIALOG_TAG = "VaccineCardInputDialog";
    private static final String EXTRA_CHOICE_VALUE = "choice_value";

    private String choiceValue;
    private RadioButton yesButton,noButton;
    private Button buttonSave;

    public static VaccineCardInputDialogFragment getInstance(String choiceValue){
        VaccineCardInputDialogFragment vaccineCardInputDialogFragment = new VaccineCardInputDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_CHOICE_VALUE,choiceValue);
        vaccineCardInputDialogFragment.setArguments(bundle);
        return vaccineCardInputDialogFragment;
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
        return inflater.inflate(R.layout.fragment_vaccine_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        buttonSave = view.findViewById(R.id.save_bf_btn);
        yesButton = view.findViewById(R.id.yes);
        noButton = view.findViewById(R.id.no);
        buttonSave.setOnClickListener(this);
        view.findViewById(R.id.close).setOnClickListener(this);
        ((RadioGroup) view.findViewById(R.id.radio_group_exclusive)).setOnCheckedChangeListener(this);
        choiceValue = getArguments().getString(EXTRA_CHOICE_VALUE,"");
        if(TextUtils.isEmpty(choiceValue)){
            enableDisableSaveBtn(false);
        }else{
            if(choiceValue.equalsIgnoreCase(getString(R.string.yes))){
                yesButton.setChecked(true);
            }else if(choiceValue.equalsIgnoreCase(getString(R.string.no))){
                noButton.setChecked(true);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close:
                dismiss();
                break;
            case R.id.save_bf_btn:
                if(!TextUtils.isEmpty(choiceValue)){
                    saveVaccineCardData();
                }
                break;
                default:
                    break;
        }

    }

    private void saveVaccineCardData() {
        ((ChildHomeVisitFragment) getActivity().getFragmentManager().findFragmentByTag(ChildHomeVisitFragment.DIALOG_TAG)).updateVaccineCard(choiceValue);
        dismiss();
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {

            case R.id.yes:
                choiceValue = yesButton.getText().toString();
                enableDisableSaveBtn(true);
                break;
            case R.id.no:
                choiceValue = noButton.getText().toString();
                enableDisableSaveBtn(true);
                break;
            default:
                break;
        }
    }
    private void enableDisableSaveBtn(boolean isEnable){
        if(isEnable)buttonSave.setAlpha(1.0f);
        else  buttonSave.setAlpha(0.3f);
    }
}
