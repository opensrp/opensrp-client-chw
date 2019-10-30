package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.core.activity.CoreChildMedicalHistoryActivity;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.Vaccine;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class DefaultChildMedicalHistoryActivity implements CoreChildMedicalHistoryActivity.Flavor {

    protected LayoutInflater inflater;
    private Context context;
    private List<Visit> visits;
    private Map<String, List<Vaccine>> vaccineMap;
    private List<ServiceRecord> serviceTypeListMap;
    private LinearLayout parentView;

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
        evaluateLastVisitDate();
        evaluateImmunizations();
        /*
        evaluateGrowthAndNutrition();
        evaluateECD();
        evaluateLLITN();

         */
    }

    private void evaluateLastVisitDate() {
        if (visits.size() > 0) {
            View view = inflater.inflate(R.layout.medical_history_item, null);
            TextView tvTitle = view.findViewById(R.id.tvTitle);
            tvTitle.setText(context.getString(R.string.last_visit));

            int days = Days.daysBetween(new DateTime(visits.get(0).getDate()), new DateTime()).getDays();
            TextView tvInfo = view.findViewById(R.id.tvInfo);
            tvInfo.setText(context.getString(R.string.last_visit_40_days_ago, Integer.toString(days)));
            parentView.addView(view);
        }
    }

    private void evaluateImmunizations() {
        if (vaccineMap != null && vaccineMap.size() > 0) {
            View view = inflater.inflate(R.layout.medical_history_item_child_immunization, null);
            TextView tvTitle = view.findViewById(R.id.tvTitle);
            tvTitle.setText(context.getString(R.string.immunization));

            RelativeLayout rlAgeOne = view.findViewById(R.id.rlAgeOne);
            rlAgeOne.setVisibility(View.GONE);
            RelativeLayout rlAgeTwo = view.findViewById(R.id.rlAgeTwo);
            rlAgeTwo.setVisibility(View.GONE);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

            LinearLayout llItems = view.findViewById(R.id.llItems);
            for (Map.Entry<String, List<Vaccine>> entry : vaccineMap.entrySet()) {
                View headerView = inflater.inflate(R.layout.vaccine_header_view, null);
                TextView tvHeader = headerView.findViewById(R.id.header_text);

                tvHeader.setText(getVaccineTitle(entry.getKey(), context));
                llItems.addView(headerView);

                for (Vaccine vaccine : entry.getValue()) {
                    View contentView = inflater.inflate(R.layout.vaccine_content_view, null);
                    TextView tvContentView = contentView.findViewById(R.id.name_date_tv);

                    String val = vaccine.getName().toLowerCase().replace(" ", "_");
                    String translated = Utils.getStringResourceByName(val, context);
                    tvContentView.setText(String.format("%s %s", translated, sdf.format(vaccine.getDate())));

                    llItems.addView(contentView);
                }
            }
            parentView.addView(view);
        }
    }

    private String getVaccineTitle(String name, Context context) {
        return name.contains("birth") ? context.getString(R.string.at_birth) :
                name.replace("w", " " + context.getString(R.string.week_full))
                        .replace("m", " " + context.getString(R.string.month_full));
    }

    /**
     private void evaluateGrowthAndNutrition() {

     }

     private void evaluateECD() {

     }

     private void evaluateLLITN() {

     }
     **/
}
