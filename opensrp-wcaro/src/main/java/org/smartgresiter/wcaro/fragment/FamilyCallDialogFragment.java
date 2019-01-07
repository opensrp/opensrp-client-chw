package org.smartgresiter.wcaro.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.contract.FamilyCallDialogContract;
import org.smartgresiter.wcaro.event.PermissionEvent;
import org.smartgresiter.wcaro.listener.CallWidgetDialogListener;
import org.smartgresiter.wcaro.presenter.FamilyCallDialogPresenter;
import org.smartregister.util.PermissionUtils;


public class FamilyCallDialogFragment extends DialogFragment implements FamilyCallDialogContract.View {


    public static final String DIALOG_TAG = "FamilyCallWidgetDialogFragment_DIALOG_TAG";

    View.OnClickListener listner = null;
    FamilyCallDialogContract.Dialer mDialer;
    String familyBaseEntityId;
    ImageView ivClose;
    LinearLayout llFamilyHead;
    TextView tvFamilyHeadTitle;
    TextView tvFamilyHeadName;
    TextView tvFamilyHeadPhone;
    LinearLayout llCareGiver;
    TextView tvCareGiverTitle;
    TextView tvCareGiverName;
    TextView tvCareGiverPhone;

    public static FamilyCallDialogFragment showDialog(Activity activity, String familyBaseEntityId) {
        FamilyCallDialogFragment dialog = new FamilyCallDialogFragment();
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        Fragment prev = activity.getFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialog.setFamilyBaseEntityId(familyBaseEntityId);
        dialog.show(ft, DIALOG_TAG);
        return dialog;
    }

    protected void setFamilyBaseEntityId(String familyBaseEntityId) {
        this.familyBaseEntityId = familyBaseEntityId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.app.DialogFragment.STYLE_NO_TITLE, R.style.WcaroTheme_Dialog_FullWidth);


    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.family_call_widget_dialog_fragment, container, false);
        setUpPosition();

        if (listner == null) {
            listner = new CallWidgetDialogListener(this);
        }

        initUI(dialogView);
        initializePresenter();
        return dialogView;
    }

    private void initUI(ViewGroup rootView) {
        ivClose = rootView.findViewById(R.id.close);

        llFamilyHead = rootView.findViewById(R.id.layout_family_head);
        tvFamilyHeadTitle = rootView.findViewById(R.id.call_head_title);
        tvFamilyHeadName = rootView.findViewById(R.id.call_head_name);
        tvFamilyHeadPhone = rootView.findViewById(R.id.call_head_phone);

        llCareGiver = rootView.findViewById(R.id.layout_caregiver);
        tvCareGiverTitle = rootView.findViewById(R.id.call_caregiver_title);
        tvCareGiverName = rootView.findViewById(R.id.call_caregiver_name);
        tvCareGiverPhone = rootView.findViewById(R.id.call_caregiver_phone);

        ivClose.setOnClickListener(listner);
        tvFamilyHeadPhone.setOnClickListener(listner);
        tvCareGiverPhone.setOnClickListener(listner);
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
    public void onDestroy() {
        super.onDestroy();
        if (getPendingCallRequest() == null) {
            EventBus.getDefault().unregister(this);
        }
        listner = null;
    }

    @Override
    public void refreshHeadOfFamilyView(FamilyCallDialogContract.Model model) {
        if (model != null) {
            llFamilyHead.setVisibility(View.VISIBLE);
            tvFamilyHeadName.setText(model.getName());

            tvFamilyHeadPhone.setText(String.format("CALL [%s]", model.getPhoneNumber()));
            tvFamilyHeadPhone.setTag(model.getPhoneNumber());

            tvFamilyHeadTitle.setText(model.getRole());
        } else {
            llFamilyHead.setVisibility(View.GONE);

            tvFamilyHeadName.setText("");
            tvFamilyHeadPhone.setText("");
            tvFamilyHeadPhone.setTag(null);
            tvFamilyHeadTitle.setText("");
        }
    }

    @Override
    public void refreshCareGiverView(FamilyCallDialogContract.Model model) {
        if (model != null) {
            llCareGiver.setVisibility(View.VISIBLE);
            tvCareGiverName.setText(model.getName());

            tvCareGiverPhone.setText(String.format("CALL [%s]", model.getPhoneNumber()));
            tvCareGiverPhone.setTag(model.getPhoneNumber());

            tvCareGiverTitle.setText(model.getRole());

        } else {

            llCareGiver.setVisibility(View.GONE);

            tvCareGiverName.setText("");
            tvCareGiverPhone.setText("");
            tvCareGiverPhone.setTag(null);
            tvCareGiverTitle.setText("");
        }
    }

    @Override
    public FamilyCallDialogContract.Dialer getPendingCallRequest() {
        return mDialer;
    }

    @Override
    public void setPendingCallRequest(FamilyCallDialogContract.Dialer dialer) {
        mDialer = dialer;
    }

    @Override
    public FamilyCallDialogContract.Presenter initializePresenter() {
        return new FamilyCallDialogPresenter(this, familyBaseEntityId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPermissionEvent(PermissionEvent permissionEvent) {
        if (permissionEvent.getPermissionType() == PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE) {
            if (permissionEvent.isGranted()) {
                if (getPendingCallRequest() != null) {
                    getPendingCallRequest().callMe();
                    setPendingCallRequest(null); // delete pending request
                    return;
                }
                EventBus.getDefault().unregister(this);
                return;
            }
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

}
