package org.smartregister.chw.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.referral.util.Constants.ACTIVITY_PAYLOAD;
import org.smartregister.chw.util.Constants;
import org.smartregister.family.util.JsonFormUtils;

import java.util.ArrayList;

import static org.robolectric.Shadows.shadowOf;
import static org.smartregister.chw.referral.util.Constants.ACTIVITY_PAYLOAD_TYPE;

@RunWith(RobolectricTestRunner.class)
public class ClientReferralActivityTest {

    private ClientReferralActivity clientReferralActivity;

    private String baseEntityID = "s4m9le-ba533n-tity1d";

    @Before
    public void setUp() {

        ArrayList<ReferralTypeModel> referralTypeModels = new ArrayList<>();
        referralTypeModels.add(new ReferralTypeModel("Sick child", Constants.JSON_FORM.getChildReferralForm()));

        Bundle bundle = new Bundle();
        bundle.putString(Constants.ENTITY_ID, baseEntityID);
        bundle.setClassLoader(ReferralTypeModel.class.getClassLoader());
        bundle.putParcelableArrayList(Constants.REFERRAL_TYPES, referralTypeModels);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ClientReferralActivity.class);
        intent.putExtras(bundle);

        clientReferralActivity = Robolectric.buildActivity(ClientReferralActivity.class, intent)
                .create()
                .get();
    }

    @Test
    public void shouldStartClientReferralActivity() {
        Assert.assertNotNull(clientReferralActivity.getBaseEntityId());
        Assert.assertEquals(clientReferralActivity.getBaseEntityId(), baseEntityID);
        Assert.assertNotNull(clientReferralActivity.getReferralTypeAdapter());
    }

    @Test
    public void shouldStartReferralForm() {

        JSONObject formJson = new JSONObject();

        clientReferralActivity.startReferralForm(formJson, new ReferralTypeModel("Sick child",
                Constants.JSON_FORM.getChildReferralForm(), "1"));
        Intent intent = new Intent(clientReferralActivity, ReferralRegistrationActivity.class);
        intent.putExtra(ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        intent.putExtra(ACTIVITY_PAYLOAD.REFERRAL_SERVICE_IDS, "1");
        intent.putExtra(ACTIVITY_PAYLOAD.JSON_FORM, formJson.toString());
        intent.putExtra(ACTIVITY_PAYLOAD.ACTION, ACTIVITY_PAYLOAD_TYPE.REGISTRATION);
        shadowOf(clientReferralActivity).receiveResult(new Intent(clientReferralActivity,
                ReferralRegistrationActivity.class), JsonFormUtils.REQUEST_CODE_GET_JSON, intent);

        Assert.assertEquals(ReferralRegistrationActivity.BASE_ENTITY_ID, baseEntityID);
    }
}
