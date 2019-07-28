package org.smartregister.chw.sync;

import android.content.ContentValues;
import android.content.Context;
import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.repository.HomeVisitRepository;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.WashCheck;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.Obs;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.Column;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.ServiceType;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.service.intent.RecurringIntentService;
import org.smartregister.immunization.service.intent.VaccineIntentService;
import org.smartregister.sync.ClientProcessorForJava;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import timber.log.Timber;


public class ChwClientProcessor extends ClientProcessorForJava {

    private ClientClassification classification;
    private Table vaccineTable;
    private Table serviceTable;

    private ChwClientProcessor(Context context) {
        super(context);
    }

    public static ClientProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new ChwClientProcessor(context);
        }
        return instance;
    }

    private Table getVaccineTable() {
        if (vaccineTable == null) {
            vaccineTable = assetJsonToJava("ec_client_vaccine.json", Table.class);
        }
        return vaccineTable;
    }

    private Table getServiceTable() {
        if (serviceTable == null) {
            serviceTable = assetJsonToJava("ec_client_service.json", Table.class);
        }
        return serviceTable;
    }

    private ClientClassification getClassification() {
        if (classification == null) {
            classification = assetJsonToJava("ec_client_classification.json", ClientClassification.class);
        }
        return classification;
    }

    @Override
    public synchronized void processClient(List<EventClient> eventClients) throws Exception {

        ClientClassification clientClassification = getClassification();
        Table vaccineTable = getVaccineTable();
        Table serviceTable = getServiceTable();

        if (!eventClients.isEmpty()) {
            for (EventClient eventClient : eventClients) {
                Event event = eventClient.getEvent();
                if (event == null) {
                    return;
                }

                String eventType = event.getEventType();
                if (eventType == null) {
                    continue;
                }

                switch (eventType) {
                    case VaccineIntentService.EVENT_TYPE:
                    case VaccineIntentService.EVENT_TYPE_OUT_OF_CATCHMENT:
                        if (vaccineTable == null) {
                            continue;
                        }
                        processVaccine(eventClient, vaccineTable, eventType.equals(VaccineIntentService.EVENT_TYPE_OUT_OF_CATCHMENT));
                        break;
                    case RecurringIntentService.EVENT_TYPE:
                        if (serviceTable == null) {
                            continue;
                        }
                        processService(eventClient, serviceTable);
                        break;
                    case HomeVisitRepository.EVENT_TYPE:
                    case HomeVisitRepository.NOT_DONE_EVENT_TYPE:
                        processHomeVisit(eventClient);
                        processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
                        break;
                    case Constants.EventType.MINIMUM_DIETARY_DIVERSITY:
                    case Constants.EventType.MUAC:
                    case Constants.EventType.LLITN:
                    case Constants.EventType.ECD:
                        processHomeVisitService(eventClient);
                        processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
                        break;
                    case Constants.EventType.ANC_HOME_VISIT:
                    case org.smartregister.chw.anc.util.Constants.EVENT_TYPE.ANC_HOME_VISIT_NOT_DONE:
                    case org.smartregister.chw.anc.util.Constants.EVENT_TYPE.ANC_HOME_VISIT_NOT_DONE_UNDO:
                        if (eventClient.getEvent() == null) {
                            continue;
                        }
                        processVisitEvent(eventClient);
                        processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
                        break;
                    case Constants.EventType.REMOVE_FAMILY:
                        if (eventClient.getClient() == null) {
                            continue;
                        }
                        processRemoveFamily(eventClient.getClient().getBaseEntityId(), event.getEventDate().toDate());
                        break;
                    case Constants.EventType.REMOVE_MEMBER:
                        if (eventClient.getClient() == null) {
                            continue;
                        }
                        processRemoveMember(eventClient.getClient().getBaseEntityId(), event.getEventDate().toDate());
                        break;
                    case Constants.EventType.REMOVE_CHILD:
                        if (eventClient.getClient() == null) {
                            continue;
                        }
                        processRemoveChild(eventClient.getClient().getBaseEntityId(), event.getEventDate().toDate());
                        break;
                    case Constants.EventType.VACCINE_CARD_RECEIVED:
                        if (eventClient.getClient() == null) {
                            continue;
                        }
                        processVaccineCardEvent(eventClient);
                        processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
                        break;
                    case Constants.EventType.WASH_CHECK:
                        processWashCheckEvent(eventClient);
                        processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);

                        break;
                    default:
                        if (eventClient.getClient() != null) {
                            if (eventType.equals(Constants.EventType.UPDATE_FAMILY_RELATIONS) && event.getEntityType().equalsIgnoreCase(Constants.TABLE_NAME.FAMILY_MEMBER)) {
                                event.setEventType(Constants.EventType.UPDATE_FAMILY_MEMBER_RELATIONS);
                            }
                            processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
                        }
                        break;
                }

            }

        }
    }

    private void processHomeVisit(EventClient eventClient) {
        List<Obs> observations = eventClient.getEvent().getObs();

        ChildUtils.addToHomeVisitTable(eventClient.getEvent().getBaseEntityId(), eventClient.getEvent().getFormSubmissionId(), observations);
    }

    private void processVaccineCardEvent(EventClient eventClient) {
        List<Obs> observations = eventClient.getEvent().getObs();
        ChildUtils.addToChildTable(eventClient.getEvent().getBaseEntityId(), observations);
    }

    private void processHomeVisitService(EventClient eventClient) {
        ChildUtils.addToHomeVisitService(eventClient.getEvent().getEventType(), eventClient.getEvent().getObs(), eventClient.getEvent().getEventDate().toDate(), ChildUtils.gsonConverter.toJson(eventClient.getEvent()));
    }
    private void processWashCheckEvent(EventClient eventClient){
        WashCheck washCheck = new WashCheck();
        for (Obs obs : eventClient.getEvent().getObs()) {

            if (obs.getFormSubmissionField().equalsIgnoreCase(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.FAMILY_ID)) {
                washCheck.setFamilyBaseEntityId((String) obs.getValue());
            }
            if (obs.getFormSubmissionField().equalsIgnoreCase(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.WASH_CHECK_DETAILS)) {
                washCheck.setDetailsJson((String) obs.getValue());
            }
            if (obs.getFormSubmissionField().equalsIgnoreCase(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.WASH_CHECK_LAST_VISIT)) {
                washCheck.setLastVisit(Long.parseLong((String) obs.getValue()));
            }
        }
        ChwApplication.getWashCheckRepo().add(washCheck);
    }

    // possible to delegate
    private void processVisitEvent(EventClient eventClient) {
        org.smartregister.chw.anc.util.Util.processAncHomeVisit(eventClient); // save locally
    }

    // possible to delegate
    private Boolean processVaccine(EventClient vaccine, Table vaccineTable, boolean outOfCatchment) {

        try {
            if (vaccine == null || vaccine.getEvent() == null) {
                return false;
            }

            if (vaccineTable == null) {
                return false;
            }

            Timber.d("Starting processVaccine table: " + vaccineTable.name);

            ContentValues contentValues = processCaseModel(vaccine, vaccineTable);

            // updateFamilyRelations the values to db
            if (contentValues != null && contentValues.size() > 0) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = simpleDateFormat.parse(contentValues.getAsString(VaccineRepository.DATE));

                VaccineRepository vaccineRepository = ChwApplication.getInstance().vaccineRepository();
                Vaccine vaccineObj = new Vaccine();
                vaccineObj.setBaseEntityId(contentValues.getAsString(VaccineRepository.BASE_ENTITY_ID));
                vaccineObj.setName(contentValues.getAsString(VaccineRepository.NAME));
                if (contentValues.containsKey(VaccineRepository.CALCULATION)) {
                    vaccineObj.setCalculation(parseInt(contentValues.getAsString(VaccineRepository.CALCULATION)));
                }
                vaccineObj.setDate(date);
                vaccineObj.setAnmId(contentValues.getAsString(VaccineRepository.ANMID));
                vaccineObj.setLocationId(contentValues.getAsString(VaccineRepository.LOCATION_ID));
                vaccineObj.setSyncStatus(VaccineRepository.TYPE_Synced);
                vaccineObj.setFormSubmissionId(vaccine.getEvent().getFormSubmissionId());
                vaccineObj.setEventId(vaccine.getEvent().getEventId());
                vaccineObj.setOutOfCatchment(outOfCatchment ? 1 : 0);

                String createdAtString = contentValues.getAsString(VaccineRepository.CREATED_AT);
                Date createdAt = getDate(createdAtString);
                vaccineObj.setCreatedAt(createdAt);

                addVaccine(vaccineRepository, vaccineObj);

                Timber.d("Ending processVaccine table: " + vaccineTable.name);
            }
            return true;

        } catch (Exception e) {

            Timber.e(e, "Process Vaccine Error");
            return null;
        }
    }

    // possible to delegate
    private Boolean processService(EventClient service, Table serviceTable) {

        try {

            if (service == null || service.getEvent() == null) {
                return false;
            }

            if (serviceTable == null) {
                return false;
            }

            Timber.d("Starting processService table: " + serviceTable.name);

            ContentValues contentValues = processCaseModel(service, serviceTable);

            // updateFamilyRelations the values to db
            if (contentValues != null && contentValues.size() > 0) {
                String name = contentValues.getAsString(RecurringServiceTypeRepository.NAME);

                if (StringUtils.isNotBlank(name)) {
                    name = name.replaceAll("_", " ").replace("dose", "").trim();
                }


                String eventDateStr = contentValues.getAsString(RecurringServiceRecordRepository.DATE);
                Date date = getDate(eventDateStr);
                String value = null;

                if (StringUtils.containsIgnoreCase(name, "Exclusive breastfeeding")) {
                    value = contentValues.getAsString(RecurringServiceRecordRepository.VALUE);
                }

                RecurringServiceTypeRepository recurringServiceTypeRepository = ImmunizationLibrary.getInstance().getInstance().recurringServiceTypeRepository();
                List<ServiceType> serviceTypeList = recurringServiceTypeRepository.searchByName(name);
                if (serviceTypeList == null || serviceTypeList.isEmpty()) {
                    return false;
                }

                if (date == null) {
                    return false;
                }

                RecurringServiceRecordRepository recurringServiceRecordRepository = ImmunizationLibrary.getInstance().getInstance().recurringServiceRecordRepository();
                ServiceRecord serviceObj = new ServiceRecord();
                serviceObj.setBaseEntityId(contentValues.getAsString(RecurringServiceRecordRepository.BASE_ENTITY_ID));
                serviceObj.setName(name);
                serviceObj.setDate(date);
                serviceObj.setAnmId(contentValues.getAsString(RecurringServiceRecordRepository.ANMID));
                serviceObj.setLocationId(contentValues.getAsString(RecurringServiceRecordRepository.LOCATION_ID));
                serviceObj.setSyncStatus(RecurringServiceRecordRepository.TYPE_Synced);
                serviceObj.setFormSubmissionId(service.getEvent().getFormSubmissionId());
                serviceObj.setEventId(service.getEvent().getEventId()); //FIXME hard coded id
                serviceObj.setValue(value);
                serviceObj.setRecurringServiceId(serviceTypeList.get(0).getId());

                String createdAtString = contentValues.getAsString(RecurringServiceRecordRepository.CREATED_AT);
                Date createdAt = getDate(createdAtString);
                serviceObj.setCreatedAt(createdAt);

                recurringServiceRecordRepository.add(serviceObj);

                Timber.d("Ending processService table: " + serviceTable.name);
            }
            return true;

        } catch (Exception e) {
            Timber.e(e, "Process Service Error");
            return null;
        }
    }

    private Integer parseInt(String string) {
        try {
            return Integer.valueOf(string);
        } catch (NumberFormatException e) {
            Timber.e(e);
        }
        return null;
    }

    private Float parseFloat(String string) {
        try {
            return Float.valueOf(string);
        } catch (NumberFormatException e) {
            Timber.e(e);
        }
        return null;
    }

    private Date getDate(String eventDateStr) {
        Date date = null;
        if (StringUtils.isNotBlank(eventDateStr)) {
            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
                date = dateFormat.parse(eventDateStr);
            } catch (ParseException e) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    date = dateFormat.parse(eventDateStr);
                } catch (ParseException pe) {
                    try {
                        date = DateUtil.parseDate(eventDateStr);
                    } catch (ParseException pee) {
                        Timber.e(pee, pee.toString());
                    }
                }
            }
        }
        return date;
    }

    private ContentValues processCaseModel(EventClient eventClient, Table table) {
        try {
            List<Column> columns = table.columns;
            ContentValues contentValues = new ContentValues();

            for (Column column : columns) {
                processCaseModel(eventClient.getEvent(), eventClient.getClient(), column, contentValues);
            }

            return contentValues;
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public static void addVaccine(VaccineRepository vaccineRepository, Vaccine vaccine) {
        try {
            if (vaccineRepository == null || vaccine == null) {
                return;
            }

            // Add the vaccine
            vaccineRepository.add(vaccine);

            String name = vaccine.getName();
            if (StringUtils.isBlank(name)) {
                return;
            }

            // Update vaccines in the same group where either can be given
            // For example measles 1 / mr 1
            name = VaccineRepository.removeHyphen(name);
            String ftsVaccineName = null;

            if (VaccineRepo.Vaccine.measles1.display().equalsIgnoreCase(name)) {
                ftsVaccineName = VaccineRepo.Vaccine.mr1.display();
            } else if (VaccineRepo.Vaccine.mr1.display().equalsIgnoreCase(name)) {
                ftsVaccineName = VaccineRepo.Vaccine.measles1.display();
            } else if (VaccineRepo.Vaccine.measles2.display().equalsIgnoreCase(name)) {
                ftsVaccineName = VaccineRepo.Vaccine.mr2.display();
            } else if (VaccineRepo.Vaccine.mr2.display().equalsIgnoreCase(name)) {
                ftsVaccineName = VaccineRepo.Vaccine.measles2.display();
            }

            if (ftsVaccineName != null) {
                ftsVaccineName = VaccineRepository.addHyphen(ftsVaccineName.toLowerCase());
                Vaccine ftsVaccine = new Vaccine();
                ftsVaccine.setBaseEntityId(vaccine.getBaseEntityId());
                ftsVaccine.setName(ftsVaccineName);
                vaccineRepository.updateFtsSearch(ftsVaccine);
            }

        } catch (Exception e) {
            Timber.e(e);
        }

    }

    private void processRemoveMember(String baseEntityId, Date eventDate) {

        Date myEventDate = eventDate;
        if (myEventDate == null) {
            myEventDate = new Date();
        }

        if (baseEntityId == null) {
            return;
        }

        AllCommonsRepository commonsRepository = ChwApplication.getInstance().getAllCommonsRepository(Constants.TABLE_NAME.FAMILY_MEMBER);
        if (commonsRepository != null) {

            ContentValues values = new ContentValues();
            values.put(DBConstants.KEY.DATE_REMOVED, new SimpleDateFormat("yyyy-MM-dd").format(myEventDate));
            values.put("is_closed", 1);

            ChwApplication.getInstance().getRepository().getWritableDatabase().update(Constants.TABLE_NAME.FAMILY_MEMBER, values,
                    DBConstants.KEY.BASE_ENTITY_ID + " = ?  ", new String[]{baseEntityId});

            // clean fts table
            ChwApplication.getInstance().getRepository().getWritableDatabase().update(CommonFtsObject.searchTableName(Constants.TABLE_NAME.FAMILY_MEMBER), values,
                    " object_id  = ?  ", new String[]{baseEntityId});

            // Utils.context().commonrepository(Constants.TABLE_NAME.FAMILY_MEMBER).populateSearchValues(baseEntityId, DBConstants.KEY.DATE_REMOVED, new SimpleDateFormat("yyyy-MM-dd").format(eventDate), null);

        }
    }

    private void processRemoveChild(String baseEntityId, Date eventDate) {

        Date myEventDate = eventDate;
        if (myEventDate == null) {
            myEventDate = new Date();
        }

        if (baseEntityId == null) {
            return;
        }

        AllCommonsRepository commonsRepository = ChwApplication.getInstance().getAllCommonsRepository(Constants.TABLE_NAME.CHILD);
        if (commonsRepository != null) {

            ContentValues values = new ContentValues();
            values.put(DBConstants.KEY.DATE_REMOVED, new SimpleDateFormat("yyyy-MM-dd").format(myEventDate));
            values.put("is_closed", 1);

            ChwApplication.getInstance().getRepository().getWritableDatabase().update(Constants.TABLE_NAME.CHILD, values,
                    DBConstants.KEY.BASE_ENTITY_ID + " = ?  ", new String[]{baseEntityId});

            // clean fts table
            ChwApplication.getInstance().getRepository().getWritableDatabase().update(CommonFtsObject.searchTableName(Constants.TABLE_NAME.CHILD), values,
                    CommonFtsObject.idColumn + "  = ?  ", new String[]{baseEntityId});

            // Utils.context().commonrepository(Constants.TABLE_NAME.CHILD).populateSearchValues(baseEntityId, DBConstants.KEY.DATE_REMOVED, new SimpleDateFormat("yyyy-MM-dd").format(eventDate), null);

        }
    }

    /**
     * Update the family members
     *
     * @param familyID
     */
    private void processRemoveFamily(String familyID, Date eventDate) {

        Date myEventDate = eventDate;
        if (myEventDate == null) {
            myEventDate = new Date();
        }

        if (familyID == null) {
            return;
        }

        AllCommonsRepository commonsRepository = ChwApplication.getInstance().getAllCommonsRepository(Constants.TABLE_NAME.FAMILY);
        if (commonsRepository != null) {

            ContentValues values = new ContentValues();
            values.put(DBConstants.KEY.DATE_REMOVED, new SimpleDateFormat("yyyy-MM-dd").format(myEventDate));
            values.put("is_closed", 1);

            ChwApplication.getInstance().getRepository().getWritableDatabase().update(Constants.TABLE_NAME.FAMILY, values,
                    DBConstants.KEY.BASE_ENTITY_ID + " = ?  ", new String[]{familyID});

            ChwApplication.getInstance().getRepository().getWritableDatabase().update(Constants.TABLE_NAME.CHILD, values,
                    DBConstants.KEY.RELATIONAL_ID + " = ?  ", new String[]{familyID});

            ChwApplication.getInstance().getRepository().getWritableDatabase().update(Constants.TABLE_NAME.FAMILY_MEMBER, values,
                    DBConstants.KEY.RELATIONAL_ID + " = ?  ", new String[]{familyID});

            // clean fts table
            ChwApplication.getInstance().getRepository().getWritableDatabase().update(CommonFtsObject.searchTableName(Constants.TABLE_NAME.FAMILY), values,
                    CommonFtsObject.idColumn + " = ?  ", new String[]{familyID});

            ChwApplication.getInstance().getRepository().getWritableDatabase().update(CommonFtsObject.searchTableName(Constants.TABLE_NAME.CHILD), values,
                    String.format(" %s in (select base_entity_id from %s where relational_id = ? )  ", CommonFtsObject.idColumn, Constants.TABLE_NAME.CHILD), new String[]{familyID});

            ChwApplication.getInstance().getRepository().getWritableDatabase().update(CommonFtsObject.searchTableName(Constants.TABLE_NAME.FAMILY_MEMBER), values,
                    String.format(" %s in (select base_entity_id from %s where relational_id = ? )  ", CommonFtsObject.idColumn, Constants.TABLE_NAME.FAMILY_MEMBER), new String[]{familyID});

        }
    }

    @Override
    public void updateClientDetailsTable(Event event, Client client) {
        Timber.d("Started updateClientDetailsTable");
        event.addDetails("detailsUpdated", Boolean.TRUE.toString());
        Timber.d("Finished updateClientDetailsTable");
    }
}