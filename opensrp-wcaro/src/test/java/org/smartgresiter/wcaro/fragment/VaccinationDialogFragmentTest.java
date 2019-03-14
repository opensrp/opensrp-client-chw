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
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.VaccineWrapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.doNothing;
@PrepareForTest({VaccinationDialogFragment.class})
public class VaccinationDialogFragmentTest extends BaseUnitTest {

    @Mock
    HomeVisitImmunizationContract.View homeVisitImmunizationView;

    @Mock
    HomeVisitImmunizationContract.Presenter presenter;

    @Mock
    private VaccinationDialogFragment vaccinationDialogFragment;

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

        doNothing().when(vaccinationDialogFragment).onVaccinateEarlier(Mockito.any(ArrayList.class));

        Mockito.when(homeVisitImmunizationView.getPresenter()).thenReturn(presenter);

        doNothing().when(presenter).assigntoGivenVaccines(Mockito.any(ArrayList.class));

        Whitebox.invokeMethod(vaccinationDialogFragment, "handleSingleVaccineLogic",singleVaccineMap,dateOfBirth.toDate());

        Mockito.verify(vaccineWrapper,Mockito.atLeastOnce()).setUpdatedVaccineDate(Mockito.any(DateTime.class),Mockito.any(Boolean.class));

    }
    @Test
    public void handleMultipleVaccineGiven_true() throws Exception{
        setTagValue();
        List<String> selectedCheckBox = new ArrayList<>();

        selectedCheckBox.add("OPV 1");

        Whitebox.setInternalState(vaccinationDialogFragment,"homeVisitImmunizationView",homeVisitImmunizationView);

        doNothing().when(vaccinationDialogFragment).onVaccinateEarlier(Mockito.any(ArrayList.class));

        Mockito.when(homeVisitImmunizationView.getPresenter()).thenReturn(presenter);

        doNothing().when(presenter).assigntoGivenVaccines(Mockito.any(ArrayList.class));

        Whitebox.invokeMethod(vaccinationDialogFragment, "handleMultipleVaccineGiven",getTestVaccineDate(),getTestDateOfBirth().toDate(),selectedCheckBox);

        Mockito.verify(presenter,Mockito.atLeastOnce()).assigntoGivenVaccines(Mockito.any(ArrayList.class));

    }
    @Test
    public void handleNotGivenVaccines_true() throws Exception{
        setTagValue();
        List<String> uncheckedBox = new ArrayList<>();

        uncheckedBox.add("OPV 1");

        Whitebox.setInternalState(vaccinationDialogFragment,"homeVisitImmunizationView",homeVisitImmunizationView);

        Mockito.when(homeVisitImmunizationView.getPresenter()).thenReturn(presenter);

        doNothing().when(presenter).updateNotGivenVaccine(Mockito.any(VaccineWrapper.class));

        Whitebox.invokeMethod(vaccinationDialogFragment, "handleNotGivenVaccines",getTestVaccineDate(),getTestDateOfBirth().toDate(),uncheckedBox);

        Mockito.verify(presenter,Mockito.atLeastOnce()).updateNotGivenVaccine(Mockito.any(VaccineWrapper.class));

    }
    @Test
    public void validateVaccinationDate_true_currentdate() throws Exception{

        Boolean value = Whitebox.invokeMethod(vaccinationDialogFragment, "validateVaccinationDate",getTestDateOfBirth().toDate(),getTestVaccineDate().toDate());
        Assert.assertTrue(value);

    }
    @Test
    public void searchWrapperByName_true() throws Exception{
        setTagValue();
        VaccineWrapper value = Whitebox.invokeMethod(vaccinationDialogFragment, "searchWrapperByName","opv 1");

        Assert.assertEquals("OPV 1",value.getName());

    }
    private void setTagValue(){
        VaccineWrapper vaccineWrapper = new VaccineWrapper();
        vaccineWrapper.setName("OPV 1");

        vaccineWrapper.setVaccine(VaccineRepo.Vaccine.valueOf("opv1"));
        List<VaccineWrapper> list=new ArrayList<>();
        list.add(vaccineWrapper);
        Whitebox.setInternalState(vaccinationDialogFragment,"tags",list);
    }
    private DateTime getTestDateOfBirth(){
        Calendar bcalendar = Calendar.getInstance();
        bcalendar.set(2000, 5, 15);
        return new DateTime(bcalendar.getTime());
    }
    private DateTime getTestVaccineDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 2, 14);

        return new DateTime(calendar.getTime());
    }

}