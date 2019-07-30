package org.smartregister.chw.util;

import android.text.SpannableString;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.rule.HomeAlertRule;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildUtilsTest extends BaseUnitTest {

    @Mock
    private ChildUtils.Flavor childUtilsFlv;

    private static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        childUtilsFlv = Mockito.spy(ChildUtilsFlv.class);
    }

    @Test
    public void isFullyImmunizedForTwoYears() throws Exception {

        List<String> receivedVaccine = Arrays.asList(TestConstant.getTestReceivedTwoYearVaccine());
        setFinalStatic(ChildUtils.class.getDeclaredField("childUtilsFlv"), childUtilsFlv);

        Assert.assertEquals("2", ChildUtils.isFullyImmunized(receivedVaccine));
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
    public void lowerCaseVaccineName() {
        Assert.assertEquals("MenA", ChildUtils.fixVaccineCasing("MENA"));
        Assert.assertEquals("Rubella 1", ChildUtils.fixVaccineCasing("RUBELLA 1"));
        Assert.assertEquals("Rubella 2", ChildUtils.fixVaccineCasing("RUBELLA 2"));
    }

    @Test
    public void threeTextAfterNewlineSplit() {
        String str = "Developmental warning signs:no" + "\n" + "Caregiver stimulation skills:no" + "\n" + "Early learning program:yes";
        String[] strings = ChildUtils.splitStringByNewline(str);
        List<String> list = Arrays.asList(strings);
        Assert.assertEquals(3, list.size());

    }

    @Test
    public void durationWithTwoDate() {
        CommonPersonObjectClient childClient = new CommonPersonObjectClient("", null, "");
        Map<String, String> map = new HashMap<>();
        map.put(DBConstants.KEY.DOB, "2019-03-01T03:00:00.000+03:00");
        childClient.setColumnmaps(map);
        String dateOfBirth = org.smartregister.family.util.Utils.getValue(childClient.getColumnmaps(), DBConstants.KEY.DOB, false);
        Date date1 = Utils.dobStringToDate(dateOfBirth);
        Date date2 = Utils.dobStringToDate("2019-06-01T03:00:00.000+03:00");
        String str2 = ChildUtils.getDurationFromTwoDate(date1, date2);
        Assert.assertEquals("13w 1d", str2);
    }

    @Test
    public void testDobStringToYear() {
        Assert.assertNull(ChildUtils.dobStringToYear("7d"));
        Assert.assertNull(ChildUtils.dobStringToYear(""));
        Integer yob = ChildUtils.dobStringToYear("2y");
        Assert.assertEquals(Integer.valueOf(2), yob);
    }

    @Test
    public void testGetFirstSecondAsNumber() {
        Assert.assertEquals("Zero", ChildUtils.getFirstSecondAsNumber("0"));
        Assert.assertEquals("1st", ChildUtils.getFirstSecondAsNumber("1"));
        Assert.assertEquals("5th", ChildUtils.getFirstSecondAsNumber("5"));
        Assert.assertEquals("3rd", ChildUtils.getFirstSecondAsNumber("3"));
        Assert.assertEquals("6th", ChildUtils.getFirstSecondAsNumber("6"));
        Assert.assertEquals("8th", ChildUtils.getFirstSecondAsNumber("8"));
        Assert.assertEquals("9th", ChildUtils.getFirstSecondAsNumber("9"));
        Assert.assertEquals("7th", ChildUtils.getFirstSecondAsNumber("7"));
    }

    @Test
    public void testReturnMainColumns() throws InvocationTargetException, IllegalAccessException {
        String familyTableName = "ec_family";
        String tableName = "ec_child";
        String familyMemberTableName = "ec_family_member";
        Method method = Whitebox.getMethod(ChildUtils.class, "mainColumns",
                String.class, String.class, String.class);

        String [] columns  = (String[]) method.invoke(null, tableName, familyTableName, familyMemberTableName);
        Assert.assertNotNull(columns);
        Assert.assertEquals(tableName + "." + DBConstants.KEY.FIRST_NAME, columns[3]);
    }

    @Test
    public void testGetChildVisitStatus(){
        HomeAlertRule homeAlertRule = Mockito.mock(HomeAlertRule.class);
        ChildVisit childVisit = ChildUtils.getChildVisitStatus(homeAlertRule, 1213441433);
        Assert.assertNotNull(childVisit);
    }

    @Test
    public void testDaysAwayWithPreviousDate(){
        //Test with 10 days ago should return "10 days overdue"
        String tenDaysAgo = String.valueOf(new LocalDate().minusDays(10));
        SpannableString tenDaysAway = ChildUtils.daysAway(tenDaysAgo);
        Assert.assertTrue(tenDaysAway.length() > 0);
        Assert.assertTrue(tenDaysAway.toString().contains("10"));
        Assert.assertTrue(tenDaysAway.toString().contains(" days overdue"));
    }


    @Test
    public void testDaysAwayWithTodayAndFutureDate(){
        //Test with 10 days in future should return "10 days away"
        String tenDaysInFuture = String.valueOf(new LocalDate().plusDays(10));
        SpannableString tenDaysAway = ChildUtils.daysAway(tenDaysInFuture);
        Assert.assertTrue(tenDaysAway.length() > 0);
        Assert.assertTrue(tenDaysAway.toString().contains("10"));
        Assert.assertTrue(tenDaysAway.toString().contains(" days away"));

        String today = String.valueOf(new LocalDate().plusDays(10));
        SpannableString todayDaysAway = ChildUtils.daysAway(today);
        Assert.assertTrue(todayDaysAway.length() > 0);
        Assert.assertTrue(todayDaysAway.toString().contains("0"));
        Assert.assertTrue(todayDaysAway.toString().contains(" days away"));
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
