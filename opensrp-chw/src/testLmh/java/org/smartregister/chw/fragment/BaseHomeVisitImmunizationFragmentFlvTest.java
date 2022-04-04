package org.smartregister.chw.fragment;

import android.widget.DatePicker;

import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Spy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class BaseHomeVisitImmunizationFragmentFlvTest  {

    @Spy
    BaseHomeVisitImmunizationFragmentFlv fragmentFlv;

    @Before
    public void setUp(){
        initMocks(this);
    }

    @Test
    public void testSetDatePickerThemeSetsCorrectFormat() {
        DatePicker datePicker = mock(DatePicker.class);
        doNothing().when(fragmentFlv).callDatePickerUtilsThemeDatePicker(any(), any());
        fragmentFlv.setDatePickerTheme(datePicker);
        verify(fragmentFlv).callDatePickerUtilsThemeDatePicker(eq(datePicker), AdditionalMatchers.aryEq(new char[]{'d', 'm', 'y'}));
    }


}
