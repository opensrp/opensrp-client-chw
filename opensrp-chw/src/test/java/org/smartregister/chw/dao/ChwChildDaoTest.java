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
import org.smartregister.chw.core.domain.Child;
import org.smartregister.repository.Repository;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ChwChildDaoTest extends ChwChildDao {

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
    public void testGetChild() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"base_entity_id"});
        matrixCursor.addRow(new Object[]{"12345"});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        Child child = ChwChildDao.getChild("12345");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(child.getBaseEntityID(), "12345");
    }

    @Test
    public void testGetChildReturnsNull() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"base_entity_id"});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        Child child = ChwChildDao.getChild("12345");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertNull(child);
    }


    @Test
    public void testGetChildGender() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"gender"});
        matrixCursor.addRow(new Object[]{"female"});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        String gender = ChwChildDao.getChildGender("12345");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(gender, "female");
    }

    @Test
    public void testGetChildGenderReturnsEmptyString() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"gender"});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        String gender = ChwChildDao.getChildGender("12345");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        String empty = "";
        Assert.assertEquals(gender, empty);
    }

    @Test
    public void testGetChildFamilyName() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"first_name"});
        matrixCursor.addRow(new Object[]{"Tumba"});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        String familyName = ChwChildDao.getChildFamilyName("12345");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(familyName, "Tumba");
    }

    @Test
    public void testGetChildFamilyNameReturnsEmptyString() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"first_name"});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        String familyName = ChwChildDao.getChildFamilyName("12345");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(familyName, "");
    }

    @Test
    public void testHasDueVaccines() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"count"});
        matrixCursor.addRow(new Object[]{2});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        Boolean dueVaccines = ChwChildDao.hasDueVaccines("12345");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(dueVaccines, true);
    }

    @Test
    public void testHasDueVaccinesReturnsFalse() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"count"});
        matrixCursor.addRow(new Object[]{0});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        Boolean dueVaccines = ChwChildDao.hasDueVaccines("12345");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(dueVaccines, false);
    }


    @Test
    public void testHasDueAlerts() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"count"});
        matrixCursor.addRow(new Object[]{2});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        Boolean dueVaccines = ChwChildDao.hasDueAlerts("12345");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(dueVaccines, true);
    }

    @Test
    public void testHasDueAlertsReturnsFalse() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"count"});
        matrixCursor.addRow(new Object[]{0});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        Boolean dueVaccines = ChwChildDao.hasDueAlerts("12345");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(dueVaccines, false);
    }


    @Test
    public void testHasActiveSchedule() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"count"});
        matrixCursor.addRow(new Object[]{2});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        Boolean dueVaccines = ChwChildDao.hasActiveSchedule("12345");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(dueVaccines, true);
    }

    @Test
    public void testHasActiveVaccinesReturnsFalse() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"count"});
        matrixCursor.addRow(new Object[]{0});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        Boolean dueVaccines = ChwChildDao.hasActiveSchedule("12345");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(dueVaccines, false);
    }

    @Test
    public void testGetRegisteredCertificateNumbers() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"birth_cert_num"});
        matrixCursor.addRow(new Object[]{0});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        List<String> certificateNumbers = ChwChildDao.getRegisteredCertificateNumbers();

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertTrue(certificateNumbers.size() > 0);
    }
}
