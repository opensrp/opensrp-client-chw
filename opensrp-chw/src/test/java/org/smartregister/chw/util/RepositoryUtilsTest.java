package org.smartregister.chw.util;

import net.sqlcipher.Cursor;
import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.domain.Event;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(application = ChwApplication.class, sdk = 22)
public class RepositoryUtilsTest {

    @Mock
    private SQLiteDatabase database;

    @Mock
    private Cursor cursor;

    @Mock
    private ECSyncHelper ecSyncHelper;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void updateNullEventIdsUpdatesCorrectEvents() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"json"});
        matrixCursor.addRow(new Object[]{getSampleEventJSONString()});
        Mockito.doReturn(matrixCursor).when(database).query(ArgumentMatchers.eq(EventClientRepository.Table.event.name()),
                ArgumentMatchers.any(String[].class), ArgumentMatchers.eq("eventId IS NULL AND validationStatus = ?"),
                ArgumentMatchers.any(String[].class), ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.isNull());
        RepositoryUtils.updateNullEventIds(database);
        verify(database).execSQL("UPDATE event SET eventId = '3b598b80-13ee-4a9a-8cd1-8e66fa76bbe9', " +
                "syncStatus = 'Synced' WHERE formSubmissionId = '45a294f5-ec2f-4233-847a-6f7910a6e63f';");
    }

    private static String getSampleEventJSONString() {
            return "{\n" +
                    "\n" +
                    "    baseEntityId: \"158cb2d0-a78f-4087-9ccc-0a0de9724548\",\n" +
                    "    childLocationId: \"Nairobi\",\n" +
                    "    duration: 0,\n" +
                    "    entityType: \"ec_family_member\",\n" +
                    "    eventDate: \"2020-12-20T00:00:00.000Z\",\n" +
                    "    eventType: \"Family Member Registration\",\n" +
                    "    formSubmissionId: \"45a294f5-ec2f-4233-847a-6f7910a6e63f\",\n" +
                    "    locationId: \"bd657bc4-fad6-40b1-bdc4-38a8f1ddfdc4\",\n" +
                    "    obs: [\n" +
                    "        {\n" +
                    "            fieldCode: \"\",\n" +
                    "            fieldDataType: \"text\",\n" +
                    "            fieldType: \"concept\",\n" +
                    "            formSubmissionField: \"surname\",\n" +
                    "            humanReadableValues: [],\n" +
                    "            parentCode: \"\",\n" +
                    "            values: [\n" +
                    "                \"Jeff\"\n" +
                    "            ]\n" +
                    "        },\n" +
                    "        {\n" +
                    "            fieldCode: \"\",\n" +
                    "            fieldDataType: \"text\",\n" +
                    "            fieldType: \"concept\",\n" +
                    "            formSubmissionField: \"fam_name\",\n" +
                    "            humanReadableValues: [],\n" +
                    "            parentCode: \"\",\n" +
                    "            values: [\n" +
                    "                \"Koinange\"\n" +
                    "            ]\n" +
                    "        },\n" +
                    "        {\n" +
                    "            fieldCode: \"dob_unknown\",\n" +
                    "            fieldDataType: \"text\",\n" +
                    "            fieldType: \"formsubmissionField\",\n" +
                    "            formSubmissionField: \"dob_unknown\",\n" +
                    "            humanReadableValues: [],\n" +
                    "            parentCode: \"\",\n" +
                    "            values: [\n" +
                    "                \"true\"\n" +
                    "            ]\n" +
                    "        },\n" +
                    "        {\n" +
                    "            fieldCode: \"last_interacted_with\",\n" +
                    "            fieldDataType: \"text\",\n" +
                    "            fieldType: \"formsubmissionField\",\n" +
                    "            formSubmissionField: \"last_interacted_with\",\n" +
                    "            humanReadableValues: [],\n" +
                    "            parentCode: \"\",\n" +
                    "            values: [\n" +
                    "                \"1608491344580\"\n" +
                    "            ]\n" +
                    "        },\n" +
                    "        {\n" +
                    "            fieldCode: \"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                    "            fieldDataType: \"start\",\n" +
                    "            fieldType: \"concept\",\n" +
                    "            formSubmissionField: \"start\",\n" +
                    "            humanReadableValues: [],\n" +
                    "            parentCode: \"\",\n" +
                    "            values: [\n" +
                    "                \"2020-12-20 19:08:16\"\n" +
                    "            ]\n" +
                    "        },\n" +
                    "        {\n" +
                    "            fieldCode: \"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                    "            fieldDataType: \"end\",\n" +
                    "            fieldType: \"concept\",\n" +
                    "            formSubmissionField: \"end\",\n" +
                    "            humanReadableValues: [],\n" +
                    "            parentCode: \"\",\n" +
                    "            values: [\n" +
                    "                \"2020-12-20 19:09:04\"\n" +
                    "            ]\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    providerId: \"onatester\",\n" +
                    "    team: \"ONA\",\n" +
                    "    teamId: \"127edf8b-d021-41b9-a74a-71896397ed42\",\n" +
                    "    version: 1608491344583,\n" +
                    "    clientApplicationVersion: 13,\n" +
                    "    clientDatabaseVersion: 15,\n" +
                    "    dateCreated: \"2020-12-20T19:09:04.583Z\",\n" +
                    "    type: \"Event\",\n" +
                        "_id: \"3b598b80-13ee-4a9a-8cd1-8e66fa76bbe9\",\n" +
                        "_rev: \"v1\"" +
                    "}";
        }

    @Test
    public void testReadEvents() throws JSONException {
        List<Event> expectedEvents = new ArrayList<>();
        Event event1 = new Event();
        event1.setEventId("event1");
        event1.setBaseEntityId("158cb2d0-a78f-4087-9ccc-0a0de9724548");
        expectedEvents.add(event1);
        when(cursor.getCount()).thenReturn(1);
        when(cursor.moveToFirst()).thenReturn(true);
        when(cursor.isAfterLast()).thenReturn(false, false, true);
        when(cursor.getString(cursor.getColumnIndex("json"))).thenReturn(getSampleEventJSONString(), getSampleEventJSONString());
        when(ecSyncHelper.convert(new JSONObject(getSampleEventJSONString()), Event.class)).thenReturn(event1);

        List<Event> actualEvents = RepositoryUtils.readEvents(cursor);

        assertEquals(expectedEvents.get(0).getBaseEntityId(), actualEvents.get(0).getBaseEntityId());
    }


    @Test
    public void addDetailsColumnToFamilySearchTableShouldAddColumnsSuccessfully() {
        // When
        RepositoryUtils.addDetailsColumnToFamilySearchTable(database);

        // Then
        verify(database, times(1)).execSQL("ALTER TABLE ec_family ADD COLUMN entity_type VARCHAR; " +
                "UPDATE ec_family SET entity_type = 'ec_family' WHERE id is not null;");
        verify(database, times(5)).execSQL(anyString());
    }

    @Test
    public void updateClientValidationStatusTest()
    {
        RepositoryUtils.updateClientValidateStatus(database);
        verify(database, times(1)).execSQL(anyString());
    }

}
