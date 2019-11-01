package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartregister.chw.R;
import org.smartregister.chw.adapter.MedicalHistoryAdapter;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.core.activity.CoreChildMedicalHistoryActivity;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.chw.domain.MedicalHistory;
import org.smartregister.chw.util.CustomDividerItemDecoration;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.Vaccine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class DefaultChildMedicalHistoryActivity implements CoreChildMedicalHistoryActivity.Flavor {

    protected LayoutInflater inflater;
    private Context context;
    private List<Visit> visits;
    private Map<String, List<Visit>> visitMap = new LinkedHashMap<>();
    private Map<String, List<Vaccine>> vaccineMap = new LinkedHashMap<>();
    private List<ServiceRecord> serviceTypeListMap;
    private LinearLayout parentView;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    @Override
    public View bindViews(Activity activity) {
        inflater = activity.getLayoutInflater();
        this.context = activity;
        parentView = new LinearLayout(activity);
        parentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parentView.setOrientation(LinearLayout.VERTICAL);
        return parentView;
    }

    @Override
    public void processViewData(List<Visit> visits, Map<String, List<Vaccine>> vaccineMap, List<ServiceRecord> serviceTypeListMap, Context context) {
        this.visits = visits;
        this.vaccineMap = vaccineMap;
        this.serviceTypeListMap = serviceTypeListMap;

        for (Visit v : this.visits) {
            List<Visit> type_visits = visitMap.get(v.getVisitType());
            if (type_visits == null) type_visits = new ArrayList<>();

            type_visits.add(v);
            visitMap.put(v.getVisitType(), type_visits);
        }

        evaluateLastVisitDate();
        evaluateImmunizations();
        evaluateGrowthAndNutrition();
        evaluateECD();
        evaluateLLITN();
    }

    private void evaluateLastVisitDate() {
        if (visits.size() > 0) {

            List<MedicalHistory> medicalHistories = new ArrayList<>();
            MedicalHistory history = new MedicalHistory();
            int days = Days.daysBetween(new DateTime(visits.get(visits.size() - 1).getDate()), new DateTime()).getDays();
            history.setText(context.getString(R.string.last_visit_40_days_ago, Integer.toString(days)));
            medicalHistories.add(history);

            View view = new ViewBuilder()
                    .withHistory(medicalHistories)
                    .withTitle(context.getString(R.string.last_visit))
                    .build();

            parentView.addView(view);
        }
    }

    private void evaluateImmunizations() {
        if (vaccineMap != null && vaccineMap.size() > 0) {

            // generate data
            List<MedicalHistory> medicalHistories = new ArrayList<>();
            for (Map.Entry<String, List<Vaccine>> entry : vaccineMap.entrySet()) {
                MedicalHistory history = new MedicalHistory();
                history.setTitle(getVaccineTitle(entry.getKey(), context));
                List<String> content = new ArrayList<>();
                for (Vaccine vaccine : entry.getValue()) {
                    String val = vaccine.getName().toLowerCase().replace(" ", "_");
                    String translated = Utils.getStringResourceByName(val, context);
                    content.add(String.format("%s - %s %s", translated, context.getString(R.string.done), sdf.format(vaccine.getDate())));
                }
                history.setText(content);
                medicalHistories.add(history);

            }

            View view = new ViewBuilder()
                    .withTitle(context.getString(R.string.immunization))
                    .withHistory(medicalHistories)
                    .withSeparator(false)
                    .withChildLayout(R.layout.medical_history_vaccine_item)
                    .withRootLayout(R.layout.medical_history_item_child_immunization)
                    .build();

            view.findViewById(R.id.rlAgeOne).setVisibility(View.GONE);
            view.findViewById(R.id.rlAgeTwo).setVisibility(View.GONE);

            parentView.addView(view);
        }
    }

    private String getVaccineTitle(String name, Context context) {
        return name.contains("birth") ? context.getString(R.string.at_birth) :
                name.replace("w", " " + context.getString(R.string.week_full))
                        .replace("m", " " + context.getString(R.string.month_full));
    }

    private void evaluateGrowthAndNutrition() {
        if (visitMap.size() > 0) {

            // generate data
            List<MedicalHistory> medicalHistories = new ArrayList<>();

            VisitDetailsFormatter vitaminA = (title, details, visitDate) -> {
                String numberOnly = title.replaceAll("[^0-9]", "");

                String date = NCUtils.getText(details);
                String done = context.getString(R.string.done);
                if (Constants.HOME_VISIT.VACCINE_NOT_GIVEN.equalsIgnoreCase(date))
                    return null;

                Date vaccineDate = VisitUtils.getDateFromString(date);

                return String.format("%s - %s %s",
                        context.getString(R.string.dose_number, numberOnly),
                        done,
                        vaccineDate != null ? sdf.format(vaccineDate) : ""
                );
            };
            medicalHistory(medicalHistories, CoreConstants.EventType.VITAMIN_A, context.getString(R.string.vitamin_a), vitaminA);

            VisitDetailsFormatter deworming = (title, details, visitDate) -> {
                String numberOnly = title.replaceAll("[^0-9]", "");

                String date = NCUtils.getText(details);
                String done = context.getString(R.string.done);
                if (Constants.HOME_VISIT.VACCINE_NOT_GIVEN.equalsIgnoreCase(date))
                    return null;

                Date vaccineDate = VisitUtils.getDateFromString(date);

                return String.format("%s - %s %s",
                        context.getString(R.string.dose_number, numberOnly),
                        done,
                        vaccineDate != null ? sdf.format(vaccineDate) : ""
                );
            };
            medicalHistory(medicalHistories, CoreConstants.EventType.DEWORMING, context.getString(R.string.deworming), deworming);

            VisitDetailsFormatter dietary = (title, details, visitDate) -> {
                String translated = NCUtils.getText(details);
                return String.format("%s - %s %s",
                        translated,
                        context.getString(R.string.done),
                        sdf.format(visitDate)
                );
            };
            medicalHistory(medicalHistories, CoreConstants.EventType.MINIMUM_DIETARY_DIVERSITY, context.getString(R.string.minimum_dietary_title), dietary);

            VisitDetailsFormatter muac = (title, details, visitDate) -> {
                String translated = NCUtils.getText(details);
                return String.format("%s - %s %s",
                        translated,
                        context.getString(R.string.done),
                        sdf.format(visitDate)
                );
            };
            medicalHistory(medicalHistories, CoreConstants.EventType.MUAC, context.getString(R.string.muac_title), muac);

            View view = new ViewBuilder()
                    .withHistory(medicalHistories)
                    .withTitle(context.getString(R.string.growth_and_nutrition))
                    .build();

            parentView.addView(view);
        }
    }

    private void evaluateECD() {
        if (visits.size() > 0) {

            List<Visit> visits = visitMap.get(CoreConstants.EventType.ECD);
            if (visits != null) {

                // generate data
                List<MedicalHistory> medicalHistories = new ArrayList<>();


                View view = new ViewBuilder()
                        .withHistory(medicalHistories)
                        .withTitle(context.getString(R.string.growth_and_nutrition))
                        .build();

                parentView.addView(view);
            }
        }
    }

    private void evaluateLLITN() {
        if (visits.size() > 0) {
            List<MedicalHistory> medicalHistories = new ArrayList<>();

            VisitDetailsFormatter llitn = (title, details, visitDate) -> {
                String text = NCUtils.getText(details);
                String translated = context.getString(text.toLowerCase().contains("yes") ? R.string.yes : R.string.no);
                return String.format("%s - %s %s",
                        translated,
                        context.getString(R.string.done),
                        sdf.format(visitDate)
                );
            };
            medicalHistory(medicalHistories, CoreConstants.EventType.LLITN, null, llitn);

            View view = new ViewBuilder()
                    .withHistory(medicalHistories)
                    .withTitle(context.getString(R.string.llitn_title))
                    .build();


            parentView.addView(view);
        }
    }

    private void medicalHistory(List<MedicalHistory> medicalHistories, String type, String title, VisitDetailsFormatter formatter) {
        List<Visit> content = visitMap.get(type);
        if (content != null) {
            MedicalHistory history = new MedicalHistory();

            if (StringUtils.isNotBlank(title)) history.setTitle(title);

            for (Visit v : content) {
                Map<String, List<VisitDetail>> detailsMap = v.getVisitDetails();
                if (detailsMap != null && detailsMap.size() > 0) {
                    for (Map.Entry<String, List<VisitDetail>> entry : detailsMap.entrySet()) {
                        String text = formatter.format(entry.getKey(), entry.getValue(), v.getDate());
                        if (StringUtils.isNotBlank(text)) history.setText(text);
                    }
                }
            }
            medicalHistories.add(history);
        }
    }

    private interface VisitDetailsFormatter {
        String format(String title, List<VisitDetail> details, Date visitDate);
    }

    private class ViewBuilder {
        @LayoutRes
        private int rootLayout = R.layout.medical_history_nested;
        private String title = null;
        private List<MedicalHistory> histories = new ArrayList<>();
        private boolean hasSeparator = true;
        @LayoutRes
        private int childLayout = R.layout.medical_history_nested_sub_item;

        public ViewBuilder withRootLayout(int rootLayout) {
            this.rootLayout = rootLayout;
            return this;
        }

        public ViewBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public ViewBuilder withHistory(List<MedicalHistory> histories) {
            this.histories = histories;
            return this;
        }

        public ViewBuilder withSeparator(boolean hasSeparator) {
            this.hasSeparator = hasSeparator;
            return this;
        }

        public ViewBuilder withChildLayout(int childLayout) {
            this.childLayout = childLayout;
            return this;
        }

        public View build() {
            View view = inflater.inflate(rootLayout, null);
            TextView tvTitle = view.findViewById(R.id.tvTitle);
            tvTitle.setText(title);
            tvTitle.setAllCaps(true);

            RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MedicalHistoryAdapter(histories, childLayout));

            if (hasSeparator)
                recyclerView.addItemDecoration(new CustomDividerItemDecoration(ContextCompat.getDrawable(context, R.drawable.divider)));

            return view;
        }
    }
}
