package org.smartregister.chw.util;

import android.app.Activity;
import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import static org.mockito.Mockito.verify;

public class AllClientsUtilsTest {


    @Mock
    Activity mockActivity;

    @Mock
    CommonPersonObjectClient mockPatient;

    @Mock
    Bundle mockBundle;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGoToChildProfile() {
        AllClientsUtils.goToChildProfile(mockActivity, mockPatient, mockBundle);
        verify(mockActivity).startActivity(ArgumentMatchers.any());
    }
}
