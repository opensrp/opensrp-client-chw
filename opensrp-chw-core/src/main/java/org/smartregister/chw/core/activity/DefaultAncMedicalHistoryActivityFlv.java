package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.R;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class DefaultAncMedicalHistoryActivityFlv implements CoreAncMedicalHistoryActivity.Flavor {

    protected LinearLayout linearLayoutAncCard;
    protected LinearLayout linearLayoutHealthFacilityVisit;
    protected LinearLayout linearLayoutHealthFacilityVisitDetails;
    protected LayoutInflater inflater;
    private LinearLayout linearLayoutLastVisit;
    private LinearLayout linearLayoutTTImmunization, linearLayoutTTImmunizationDetails;
    private LinearLayout linearLayoutIPTp, linearLayoutIPTpDetails;
    private TextView customFontTextViewLastVisit;
    private TextView customFontTextViewAncCard;

    @Override
    public View bindViews(Activity activity) {
        inflater = activity.getLayoutInflater();
        View view = inflater.inflate(org.smartregister.chw.opensrp_chw_anc.R.layout.medical_history_details, null);

        linearLayoutLastVisit = view.findViewById(R.id.linearLayoutLastVisit);
        linearLayoutAncCard = view.findViewById(R.id.linearLayoutAncCard);
        linearLayoutHealthFacilityVisit = view.findViewById(R.id.linearLayoutHealthFacilityVisit);
        linearLayoutHealthFacilityVisitDetails = view.findViewById(R.id.linearLayoutHealthFacilityVisitDetails);
        linearLayoutTTImmunization = view.findViewById(R.id.linearLayoutTTImmunization);
        linearLayoutTTImmunizationDetails = view.findViewById(R.id.linearLayoutTTImmunizationDetails);
        linearLayoutIPTp = view.findViewById(R.id.linearLayoutIPTp);
        linearLayoutIPTpDetails = view.findViewById(R.id.linearLayoutIPTpDetails);
        customFontTextViewLastVisit = view.findViewById(R.id.customFontTextViewLastVisit);
        customFontTextViewAncCard = view.findViewById(R.id.customFontTextViewAncCard);
        return view;
    }

    @Override
    public void processViewData(List<Visit> visits, Context context) {

        if (visits.size() > 0) {

            int days = 0;
            String has_card = "No";
            List<Map<String, String>> hf_visits = new ArrayList<>();
            Map<String, String> immunizations = new HashMap<>();
            Map<String, String> services = new HashMap<>();

            int x = 0;
            while (x < visits.size()) {

                // the first object in this list is the days difference
                if (x == 0) {
                    days = Days.daysBetween(new DateTime(visits.get(0).getDate()), new DateTime()).getDays();
                }

                // anc card
                if (has_card.equalsIgnoreCase("No")) {
                    List<VisitDetail> details = visits.get(x).getVisitDetails().get("anc_card");
                    if (details != null && StringUtils.isNotBlank(details.get(0).getHumanReadable())) {
                        has_card = details.get(0).getHumanReadable();
                    }

                }


                String[] hf_params = {"anc_hf_visit_date", "weight", "sys_bp", "dia_bp", "hb_level", "ifa_received", "tests_done"};
                extractHFVisit(visits, hf_params, hf_visits, x, context);
                extractImmunization(visits, immunizations, x);
                extractIPSp(visits, services, x);

                x++;
            }

            processLastVisit(days, context);
            processAncCard(has_card, context);
            processHealthFacilityVisit(hf_visits, context);
            processTTImmunization(immunizations, context);
            processIPTp(services, context);
        }
    }
    private String getString(String x, Context context){
        switch (x.toLowerCase().trim()){
            case "none":
                return context.getString(R.string.edu_level_none);
            case "urine analysis":
                return context.getString(R.string.urine_analysis);
            case "hiv test":
                return context.getString(R.string.hiv_test);
            case "syphilis test":
                return context.getString(R.string.syphilis_test);
                default:
                    return x;
        }
    }

    public String getTexts(Context context, List<VisitDetail> visitDetails){
        if(visitDetails == null)
            return "";

        List<String> texts = new ArrayList<>();
        for (VisitDetail vd: visitDetails){
            String val = NCUtils.getText(vd);
            if(StringUtils.isNotBlank(val))
                texts.add(getString(val.trim(), context));
        }
        return NCUtils.toCSV(texts);
    }

    private void extractHFVisit(List<Visit> sourceVisits, String[] hf_params, List<Map<String, String>> hf_visits, int iteration, Context context) {
        List<VisitDetail> hf_details = sourceVisits.get(iteration).getVisitDetails().get("anc_hf_visit");
        if (hf_details != null) {
            String val = hf_details.get(0).getHumanReadable();
            if (StringUtils.isNotBlank(val) && val.equalsIgnoreCase("Yes")) {
                // get the hf details
                Map<String, String> map = new HashMap<>();
                for (String param : hf_params) {
                    List<VisitDetail> details = sourceVisits.get(iteration).getVisitDetails().get(param);
                    map.put(param, getTexts(context,details));
                }
                hf_visits.add(map);
            }
        }
    }

    private void extractImmunization(List<Visit> sourceVisits, Map<String, String> destinationMap, int iteration) {
        int tt_x = 1;
        while (tt_x <= 5) {
            String name = MessageFormat.format("tt{0}_date", tt_x);
            List<VisitDetail> details = sourceVisits.get(iteration).getVisitDetails().get(name);

            if (details != null && StringUtils.isNotBlank(details.get(0).getDetails())) {
                String tt_date = details.get(0).getDetails();
                destinationMap.put(name, tt_date);
            }
            tt_x++;
        }
    }

    private void extractIPSp(List<Visit> sourceVisits, Map<String, String> destinationMap, int iteration) {
        int ipsp_x = 1;
        while (ipsp_x <= 5) {
            String name = MessageFormat.format("iptp{0}_date", ipsp_x);
            List<VisitDetail> details = sourceVisits.get(iteration).getVisitDetails().get(name);

            if (details != null && StringUtils.isNotBlank(details.get(0).getDetails())) {
                String date = details.get(0).getDetails();
                destinationMap.put(name, date);
            }
            ipsp_x++;
        }
    }

    private void processLastVisit(int days, Context context) {
        linearLayoutLastVisit.setVisibility(View.VISIBLE);
        String str_days = days < 1 ? context.getString(R.string.less_than_twenty_four) : String.valueOf(days);
        customFontTextViewLastVisit.setText(StringUtils.capitalize(MessageFormat.format(context.getString(R.string.days_ago), str_days)));

    }

    protected void processAncCard(String has_card, Context context) {
        linearLayoutAncCard.setVisibility(View.VISIBLE);
        customFontTextViewAncCard.setText(MessageFormat.format("{0}: {1}", context.getString(R.string.anc_home_visit_anc_card_received),getTranslatedText(context, has_card.toLowerCase())));
    }

    protected void processHealthFacilityVisit(List<Map<String, String>> hf_visits, Context context) {
        if (hf_visits != null && hf_visits.size() > 0) {
            linearLayoutHealthFacilityVisit.setVisibility(View.VISIBLE);

            int x = 0;
            for (Map<String, String> vals : hf_visits) {
                View view = inflater.inflate(R.layout.medial_history_anc_visit, null);

                TextView tvTitle = view.findViewById(R.id.title);
                TextView tvWeight = view.findViewById(R.id.weight);
                TextView tvBP = view.findViewById(R.id.bp);
                TextView tvHB = view.findViewById(R.id.hb);
                TextView tvIfa = view.findViewById(R.id.ifa_received);
                TextView tvTests = view.findViewById(R.id.tests);

                tvTitle.setText(MessageFormat.format(context.getString(R.string.anc_visit_date), (hf_visits.size() - x), getMapValue(vals, "anc_hf_visit_date")));
                tvWeight.setText(MessageFormat.format(context.getString(R.string.weight_in_kgs), getMapValue(vals, "weight")));
                tvBP.setText(MessageFormat.format(context.getString(R.string.bp_in_mmhg), getMapValue(vals, "sys_bp"), getMapValue(vals, "dia_bp")));
                tvHB.setText(context.getString(R.string.hb_level_in_g_dl, getMapValue(vals, "hb_level")));
                tvIfa.setText(MessageFormat.format(context.getString(R.string.ifa_received_status), getTranslatedText(context, getMapValue(vals, "ifa_received"))));
                tvTests.setText(MessageFormat.format(context.getString(R.string.tests_done_details), getMapValue(vals, "tests_done")));

                linearLayoutHealthFacilityVisitDetails.addView(view, 0);

                x++;
            }
        }
    }

    private String getTranslatedText(Context context, String val){

        switch (val.toLowerCase()){
            case "yes":
                return context.getString(R.string.yes);
            case "no":
                return context.getString(R.string.no);
                default:
                    return val;
        }

    }

    private void processTTImmunization(Map<String, String> immunizations, Context context) {
        int visible = 0;
        for (Map.Entry<String, String> vals : new TreeMap<>(immunizations).entrySet()) {
            String key = vals.getKey().toLowerCase().replace("_date", "").toUpperCase();
            String val = vals.getValue();

            if (!val.contains("not")) {
                View view = inflater.inflate(R.layout.vaccine_content_view, null);
                TextView info = view.findViewById(R.id.name_date_tv);
                info.setText(MessageFormat.format(context.getString(R.string.vaccines_done_date), key, val));

                linearLayoutTTImmunizationDetails.addView(view, visible);
                visible++;
            }
        }
        linearLayoutTTImmunization.setVisibility(visible > 0 ? View.VISIBLE : View.GONE);
    }

    private void processIPTp(Map<String, String> services, Context context) {
        int visible = 0;
        for (Map.Entry<String, String> vals : new TreeMap<>(services).entrySet()) {
            String key = "IPTp-SP dose " + vals.getKey().replace("iptp", "").replace("_date", "");
            String val = vals.getValue();

            if (!val.contains("not")) {
                View view = inflater.inflate(R.layout.vaccine_content_view, null);
                TextView info = view.findViewById(R.id.name_date_tv);
                info.setText(MessageFormat.format(context.getString(R.string.vaccines_done_date), key, val));

                linearLayoutIPTpDetails.addView(view, visible);
                visible++;
            }
        }
        linearLayoutIPTp.setVisibility(visible > 0 ? View.VISIBLE : View.GONE);
    }

    private String getMapValue(Map<String, String> map, String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return "";
    }
}
