package org.smartregister.chw.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.contract.ChildProfileContract;
import org.smartregister.chw.interactor.ChildProfileInteractor;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.UUID;

public class ChildProfilePresenterTest {

    private static final String testBaseEntityId = UUID.randomUUID().toString();
    @Mock
    private ChildProfileContract.Presenter childProfilePresenter;
    @Mock
    private ChildProfileContract.View childProfileView;
    @Mock
    private ChildProfileContract.Model childProfileModel;
    @Mock
    private ChildProfileContract.InteractorCallBack callBack;

    @Mock
    private CommonPersonObjectClient personObjectClient;

    private ChildProfileInteractor interactor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        childProfilePresenter = new ChildProfilePresenter(childProfileView, childProfileModel, testBaseEntityId);
        interactor = (ChildProfileInteractor) ((ChildProfilePresenter) childProfilePresenter).getInteractor();
    }


    @Test
    public void testThatModelWasInitialized() {
        ChildProfileContract.Model model = ((ChildProfilePresenter) childProfilePresenter).getModel();
        Assert.assertNotNull(model);
    }

    @Test
    public void testFetchVisitStatus() {
        childProfilePresenter.fetchVisitStatus(testBaseEntityId);
       interactor.setpClient(personObjectClient);
        Mockito.verify(((ChildProfilePresenter) childProfilePresenter).getInteractor(), Mockito.atLeastOnce())
                .refreshChildVisitBar(childProfileView.getApplicationContext(), testBaseEntityId, callBack);
    }
}
