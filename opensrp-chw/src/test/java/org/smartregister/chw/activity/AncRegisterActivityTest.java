package org.smartregister.chw.activity;

import android.content.Intent;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.BaseActivityTest;
import org.smartregister.chw.fragment.AncRegisterFragment;
import org.smartregister.view.contract.BaseRegisterContract;

import static org.smartregister.chw.core.utils.CoreConstants.EventType.ANC_REGISTRATION;

public class AncRegisterActivityTest extends BaseActivityTest<AncRegisterActivity> {

    @Test
    public void testOnActivityResult() {
        AncRegisterActivity spyActivity = Mockito.spy(activity);
        spyActivity.onActivityResult(0, 0, new Intent());
        Assert.assertFalse(AncRegisterActivity.shouldFinishOnBack);
    }

    @Test
    public void getRegisterActivityReturnsCorrectActivity() {
        Assert.assertEquals(AncRegisterActivity.class, activity.getRegisterActivity(ANC_REGISTRATION));
    }

    @Test
    public void getRegisterFragmentReturnsCorrectFragment() {
        Assert.assertTrue(getActivity().getRegisterFragment() instanceof AncRegisterFragment);
    }

    @Override
    protected Class<AncRegisterActivity> getActivityClass() {
        return AncRegisterActivity.class;
    }

    @Override
    protected Intent getControllerIntent() {
        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //Auto login by default
        String password = "pwd";
        context.session().start(context.session().lengthInMilliseconds());
        context.configuration().getDrishtiApplication().setPassword(password.getBytes());
        context.session().setPassword(password.getBytes());

        return super.getControllerIntent();
    }

    @Override
    public void testNoErrorOnResume() {
        BaseRegisterContract.Presenter presenter = Whitebox.getInternalState(activity, "presenter");
        presenter = Mockito.spy(presenter);
        Mockito.doNothing().when(presenter).registerViewConfigurations(ArgumentMatchers.any());
        Whitebox.setInternalState(activity, "presenter", presenter);

        controller.pause();
        controller.resume();
        Mockito.verify(presenter, Mockito.atLeastOnce())
                .registerViewConfigurations(ArgumentMatchers.any());
    }
}
