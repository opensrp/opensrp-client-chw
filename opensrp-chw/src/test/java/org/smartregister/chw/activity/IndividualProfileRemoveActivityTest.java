package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.fragment.IndividualProfileRemoveFragment;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;

public class IndividualProfileRemoveActivityTest { //extends BaseActivityTest<IndividualProfileRemoveActivity> {
    @Mock
    IndividualProfileRemoveActivity individualProfileRemoveActivity;

    @Mock
    IndividualProfileRemoveFragment individualProfileRemoveFragment;

    @Mock
    JSONObject jsonObject;

    @Mock
    Intent intent;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOnActivityResultWithResultCanceled() {
        doNothing().when(individualProfileRemoveFragment).confirmRemove(jsonObject);
        individualProfileRemoveActivity.onActivityResult(org.smartregister.chw.util.Constants.ProfileActivityResults.CHANGE_COMPLETED, Activity.RESULT_CANCELED, intent);
        Mockito.verify(individualProfileRemoveFragment, never()).confirmRemove(jsonObject);
    }

    @Test
    public void testOnActivityResultWithResultFirstUser() {
        doNothing().when(individualProfileRemoveFragment).confirmRemove(jsonObject);
        individualProfileRemoveActivity.onActivityResult(org.smartregister.chw.util.Constants.ProfileActivityResults.CHANGE_COMPLETED, Activity.RESULT_FIRST_USER, intent);
        Mockito.verify(individualProfileRemoveFragment, never()).confirmRemove(jsonObject);
    }
}
