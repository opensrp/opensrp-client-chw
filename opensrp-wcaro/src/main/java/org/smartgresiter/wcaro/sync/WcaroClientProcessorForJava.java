package org.smartgresiter.wcaro.sync;

import android.content.ContentValues;
import android.content.Context;

import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.family.sync.FamilyClientProcessorForJava;
import org.smartregister.family.util.DBConstants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WcaroClientProcessorForJava extends FamilyClientProcessorForJava {

    private static final String TAG = WcaroClientProcessorForJava.class.getName();
    private static WcaroClientProcessorForJava instance;

    public WcaroClientProcessorForJava(Context context) {
        super(context);
    }

    public static WcaroClientProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new WcaroClientProcessorForJava(context);
        }
        return instance;
    }


    @Override
    public void processClient(List<EventClient> eventClients) throws Exception {
        if (eventClients != null && eventClients.size() > 0) {
            for (EventClient eventClient : eventClients) {
                Event event = eventClient.getEvent();
                if (event == null) {
                    return;
                }

                String eventType = event.getEventType();
                if (eventType == null) {
                    continue;
                }


                Client client = eventClient.getClient();
                //iterate through the events
                if (client != null) {
                    if (eventType.equals(Constants.EventType.REMOVE_FAMILY)) {
                        processRemoveFamily(client.getBaseEntityId(), event.getEventDate().toDate());
                    }
                }
            }
        }
        super.processClient(eventClients);
    }

    /**
     * Update the family members
     *
     * @param familyID
     */
    private void processRemoveFamily(String familyID, Date eventDate) {

        if (eventDate == null) {
            eventDate = new Date();
        }

        if (familyID == null) {
            return;
        }

        AllCommonsRepository commonsRepository = WcaroApplication.getInstance().getAllCommonsRepository(Constants.TABLE_NAME.FAMILY);
        if (commonsRepository != null) {

            ContentValues values = new ContentValues();
            values.put(DBConstants.KEY.DATE_REMOVED, new SimpleDateFormat("yyyy-MM-dd").format(eventDate));
            values.put("is_closed", 1);

            WcaroApplication.getInstance().getRepository().getWritableDatabase().update(Constants.TABLE_NAME.FAMILY, values,
                    DBConstants.KEY.BASE_ENTITY_ID + " = ?  ", new String[]{familyID});

            WcaroApplication.getInstance().getRepository().getWritableDatabase().update(Constants.TABLE_NAME.CHILD, values,
                    DBConstants.KEY.RELATIONAL_ID + " = ?  ", new String[]{familyID});

            WcaroApplication.getInstance().getRepository().getWritableDatabase().update(Constants.TABLE_NAME.FAMILY_MEMBER, values,
                    DBConstants.KEY.RELATIONAL_ID + " = ?  ", new String[]{familyID});
        }
    }

}
