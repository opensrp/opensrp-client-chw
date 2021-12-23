package org.smartregister.chw.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.VaccineDisplay;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.util.DatePickerUtils;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public class BaseHomeVisitImmunizationFragmentFlv extends DefaultBaseHomeVisitImmunizationFragment {

    public static BaseHomeVisitImmunizationFragmentFlv getInstance(final BaseAncHomeVisitContract.VisitView view, String baseEntityID, Map<String, List<VisitDetail>> details, List<VaccineDisplay> vaccineDisplays) {
        return getInstance(view, baseEntityID, details, vaccineDisplays, true);
    }

    public static BaseHomeVisitImmunizationFragmentFlv getInstance(final BaseAncHomeVisitContract.VisitView view, String baseEntityID, Map<String, List<VisitDetail>> details, List<VaccineDisplay> vaccineDisplays, boolean defaultChecked) {
        BaseHomeVisitImmunizationFragmentFlv fragment = new BaseHomeVisitImmunizationFragmentFlv();
        fragment.visitView = view;
        fragment.baseEntityID = baseEntityID;
        fragment.details = details;
        fragment.vaccinesDefaultChecked = defaultChecked;
        for (VaccineDisplay vaccineDisplay : vaccineDisplays) {
            String name = NCUtils.removeSpaces(vaccineDisplay.getVaccineWrapper().getName());
            if (details != null && details.containsKey(name)) {
                String value = NCUtils.getText(details.get(name));

                try {
                    vaccineDisplay.setDateGiven(dateFormat.parse(value));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            fragment.vaccineDisplays.put(vaccineDisplay.getVaccineWrapper().getName(), vaccineDisplay);
        }

        if (details != null && details.size() > 0) {
            fragment.jsonObject = NCUtils.getVisitJSONFromVisitDetails(view.getMyContext(), baseEntityID, details, vaccineDisplays);
            JsonFormUtils.populateForm(fragment.jsonObject, details);
        }

        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        callDatePickerUtilsThemeDatePicker(singleDatePicker, new char[]{'d', 'm', 'y'});
        super.onViewCreated(view, savedInstanceState);
    }

    @VisibleForTesting
    void callDatePickerUtilsThemeDatePicker(DatePicker datePicker, char[] ymdOrder) {
        DatePickerUtils.themeDatePicker(datePicker, ymdOrder);
    }

    @Override
    protected void setDatePickerTheme(DatePicker picker) {
        callDatePickerUtilsThemeDatePicker(picker, new char[]{'d', 'm', 'y'});
    }
}
