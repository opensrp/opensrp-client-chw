package org.smartregister.chw.actionhelper;

import android.content.Context;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.core.utils.NativeFormProcessor;
import org.smartregister.util.FormUtils;

import java.util.HashMap;
import java.util.Map;

public class ANCCardActionTest extends BaseUnitTest {

    @Test
    public void testOnPayloadReceive() throws Exception {
        ANCCardAction ancCardAction = new ANCCardAction();

        String formName = "anc_hv_anc_card_received";

        Map<String, Object> values = new HashMap<>();
        values.put("anc_card", "Yes");

        JSONObject jsonObject = FormUtils.getInstance(RuntimeEnvironment.application).getFormJson(formName);
        NativeFormProcessor.createInstance(jsonObject)
                .populateValues(values);

        ancCardAction.onPayloadReceived(jsonObject.toString());
        Assert.assertEquals("Yes", ReflectionHelpers.getField(ancCardAction, "anc_card"));
    }

    @Test
    public void testEvaluateSubTitle() {
        ANCCardAction ancCardAction = new ANCCardAction();
        Context context = RuntimeEnvironment.application;

        ReflectionHelpers.setField(ancCardAction, "context", context);
        ReflectionHelpers.setField(ancCardAction, "anc_card", "No");

        Assert.assertEquals(context.getString(R.string.no), ancCardAction.evaluateSubTitle());
    }

    @Test
    public void testEvaluateStatusOnPayload() {
        ANCCardAction ancCardAction = new ANCCardAction();

        Assert.assertEquals(ancCardAction.evaluateStatusOnPayload(), BaseAncHomeVisitAction.Status.PENDING);

        ReflectionHelpers.setField(ancCardAction, "anc_card", "Yes");
        Assert.assertEquals(ancCardAction.evaluateStatusOnPayload(), BaseAncHomeVisitAction.Status.COMPLETED);

        ReflectionHelpers.setField(ancCardAction, "anc_card", "No");
        Assert.assertEquals(ancCardAction.evaluateStatusOnPayload(), BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED);
    }
}
