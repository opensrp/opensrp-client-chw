package org.smartregister.chw.provider;

import org.apache.commons.lang3.StringUtils;

public class ChildRegisterProviderFlv implements ChildRegisterProvider.Flavor {

    @Override
    public String getChildName(String firstName, String middleName, String lastName) {
        firstName = firstName.trim();
        middleName = middleName.trim();
        if (StringUtils.isNotBlank(firstName) && StringUtils.isNoneBlank(middleName)) {
            return firstName + " " + middleName;
        } else if (StringUtils.isNotBlank(firstName)) {
            return firstName;
        }
        return "";
    }
}
