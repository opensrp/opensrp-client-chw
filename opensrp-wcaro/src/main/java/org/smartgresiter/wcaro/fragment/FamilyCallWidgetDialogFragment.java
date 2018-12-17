package org.smartgresiter.wcaro.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.listener.CallWidgetDialogListener;


public class FamilyCallWidgetDialogFragment extends DialogFragment {


    public static final String DIALOG_TAG = "FamilyCallWidgetDialogFragment_DIALOG_TAG";

    View.OnClickListener listner = null;

    public static FamilyCallWidgetDialogFragment showDialog(Activity activity) {
        FamilyCallWidgetDialogFragment dialog = new FamilyCallWidgetDialogFragment();
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        Fragment prev = activity.getFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialog.show(ft, DIALOG_TAG);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.app.DialogFragment.STYLE_NO_TITLE, R.style.WcaroTheme_Dialog_FullWidth);

    }

    ImageView ivClose;
    TextView tvFamilyHeadTitle;
    TextView tvFamilyHeadName;
    TextView tvFamilyHeadPhone;

    LinearLayout llCareGiver;
    TextView tvCareGiverTitle;
    TextView tvCareGiverName;
    TextView tvCareGiverPhone;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.family_call_widget_dialog_fragment, container, false);
        setUpPosition();

        if(listner == null){
            listner = new CallWidgetDialogListener(this);
        }

        initUI(dialogView);
        return dialogView;
    }

    private void initUI(ViewGroup rootView){

        ivClose = rootView.findViewById(R.id.close);
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

    private void setUpPosition(){
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
        listner =  null;
    }
}
