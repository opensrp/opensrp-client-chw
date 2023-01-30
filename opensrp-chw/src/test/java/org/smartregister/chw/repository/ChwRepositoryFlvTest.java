package org.smartregister.chw.repository;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.immunization.repository.VaccineRepository;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class ChwRepositoryFlvTest {

    @Test
    public void testOnUpgrade() {
        SQLiteDatabase db = Mockito.mock(SQLiteDatabase.class);
        ChwRepositoryFlv.onUpgrade(null, db, 1, 2);
        verify(db).execSQL(VaccineRepository.UPDATE_TABLE_ADD_EVENT_ID_COL);
        verify(db).execSQL(VaccineRepository.EVENT_ID_INDEX);
        verify(db).execSQL(VaccineRepository.UPDATE_TABLE_ADD_FORMSUBMISSION_ID_COL);
        verify(db).execSQL(VaccineRepository.FORMSUBMISSION_INDEX);
        verify(db).execSQL(VaccineRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL);
        verify(db).execSQL(VaccineRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL_INDEX);
        verify(db).execSQL(VaccineRepository.UPDATE_TABLE_ADD_HIA2_STATUS_COL);


    }
}