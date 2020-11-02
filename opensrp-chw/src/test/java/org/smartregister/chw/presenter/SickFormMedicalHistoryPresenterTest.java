package org.smartregister.chw.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.contract.SickFormMedicalHistoryContract;

import java.util.ArrayList;
import java.util.List;

public class SickFormMedicalHistoryPresenterTest extends BaseUnitTest {

    private SickFormMedicalHistoryPresenter presenter;

    @Mock
    private SickFormMedicalHistoryContract.Interactor interactor;

    @Mock
    private SickFormMedicalHistoryContract.View view;

    @Mock
    private MemberObject memberObject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new SickFormMedicalHistoryPresenter(memberObject, interactor, view);
    }

    @Test
    public void testInitialize() {
        presenter.initialize();
        Mockito.verify(interactor, Mockito.times(2)).getUpComingServices(memberObject, null, presenter);
    }

    public void testOnDataFetched(){
        List<Visit> serviceList = new ArrayList<>();
        presenter.onDataFetched(serviceList);
        view.refreshVisits(serviceList);
    }

}
