package org.smartregister.chw.util;

import android.app.Activity;
import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AllClientUtilsTest {

    @Mock
    private Activity mockActivity;

    @Mock
    private CommonPersonObjectClient mockPatient;

    @Mock
    private Bundle mockBundle;

    @Mock
    private Map<String,String> map;


    @Before
    public void setup (){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGoToChildProfileAboveFiveYears() {
        String dobString = "15";
        when(mockPatient.getColumnmaps()).thenReturn(map);
        when(map.get(DBConstants.KEY.DOB)).thenReturn(dobString);
        AllClientsUtils.goToChildProfile(mockActivity, mockPatient, mockBundle);
        verify(mockActivity).startActivity(ArgumentMatchers.any());

    }
}
