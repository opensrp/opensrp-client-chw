package org.smartregister.chw.util;

import android.text.Editable;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.core.adapter.MemberAdapter;

public class PhoneNumberFlv implements MemberAdapter.Flavor {
    @Override
    public boolean isPhoneNumberLength16Digit() {
        return false;
    }

    @Override
    public boolean isPhoneNumberValid(EditText phoneEditText, EditText alternatePhoneEditText) {
        validatePhoneNumber(phoneEditText);
        validatePhoneNumber(alternatePhoneEditText);

        return (StringUtils.isBlank(alternatePhoneEditText.getText()) && StringUtils.isNoneBlank(phoneEditText.getText()) &&
                phoneEditText.getText().length() == 10) || (StringUtils.isNoneBlank(alternatePhoneEditText.getText()) &&
                StringUtils.isNoneBlank(phoneEditText.getText()) && phoneEditText.getText().length() == 10 &&
                alternatePhoneEditText.getText().length() == 10);
    }

    private void validatePhoneNumber(EditText phoneEditText) {
        Editable phoneNumber = phoneEditText.getText();
        if (phoneNumber.length() > 0 && phoneNumber.length() < 10 || phoneNumber.length() > 10) {
            phoneEditText.setError(phoneEditText.getContext().getString(R.string.ten_digits_validation));
        } else {
            phoneEditText.setError(null);
        }
    }
}
