package org.smartregister.chw.sync;


import android.content.Context;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.sync.CoreClientProcessor;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.schedulers.ChwScheduleTaskExecutor;
import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.sync.ClientProcessorForJava;

public class ChwClientProcessor extends CoreClientProcessor {

    private ChwClientProcessor(Context context) {
        super(context);
    }

    public static ClientProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new ChwClientProcessor(context);
        }
        return instance;
    }

    @Override
    protected void processEvents(ClientClassification clientClassification, Table vaccineTable, Table serviceTable, EventClient eventClient, Event event, String eventType) throws Exception {
        if (eventClient != null && eventClient.getEvent() != null) {
            String baseEntityID = eventClient.getEvent().getBaseEntityId();

            switch (eventType) {
                case CoreConstants.EventType.REMOVE_FAMILY:
                    ChwApplication.getInstance().getScheduleRepository().deleteSchedulesByFamilyEntityID(baseEntityID);
                case CoreConstants.EventType.REMOVE_MEMBER:
                    ChwApplication.getInstance().getScheduleRepository().deleteSchedulesByEntityID(baseEntityID);
                case CoreConstants.EventType.REMOVE_CHILD:
                    ChwApplication.getInstance().getScheduleRepository().deleteSchedulesByEntityID(baseEntityID);
                    break;
                default:
                    break;
            }
        }

        super.processEvents(clientClassification, vaccineTable, serviceTable, eventClient, event, eventType);
        eventClient.getEvent().addDetails("planIdentifier", "2427d5fe-3c19-5bbe-b3c4-142482082a74");
        super.processPlanEvaluation(eventClient);
        ChwScheduleTaskExecutor.getInstance().execute(event.getBaseEntityId(), event.getEventType(), event.getEventDate().toDate());
    }
}
