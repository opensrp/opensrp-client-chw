package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.dao.AbstractDao;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public abstract class DefaultPncMedicalHistoryActivityFlv implements PncMedicalHistoryActivity.Flavor {

    protected LayoutInflater inflater;
    protected LinearLayout linearLayoutHealthFacilityVisit;
    protected LinearLayout linearLayoutHealthFacilityVisitDetails;
    private TextView customFontTextViewLastVisit;
    private LinearLayout linearLayoutLastVisit;
    protected LinearLayout linearLayoutPncHomeVisit;
    protected LinearLayout linearLayoutPncHomeVisitDetails;



    @Override
    public View bindViews(Activity activity) {
        inflater = activity.getLayoutInflater();
        View view = inflater.inflate(org.smartregister.chw.opensrp_chw_anc.R.layout.medical_history_details, null);

        linearLayoutLastVisit = view.findViewById(R.id.linearLayoutLastVisit);
        customFontTextViewLastVisit = view.findViewById(R.id.customFontTextViewLastVisit);
        linearLayoutHealthFacilityVisit = view.findViewById(R.id.linearLayoutPncHealthFacilityVisit);
        linearLayoutHealthFacilityVisitDetails = view.findViewById(R.id.linearLayoutPncHealthFacilityVisitDetails);
        linearLayoutPncHomeVisit = view.findViewById(R.id.linearLayoutPncHomeVisit);
        linearLayoutPncHomeVisitDetails = view.findViewById(R.id.linearLayoutPncHomeVisitDetails);

        return view;
    }

    @Override
    public void processViewData(List<Visit> visits, Context context) {
        if (visits.size() > 0) {

            int days = 0;
            int x = 0;
            Map<String, String> hf_visits = new LinkedHashMap<>();
            while (x < visits.size()) {

                // the first object in this list is the days difference
                if (x == 0) {
                    days = Days.daysBetween(new DateTime(visits.get(0).getDate()), new DateTime()).getDays();
                }
                x++;
            }

            processLastVisit(days, context);

        }
    }

    protected void processLastVisit(int days, Context context) {
        linearLayoutLastVisit.setVisibility(View.VISIBLE);
        customFontTextViewLastVisit.setText(StringUtils.capitalize(MessageFormat.format(context.getString(R.string.days_ago), String.valueOf(days))));
    }


    /**
     * Extract value from VisitDetail
     *
     * @return
     */
    @NotNull
    protected String getText(@Nullable VisitDetail visitDetail) {
        if (visitDetail == null)
            return "";

        String val = visitDetail.getHumanReadable();
        if (StringUtils.isNotBlank(val))
            return val.trim();

        return (StringUtils.isNotBlank(visitDetail.getDetails()))? visitDetail.getDetails().trim() : "";
    }

    @NotNull
    protected String getText(@Nullable List<VisitDetail> visitDetails) {
        if (visitDetails == null)
            return "";

        List<String> vals = new ArrayList<>();
        for (VisitDetail vd : visitDetails) {
            String val = getText(vd);
            if (StringUtils.isNotBlank(val))
                vals.add(val);
        }

        return toCSV(vals);
    }

    protected static String toCSV(List<String> list) {
        String result = "";
        if (list.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : list) {
                sb.append(s).append(",");
            }
            result = sb.deleteCharAt(sb.length() - 1).toString();
        }
        return result;
    }




}