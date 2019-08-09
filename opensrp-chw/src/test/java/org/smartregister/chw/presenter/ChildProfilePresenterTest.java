package org.smartregister.chw.presenter;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.contract.ChildProfileContract;
import org.smartregister.chw.interactor.ChildProfileInteractor;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.UUID;

public class ChildProfilePresenterTest extends BaseUnitTest {

    private static final String testBaseEntityId = UUID.randomUUID().toString();
    @Mock
    private ChildProfileContract.Presenter childProfilePresenter;
    @Mock
    private ChildProfileContract.View childProfileView;
    @Mock
    private ChildProfileContract.Model childProfileModel;

    @Mock
    private CommonPersonObjectClient personObjectClient;

    @Mock
    private ChildProfileInteractor interactor;

    @Mock
    private Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        childProfilePresenter = new ChildProfilePresenter(childProfileView, childProfileModel, testBaseEntityId);
        interactor.setpClient(personObjectClient);
        ((ChildProfilePresenter) childProfilePresenter).setInteractor(interactor);
        Mockito.doReturn(context).when(childProfileView).getContext();
    }


    @Test
    public void testThatModelWasInitialized() {
        ChildProfileContract.Model model = ((ChildProfilePresenter) childProfilePresenter).getModel();
        Assert.assertNotNull(model);
    }

    @Test
    public void testThatViewlWasInitialized() {
        ChildProfileContract.View view = childProfilePresenter.getView();
        Assert.assertNotNull(view);
    }

    @Test
    public void testFetchVisitStatus() {
        interactor.setpClient(personObjectClient);
        childProfilePresenter.fetchVisitStatus(testBaseEntityId);
        Mockito.verify(interactor, Mockito.atLeastOnce()).refreshChildVisitBar(childProfileView.getContext(),
                testBaseEntityId, (ChildProfilePresenter) childProfilePresenter);
    }

    @Test
    public void testProcessBackGroundEvent() {
        childProfilePresenter.processBackGroundEvent();
        Mockito.verify(interactor, Mockito.atLeastOnce()).processBackGroundEvent ((ChildProfilePresenter) childProfilePresenter);
    }
}
