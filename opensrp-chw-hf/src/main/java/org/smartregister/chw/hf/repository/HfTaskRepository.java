package org.smartregister.chw.hf.repository;

import net.sqlcipher.Cursor;

import org.smartregister.chw.core.utils.ChwDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.domain.Task;
import org.smartregister.repository.Repository;
import org.smartregister.repository.TaskNotesRepository;
import org.smartregister.repository.TaskRepository;

import timber.log.Timber;

public class HfTaskRepository extends TaskRepository {
    public HfTaskRepository(Repository repository, TaskNotesRepository taskNotesRepository) {
        super(repository, taskNotesRepository);
    }

    public Task getLatestTaskByEntityId(String forEntity, String referralType) {
        Task task = new Task();
        try (Cursor cursor = getReadableDatabase().rawQuery(String.format("SELECT * FROM %s WHERE %s = ? AND %s = ? AND %s = ? AND %s = ?  ORDER BY %s DESC LIMIT 1",
                TASK_TABLE, ChwDBConstants.TaskTable.BUSINESS_STATUS, ChwDBConstants.TaskTable.STATUS,
                TASK_TABLE + "." + ChwDBConstants.TaskTable.FOR, ChwDBConstants.TaskTable.FOCUS, ChwDBConstants.TaskTable.START),
                new String[]{CoreConstants.BUSINESS_STATUS.REFERRED, Task.TaskStatus.READY.name(), forEntity, referralType})) {
            if (cursor.moveToFirst()) {
                task = readCursor(cursor);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return task;
    }
}
