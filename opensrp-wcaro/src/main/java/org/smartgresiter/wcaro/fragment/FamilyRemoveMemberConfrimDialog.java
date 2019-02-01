package org.smartgresiter.wcaro.fragment;


import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.smartgresiter.wcaro.R;
import org.smartregister.immunization.util.Utils;

public class FamilyRemoveMemberConfrimDialog extends DialogFragment implements View.OnClickListener {


    private Context context;
    private Runnable onRemove;
    private String message;

    public static FamilyRemoveMemberConfrimDialog newInstance(String message) {
        FamilyRemoveMemberConfrimDialog dialog = new FamilyRemoveMemberConfrimDialog();
        dialog.message = message;
        return dialog;
    }

    public void setOnRemove(Runnable onRemove) {
        this.onRemove = onRemove;
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
        setUpView(rootview);
        return rootview;
    }

    private void setUpView(View rootView) {
        rootView.findViewById(R.id.remove).setOnClickListener(this);
        rootView.findViewById(R.id.cancel).setOnClickListener(this);

        if (StringUtils.isNotBlank(message)) {
            ((TextView) rootView.findViewById(R.id.message)).setText(message);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // without a handler, the window sizes itself correctly
        // but the keyboard does not show up
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Window window = null;
                if (getDialog() != null) {
                    window = getDialog().getWindow();
                }

                if (window == null) {
                    return;
                }

                Point size = new Point();

                Display display = window.getWindowManager().getDefaultDisplay();
                display.getSize(size);

                window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.TOP);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remove:
                if (this.onRemove != null) {
                    onRemove.run();
                }
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
            default:
                break;
        }
    }
}
