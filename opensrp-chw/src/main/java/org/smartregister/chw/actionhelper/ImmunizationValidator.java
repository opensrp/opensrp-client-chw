package org.smartregister.chw.actionhelper;

import org.joda.time.LocalDate;
import org.smartregister.chw.anc.domain.VaccineDisplay;
import org.smartregister.chw.anc.fragment.BaseHomeVisitImmunizationFragment;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.core.model.VaccineTaskModel;
import org.smartregister.chw.core.utils.VaccineScheduleUtil;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.VaccineWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This view driver evaluates if a view is valid or not
 */
public class ImmunizationValidator implements BaseAncHomeVisitAction.Validator {
    private Map<String, BaseHomeVisitImmunizationFragment> fragments = new LinkedHashMap<>();
    private Map<String, VaccineTaskModel> vaccineTaskModelMap = new LinkedHashMap<>();
    private List<String> keyPositions = new ArrayList<>();
    private int lastValidKeyPosition = 0;

    public void addFragment(String key, BaseHomeVisitImmunizationFragment fragment, VaccineTaskModel vaccineTaskModel) {
        if (!fragments.containsKey(key)) {
            keyPositions.add(key);
        }
        fragments.put(key, fragment);
        vaccineTaskModelMap.put(key, vaccineTaskModel);
    }

    /**
     * An immunization window is valid only if there are pending vaccines to be recorded
     *
     * @param s
     * @return
     */
    @Override
    public boolean isValid(String s) {
        BaseHomeVisitImmunizationFragment fragment = fragments.get(s);
        if (fragment == null)
            return false;

        return fragment.getVaccineDisplays().size() > 0;
    }

    /**
     * Only enable the other windows when an object is recorded.
     *
     * @param s
     * @return
     */
    @Override
    public boolean isEnabled(String s) {
        int position = keyPositions.indexOf(s);
        return position >= 0 && position <= lastValidKeyPosition;
    }

    /**
     * receives a notification on the changed action
     *
     * @param s
     */
    @Override
    public void onChanged(String s) {
        int position = keyPositions.indexOf(s);
        lastValidKeyPosition = position + 1;

        int next = position + 1;
        if (next == keyPositions.size())
            return;

        int y = position + 1;
        int x = position + 1;
        String key = s;
        while (x < keyPositions.size()) {

            BaseHomeVisitImmunizationFragment prevFragment = fragments.get(key);

            key = keyPositions.get(x);
            BaseHomeVisitImmunizationFragment fragment = fragments.get(key);
            VaccineTaskModel taskModel = vaccineTaskModelMap.get(key);
            if (fragment == null || taskModel == null)
                continue;

            if (x != y) {
                // fragment.resetViewPayload();
            }

            List<VaccineDisplay> vaccineDisplays = new ArrayList<>();
            if (prevFragment != null)
                vaccineDisplays.addAll(prevFragment.getVaccineDisplays().values());

            List<VaccineWrapper> wrappers = VaccineScheduleUtil.recomputeSchedule(taskModel, vaccineDisplays);
            List<VaccineDisplay> displays = generateDisplaysFromWrappers(wrappers, taskModel.getAnchorDate().toDate());

            // update the vaccines
            Map<String, VaccineDisplay> linkedHashMap = new LinkedHashMap<>();
            for (VaccineDisplay vaccineDisplay : displays) {
                linkedHashMap.put(vaccineDisplay.getVaccineWrapper().getName(), vaccineDisplay);
            }
            fragment.setVaccineDisplays(linkedHashMap);

            x++;
        }

        String skey = keyPositions.get(next);
        BaseHomeVisitImmunizationFragment nextFragment = fragments.get(skey);
        while (nextFragment != null && nextFragment.getVaccineDisplays().size() == 0) {
            lastValidKeyPosition++;
            next++;
            if(next < keyPositions.size()){
                skey = keyPositions.get(next);
                nextFragment = fragments.get(skey);
            }
        }

    }

    private List<VaccineDisplay> generateDisplaysFromWrappers(List<VaccineWrapper> wrappers, Date startDate) {
        List<VaccineDisplay> displays = new ArrayList<>();
        for (VaccineWrapper vaccineWrapper : wrappers) {
            Alert alert = vaccineWrapper.getAlert();
            VaccineDisplay display = new VaccineDisplay();
            display.setVaccineWrapper(vaccineWrapper);
            display.setStartDate(alert != null ? new LocalDate(alert.startDate()).toDate() : startDate);
            display.setEndDate(alert != null && alert.expiryDate() != null ? new LocalDate(alert.expiryDate()).toDate() : new Date());
            display.setValid(false);
            displays.add(display);
        }
        return displays;
    }

}
