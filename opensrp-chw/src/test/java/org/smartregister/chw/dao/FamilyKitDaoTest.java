package org.smartregister.chw.dao;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.repository.Repository;

import java.util.Map;

public class FamilyKitDaoTest extends FamilyKitDao {
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
    public void testGetFamilyKitDetails() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"visit_id", "base_entity_id", "visit_key",
        "parent_code", "preprocessed_type", "details", "human_readable_details"});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        Map<String, VisitDetail> map = FamilyKitDao.getFamilyKitDetails(239872398L, "123456");


        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(map.size(), 0);
    }

    @Test
    public void testGetLastFamilyKitDate() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"family_kit_date"});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        long eventDate = FamilyKitDao.getLastFamilyKitDate("1234567");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(eventDate, 0);
    }
}
