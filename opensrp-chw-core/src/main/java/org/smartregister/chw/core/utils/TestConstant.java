package org.smartregister.chw.core.utils;

public class TestConstant {

    public static boolean IS_PHONE_NO_CHECK = false;
    public static boolean IS_TASK_VISIBLE = false;
    public static boolean IS_WASH_CHECK_VISIBLE = false;

    public static String[] getTestReceivedTwoYearVaccine() {
        return new String[]{"OPV0".toLowerCase(), "BCG".toLowerCase(), "OPV1".toLowerCase(), "OPV2".toLowerCase(), "OPV3".toLowerCase()
                , "Penta1".toLowerCase(), "Penta2".toLowerCase(), "Penta3".toLowerCase(), "PCV1".toLowerCase(), "PCV2".toLowerCase()
                , "PCV3".toLowerCase(), "Rota1".toLowerCase(), "Rota2".toLowerCase(), "IPV".toLowerCase(), "MCV1".toLowerCase()
                , "MCV2".toLowerCase(), "yellowfever".toLowerCase()};
    }

    public static String[] getTestReceivedOneYearVaccine() {
        return new String[]{"OPV0".toLowerCase(), "BCG".toLowerCase(), "OPV1".toLowerCase(), "OPV2".toLowerCase(), "OPV3".toLowerCase()
                , "Penta1".toLowerCase(), "Penta2".toLowerCase(), "Penta3".toLowerCase(), "PCV1".toLowerCase(), "PCV2".toLowerCase()
                , "PCV3".toLowerCase(), "Rota1".toLowerCase(), "Rota2".toLowerCase(), "IPV".toLowerCase(),
                "MCV1".toLowerCase(), "yellowfever".toLowerCase()};
    }
}
