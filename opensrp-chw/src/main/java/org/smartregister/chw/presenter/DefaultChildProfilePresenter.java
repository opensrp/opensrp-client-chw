package org.smartregister.chw.presenter;

import org.apache.commons.lang3.StringUtils;

public class DefaultChildProfilePresenter implements ChildProfilePresenter.Flavor{
    @Override
    public String getChildName(String firstName, String middleName, String lastName) {
        lastName = middleName + " " + lastName;
        firstName = firstName.trim();
        lastName = lastName.trim();
        if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
            return firstName + " " + lastName;
        } else {
            if (StringUtils.isNotBlank(firstName)) {
                return firstName;
            } else if (StringUtils.isNotBlank(lastName)) {
                return lastName;
            }
        }
        return "";
    }
}
