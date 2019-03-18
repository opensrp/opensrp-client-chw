package org.smartgresiter.wcaro.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartgresiter.wcaro.contract.FamilyCallDialogContract;
import org.smartgresiter.wcaro.interactor.FamilyCallDialogInteractor;
import org.smartgresiter.wcaro.model.FamilyCallDialogModel;

public class FamilyCallDialogPresenterTest {


    FamilyCallDialogPresenter presenter;

    @Mock
    FamilyCallDialogContract.View view;

    @Mock
    FamilyCallDialogInteractor mInteractor;

    String familyBaseEntityId = "adawrfvsr553";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new FamilyCallDialogPresenter(view, familyBaseEntityId);
        Whitebox.setInternalState(presenter, "mInteractor", mInteractor);
    }

    @Test
    public void testUpdateHeadOfFamily() {
        FamilyCallDialogContract.Model model = new FamilyCallDialogModel();
        presenter.updateHeadOfFamily(model);
        Mockito.verify(view).refreshHeadOfFamilyView(model);
    }

    @Test
    public void testUpdateCareGiver() {
        FamilyCallDialogContract.Model model = new FamilyCallDialogModel();
        presenter.updateHeadOfFamily(model);
        Mockito.verify(view).refreshHeadOfFamilyView(model);
    }

    @Test
    public void testInitalize() {
        // should have been called
        Mockito.verify(view).refreshHeadOfFamilyView(null);
        Mockito.verify(view).refreshCareGiverView(null);

        // second call for stubbed interactor
        presenter.initalize();
        Mockito.verify(mInteractor).getHeadOfFamily(presenter, view.getContext());
    }

}