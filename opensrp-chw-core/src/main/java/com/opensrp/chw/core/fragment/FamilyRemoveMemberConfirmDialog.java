package com.opensrp.chw.core.fragment;


import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.opensrp.chw.core.R;

import org.apache.commons.lang3.StringUtils;

public class FamilyRemoveMemberConfirmDialog extends DialogFragment implements View.OnClickListener {


    private Context context;
    private Runnable onRemove;
    private Runnable onRemoveActivity;
    private String message;

    public FamilyRemoveMemberConfirmDialog() {
        // Required empty public constructor
    }

    public static FamilyRemoveMemberConfirmDialog newInstance(String message) {
        FamilyRemoveMemberConfirmDialog dialog = new FamilyRemoveMemberConfirmDialog();
        dialog.message = message;
        return dialog;
    }

    public void setOnRemove(Runnable onRemove) {
        this.onRemove = onRemove;
    }

    public void setOnRemoveActivity(Runnable onRemoveActivity) {
        this.onRemoveActivity = onRemoveActivity;
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
        int i = v.getId();
        if (i == R.id.remove) {
            if (this.onRemove != null) {
                onRemove.run();
            }
            dismiss();
        } else if (i == R.id.cancel) {
            if (this.onRemoveActivity != null) {
                onRemoveActivity.run();
            }
            dismiss();
        }
    }

    /**
     * handle backpress from dialog.it'll finish childremoveactivity when back press
     */

    @Override
    public void onResume() {
        super.onResume();
        if (getView() == null) return;
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (onRemoveActivity != null) {
                        onRemoveActivity.run();
                    }
                    dismiss();
                    return true;
                }
                return false;
            }
        });
    }
}
