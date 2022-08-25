package org.smartregister.chw.activity;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.core.activity.DefaultAncMedicalHistoryActivityFlv;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AncHfMedicalHistoryActivityFlv extends DefaultAncMedicalHistoryActivityFlv {

    @Override
    protected void processAncCard(String has_card, Context context) {
        // super.processAncCard(has_card, context);
        linearLayoutAncCard.setVisibility(View.GONE);
    }

    @Override
    protected void processHealthFacilityVisit(List<Map<String, String>> hf_visits, Context context) {
        //super.processHealthFacilityVisit(hf_visits, context);

        if (hf_visits != null && hf_visits.size() > 0) {
            linearLayoutHealthFacilityVisit.setVisibility(View.VISIBLE);

            int x = 1;
            for (Map<String, String> vals : hf_visits) {
                View view = inflater.inflate(R.layout.medical_history_anc_visit, null);

                TextView tvTitle = view.findViewById(R.id.title);
                TextView tvTests = view.findViewById(R.id.tests);

                view.findViewById(R.id.weight).setVisibility(View.GONE);
                view.findViewById(R.id.bp).setVisibility(View.GONE);
                view.findViewById(R.id.hb).setVisibility(View.GONE);
                view.findViewById(R.id.ifa_received).setVisibility(View.GONE);


                tvTitle.setText(MessageFormat.format(context.getString(R.string.anc_visit_date), (x), vals.get("anc_hf_visit_date")));
                tvTests.setText(MessageFormat.format(context.getString(R.string.tests_done_details), vals.get("tests_done")));

                linearLayoutHealthFacilityVisitDetails.addView(view, 0);

                x++;
            }
        }
    }

    @Override
    public void processViewData(List<Visit> visits, Context context) {

        if (visits.size() > 0) {

            int days = 0;
            String has_card = "No";
            List<Map<String, String>> hf_visits = new ArrayList<>();

            int x = 0;
            while (x < visits.size()) {

                // the first object in this list is the days difference
                if (x == 0) {
                    days = Days.daysBetween(new DateTime(visits.get(visits.size() - 1).getDate()), new DateTime()).getDays();
                }

                // anc card
                if (has_card.equalsIgnoreCase("No")) {
                    List<VisitDetail> details = visits.get(x).getVisitDetails().get("anc_card");
                    if (details != null && StringUtils.isNotBlank(details.get(0).getHumanReadable())) {
                        has_card = details.get(0).getHumanReadable();
                    }

                }


                String[] hf_params = {"weight", "height", "systolic", "fundal_height", "diastolic", "hb_level", "mRDT_for_malaria", "hiv", "anc_visit_date", "gest_age"};
                extractHFVisit(visits, hf_params, hf_visits, x, context);

                x++;
            }

            processLastVisit(days, context);
            processAncCard(has_card, context);
            processFacilityVisit(hf_visits, context);
        }
    }

    private void extractHFVisit(List<Visit> sourceVisits, String[] hf_params, List<Map<String, String>> hf_visits, int iteration, Context context) {
        // get the hf details
        Map<String, String> map = new HashMap<>();
        for (String param : hf_params) {
            try {
                List<VisitDetail> details = sourceVisits.get(iteration).getVisitDetails().get(param);
                map.put(param, getTexts(context, details));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        hf_visits.add(map);
    }


    private void processLastVisit(int days, Context context) {
        linearLayoutLastVisit.setVisibility(View.VISIBLE);
        if (days < 1) {
            customFontTextViewLastVisit.setText(org.smartregister.chw.core.R.string.less_than_twenty_four);
        } else {
            customFontTextViewLastVisit.setText(StringUtils.capitalize(MessageFormat.format(context.getString(org.smartregister.chw.core.R.string.days_ago), String.valueOf(days))));
        }
    }


    protected void processFacilityVisit(List<Map<String, String>> hf_visits, Context context) {
        if (hf_visits != null && hf_visits.size() > 0) {
            linearLayoutHealthFacilityVisit.setVisibility(View.VISIBLE);

            int x = 0;
            for (Map<String, String> vals : hf_visits) {
                View view = inflater.inflate(R.layout.medical_history_anc_visit, null);

                TextView tvTitle = view.findViewById(R.id.title);
                TextView tvGA = view.findViewById(R.id.gest_age);
                TextView tvFundalHeight = view.findViewById(R.id.fundal_height);
                TextView tvHeight = view.findViewById(R.id.height);
                TextView tvWeight = view.findViewById(R.id.weight);
                TextView tvBP = view.findViewById(R.id.bp);
                TextView tvHB = view.findViewById(R.id.hb);
                TextView tvMrdtMalaria = view.findViewById(R.id.mrdt_malaria);
                TextView tvHivStatus = view.findViewById(R.id.hiv_status);


                tvTitle.setText(MessageFormat.format(context.getString(org.smartregister.chw.core.R.string.anc_visit_date), x + 1, getMapValue(vals, "anc_visit_date")));

                if (StringUtils.isBlank(getMapValue(vals, "fundal_height"))) {
                    tvFundalHeight.setVisibility(View.GONE);
                } else {
                    tvFundalHeight.setText(MessageFormat.format(context.getString(R.string.fundal_height), getMapValue(vals, "fundal_height")));
                }

                tvGA.setText(MessageFormat.format(context.getString(R.string.gestation_age), getMapValue(vals, "gest_age")));
                if (StringUtils.isBlank(getMapValue(vals, "weight"))) {
                    tvWeight.setVisibility(View.GONE);
                } else {
                    tvWeight.setText(MessageFormat.format(context.getString(org.smartregister.chw.core.R.string.weight_in_kgs), getMapValue(vals, "weight")));
                }

                if (StringUtils.isBlank(getMapValue(vals, "height"))) {
                    tvHeight.setVisibility(View.GONE);
                } else {
                    tvHeight.setText(MessageFormat.format(context.getString(R.string.height_in_cm), getMapValue(vals, "height")));
                }

                if (StringUtils.isBlank(getMapValue(vals, "systolic"))) {
                    tvBP.setVisibility(View.GONE);
                } else {
                    tvBP.setText(MessageFormat.format(context.getString(org.smartregister.chw.core.R.string.bp_in_mmhg), getMapValue(vals, "systolic"), getMapValue(vals, "diastolic")));
                }

                if (StringUtils.isBlank(getMapValue(vals, "hb_level"))) {
                    tvHB.setVisibility(View.GONE);
                } else {
                    tvHB.setText(context.getString(org.smartregister.chw.core.R.string.hb_level_in_g_dl, getMapValue(vals, "hb_level")));
                }

                if (StringUtils.isBlank(getMapValue(vals, "mRDT_for_malaria"))) {
                    tvMrdtMalaria.setVisibility(View.GONE);
                } else {
                    tvMrdtMalaria.setText(MessageFormat.format(context.getString(R.string.mrdt_malaria), getMapValue(vals, "mRDT_for_malaria")));
                }

                if (StringUtils.isBlank(getMapValue(vals, "hiv"))) {
                    tvHivStatus.setVisibility(View.GONE);
                } else {
                    tvHivStatus.setText(MessageFormat.format(context.getString(R.string.hiv_status), getMapValue(vals, "hiv")));
                }


                linearLayoutHealthFacilityVisitDetails.addView(view, 0);

                x++;
            }
        }
    }

    private String getMapValue(Map<String, String> map, String key) {
        if (map.containsKey(key)) {
            if (map.get(key) != null && map.get(key).length() > 1) {
                return map.get(key).split(",")[0];
            }
            return map.get(key);
        }
        return "";
    }
}
