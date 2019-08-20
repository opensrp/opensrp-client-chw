package org.smartregister.chw.core.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.smartregister.chw.core.domain.ContactInfo;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import timber.log.Timber;


public class ContactInfoRepository extends BaseRepository {
    public static final String TABLE_NAME = "contact_info";
    public static final String BASE_ENTITY_ID = "base_entity_id";
    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String CREATED_AT = "created_at";
    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "(" +
            BASE_ENTITY_ID + "  VARCHAR NOT NULL, " +
            KEY + "  VARCHAR, " +
            VALUE + "  VARCHAR NOT NULL, " +
            CREATED_AT + " INTEGER NOT NULL, " +
            "UNIQUE(" + BASE_ENTITY_ID + ", " + KEY + ") ON CONFLICT REPLACE )";


    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + TABLE_NAME + "_" + BASE_ENTITY_ID +
            "_index ON " + TABLE_NAME + "(" + BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_KEY = "CREATE INDEX " + TABLE_NAME + "_" + KEY +
            "_index ON " + TABLE_NAME + "(" + KEY + " COLLATE NOCASE);";

    private String[] projectionArgs = new String[]{KEY, VALUE, BASE_ENTITY_ID, CREATED_AT};

    public ContactInfoRepository(Repository repository) {
        super(repository);
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_KEY);
    }

    public void saveContactInfo(ContactInfo contactInfo) {
        if (contactInfo == null) {
            return;
        }
        contactInfo.setEventDate((new LocalDate()).toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
        getWritableDatabase().insert(TABLE_NAME, null, createValuesFor(contactInfo));


    }

    private ContentValues createValuesFor(ContactInfo contactInfo) {
        ContentValues values = new ContentValues();
        values.put(BASE_ENTITY_ID, contactInfo.getBaseEntityId());
        values.put(VALUE, contactInfo.getValue());
        values.put(KEY, contactInfo.getKey());
        values.put(CREATED_AT, contactInfo.getEventDate());
        return values;
    }

    /**
     * @param contactInfoRequest object holding contact request params
     *                           it MUST contain NON NULL values for
     *                           key
     *                           baseEntityId
     *                           contactNo
     */
    public ContactInfo getContactInfo(ContactInfo contactInfoRequest) {
        String selection = null;
        String[] selectionArgs = null;
        ContactInfo dbContactInfo = null;
        Cursor mCursor = null;
        try {
            if (StringUtils.isNotBlank(contactInfoRequest.getBaseEntityId()) && StringUtils.isNotBlank(contactInfoRequest.getKey())) {
                selection = BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE + " AND " + KEY + " = ? " + COLLATE_NOCASE;
                selectionArgs = new String[]{contactInfoRequest.getBaseEntityId(), contactInfoRequest.getKey()};
            }

            mCursor = getReadableDatabase().query(TABLE_NAME, projectionArgs, selection, selectionArgs, null, null, null, null);
            if (mCursor.getCount() > 0) {

                mCursor.moveToFirst();

                dbContactInfo = getContactResult(mCursor);
            }
        } catch (Exception e) {
            Timber.e(e);

        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
        return dbContactInfo;
    }


    private ContactInfo getContactResult(Cursor cursor) {

        ContactInfo previousContact = new ContactInfo();
        previousContact.setKey(cursor.getString(cursor.getColumnIndex(KEY)));
        previousContact.setValue(cursor.getString(cursor.getColumnIndex(VALUE)));
        previousContact.setBaseEntityId(cursor.getString(cursor.getColumnIndex(BASE_ENTITY_ID)));
        previousContact.setEventDate(cursor.getString(cursor.getColumnIndex(CREATED_AT)));

        return previousContact;
    }

}
