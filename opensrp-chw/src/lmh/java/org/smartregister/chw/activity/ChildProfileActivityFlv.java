package org.smartregister.chw.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.Utils;

import static org.smartregister.chw.util.Utils.formatDateForVisual;
import static org.smartregister.opd.utils.OpdConstants.DateFormat.YYYY_MM_DD;

public class ChildProfileActivityFlv extends DefaultChildProfileActivityFlv {
    @Override
    public boolean isChildOverTwoMonths(CommonPersonObjectClient client) {
        return true; // This is because LMH isn't concerned with hiding the sick child form for children < 2 months
    }

    @Override
    public Intent getSickChildFormActivityIntent(JSONObject jsonObject, Context context) {
        return CoreJsonFormUtils.getJsonIntent(context, jsonObject, Utils.metadata().familyMemberFormActivity);
    }

    @Override
    public String getFormattedDateForVisual(String dueDate, String inputFormat) {
        return formatDateForVisual(dueDate, YYYY_MM_DD);
    }

    @Override
    public void setLastVisitRowView(String days, RelativeLayout layoutLastVisitRow, View viewLastVisitRow, TextView textViewLastVisit, Context context) {
        layoutLastVisitRow.setVisibility(View.GONE);
        viewLastVisitRow.setVisibility(View.GONE);
    }

    @Override
    public void setVaccineHistoryView(String days, RelativeLayout layoutVaccineHistoryRow, View viewVaccineHistoryRow, Context context) {
        if (TextUtils.isEmpty(days)) {
            layoutVaccineHistoryRow.setVisibility(View.GONE);
            viewVaccineHistoryRow.setVisibility(View.GONE);
        } else {
            layoutVaccineHistoryRow.setVisibility(View.VISIBLE);
            viewVaccineHistoryRow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public String getToolbarTitleName(MemberObject memberObject) {
        return memberObject.getFamilyName();
    }

    @Override
    public boolean usesEligibleChildText(){
        return true;
    }
}
