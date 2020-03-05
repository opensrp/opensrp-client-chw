package org.smartregister.chw.activity;

import org.smartregister.commonregistry.CommonPersonObjectClient;

public class ChildProfileActivityFlv extends DefaultChildProfileActivityFlv {
    @Override
    public boolean isChildOverTwoMonths(CommonPersonObjectClient client) {
        return true; // This is because LMH isn't concerned with hiding the sick child form for children < 2 months
    }
}
