package org.smartregister.chw.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.chw.R;

public class RoutineHouseholdDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String DIALOG_TAG = "RoutineHouseholdDialogFragment";
    private static final String BASE_ENTITY_ID = "base_entity_id";
    private static final String VISIT_DATE = "visit_date";

    public static RoutineHouseholdDialogFragment getInstance(String familyBaseEntityID, Long visitDate) {
        RoutineHouseholdDialogFragment RoutineHouseholdDialogFragment = new RoutineHouseholdDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BASE_ENTITY_ID, familyBaseEntityID);
        bundle.putLong(VISIT_DATE, visitDate);
        RoutineHouseholdDialogFragment.setArguments(bundle);
        return RoutineHouseholdDialogFragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.close).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close) {
            dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        return inflater.inflate(R.layout.fragment_routine_visit, container, false);
    }
}
