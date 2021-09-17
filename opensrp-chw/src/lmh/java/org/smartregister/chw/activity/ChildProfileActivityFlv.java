package org.smartregister.chw.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.json.JSONObject;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.dao.ChwChildDao;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

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

    public int immunizationCeiling(final MemberObject memberObject) {
        String gender = ChwChildDao.getChildGender(memberObject.getBaseEntityId());

        if(gender != null && gender.equalsIgnoreCase("Female")){
            if(memberObject.getAge() >= 9 && memberObject.getAge() <= 11) {
                return 132;
            }
            else {
                return 60;
            }
        }

        return 60;
    }

    public int getAgeInMonths(final MemberObject memberObject) {
        Date dob = null;
        try {
            dob = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(memberObject.getDob());
        } catch (ParseException e) {
            Timber.e(e);
        }
        return Months.monthsBetween(new LocalDate(dob), new LocalDate()).getMonths();
    }

    @Override
    public boolean childHasPassedImmunizationCeiling(MemberObject memberObject) {
        return getAgeInMonths(memberObject) > immunizationCeiling(memberObject);
    }
}
