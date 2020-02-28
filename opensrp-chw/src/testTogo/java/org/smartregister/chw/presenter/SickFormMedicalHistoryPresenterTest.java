package org.smartregister.chw.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.contract.SickFormMedicalHistoryContract;

import java.lang.ref.WeakReference;
import java.util.List;

public class SickFormMedicalHistoryPresenterTest {
    @Mock
    private SickFormMedicalHistoryContract.Interactor interactor;
    @Mock
    private SickFormMedicalHistoryContract.View view;

    @Mock
    private MemberObject memberObject;

    private SickFormMedicalHistoryPresenter presenter;

    @Mock
    private List<Visit> serviceList;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = Mockito.mock(SickFormMedicalHistoryPresenter.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(presenter, "interactor", interactor);
        ReflectionHelpers.setField(presenter, "view", new WeakReference<>(view));
        ReflectionHelpers.setField(presenter, "memberObject", memberObject);
    }

    @Test
    public void getView() {
        Assert.assertEquals(view, presenter.getView());
    }

    @Test
    public void onDataFetched() {
        presenter.onDataFetched(serviceList);
        if (presenter.getView() != null) {
            Mockito.verify(view).displayLoadingState(false);
            Mockito.verify(view).refreshVisits(serviceList);
        }
    }

}