package org.smartregister.chw.fragment;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.immunization.domain.VaccineWrapper;

import java.util.ArrayList;
import java.util.Date;

@PrepareForTest({VaccinationDialogFragment.class})
public class VaccinationDialogFragmentTest extends BaseUnitTest {

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

}
