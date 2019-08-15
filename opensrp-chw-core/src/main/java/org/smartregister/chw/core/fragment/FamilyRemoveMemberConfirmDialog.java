package org.smartregister.chw.core.fragment;


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

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.core.R;

public class FamilyRemoveMemberConfirmDialog extends DialogFragment implements View.OnClickListener {
    private Runnable onRemove;
    private Runnable onRemoveActivity;
    private String message;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.family_remove_member_confrim_dialog_fragment, container, false);
        setUpView(rootView);
        return rootView;
    }

    private void setUpView(View rootView) {
        rootView.findViewById(R.id.remove).setOnClickListener(this);
        rootView.findViewById(R.id.cancel).setOnClickListener(this);

        if (StringUtils.isNotBlank(message)) {
            ((TextView) rootView.findViewById(R.id.message)).setText(message);
        }
    }

    /**
     * handle backpress from dialog.it'll finish childremoveactivity when back press
     */

    @Override
    public void onResume() {
        super.onResume();
        if (getView() == null) {
            return;
        }
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                if (onRemoveActivity != null) {
                    onRemoveActivity.run();
                }
                dismiss();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // without a handler, the window sizes itself correctly
        // but the keyboard does not show up
        new Handler().post(() -> {
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
}
