package org.smartregister.chw.fragment;

import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.opensrp.chw.core.fragment.VaccinationDialogFragment;
import com.vijay.jsonwizard.customviews.CheckBox;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.R;
import com.opensrp.chw.core.custom_views.ImmunizationView;
import com.opensrp.chw.core.presenter.ImmunizationViewPresenter;
import org.smartregister.immunization.domain.VaccineWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.fail;


@PrepareForTest({VaccinationDialogFragment.class})
public class VaccinationDialogFragmentTest extends BaseUnitTest {

    @Mock
    private VaccinationDialogFragment vaccinationDialogFragment;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        vaccinationDialogFragment = Mockito.spy(VaccinationDialogFragment.class);

    }

    @Test
    public void newInstanceReturnsFragment() {
        Date dateOfBirth = new Date();
        ArrayList<VaccineWrapper> notGiven = new ArrayList<>();
        ArrayList<VaccineWrapper> given = new ArrayList<>();
        ArrayList<VaccineWrapper> tags = new ArrayList<>();
        String groupName = "";

        VaccinationDialogFragment fragment = VaccinationDialogFragment.newInstance(dateOfBirth, notGiven, given, tags, groupName);

        Assert.assertNotNull(fragment);
    }

    @Test
    public void testUpdateVaccineList() {
        try {
            List<VaccineWrapper> tags = new ArrayList<>();
            Whitebox.setInternalState(vaccinationDialogFragment, "tags", tags);

            CheckBox childSelect = Mockito.mock(CheckBox.class);
            View view = Mockito.mock(View.class);
            Mockito.when(view.findViewById(R.id.select)).thenReturn(childSelect);
            LinearLayout vaccinationNameLayout = Mockito.mock(LinearLayout.class);

            Mockito.when(vaccinationNameLayout.getChildCount()).thenReturn(1);
            Mockito.when(vaccinationNameLayout.getChildAt(Mockito.anyInt())).thenReturn(view);

            Whitebox.setInternalState(vaccinationDialogFragment, "vaccinationNameLayout", vaccinationNameLayout);

            Whitebox.invokeMethod(vaccinationDialogFragment, "updateVaccineList");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testShowSingleVaccineDetailsView() {

        try {
            View view = Mockito.mock(View.class);

            LinearLayout vaccinationNameLayout = Mockito.mock(LinearLayout.class);
            Mockito.when(vaccinationNameLayout.getChildAt(Mockito.anyInt())).thenReturn(view);
            Whitebox.setInternalState(vaccinationDialogFragment, "vaccinationNameLayout", vaccinationNameLayout);

            LinearLayout multipleVaccineDatePickerView = Mockito.mock(LinearLayout.class);
            Whitebox.setInternalState(vaccinationDialogFragment, "multipleVaccineDatePickerView", multipleVaccineDatePickerView);

            LinearLayout singleVaccineAddView = Mockito.mock(LinearLayout.class);
            Whitebox.setInternalState(vaccinationDialogFragment, "singleVaccineAddView", singleVaccineAddView);

            Map<VaccineWrapper, DatePicker> singleVaccineMap = new LinkedHashMap<>();
            Whitebox.setInternalState(vaccinationDialogFragment, "singleVaccineMap", singleVaccineMap);


            Whitebox.invokeMethod(vaccinationDialogFragment, "showSingleVaccineDetailsView");
        } catch (Exception e) {
            fail();
        }
    }


    @Test
    public void testHandleSingleVaccineLogic() {
        try {
            Map<VaccineWrapper, DatePicker> singleVaccineMap = new HashMap<>();
            Date dateOfBirth = new Date();

            DatePicker datePicker = Mockito.mock(DatePicker.class);
            singleVaccineMap.put(new VaccineWrapper(), datePicker);

            Mockito.doReturn(true).when(vaccinationDialogFragment).validateVaccinationDate(Mockito.any(Date.class), Mockito.any(Date.class));

            ImmunizationView immunizationView = Mockito.mock(ImmunizationView.class);
            ImmunizationViewPresenter presenter = Mockito.mock(ImmunizationViewPresenter.class);

            Mockito.doReturn(presenter).when(immunizationView).getPresenter();
            Mockito.doReturn(true).when(vaccinationDialogFragment).validateVaccinationDate(Mockito.any(Date.class), Mockito.any(Date.class));

            Whitebox.setInternalState(vaccinationDialogFragment, "immunizationView", immunizationView);


            Whitebox.invokeMethod(vaccinationDialogFragment, "handleSingleVaccineLogic", singleVaccineMap, dateOfBirth);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testValidateVaccinationDate() {
        Boolean returnedTypeValue = vaccinationDialogFragment.validateVaccinationDate(new Date(), new Date());
        Assert.assertTrue(returnedTypeValue);
    }
}
