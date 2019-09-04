package org.smartregister.chw.core.repository;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.chw.core.contract.ScheduleTask;
import org.smartregister.chw.core.domain.BaseScheduleTask;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class ScheduleRepository extends BaseRepository {
    public static final String TABLE_NAME = "schedule_service";
    public static final String ID = "id";
    public static final String BASE_ENTITY_ID = "base_entity_id";
    public static final String SCHEDULE_GROUP_NAME = "schedule_group_name";
    public static final String SCHEDULE_NAME = "schedule_name";
    public static final String DUE_DATE = "due_date";
    public static final String NOT_DONE_DATE = "not_done_date";
    public static final String OVER_DUE_DATE = "over_due_date";
    public static final String EXPIRY_DATE = "expiry_date";
    public static final String COMPLETION_DATE = "completion_date";
    private static final String UPDATED_AT = "updated_at";
    private static final String CREATED_AT = "created_at";


    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + "  VARCHAR NOT NULL, " +
            BASE_ENTITY_ID + "  VARCHAR NOT NULL, " +
            SCHEDULE_GROUP_NAME + "  VARCHAR, " +
            SCHEDULE_NAME + "  VARCHAR NOT NULL, " +
            DUE_DATE + " VARCHAR NOT NULL, " +
            NOT_DONE_DATE + " VARCHAR NOT NULL, " +
            OVER_DUE_DATE + " VARCHAR NOT NULL, " +
            EXPIRY_DATE + " VARCHAR NOT NULL, " +
            COMPLETION_DATE + " VARCHAR NOT NULL, " +
            UPDATED_AT + " DATETIME NOT NULL, " +
            CREATED_AT + " DATETIME NOT NULL " +
            ")";


    private String[] COLUMNS = {BASE_ENTITY_ID, SCHEDULE_GROUP_NAME, SCHEDULE_NAME, DUE_DATE, NOT_DONE_DATE, OVER_DUE_DATE, EXPIRY_DATE, COMPLETION_DATE, UPDATED_AT, CREATED_AT};

    private static final String BASE_ID_INDEX = "CREATE UNIQUE INDEX " + TABLE_NAME + "_" + ID + "_index ON " + TABLE_NAME + "(" + ID + " COLLATE NOCASE " + ")";
    private static final String BASE_ENTITY_ID_INDEX = "CREATE INDEX " + TABLE_NAME + "_" + BASE_ENTITY_ID + "_index ON " + TABLE_NAME + "(" + BASE_ENTITY_ID + " COLLATE NOCASE " + ")";
    private static final String SCHEDULE_GROUP_NAME_INDEX = "CREATE INDEX " + TABLE_NAME + "_" + SCHEDULE_GROUP_NAME + "_index ON " + TABLE_NAME + "(" + SCHEDULE_GROUP_NAME + " COLLATE NOCASE " + ")";

    public ScheduleRepository(Repository repository) {
        super(repository);
    }

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(BASE_ID_INDEX);
        database.execSQL(BASE_ENTITY_ID_INDEX);
        database.execSQL(SCHEDULE_GROUP_NAME_INDEX);
    }

    public void addSchedule(ScheduleTask task) {
        addSchedule(task, getWritableDatabase());
    }

    public void addSchedule(ScheduleTask task, SQLiteDatabase database) {
        if (task == null) {
            return;
        }
        // Handle updated home visit details
        database.insert(TABLE_NAME, null, createValues(task));
    }

    public void addSchedules(List<ScheduleTask> tasks) {
        for (ScheduleTask task : tasks) {
            addSchedule(task);
        }
    }

    public void addSchedules(List<ScheduleTask> tasks, SQLiteDatabase database) {
        for (ScheduleTask task : tasks) {
            addSchedule(task, database);
        }
    }

    private ContentValues createValues(ScheduleTask scheduleTask) {
        ContentValues values = new ContentValues();
        values.put(BASE_ENTITY_ID, scheduleTask.getBaseEntityID());
        values.put(SCHEDULE_GROUP_NAME, scheduleTask.getScheduleGroupName());
        values.put(SCHEDULE_NAME, scheduleTask.getScheduleName());
        values.put(DUE_DATE, scheduleTask.getScheduleDueDate() != null ? scheduleTask.getScheduleDueDate().getTime() : null);
        values.put(NOT_DONE_DATE, scheduleTask.getScheduleNotDoneDate() != null ? scheduleTask.getScheduleNotDoneDate().getTime() : null);
        values.put(OVER_DUE_DATE, scheduleTask.getScheduleOverDueDate() != null ? scheduleTask.getScheduleOverDueDate().getTime() : null);
        values.put(EXPIRY_DATE, scheduleTask.getScheduleExpiryDate() != null ? scheduleTask.getScheduleExpiryDate().getTime() : null);
        values.put(EXPIRY_DATE, scheduleTask.getScheduleCompletionDate() != null ? scheduleTask.getScheduleCompletionDate().getTime() : null);
        values.put(UPDATED_AT, new Date().getTime());
        values.put(CREATED_AT, new Date().getTime());
        return values;
    }

    private List<BaseScheduleTask> readSchedules(Cursor cursor) {
        List<BaseScheduleTask> scheduleTasks = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    BaseScheduleTask task = new BaseScheduleTask();
                    task.setBaseEntityID(cursor.getString(cursor.getColumnIndex(BASE_ENTITY_ID)));
                    task.setScheduleGroupName(cursor.getString(cursor.getColumnIndex(SCHEDULE_GROUP_NAME)));
                    task.setScheduleName(cursor.getString(cursor.getColumnIndex(SCHEDULE_NAME)));
                    task.setScheduleDueDate(getCursorDate(cursor, DUE_DATE));
                    task.setScheduleNotDoneDate(getCursorDate(cursor, NOT_DONE_DATE));
                    task.setScheduleOverDueDate(getCursorDate(cursor, OVER_DUE_DATE));
                    task.setScheduleExpiryDate(getCursorDate(cursor, EXPIRY_DATE));
                    task.setScheduleCompletionDate(getCursorDate(cursor, COMPLETION_DATE));
                    task.setCreatedAt(new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(CREATED_AT)))));
                    task.setUpdatedAt(new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(UPDATED_AT)))));

                    scheduleTasks.add(task);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return scheduleTasks;
    }

    public void deleteScheduleByID(String id) {
        try {
            getWritableDatabase().delete(TABLE_NAME, ID + "= ?", new String[]{id});
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void deleteScheduleByGroupName(String group_name) {
        try {
            getWritableDatabase().delete(TABLE_NAME, SCHEDULE_GROUP_NAME + "= ?", new String[]{group_name});
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void deleteScheduleByName(String name) {
        try {
            getWritableDatabase().delete(TABLE_NAME, SCHEDULE_NAME + "= ?", new String[]{name});
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public List<BaseScheduleTask> getSchedulesByName(String scheduleName) {
        List<BaseScheduleTask> scheduleTasks = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(TABLE_NAME, COLUMNS, SCHEDULE_NAME + " = ? ", new String[]{scheduleName}, null, null, CREATED_AT + " ASC ", null);
            scheduleTasks = readSchedules(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return scheduleTasks;
    }

    public List<BaseScheduleTask> getSchedulesByBaseID(String scheduleName) {
        List<BaseScheduleTask> scheduleTasks = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(TABLE_NAME, COLUMNS, BASE_ENTITY_ID + " = ? ", new String[]{scheduleName}, null, null, CREATED_AT + " ASC ", null);
            scheduleTasks = readSchedules(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return scheduleTasks;
    }

    public List<BaseScheduleTask> getSchedulesByGroup(String groupName) {
        List<BaseScheduleTask> scheduleTasks = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(TABLE_NAME, COLUMNS, SCHEDULE_GROUP_NAME + " = ? ", new String[]{groupName}, null, null, CREATED_AT + " ASC ", null);
            scheduleTasks = readSchedules(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return scheduleTasks;
    }

    private Date getCursorDate(Cursor c, String column_name) {
        Long val = c.getType(c.getColumnIndex(column_name)) == Cursor.FIELD_TYPE_NULL ? null : c.getLong(c.getColumnIndex(column_name));
        if (val == null)
            return null;

        return new Date(val);
    }
}
