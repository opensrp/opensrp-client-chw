package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.activity.CoreChildMedicalHistoryActivity;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.Vaccine;

import java.util.List;
import java.util.Map;

public class ChildMedicalHistoryActivity extends CoreChildMedicalHistoryActivity {
    private CoreChildMedicalHistoryActivity.Flavor flavor = new ChildMedicalHistoryActivityFlv();

    public static void startMe(Activity activity, MemberObject memberObject) {
        Intent intent = new Intent(activity, ChildMedicalHistoryActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    @Override
    public View renderView(List<Visit> visits, Map<String, List<Vaccine>> vaccines, List<ServiceRecord> serviceRecords) {
        super.renderView(visits, vaccines, serviceRecords);
        View view = flavor.bindViews(this);

        displayLoadingState(true);
        flavor.processViewData(visits, vaccines, serviceRecords, this);
        displayLoadingState(false);

        return view;
    }
}
