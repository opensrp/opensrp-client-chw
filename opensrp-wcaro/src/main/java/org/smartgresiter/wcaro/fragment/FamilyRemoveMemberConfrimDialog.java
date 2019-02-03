package org.smartgresiter.wcaro.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.smartgresiter.wcaro.R;

public class FamilyRemoveMemberConfrimDialog extends DialogFragment implements View.OnClickListener {


    private Context context;
    private Runnable onRemove;
    private Runnable onRemoveActivity;
    private String message;

    public static FamilyRemoveMemberConfrimDialog newInstance(String message) {
        FamilyRemoveMemberConfrimDialog dialog = new FamilyRemoveMemberConfrimDialog();
        dialog.message = message;
        return dialog;
    }

    public void setOnRemove(Runnable onRemove) {
        this.onRemove = onRemove;
    }
    public void setOnRemoveActivity(Runnable onRemoveActivity) {
        this.onRemoveActivity = onRemoveActivity;
    }
    public FamilyRemoveMemberConfrimDialog() {
        // Required empty public constructor
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.family_remove_member_confrim_dialog_fragment, container, false);
        setUpPosition();
        setUpView(rootview);
        return rootview;
    }

    private void setUpView(View rootView){
        rootView.findViewById(R.id.remove).setOnClickListener(this);
        rootView.findViewById(R.id.cancel).setOnClickListener(this);

        if(StringUtils.isNotBlank(message)){
            ((TextView)rootView.findViewById(R.id.message)).setText(message);
        }
    }


    private void setUpPosition() {
        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
        p.y = 20;
        getDialog().getWindow().setAttributes(p);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.remove:
                if(this.onRemove != null){
                    onRemove.run();
                }
                dismiss();
                break;
            case R.id.cancel:
                if(this.onRemoveActivity != null){
                    onRemoveActivity.run();
                }
                dismiss();
                break;
                default:
                    break;
        }
    }
}
