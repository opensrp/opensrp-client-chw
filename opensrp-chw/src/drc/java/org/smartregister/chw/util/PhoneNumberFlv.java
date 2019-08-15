package org.smartregister.chw.util;

import org.smartregister.chw.adapter.MemberAdapter;

public class PhoneNumberFlv implements MemberAdapter.Flavor {
    @Override
    public boolean isPhoneNumberLength16Digit() {
        return false;
    }
}
