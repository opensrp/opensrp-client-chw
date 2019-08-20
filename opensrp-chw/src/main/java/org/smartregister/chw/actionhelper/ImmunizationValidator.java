package org.smartregister.chw.actionhelper;

import org.smartregister.chw.anc.fragment.BaseHomeVisitImmunizationFragment;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This view driver evaluates if a view is valid or not
 */
public class ImmunizationValidator implements BaseAncHomeVisitAction.Validator {
    private Map<String, BaseHomeVisitImmunizationFragment> fragments = new LinkedHashMap<>();
    private List<String> keyPositions = new ArrayList<>();
    private int lastValidKeyPosition = 0;

    public void addFragment(String key, BaseHomeVisitImmunizationFragment fragment) {
        if (!fragments.containsKey(key)) {
            keyPositions.add(key);
        }
        fragments.put(key, fragment);
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

        if(position > 0){
        }
    }
}
