package org.smartregister.chw.task;

import static com.vijay.jsonwizard.constants.JsonFormConstants.REPORT_MONTH;

import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.core.activity.HIA2ReportsActivity;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.domain.Hia2Indicator;
import org.smartregister.chw.core.domain.MonthlyTally;
import org.smartregister.chw.core.repository.HIA2IndicatorsRepository;
import org.smartregister.chw.core.repository.MonthlyTalliesRepository;
import org.smartregister.chw.core.task.StartDraftMonthlyFormTask;
import org.smartregister.util.FormUtils;

import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class ChwStartDraftMonthlyFormTask extends StartDraftMonthlyFormTask {

    public ChwStartDraftMonthlyFormTask(HIA2ReportsActivity baseActivity, Date date, String formName) {
        super(baseActivity, date, formName);
    }

    @Override
    protected Intent doInBackground(Void... params) {
        try {
            MonthlyTalliesRepository monthlyTalliesRepository = CoreChwApplication.getInstance().monthlyTalliesRepository();
            List<MonthlyTally> monthlyTallies = monthlyTalliesRepository.findDrafts(MonthlyTalliesRepository.DF_YYYYMM.format(date));

            HIA2IndicatorsRepository hIA2IndicatorsRepository = CoreChwApplication.getInstance().hIA2IndicatorsRepository();
            List<Hia2Indicator> hia2Indicators = hIA2IndicatorsRepository.fetchAll();
            if (hia2Indicators == null || hia2Indicators.isEmpty()) {
                return null;
            }
            JSONObject form = new FormUtils(baseActivity).getFormJson(formName);

            JSONArray fieldsArray = form.getJSONObject("step1").getJSONArray("fields");
            JSONArray fieldsArray2 = form.getJSONObject("step2").getJSONArray("fields");
            JSONArray fieldsArray3 = form.getJSONObject("step3").getJSONArray("fields");
            JSONArray fieldsArray4 = form.getJSONObject("step4").getJSONArray("fields");
            JSONArray fieldsArray5 = form.getJSONObject("step5").getJSONArray("fields");
            JSONArray fieldsArray6 = form.getJSONObject("step6").getJSONArray("fields");
            JSONArray fieldsArray7 = form.getJSONObject("step7").getJSONArray("fields");


            int i = 1;
            // This map holds each category as key and all the fields for that category as the
            // value (jsonarray)
            for (Hia2Indicator hia2Indicator : hia2Indicators) {

                if (hia2Indicator.getDescription() == null) {
                    hia2Indicator.setDescription("");
                }
                String label;

                try {
                    int resourceId = baseActivity.getResources().getIdentifier(hia2Indicator.getDescription(), "string", baseActivity.getPackageName());
                    label = baseActivity.getResources().getString(resourceId);
                } catch (Exception e) {
                    Timber.e(e);
                    label = hia2Indicator.getDescription();
                }
                JSONObject labelJsonObject = new JSONObject();
                JSONObject editTextJsonObject = new JSONObject();
                updateJsonObjects(editTextJsonObject, labelJsonObject, hia2Indicator, label, monthlyTallies);
                if (i <= 5) {
                    fieldsArray.put(labelJsonObject);
                    fieldsArray.put(editTextJsonObject);
                } else if (i <= 9) {
                    fieldsArray2.put(labelJsonObject);
                    fieldsArray2.put(editTextJsonObject);
                } else if (i <= 10) {
                    fieldsArray3.put(labelJsonObject);
                    fieldsArray3.put(editTextJsonObject);
                } else if (i <= 15) {
                    fieldsArray4.put(labelJsonObject);
                    fieldsArray4.put(editTextJsonObject);
                } else if (i <= 17) {
                    fieldsArray5.put(labelJsonObject);
                    fieldsArray5.put(editTextJsonObject);
                } else if (i <= 29) {
                    fieldsArray6.put(labelJsonObject);
                    fieldsArray6.put(editTextJsonObject);
                } else if (i <= 44) {
                    fieldsArray7.put(labelJsonObject);
                    fieldsArray7.put(editTextJsonObject);
                }
                i++;
            }
            // Add the confirm button
            JSONObject buttonObject = createFormConfirmButton();
            fieldsArray7.put(buttonObject);
            form.put(REPORT_MONTH, HIA2ReportsActivity.dfyymmdd.format(date));
            form.put("identifier", "HIA2ReportForm");

            return createServiceIntent(form);
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }

        return null;
    }
}
