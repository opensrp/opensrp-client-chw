package org.smartregister.brac.hnpp.utils;

import android.text.TextUtils;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class HnppUtils {
    public static boolean isWomanOfReproductiveAge(CommonPersonObjectClient commonPersonObject) {
        if (commonPersonObject == null) {
            return false;
        }

        // check age and gender
        String dobString = org.smartregister.util.Utils.getValue(commonPersonObject.getColumnmaps(), "dob", false);
        String gender = org.smartregister.util.Utils.getValue(commonPersonObject.getColumnmaps(), "gender", false);
        if (!TextUtils.isEmpty(dobString) && gender.trim().equalsIgnoreCase("F")) {
            Period period = new Period(new DateTime(dobString), new DateTime());
            int age = period.getYears();
            return age >= 15 && age <= 49;
        }

        return false;
    }
}
