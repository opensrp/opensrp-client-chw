package org.smartregister.chw.sync;


import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.cdp.CdpLibrary;
import org.smartregister.chw.cdp.dao.CdpOrderDao;
import org.smartregister.chw.cdp.dao.CdpStockingDao;
import org.smartregister.chw.core.sync.CoreClientProcessor;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.schedulers.ChwScheduleTaskExecutor;
import org.smartregister.chw.service.ChildAlertService;
import org.smartregister.chw.util.Constants;
import org.smartregister.domain.Event;
import org.smartregister.domain.Obs;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.sync.ClientProcessorForJava;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

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
    public void processEvents(ClientClassification clientClassification, Table vaccineTable, Table serviceTable, EventClient eventClient, Event event, String eventType) throws Exception {
        if (eventClient != null && eventClient.getEvent() != null) {
            String baseEntityID = eventClient.getEvent().getBaseEntityId();

            switch (eventType) {
                case CoreConstants.EventType.REMOVE_FAMILY:
                    ChwApplication.getInstance().getScheduleRepository().deleteSchedulesByFamilyEntityID(baseEntityID);
                case CoreConstants.EventType.REMOVE_MEMBER:
                    ChwApplication.getInstance().getScheduleRepository().deleteSchedulesByEntityID(baseEntityID);
                case CoreConstants.EventType.REMOVE_CHILD:
                    if (!CoreLibrary.getInstance().isPeerToPeerProcessing() && !SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
                        ChwApplication.getInstance().getScheduleRepository().deleteSchedulesByEntityID(baseEntityID);
                    }
                    break;
                case org.smartregister.chw.cdp.util.Constants.EVENT_TYPE.CDP_RECEIVE_FROM_FACILITY:
                case org.smartregister.chw.cdp.util.Constants.EVENT_TYPE.CDP_OUTLET_RESTOCK:
                    processCDPStockChanges(eventClient.getEvent());
                    processVisitEvent(eventClient);
                    break;
                case org.smartregister.chw.cdp.util.Constants.EVENT_TYPE.CDP_OUTLET_VISIT:
                    if (eventClient.getEvent() == null) {
                        return;
                    }
                    processVisitEvent(eventClient);
                    processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
                    break;
                case org.smartregister.chw.cdp.util.Constants.EVENT_TYPE.CDP_CONDOM_ORDER:
                    if (eventClient.getEvent() == null) {
                        return;
                    }
                    processCDPOrderEvent(eventClient.getEvent(), org.smartregister.chw.cdp.util.Constants.TABLES.CDP_ORDERS);
                    break;
                default:
                    break;
            }
        }

        super.processEvents(clientClassification, vaccineTable, serviceTable, eventClient, event, eventType);
        if (eventClient != null && eventClient.getEvent() != null) {
            String baseEntityID = eventClient.getEvent().getBaseEntityId();
            switch (eventType) {
                case CoreConstants.EventType.CHILD_HOME_VISIT:
                case CoreConstants.EventType.CHILD_VISIT_NOT_DONE:
                case CoreConstants.EventType.CHILD_REGISTRATION:
                case CoreConstants.EventType.UPDATE_CHILD_REGISTRATION:
                    if (!CoreLibrary.getInstance().isPeerToPeerProcessing() && !SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
                        ChildAlertService.updateAlerts(baseEntityID);
                    }
                    break;
                case Constants.Events.ANC_FIRST_FACILITY_VISIT:
                case Constants.Events.ANC_RECURRING_FACILITY_VISIT:
                    if (eventClient.getEvent() == null) {
                        return;
                    }
                    processVisitEvent(eventClient);
                    processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
                    break;
                default:
                    break;
            }
        }

        if (!CoreLibrary.getInstance().isPeerToPeerProcessing() && !SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
            ChwScheduleTaskExecutor.getInstance().execute(event.getBaseEntityId(), event.getEventType(), event.getEventDate().toDate());
        }
    }

    private void processVisitEvent(EventClient eventClient) {
        try {
            NCUtils.processHomeVisit(eventClient);
        } catch (Exception e) {
            String formID = (eventClient != null && eventClient.getEvent() != null) ? eventClient.getEvent().getFormSubmissionId() : "no form id";
            Timber.e("Form id " + formID + ". " + e.toString());
        }
    }

    private void processCDPStockChanges(Event event) {
        List<Obs> visitObs = event.getObs();
        String maleCondomsOffset = "0";
        String femaleCondomsOffset = "0";
        String locationId = event.getLocationId();
        String chwName = event.getProviderId();

        if (visitObs.size() > 0) {
            for (Obs obs : visitObs) {
                if (org.smartregister.chw.cdp.util.Constants.JSON_FORM_KEY.FEMALE_CONDOMS_OFFSET.equals(obs.getFieldCode())) {
                    femaleCondomsOffset = (String) obs.getValue();
                } else if (org.smartregister.chw.cdp.util.Constants.JSON_FORM_KEY.MALE_CONDOMS_OFFSET.equals(obs.getFieldCode())) {
                    maleCondomsOffset = (String) obs.getValue();
                }
            }
            if (event.getEventType().equals(org.smartregister.chw.cdp.util.Constants.EVENT_TYPE.CDP_RECEIVE_FROM_FACILITY)) {
                String stockEventType = org.smartregister.chw.cdp.util.Constants.STOCK_EVENT_TYPES.INCREMENT;
                CdpStockingDao.updateStockLogData(locationId, event.getFormSubmissionId(), chwName, maleCondomsOffset, femaleCondomsOffset, stockEventType, event.getEventType());
                CdpStockingDao.updateStockCountData(locationId, chwName, maleCondomsOffset, femaleCondomsOffset, stockEventType);
            }
            if (event.getEventType().equals(org.smartregister.chw.cdp.util.Constants.EVENT_TYPE.CDP_OUTLET_RESTOCK)) {
                String stockEventType = org.smartregister.chw.cdp.util.Constants.STOCK_EVENT_TYPES.DECREMENT;
                CdpStockingDao.updateStockLogData(locationId, event.getFormSubmissionId(), chwName, maleCondomsOffset, femaleCondomsOffset, stockEventType, event.getEventType());
                CdpStockingDao.updateStockCountData(locationId, chwName, maleCondomsOffset, femaleCondomsOffset, stockEventType);
            }
            CdpLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(event.getFormSubmissionId());
        }
    }


    @Override
    protected String getHumanReadableConceptResponse(String value, Object object) {
        try {
            if (StringUtils.isBlank(value) || (object != null && !(object instanceof Obs))) {
                return value;
            }
            // Skip human readable values and just get values which would aid in translations
            final String VALUES = "values";
            List values = new ArrayList();

            Object valueObject = getValue(object, VALUES);
            if (valueObject instanceof List) {
                values = (List) valueObject;
            }
            if (object == null || values.isEmpty()) {
                return value;
            }

            return values.size() == 1 ? values.get(0).toString() : values.toString();

        } catch (Exception e) {
            Timber.e(e);
        }
        return value;
    }
}
