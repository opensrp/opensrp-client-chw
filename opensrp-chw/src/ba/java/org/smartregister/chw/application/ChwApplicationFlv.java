package org.smartregister.chw.application;

import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.util.ChwDBConstants;
import org.smartregister.family.util.DBConstants;

import java.util.HashMap;
import java.util.Map;

public class ChwApplicationFlv extends DefaultChwApplicationFlv {
    @Override
    public boolean hasP2P() {
        return false;
    }

    @Override
    public boolean hasReferrals() {
        return true;
    }

    @Override
    public boolean flvSetFamilyLocation() {
        return true;
    }

    @Override
    public boolean hasANC() {
        return true;
    }

    @Override
    public boolean hasPNC() {
        return true;
    }

    @Override
    public boolean hasChildSickForm() {
        return true;
    }

    @Override
    public boolean hasFamilyPlanning() {
        return true;
    }

    @Override
    public boolean hasWashCheck() {
        return false;
    }

    @Override
    public boolean hasMalaria() {
        return false;
    }

    @Override
    public boolean hasServiceReport() {
        return true;
    }

    public boolean hasQR() {
        return true;
    }

    @Override
    public boolean hasJobAids() {
        return false;
    }

    @Override
    public boolean hasTasks() {
        return true;
    }

    @Override
    public boolean hasStockUsageReport() {
        return true;
    }

    @Override
    public boolean hasHIV() {
        return true;
    }

    @Override
    public boolean hasFamilyLocationRow() {
        return true;
    }

    @Override
    public boolean hasTB() {
     return true;
    }

    @Override
    public boolean usesPregnancyRiskProfileLayout() {
        return true;
    }

    public boolean getChildFlavorUtil() {
        return true;
    }

    @Override
    public boolean includeCurrentChild() {
        return true;
    }

    @Override
    public boolean hasMap() {
        return true;
    }

    @Override
    public boolean hasEventDateOnFamilyProfile() {
        return true;
    }

    @Override
    public Map<String, String[]> getFTSSearchMap() {
        Map<String, String[]> map = new HashMap<>();
        map.put(CoreConstants.TABLE_NAME.FAMILY, new String[]{
                DBConstants.KEY.BASE_ENTITY_ID, DBConstants.KEY.VILLAGE_TOWN, DBConstants.KEY.FIRST_NAME,
                DBConstants.KEY.LAST_NAME, DBConstants.KEY.UNIQUE_ID, ChwDBConstants.NEAREST_HEALTH_FACILITY
        });

        map.put(CoreConstants.TABLE_NAME.FAMILY_MEMBER, new String[]{
                DBConstants.KEY.BASE_ENTITY_ID, DBConstants.KEY.FIRST_NAME, DBConstants.KEY.MIDDLE_NAME,
                DBConstants.KEY.LAST_NAME, DBConstants.KEY.UNIQUE_ID, ChildDBConstants.KEY.ENTRY_POINT, DBConstants.KEY.DOB, DBConstants.KEY.DATE_REMOVED
        });

        map.put(CoreConstants.TABLE_NAME.CHILD, new String[]{
                DBConstants.KEY.BASE_ENTITY_ID, DBConstants.KEY.FIRST_NAME, DBConstants.KEY.MIDDLE_NAME,
                DBConstants.KEY.LAST_NAME, DBConstants.KEY.UNIQUE_ID, ChildDBConstants.KEY.ENTRY_POINT, DBConstants.KEY.DOB, DBConstants.KEY.DATE_REMOVED
        });
        return map;
    }
<<<<<<< HEAD

    @Override
    public boolean showsPhysicallyDisabledView() { return false; }
=======
>>>>>>> 939cab83bf354adff709f1c84ad320faf058d44c
}
