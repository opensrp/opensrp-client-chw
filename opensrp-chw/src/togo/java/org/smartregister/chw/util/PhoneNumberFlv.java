package org.smartregister.chw.util;

import com.opensrp.chw.core.adapter.MemberAdapter;

public class PhoneNumberFlv implements MemberAdapter.Flavor {
    @Override
    public boolean isPhoneNumberLength16Digit() {
        return true;
    }
}
