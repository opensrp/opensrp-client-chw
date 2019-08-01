package org.smartregister.chw.fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
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
import org.smartregister.chw.listener.OnUpdateServiceTask;
import org.smartregister.chw.util.ServiceTask;

public class LLITNInputDialogFragment extends DialogFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    public static final String DIALOG_TAG = "LLITNInputDialogFragment";

    private String choiceValue;
    private RadioButton choiceOne, choiceTwo;
    private Button buttonSave;
    private OnUpdateServiceTask onUpdateServiceTask;
    private ServiceTask serviceTask;

    public static LLITNInputDialogFragment getInstance() {
        LLITNInputDialogFragment vaccineCardInputDialogFragment = new LLITNInputDialogFragment();
        Bundle bundle = new Bundle();
        vaccineCardInputDialogFragment.setArguments(bundle);
        return vaccineCardInputDialogFragment;
    }

    public void setServiceTask(ServiceTask serviceTask, OnUpdateServiceTask onUpdateServiceTask) {
        this.onUpdateServiceTask = onUpdateServiceTask;
        this.serviceTask = serviceTask;
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
        return inflater.inflate(R.layout.fragment_llitn, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        buttonSave = view.findViewById(R.id.save_bf_btn);
        choiceOne = view.findViewById(R.id.choice_1);
        choiceTwo = view.findViewById(R.id.choice_2);
        buttonSave.setOnClickListener(this);
        view.findViewById(R.id.close).setOnClickListener(this);
        view.findViewById(R.id.info_icon).setOnClickListener(this);
        ((RadioGroup) view.findViewById(R.id.radio_group_exclusive)).setOnCheckedChangeListener(this);
        choiceValue = serviceTask.getTaskLabel();
        if (TextUtils.isEmpty(choiceValue)) {
            enableDisableSaveBtn(false);
        } else {
            if (choiceValue.equalsIgnoreCase(getString(R.string.yes))) {
                choiceOne.setChecked(true);
            } else if (choiceValue.equalsIgnoreCase(getString(R.string.no))) {
                choiceTwo.setChecked(true);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                dismiss();
                break;
            case R.id.info_icon:
                onShowInfo();
                break;
            case R.id.save_bf_btn:
                if (!TextUtils.isEmpty(choiceValue)) {
                    saveData();
                }
                break;
            default:
                break;
        }

    }

    private void saveData() {
        serviceTask.setTaskLabel(choiceValue);
        onUpdateServiceTask.onUpdateServiceTask(serviceTask);
        dismiss();
    }

    protected void onShowInfo() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getView().getContext(), com.vijay.jsonwizard.R.style.AppThemeAlertDialog);
        builderSingle.setTitle(getString(R.string.llitn_info_title));
        builderSingle.setMessage(getString(R.string.llitn_info_text));
        builderSingle.setIcon(com.vijay.jsonwizard.R.drawable.dialog_info_filled);

        builderSingle.setNegativeButton(getView().getContext().getResources().getString(com.vijay.jsonwizard.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.show();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {

            case R.id.choice_1:
                choiceValue = choiceOne.getText().toString();
                enableDisableSaveBtn(true);
                break;
            case R.id.choice_2:
                choiceValue = choiceTwo.getText().toString();
                enableDisableSaveBtn(true);
                break;
            default:
                break;
        }
    }

    private void enableDisableSaveBtn(boolean isEnable) {
        if (isEnable) buttonSave.setAlpha(1.0f);
        else buttonSave.setAlpha(0.3f);
    }
}