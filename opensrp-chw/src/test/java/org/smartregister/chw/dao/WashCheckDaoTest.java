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
        WashCheckDao.updateWashCheckVisitDetails(Long.parseLong("1567329933757"),"12345","Yes","Yes","Yes");
        Mockito.verify(database).rawExecSQL(Mockito.anyString());
    }
    @Test
    public void testUpdateWashCheckVisits() {
        Mockito.doReturn(database).when(repository).getWritableDatabase();
        WashCheckDao.updateWashCheckVisits(Long.parseLong("1567329933757"),"12345","sample_json");
        Mockito.verify(database).rawExecSQL(Mockito.anyString());
    }
}
