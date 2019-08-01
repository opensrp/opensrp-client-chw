package org.smartregister.chw.util;

public class TestConstant {

    public static boolean IS_PHONE_NO_CHECK = true;
    public static boolean IS_TASK_VISIBLE = true;

    public static String[] getTestReceivedTwoYearVaccine() {
        String[] list = {"OPV0".toLowerCase(), "BCG".toLowerCase(), "OPV1".toLowerCase(), "OPV2".toLowerCase(), "OPV3".toLowerCase()
                , "Penta1".toLowerCase(), "Penta2".toLowerCase(), "Penta3".toLowerCase(), "PCV1".toLowerCase(), "PCV2".toLowerCase()
                , "PCV3".toLowerCase(), "Rota1".toLowerCase(), "Rota2".toLowerCase(), "IPV".toLowerCase(), "MCV1".toLowerCase()
                , "MCV2".toLowerCase(), "yellowfever".toLowerCase(), "mcv2", "rota3", "mena"};
        return list;
    }

    public static String[] getTestReceivedOneYearVaccine() {
        String[] list = {"OPV0".toLowerCase(), "BCG".toLowerCase(), "OPV1".toLowerCase(), "OPV2".toLowerCase(), "OPV3".toLowerCase()
                , "Penta1".toLowerCase(), "Penta2".toLowerCase(), "Penta3".toLowerCase(), "PCV1".toLowerCase(), "PCV2".toLowerCase()
                , "PCV3".toLowerCase(), "Rota1".toLowerCase(), "Rota2".toLowerCase(), "IPV".toLowerCase(),
                "MCV1".toLowerCase(), "yellowfever".toLowerCase(), "rota3", "mena"};
        return list;
    }
}
