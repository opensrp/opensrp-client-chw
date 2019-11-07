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
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.ChildUtilsFlv;
import org.smartregister.chw.util.CustomDividerItemDecoration;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.Vaccine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public abstract class DefaultChildMedicalHistoryActivity implements CoreChildMedicalHistoryActivity.Flavor {

    protected LayoutInflater inflater;
    private Context context;
    private List<Visit> visits;
    private Map<String, List<Visit>> visitMap = new LinkedHashMap<>();
    private Map<String, List<Vaccine>> vaccineMap = new LinkedHashMap<>();
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
            int days = Days.daysBetween(new DateTime(visits.get(0).getDate()), new DateTime()).getDays();
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

            List<String> vaccineGiven = new ArrayList<>();
            // generate data
            List<MedicalHistory> medicalHistories = new ArrayList<>();
            for (Map.Entry<String, List<Vaccine>> entry : vaccineMap.entrySet()) {
                MedicalHistory history = new MedicalHistory();
                history.setTitle(getVaccineTitle(entry.getKey().toLowerCase().trim(), context));
                List<String> content = new ArrayList<>();
                for (Vaccine vaccine : entry.getValue()) {
                    String val = vaccine.getName().toLowerCase().replace(" ", "_");
                    vaccineGiven.add(val.replace("_", ""));
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

            ChildUtils.Flavor childUtilsFlv = new ChildUtilsFlv();
            boolean oneYrFully = vaccineGiven.containsAll(Arrays.asList(childUtilsFlv.getOneYearVaccines()));
            boolean twoYrFully = vaccineGiven.containsAll(Arrays.asList(childUtilsFlv.getTwoYearVaccines()));

            view.findViewById(R.id.rlAgeOne).setVisibility(oneYrFully ? View.VISIBLE : View.GONE);
            view.findViewById(R.id.rlAgeTwo).setVisibility(twoYrFully ? View.VISIBLE : View.GONE);

            parentView.addView(view);
        }
    }

    private String getVaccineTitle(String name, Context context) {
        String res = name.contains("birth") ? context.getString(R.string.at_birth) :
                name.replace("weeks", " " + context.getString(R.string.week_full))
                        .replace("months", " " + context.getString(R.string.month_full));

        return StringUtils.capitalize(res);
    }

    private void evaluateGrowthAndNutrition() {
        if (visitMap.size() > 0) {

            // generate data
            List<MedicalHistory> medicalHistories = new ArrayList<>();
            AtomicReference<Integer> count = new AtomicReference<>(0);
            medicalHistory(medicalHistories, CoreConstants.EventType.EXCLUSIVE_BREASTFEEDING, context.getString(R.string.exclusive_breastfeeding), getBreastFeedingFormatter(count));

            medicalHistory(medicalHistories, CoreConstants.EventType.VITAMIN_A, context.getString(R.string.vitamin_a), getVitaminAformatter());

            medicalHistory(medicalHistories, CoreConstants.EventType.DEWORMING, context.getString(R.string.deworming), getDewormingFormatter());

            medicalHistory(medicalHistories, CoreConstants.EventType.MINIMUM_DIETARY_DIVERSITY, context.getString(R.string.minimum_dietary_title), getDieataryFormatter());

            medicalHistory(medicalHistories, CoreConstants.EventType.MUAC, context.getString(R.string.muac_title), getMUACFormatter());

            if (medicalHistories.size() > 0) {
                View view = new ViewBuilder()
                        .withHistory(medicalHistories)
                        .withTitle(context.getString(R.string.growth_and_nutrition))
                        .build();

                parentView.addView(view);
            }
        }
    }

    private VisitDetailsFormatter getBreastFeedingFormatter(AtomicReference<Integer> count) {
        return (title, details, visitDate) -> {
            count.getAndSet(count.get() + 1);
            return String.format("%s: %s",
                    context.getString(R.string.exclusive_breastfeeding_months, Integer.toString(count.get())),
                    context.getString(NCUtils.getText(details).toLowerCase().contains("yes") ? R.string.yes : R.string.no)
            );
        };
    }

    private VisitDetailsFormatter getVitaminAformatter() {
        return (title, details, visitDate) -> {
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
    }

    private VisitDetailsFormatter getDewormingFormatter() {
        return (title, details, visitDate) -> {
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
    }

    private VisitDetailsFormatter getDieataryFormatter() {
        return (title, details, visitDate) -> {
            String diet_diversity = NCUtils.getText(details);
            String value = "";
            if ("chk_no_animal_products".equalsIgnoreCase(diet_diversity)) {
                value = context.getString(R.string.minimum_dietary_choice_1);
            } else if ("chw_one_animal_product_or_fruit".equalsIgnoreCase(diet_diversity)) {
                value = context.getString(R.string.minimum_dietary_choice_2);
            } else if ("chw_one_animal_product_and_fruit".equalsIgnoreCase(diet_diversity)) {
                value = context.getString(R.string.minimum_dietary_choice_3);
            }

            return String.format("%s - %s %s",
                    value,
                    context.getString(R.string.done),
                    sdf.format(visitDate)
            );
        };
    }

    private VisitDetailsFormatter getMUACFormatter() {
        return (title, details, visitDate) -> {
            String value = null;
            String raw_value = NCUtils.getText(details);
            if ("chk_green".equalsIgnoreCase(raw_value)) {
                value = context.getString(R.string.muac_choice_1);
            } else if ("chk_yellow".equalsIgnoreCase(raw_value)) {
                value = context.getString(R.string.muac_choice_2);
            } else if ("chk_red".equalsIgnoreCase(raw_value)) {
                value = context.getString(R.string.muac_choice_3);
            }

            return String.format("%s - %s %s",
                    value,
                    context.getString(R.string.done),
                    sdf.format(visitDate)
            );
        };
    }

    private void evaluateECD() {
        if (visits.size() > 0) {

            List<Visit> visits = visitMap.get(CoreConstants.EventType.ECD);
            if (visits != null) {

                // generate data
                List<MedicalHistory> medicalHistories = new ArrayList<>();
                VisitDetailsFormatter formatter = (title, details, visitDate) -> {

                    String translatedTitle = null;
                    switch (title) {
                        case "develop_warning_signs":
                            translatedTitle = context.getString(R.string.dev_warning_sign);
                            break;
                        case "stim_skills":
                            translatedTitle = context.getString(R.string.care_stim_skill);
                            break;
                        default:
                            break;
                    }
                    if (StringUtils.isBlank(translatedTitle)) return null;

                    return String.format("%s %s",
                            translatedTitle,
                            context.getString(NCUtils.getText(details).toLowerCase().contains("yes") ? R.string.yes : R.string.no)
                    );
                };

                List<Visit> content = visitMap.get(CoreConstants.EventType.ECD);
                if (content != null) {

                    for (Visit v : content) {
                        MedicalHistory history = new MedicalHistory();

                        String title = sdf.format(v.getDate());
                        history.setTitle(title);

                        Map<String, List<VisitDetail>> detailsMap = v.getVisitDetails();
                        if (detailsMap != null && detailsMap.size() > 0) {
                            for (Map.Entry<String, List<VisitDetail>> entry : detailsMap.entrySet()) {
                                String text = formatter.format(entry.getKey(), entry.getValue(), v.getDate());
                                if (StringUtils.isNotBlank(text)) history.setText(text);
                            }
                        }

                        medicalHistories.add(history);
                    }
                }


                if (medicalHistories.size() > 0) {
                    View view = new ViewBuilder()
                            .withHistory(medicalHistories)
                            .withTitle(context.getString(R.string.ecd_title))
                            .withSeparator(false)
                            .build();

                    parentView.addView(view);
                }
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

            if (medicalHistories.size() > 0) {
                View view = new ViewBuilder()
                        .withHistory(medicalHistories)
                        .withTitle(context.getString(R.string.llitn_title))
                        .build();


                parentView.addView(view);
            }
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
