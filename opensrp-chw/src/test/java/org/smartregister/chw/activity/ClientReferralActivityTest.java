package org.smartregister.chw.activity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.util.Constants;

import java.util.Map;


public class ClientReferralActivityTest extends BaseUnitTest {

    private ActivityController<ClientReferralActivity> controller;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(ClientReferralActivity.class);
    }

    @Test
    public void testActivityEncounterTypeToTableMap(){
        ClientReferralActivity clientReferralActivity = controller.get();
        Assert.assertNull(Whitebox.getInternalState(clientReferralActivity, "encounterTypeToTableMap"));

        controller.create();
        Map<String, String> encounterTypeMap=  Whitebox.getInternalState(clientReferralActivity, "encounterTypeToTableMap");
        Assert.assertEquals(CoreConstants.TABLE_NAME.CHILD_REFERRAL, encounterTypeMap.get(Constants.EncounterType.SICK_CHILD));
        Assert.assertEquals(CoreConstants.TABLE_NAME.PNC_REFERRAL, encounterTypeMap.get(Constants.EncounterType.PNC_REFERRAL));
        Assert.assertEquals(CoreConstants.TABLE_NAME.ANC_REFERRAL, encounterTypeMap.get(Constants.EncounterType.ANC_REFERRAL));
    }

}