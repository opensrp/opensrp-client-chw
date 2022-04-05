package org.smartregister.chw.activity;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.chw.BaseActivityTest;
import org.smartregister.view.contract.BaseRegisterContract;

public class ChildRegisterActivityTest extends BaseActivityTest<ChildRegisterActivity> {
    @Override
    protected Class<ChildRegisterActivity> getActivityClass() {
        return ChildRegisterActivity.class;
    }

    @Override
    public void testNoErrorOnResume() {
        BaseRegisterContract.Presenter presenter = Whitebox.getInternalState(activity, "presenter");
        presenter = Mockito.spy(presenter);
        Mockito.doNothing().when(presenter).registerViewConfigurations(ArgumentMatchers.any());
        Whitebox.setInternalState(activity, "presenter", presenter);

        controller.pause();
        controller.resume();
        activity.onResumption();
        Mockito.verify(presenter, Mockito.atLeastOnce())
                .registerViewConfigurations(ArgumentMatchers.any());
    }
}
