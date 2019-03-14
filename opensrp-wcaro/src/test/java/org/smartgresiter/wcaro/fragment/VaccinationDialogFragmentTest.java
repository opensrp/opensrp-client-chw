package org.smartgresiter.wcaro.fragment;

import android.content.Context;
import android.view.View;
import android.widget.DatePicker;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.FragmentController;
import org.smartgresiter.wcaro.BaseUnitTest;
import org.smartgresiter.wcaro.contract.HomeVisitImmunizationContract;
import org.smartregister.immunization.domain.VaccineWrapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.doNothing;
@PrepareForTest({VaccinationDialogFragment.class})
public class VaccinationDialogFragmentTest extends BaseUnitTest {

    @Mock
    HomeVisitImmunizationContract.View homeVisitImmunizationView;
    @Mock
    ChildImmunizationFragment childImmunizationFragment;
    @Mock
    HomeVisitImmunizationContract.Presenter presenter;

    private FragmentController<VaccinationDialogFragment> controller;
    @Mock
    private VaccinationDialogFragment vaccinationDialogFragment;
    @Mock
    Calendar calender;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        vaccinationDialogFragment = Mockito.spy(VaccinationDialogFragment.class);

    }
    @Test
    public void saveData(){

    }
    @Test
    public void handleSingleVaccineLogic_true() throws Exception{
        Map<VaccineWrapper,DatePicker> singleVaccineMap=new LinkedHashMap<>();
        VaccineWrapper vaccineWrapper = Mockito.mock(VaccineWrapper.class);
        DatePicker datePicker =  Mockito.mock(DatePicker.class);
        Mockito.when(datePicker.getDayOfMonth()).thenReturn(13);
        Mockito.when(datePicker.getYear()).thenReturn(2001);
        Mockito.when(datePicker.getMonth()).thenReturn(3);
        singleVaccineMap.put(vaccineWrapper,datePicker);

        vaccinationDialogFragment.setDisableConstraints(true);
        Calendar bcalendar = Calendar.getInstance();
        bcalendar.set(2000, 5, 15);
        DateTime dateOfBirth = new DateTime(bcalendar.getTime());
        Whitebox.setInternalState(vaccinationDialogFragment,"dateOfBirth",dateOfBirth.toDate());
        Whitebox.setInternalState(vaccinationDialogFragment,"homeVisitImmunizationView",homeVisitImmunizationView);
        PowerMockito.doAnswer(new org.mockito.stubbing.Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return null; //does nothing
            }
        }).when(vaccinationDialogFragment).onVaccinateEarlier(Mockito.any(ArrayList.class));

        Mockito.when(homeVisitImmunizationView.getPresenter()).thenReturn(presenter);

        PowerMockito.doAnswer(new org.mockito.stubbing.Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return null; //does nothing
            }
        }).when(presenter).assigntoGivenVaccines(Mockito.any(ArrayList.class));

        Whitebox.invokeMethod(vaccinationDialogFragment, "handleSingleVaccineLogic",singleVaccineMap,dateOfBirth.toDate());

        Mockito.verify(vaccineWrapper,Mockito.atLeastOnce()).setUpdatedVaccineDate(Mockito.any(DateTime.class),Mockito.any(Boolean.class));

    }
    @Test
    public void validateVaccinationDate_true_currentdate() throws Exception{
        Calendar bcalendar = Calendar.getInstance();
        bcalendar.set(2000, 5, 15);
        DateTime dateOfBirth = new DateTime(bcalendar.getTime());

        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 2, 14);

        DateTime dateTime = new DateTime(calendar.getTime());
        Boolean value = Whitebox.invokeMethod(vaccinationDialogFragment, "validateVaccinationDate",dateOfBirth.toDate(),dateTime.toDate());
        Assert.assertTrue(value);

    }
}