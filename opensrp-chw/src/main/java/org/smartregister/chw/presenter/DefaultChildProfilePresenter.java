package org.smartregister.chw.presenter;

import org.apache.commons.lang3.StringUtils;

public class DefaultChildProfilePresenter implements ChildProfilePresenter.Flavor {
    @Override
    public String getChildName(String firstName, String middleName, String lastName) {
        String midLastName = middleName + " " + lastName;
        if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(midLastName)) {
            return firstName + " " + midLastName;
        } else {
            if (StringUtils.isNotBlank(firstName)) {
                return firstName;
            } else if (StringUtils.isNotBlank(midLastName)) {
                return midLastName;
            }
        }
        return "";
    }
}
