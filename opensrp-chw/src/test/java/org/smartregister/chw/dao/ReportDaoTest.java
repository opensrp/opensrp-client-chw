package org.smartregister.chw.dao;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.domain.EligibleChild;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"location_id", "provider_id"});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        Map<String, String> locations = ReportDao.extractRecordedLocations();

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(locations.size(), 0);
    }

    @Test
    public void testFetchAllVaccines() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"location_id", "provider_id"});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        Map<String, List<Vaccine>> vaccines = ReportDao.fetchAllVaccines();

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(vaccines.size(), 0);
    }

    @Test
    public void testFetchLiveEligibleChildrenReport() {
        LocalDate dueDate = LocalDate.parse("19/06/2019", DateTimeFormat.forPattern("dd/MM/yyyy"));
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"location_id", "provider_id"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        List<String> communityIds = new ArrayList<>();
        communityIds.add("1");
        List<EligibleChild> eligibleChildren = ReportDao.fetchLiveEligibleChildrenReport(communityIds, dueDate.toDate());
        Assert.assertEquals(eligibleChildren.size(), 0);
    }
}