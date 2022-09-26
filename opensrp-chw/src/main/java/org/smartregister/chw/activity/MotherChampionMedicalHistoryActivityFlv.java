package org.smartregister.chw.activity;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.StyleSpan;
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

import timber.log.Timber;

public class MotherChampionMedicalHistoryActivityFlv extends DefaultAncMedicalHistoryActivityFlv {
    private final StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD);

    @Override
    protected void processAncCard(String has_card, Context context) {
        // super.processAncCard(has_card, context);
        linearLayoutAncCard.setVisibility(View.GONE);
    }

    @Override
    protected void processHealthFacilityVisit(List<Map<String, String>> hf_visits, Context context) {
        //super.processHealthFacilityVisit(hf_visits, context);
    }

    @Override
    public void processViewData(List<Visit> visits, Context context) {

        if (visits.size() > 0) {
            int days = 0;
            List<Map<String, String>> hf_visits = new ArrayList<>();

            int x = 0;
            while (x < visits.size()) {

                // the first object in this list is the days difference
                if (x == 0) {
                    days = Days.daysBetween(new DateTime(visits.get(visits.size() - 1).getDate()), new DateTime()).getDays();
                }


                String[] visitParams = {"followup_visit_date", "type_of_service", "linked_to_psychosocial_group", "counselling_given", "referrals_issued_other_services"};
                extractVisitDetails(visits, visitParams, hf_visits, x, context);

                x++;
            }

            processLastVisit(days, context);
            processVisit(hf_visits, context);
        }
    }

    private void extractVisitDetails(List<Visit> sourceVisits, String[] hf_params, List<Map<String, String>> hf_visits, int iteration, Context context) {
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


    protected void processVisit(List<Map<String, String>> hf_visits, Context context) {
        if (hf_visits != null && hf_visits.size() > 0) {
            linearLayoutHealthFacilityVisit.setVisibility(View.VISIBLE);

            int x = 0;
            for (Map<String, String> vals : hf_visits) {
                View view = inflater.inflate(R.layout.medical_history_mother_champion_visit, null);
                TextView tvTitle = view.findViewById(R.id.title);
                TextView tvTypeOfService = view.findViewById(R.id.type_of_service);
                TextView tvLinkedToPsychosocialGroup = view.findViewById(R.id.linked_to_psychosocial_group);
                TextView counsellingGiven = view.findViewById(R.id.counselling_given);
                TextView referralsIssuedToOtherServices = view.findViewById(R.id.referrals_issued_other_services);
                evaluateTitle(context, x, vals, tvTitle);
                evaluateTypeOfService(context, vals, tvTypeOfService);
                evaluateLinkedToPsychosocialGroup(context, vals, tvLinkedToPsychosocialGroup);
                evaluateCounsellingGiven(context, vals, counsellingGiven);
                evaluateReferralsIssuedToOtherServices(context, vals, referralsIssuedToOtherServices);
                linearLayoutHealthFacilityVisitDetails.addView(view, 0);

                x++;
            }
        }
    }


    private void evaluateTitle(Context context, int x, Map<String, String> vals, TextView tvTitle) {
        String visitDate = vals.get("followup_visit_date");
        if (StringUtils.isBlank(visitDate)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            try {
                tvTitle.setText(MessageFormat.format(context.getString(R.string.mother_champion_visit_title), x + 1, visitDate));
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    private void evaluateTypeOfService(Context context, Map<String, String> vals, TextView tvTypeOfService) {
        if (StringUtils.isNotBlank(getMapValue(vals, "type_of_service"))) {
            SpannableStringBuilder parsedTypeOfService = new SpannableStringBuilder();
            parsedTypeOfService.append(context.getString(R.string.mother_champion_type_of_service), boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE).append("\n");
            String typeOfServiceValue = getMapValue(vals, "type_of_service");
            String[] typeOfServiceValueArray;
            if (typeOfServiceValue.contains(",")) {
                typeOfServiceValueArray = typeOfServiceValue.split(",");
                for (String type : typeOfServiceValueArray) {
                    int resourceId = context.getResources().
                            getIdentifier("mother_champion_" + type.trim(), "string", context.getPackageName());
                    parsedTypeOfService.append(context.getString(resourceId) + "\n", new BulletSpan(10), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                int resourceId = context.getResources().
                        getIdentifier("mother_champion_" + typeOfServiceValue.trim(), "string", context.getPackageName());
                parsedTypeOfService.append(context.getString(resourceId)).append("\n");
            }
            tvTypeOfService.setText(parsedTypeOfService);
        } else {
            tvTypeOfService.setVisibility(View.GONE);
        }
    }

    private void evaluateLinkedToPsychosocialGroup(Context context, Map<String, String> vals, TextView textView) {
        if (StringUtils.isNotBlank(getMapValue(vals, "linked_to_psychosocial_group"))) {
            SpannableStringBuilder linkedToGroupsStringBuilder = new SpannableStringBuilder();
            linkedToGroupsStringBuilder.append(context.getString(R.string.mother_champion_linked_to_psychosocial_group), boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE).append("\n");
            int resourceId = context.getResources().
                    getIdentifier(getMapValue(vals, "linked_to_psychosocial_group"), "string", context.getPackageName());
            linkedToGroupsStringBuilder.append(context.getString(resourceId)).append("\n");
            textView.setText(linkedToGroupsStringBuilder);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private void evaluateCounsellingGiven(Context context, Map<String, String> vals, TextView textView) {
        if (StringUtils.isNotBlank(getMapValue(vals, "counselling_given"))) {
            SpannableStringBuilder parsedCounsellingGiven = new SpannableStringBuilder();
            parsedCounsellingGiven.append(context.getString(R.string.mother_champion_counselling_given), boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE).append("\n");


            String counsellingGivenValue = getMapValue(vals, "counselling_given");
            String[] counsellingGivenValueArray;
            if (counsellingGivenValue.contains(",")) {
                counsellingGivenValueArray = counsellingGivenValue.split(",");
                for (String type : counsellingGivenValueArray) {
                    int resourceId = context.getResources().
                            getIdentifier("mother_champion_counselling_" + type.trim(), "string", context.getPackageName());
                    parsedCounsellingGiven.append(context.getString(resourceId) + "\n", new BulletSpan(10), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                int resourceId = context.getResources().
                        getIdentifier("mother_champion_counselling_" + counsellingGivenValue.trim(), "string", context.getPackageName());

                parsedCounsellingGiven.append(context.getString(resourceId)).append("\n");
            }

            textView.setText(parsedCounsellingGiven);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private void evaluateReferralsIssuedToOtherServices(Context context, Map<String, String> vals, TextView textView) {
        if (StringUtils.isNotBlank(getMapValue(vals, "referrals_issued_other_services"))) {
            SpannableStringBuilder parsedReferralsIssued = new SpannableStringBuilder();
            parsedReferralsIssued.append(context.getString(R.string.mother_champion_referrals_issued_other_services), boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE).append("\n");


            String referralsIssuedValue = getMapValue(vals, "referrals_issued_other_services");
            String[] referralsIssuedList;
            if (referralsIssuedValue.contains(",")) {
                referralsIssuedList = referralsIssuedValue.split(",");
                for (String type : referralsIssuedList) {
                    int resourceId = context.getResources().
                            getIdentifier("mother_champion_referrals_issued_other_services_" + type.trim(), "string", context.getPackageName());
                    parsedReferralsIssued.append(context.getString(resourceId) + "\n", new BulletSpan(10), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                int resourceId = context.getResources().
                        getIdentifier("mother_champion_referrals_issued_other_services_" + referralsIssuedValue.trim(), "string", context.getPackageName());
                parsedReferralsIssued.append(context.getString(resourceId)).append("\n");
            }
            textView.setText(parsedReferralsIssued);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private String getMapValue(Map<String, String> map, String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return "";
    }
}
