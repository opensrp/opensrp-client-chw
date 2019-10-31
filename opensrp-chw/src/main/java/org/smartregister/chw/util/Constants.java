package org.smartregister.chw.util;

import org.smartregister.chw.core.utils.CoreConstants;

public class Constants extends CoreConstants {
    public static String pregnancyOutcome = "preg_outcome";

    public enum FamilyRegisterOptionsUtil {Miscarriage, Other}

    public enum FamilyMemberType {ANC, PNC, Other}

    public static final String REFERRAL_TYPES ="ReferralTypes";

    public class EncounterType {
        public static final String SICK_CHILD ="Sick Child Referral";
        public static final String PNC_REFERRAL ="PNC Referral";
        public static final String ANC_REFERRAL ="ANC Referral";
    }
}
