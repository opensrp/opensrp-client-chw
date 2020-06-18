package org.smartregister.chw.presenter;

import org.apache.commons.lang3.StringUtils;

public class ChildProfilePresenterFlv implements ChildProfilePresenter.Flavor {
    @Override
    public String getChildName(String firstName, String middleName, String lastName) {
        if (StringUtils.isNotBlank(firstName) && StringUtils.isNoneBlank(middleName)) {
            return firstName + " " + middleName;
        } else if (StringUtils.isNotBlank(firstName)) {
            return firstName;
        }
        return "";
    }
}
