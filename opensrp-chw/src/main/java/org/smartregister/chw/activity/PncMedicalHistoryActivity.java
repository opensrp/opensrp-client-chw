package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.pnc.activity.BasePncMedicalHistoryActivity;

import java.util.List;

public class PncMedicalHistoryActivity extends BasePncMedicalHistoryActivity {

    private Flavor flavor = new PncMedicalHistoryActivityFlv();

    public static void startMe(Activity activity, MemberObject memberObject) {
        Intent intent = new Intent(activity, PncMedicalHistoryActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    @Override
    public View renderView(List<Visit> visits) {
        View view = flavor.bindViews(this);
        displayLoadingState(true);
        flavor.processViewData(visits, this);
        displayLoadingState(false);
        return view;
    }

    public interface Flavor {

        View bindViews(Activity activity);

        void processViewData(List<Visit> visits, Context context);
    }


}
