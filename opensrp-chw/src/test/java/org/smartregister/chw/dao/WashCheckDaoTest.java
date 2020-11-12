package org.smartregister.chw.dao;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.repository.Repository;

import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class WashCheckDaoTest extends WashCheckDao {

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.repository.Repository;

import java.util.Map;

public class WashCheckDaoTest extends WashCheckDao {
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
    public void testGetLastWashCheckDate() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"created_at"});
        matrixCursor.addRow(new Object[]{getDobDateFormat().format(new Date())});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        long washCheckDate = WashCheckDao.getLastWashCheckDate("12345");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertTrue(getDobDateFormat().format(new Date()).equals(getDobDateFormat().format(washCheckDate)));
    }

    @Test
    public void testUpdateWashCheckVisitDetails() {
        Mockito.doReturn(database).when(repository).getWritableDatabase();
        WashCheckDao.updateWashCheckVisitDetails(Long.parseLong("1567329933757"), "12345", "Yes", "Yes", "Yes");
        Mockito.verify(database).rawExecSQL(Mockito.anyString());
    }

    @Test
    public void testUpdateWashCheckVisits() {
        Mockito.doReturn(database).when(repository).getWritableDatabase();
        WashCheckDao.updateWashCheckVisits(Long.parseLong("1567329933757"), "12345", "sample_json");
        Mockito.verify(database).rawExecSQL(Mockito.anyString());
    }

    @Test
    public void testGetWashCheckDetails() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"base_entity_id", "visit_key", "human_readable_details"});
        matrixCursor.addRow(new Object[]{"12345", "handwashing_facilities", "Yes"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        WashCheckDao.getWashCheckDetails(Long.parseLong("1567329933757"), "12345");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
    }

    public void testGetWashCheckDetails() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"visit_id", "base_entity_id", "visit_key",
        "parent_code", "preprocessed_type", "details", "human_readable_details"});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        Map<String, VisitDetail> map = WashCheckDao.getWashCheckDetails(239872398L, "123456");


        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(map.size(), 0);
    }

    @Test
    public void testGetLastWashCheckDate() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"eventDate"});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        long eventDate = WashCheckDao.getLastWashCheckDate("1234567");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(eventDate, 0);
    }
}
