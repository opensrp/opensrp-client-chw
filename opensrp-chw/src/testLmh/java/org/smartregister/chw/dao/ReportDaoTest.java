package org.smartregister.chw.dao;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.domain.EligibleChild;
import org.smartregister.chw.domain.VillageDose;
import org.smartregister.chw.util.ReportingConstants;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReportDaoTest extends ReportDao {
    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase database;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        setRepository(repository);
    }

    @Test
    public void testExtractRecordedLocations() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "location_id", "provider_id"});
        matrixCursor.addRow(new Object[]{"d5ff0ea1-bbc5-424d-84c2-5b084e10ef90", "demo"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        HashMap<String, String> locationList = ReportDao.extractRecordedLocations();
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        String expectedLocationId = null;
        String expectedProviderId = null;
        for (Map.Entry<String, String> entry : locationList.entrySet()) {
            expectedLocationId = entry.getKey();
            expectedProviderId = entry.getValue();
        }
        Assert.assertEquals(expectedLocationId, "d5ff0ea1-bbc5-424d-84c2-5b084e10ef90");
        Assert.assertEquals(expectedProviderId, "demo");
    }

    @Test
    public void testExtractRecordedLocationsReturnsEmptyArrayList() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "location_id", "provider_id"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        HashMap<String, String> locationList = ReportDao.extractRecordedLocations();
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(locationList.size(), 0);
    }

    @Test
    public void testEligibleChildrenReport() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "base_entity_id"});
        matrixCursor.addRow(new Object[]{"abdb6140-d54b-49d4-89e7-6f96c839c62b"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        ArrayList<String> communityIds = new ArrayList<>();
        communityIds.add("d5ff0ea1-bbc5-424d-84c2-5b084e10ef90");
        Date dueDate = new DateTime().plusDays(7).toDate();
        List<EligibleChild> eligibleChildren = ReportDao.eligibleChildrenReport(communityIds, dueDate);
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(eligibleChildren.get(0).getID(), "abdb6140-d54b-49d4-89e7-6f96c839c62b");
    }

    @Test
    public void testEligibleChildrenReportReturnsEmptyList() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "base_entity_id"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        ArrayList<String> communityIds = new ArrayList<>();
        communityIds.add("d5ff0ea1-bbc5-424d-84c2-5b084e10ef90");
        Date dueDate = new DateTime().plusDays(7).toDate();
        List<EligibleChild> eligibleChildren = ReportDao.eligibleChildrenReport(communityIds, dueDate);
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(eligibleChildren.size(), 0);
    }

    @Test
    public void testVillageDosesReportSummary() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "scheduleName"});
        matrixCursor.addRow(new Object[]{"ROTA 1"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        String villageName = "Grenaligne";
        Date dueDate = new DateTime().plusDays(7).toDate();
        Map<String, Integer> map = new TreeMap<>();
        map.put("ROTA 1", 0);
        List<VillageDose> villageDoseList = ReportDao.villageDosesReportSummary(villageName, dueDate);
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(villageDoseList.get(0).getRecurringServices(), map);
    }

    @Test
    public void testMyCommunityActivityReportDetailsChildrenDewormed() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "base_entity_id"});
        matrixCursor.addRow(new Object[]{"abdb6140-d54b-49d4-89e7-6f96c839c62b"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        String indicatorCode = ReportingConstants.ChildIndicatorKeys.COUNT_OF_CHILDREN_12_59_DEWORMED;
        List<EligibleChild> eligibleChildren = ReportDao.myCommunityActivityReportDetails(indicatorCode);
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(eligibleChildren.get(0).getID(), "abdb6140-d54b-49d4-89e7-6f96c839c62b");
    }

    @Test
    public void testMyCommunityActivityReportDetailsChildrenNotDewormed() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "base_entity_id"});
        matrixCursor.addRow(new Object[]{"abdb6140-d54b-49d4-89e7-6f96c839c62b"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        String indicatorCode = ReportingConstants.ChildIndicatorKeys.COUNT_OF_CHILDREN_12_59_NOT_DEWORMED;
        List<EligibleChild> eligibleChildren = ReportDao.myCommunityActivityReportDetails(indicatorCode);
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(eligibleChildren.get(0).getID(), "abdb6140-d54b-49d4-89e7-6f96c839c62b");
    }

    @Test
    public void testMyCommunityActivityReportDetailsChildrenUpToDateVaccination() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "base_entity_id"});
        matrixCursor.addRow(new Object[]{"abdb6140-d54b-49d4-89e7-6f96c839c62b"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        String indicatorCode = ReportingConstants.ChildIndicatorKeys.COUNT_OF_CHILDREN_0_24_UPTO_DATE_VACCINATIONS;
        List<EligibleChild> eligibleChildren = ReportDao.myCommunityActivityReportDetails(indicatorCode);
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(eligibleChildren.get(0).getID(), "abdb6140-d54b-49d4-89e7-6f96c839c62b");
    }

    @Test
    public void testMyCommunityActivityReportDetailsChildrenOverDueVaccination() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "base_entity_id"});
        matrixCursor.addRow(new Object[]{"abdb6140-d54b-49d4-89e7-6f96c839c62b"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        String indicatorCode = ReportingConstants.ChildIndicatorKeys.COUNT_OF_CHILDREN_0_24_OVERDUE_VACCINATIONS;
        List<EligibleChild> eligibleChildren = ReportDao.myCommunityActivityReportDetails(indicatorCode);
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(eligibleChildren.get(0).getID(), "abdb6140-d54b-49d4-89e7-6f96c839c62b");
    }


    @Test
    public void testMyCommunityActivityReportDetailsChildrenWithBirthCerts() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "base_entity_id"});
        matrixCursor.addRow(new Object[]{"abdb6140-d54b-49d4-89e7-6f96c839c62b"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        String indicatorCode = ReportingConstants.ChildIndicatorKeys.COUNT_OF_CHILDREN_0_59_WITH_BIRTH_CERT;
        List<EligibleChild> eligibleChildren = ReportDao.myCommunityActivityReportDetails(indicatorCode);
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(eligibleChildren.get(0).getID(), "abdb6140-d54b-49d4-89e7-6f96c839c62b");
    }

    @Test
    public void testMyCommunityActivityReportDetailsChildrenNoBirthCerts() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "base_entity_id"});
        matrixCursor.addRow(new Object[]{"abdb6140-d54b-49d4-89e7-6f96c839c62b"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        String indicatorCode = ReportingConstants.ChildIndicatorKeys.COUNT_OF_CHILDREN_0_59_WITH_NO_BIRTH_CERT;
        List<EligibleChild> eligibleChildren = ReportDao.myCommunityActivityReportDetails(indicatorCode);
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(eligibleChildren.get(0).getID(), "abdb6140-d54b-49d4-89e7-6f96c839c62b");
    }

    @Test
    public void testMyCommunityActivityReportDetailsChildrenExclusiveBreastFeeding() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "base_entity_id"});
        matrixCursor.addRow(new Object[]{"abdb6140-d54b-49d4-89e7-6f96c839c62b"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        String indicatorCode = ReportingConstants.ChildIndicatorKeys.COUNT_OF_CHILDREN_0_5_EXCLUSIVELY_BREASTFEEDING;
        List<EligibleChild> eligibleChildren = ReportDao.myCommunityActivityReportDetails(indicatorCode);
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(eligibleChildren.get(0).getID(), "abdb6140-d54b-49d4-89e7-6f96c839c62b");
    }

    @Test
    public void testMyCommunityActivityReportDetailsChildrenNotExclusiveBreastFeeding() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "base_entity_id"});
        matrixCursor.addRow(new Object[]{"abdb6140-d54b-49d4-89e7-6f96c839c62b"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        String indicatorCode = ReportingConstants.ChildIndicatorKeys.COUNT_OF_CHILDREN_0_5_NOT_EXCLUSIVELY_BREASTFEEDING;
        List<EligibleChild> eligibleChildren = ReportDao.myCommunityActivityReportDetails(indicatorCode);
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(eligibleChildren.get(0).getID(), "abdb6140-d54b-49d4-89e7-6f96c839c62b");
    }


    @Test
    public void testMyCommunityActivityReportDetailsChildrenVitaminAReceived() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "base_entity_id"});
        matrixCursor.addRow(new Object[]{"abdb6140-d54b-49d4-89e7-6f96c839c62b"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        String indicatorCode = ReportingConstants.ChildIndicatorKeys.COUNT_OF_CHILDREN_6_59_VITAMIN_RECEIVED_A;
        List<EligibleChild> eligibleChildren = ReportDao.myCommunityActivityReportDetails(indicatorCode);
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(eligibleChildren.get(0).getID(), "abdb6140-d54b-49d4-89e7-6f96c839c62b");
    }

    @Test
    public void testMyCommunityActivityReportDetailsChildrenVitaminANotReceived() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "base_entity_id"});
        matrixCursor.addRow(new Object[]{"abdb6140-d54b-49d4-89e7-6f96c839c62b"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        String indicatorCode = ReportingConstants.ChildIndicatorKeys.COUNT_OF_CHILDREN_6_59_VITAMIN_NOT_RECEIVED_A;
        List<EligibleChild> eligibleChildren = ReportDao.myCommunityActivityReportDetails(indicatorCode);
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(eligibleChildren.get(0).getID(), "abdb6140-d54b-49d4-89e7-6f96c839c62b");
    }

    @Test
    public void testMyCommunityActivityReportDetailsReturnsEmptyArray() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "base_entity_id"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        String indicatorCode = ReportingConstants.ChildIndicatorKeys.COUNT_OF_CHILDREN_12_59_DEWORMED;
        List<EligibleChild> eligibleChildren = ReportDao.myCommunityActivityReportDetails(indicatorCode);
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(eligibleChildren.size(), 0);
    }
}