package org.smartregister.chw.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.FamilyProfileActivity;
import org.smartregister.chw.util.Constants;

public class  AddMemberFragment extends DialogFragment implements View.OnClickListener {

    public static final String DIALOG_TAG = "add_member_dialog";

    private Context context;

    public static AddMemberFragment newInstance() {
        return new AddMemberFragment();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add_member, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.close).setOnClickListener(this);
        view.findViewById(R.id.layout_add_child_under_five).setOnClickListener(this);
        view.findViewById(R.id.layout_add_other_family_member).setOnClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onStart() {
        super.onStart();
        // without a handler, the window sizes itself correctly
        // but the keyboard does not show up
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getDialog().getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

            }
        });

    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.close:
                    dismiss();
                    break;
                case R.id.layout_add_child_under_five:
                    ((FamilyProfileActivity) context).startChildForm(Constants.JSON_FORM.getChildRegister(), "", "", "");
                    dismiss();
                    break;
                case R.id.layout_add_other_family_member:
                    ((FamilyProfileActivity) context).startFormActivity(Constants.JSON_FORM.getFamilyMemberRegister(), null, null);
                    dismiss();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}