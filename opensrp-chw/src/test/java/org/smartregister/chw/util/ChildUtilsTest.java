package org.smartregister.chw.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.BaseUnitTest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class ChildUtilsTest extends BaseUnitTest {


    @Mock
    private ChildUtils.Flavor childUtilsFlv;
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        childUtilsFlv = Mockito.spy(ChildUtilsFlv.class);
    }

    @Test
    public void isFullyImmunizedForTwoYears() throws Exception{

        List<String> receivedVaccine = Arrays.asList(TestConstant.getTestReceivedTwoYearVaccine());
        setFinalStatic(ChildUtils.class.getDeclaredField("childUtilsFlv"), childUtilsFlv);

        Assert.assertEquals("2", ChildUtils.isFullyImmunized(receivedVaccine));
    }
    private static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    @Test
    public void isFullyImmunizedForOneYears() throws Exception {
        List<String> receivedVaccine = Arrays.asList(TestConstant.getTestReceivedOneYearVaccine());
        setFinalStatic(ChildUtils.class.getDeclaredField("childUtilsFlv"), childUtilsFlv);
        Assert.assertEquals("1", ChildUtils.isFullyImmunized(receivedVaccine));
    }

    @Test
    public void isFullyImmunizedForEmpty() {
        String[] list = {"OPV0".toLowerCase(), "BCG".toLowerCase(), "OPV1".toLowerCase(), "OPV2".toLowerCase()};
        List<String> receivedVaccine = Arrays.asList(list);
        Assert.assertEquals("", ChildUtils.isFullyImmunized(receivedVaccine));
    }
    @Test
    public void lowerCaseVaccineName(){
        Assert.assertEquals("MenA",ChildUtils.fixVaccineCasing("MENA"));
        Assert.assertEquals("Rubella 1",ChildUtils.fixVaccineCasing("RUBELLA 1"));
        Assert.assertEquals("Rubella 2",ChildUtils.fixVaccineCasing("RUBELLA 2"));
    }

    /*
    @Test
    public void daysAway_awayTest(){
        //2019-04-08
        //20 days away
        SpannableString spannableString = ChildUtils.daysAway("2019-04-08");
        ForegroundColorSpan[] colorSpans =spannableString.getSpans(0, spannableString.length(), ForegroundColorSpan.class);
        Assert.assertTrue(colorSpans[0].getForegroundColor() == Color.GRAY);
    }
    @Test
    public void daysAway_overdueTest(){
        //2015-03-20
        //1460 days overdue color code= -1030586
        SpannableString spannableString = ChildUtils.daysAway("2015-03-20");
        ForegroundColorSpan[] colorSpans =spannableString.getSpans(0, spannableString.length(), ForegroundColorSpan.class);
        Assert.assertTrue(colorSpans[0].getForegroundColor() == -1030586);
    }
*/
}
