package org.smartregister.chw.core.repository;

import net.sqlcipher.Cursor;

import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import timber.log.Timber;

public class ReferralsRepository extends BaseRepository {
    public ReferralsRepository(Repository repository) {
        super(repository);
    }

    public int getTasksCount(String query) {
        int count = 0;
        try {
            Cursor cursor = getReadableDatabase().rawQuery(query, new String[]{});
            count = cursor != null && cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } catch (Exception e) {
            Timber.e(e, "HfChwRepository --> getTasksCount");
        }
        return count;

    }
}
