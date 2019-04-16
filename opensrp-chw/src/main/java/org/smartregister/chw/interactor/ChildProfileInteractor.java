package org.smartregister.chw.interactor;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.contract.ChildProfileContract;
import org.smartregister.chw.contract.HomeVisitGrowthNutritionContract;
import org.smartregister.chw.contract.HomeVisitImmunizationContract;
import org.smartregister.chw.fragment.ChildHomeVisitFragment;
import org.smartregister.chw.presenter.HomeVisitImmunizationPresenter;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.chw.util.ChildHomeVisit;
import org.smartregister.chw.util.ChildService;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.ChildVisit;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.GrowthServiceData;
import org.smartregister.chw.util.HomeVisitVaccineGroup;
import org.smartregister.chw.util.ImmunizationState;
import org.smartregister.chw.util.Utils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.Alert;
import org.smartregister.domain.Photo;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;
import org.smartregister.util.ImageUtils;
import org.smartregister.view.LocationPickerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static org.smartregister.chw.util.ChildUtils.fixVaccineCasing;
import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;

public class ChildProfileInteractor implements ChildProfileContract.Interactor {
    public static final String TAG = ChildProfileInteractor.class.getName();
    private AppExecutors appExecutors;
    private CommonPersonObjectClient pClient;
    private Map<String, Date> vaccineList = new LinkedHashMap<>();

    @VisibleForTesting
    ChildProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public ChildProfileInteractor() {
        this(new AppExecutors());
    }

    public CommonPersonObjectClient getpClient() {
        return pClient;
    }

    public Map<String, Date> getVaccineList() {
        return vaccineList;
    }

    public AllSharedPreferences getAllSharedPreferences() {
        return Utils.context().allSharedPreferences();
    }

    public UniqueIdRepository getUniqueIdRepository() {
        return FamilyLibrary.getInstance().getUniqueIdRepository();
    }

    public CommonRepository getCommonRepository(String tableName) {
        return Utils.context().commonrepository(tableName);
    }

    public ECSyncHelper getSyncHelper() {
        return FamilyLibrary.getInstance().getEcSyncHelper();
    }

    public ClientProcessorForJava getClientProcessorForJava() {
        return FamilyLibrary.getInstance().getClientProcessorForJava();
    }

    private void saveRegistration(Pair<Client, Event> pair, String jsonString, boolean isEditMode) {

        try {

            Client baseClient = pair.first;
            Event baseEvent = pair.second;

            if (baseClient != null) {
                JSONObject clientJson = new JSONObject(JsonFormUtils.gson.toJson(baseClient));
                if (isEditMode) {
                    JsonFormUtils.mergeAndSaveClient(getSyncHelper(), baseClient);
                } else {
                    getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);
                }
            }

            if (baseEvent != null) {
                JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
                getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson);
            }

            if (isEditMode) {
                // Unassign current OPENSRP ID
//                if (baseClient != null) {
//                    String newOpenSRPId = baseClient.getIdentifier(DBConstants.KEY.UNIQUE_ID).replace("-", "");
//                    String currentOpenSRPId = JsonFormUtils.getString(jsonString, JsonFormUtils.CURRENT_OPENSRP_ID).replace("-", "");
//                    if (!newOpenSRPId.equals(currentOpenSRPId)) {
//                        //OPENSRP ID was changed
//                        getUniqueIdRepository().open(currentOpenSRPId);
//                    }
//                }

            } else {
                if (baseClient != null) {
                    String opensrpId = baseClient.getIdentifier(Utils.metadata().uniqueIdentifierKey);

                    //mark OPENSRP ID as used
                    getUniqueIdRepository().close(opensrpId);
                }
            }

            if (baseClient != null || baseEvent != null) {
                String imageLocation = JsonFormUtils.getFieldValue(jsonString, org.smartregister.family.util.Constants.KEY.PHOTO);
                JsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
            }

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void updateVisitNotDone(long value) {
        ChildUtils.updateHomeVisitAsEvent(getpClient().entityId(), Constants.EventType.CHILD_VISIT_NOT_DONE, Constants.TABLE_NAME.CHILD, new JSONObject(), new JSONObject(), new JSONObject(),new JSONObject(), new JSONObject(),new JSONObject(), new JSONObject(), ChildDBConstants.KEY.VISIT_NOT_DONE, value + "");

    }

    @Override
    public void refreshChildVisitBar(String baseEntityId, final ChildProfileContract.InteractorCallBack callback) {
        ChildHomeVisit childHomeVisit = ChildUtils.getLastHomeVisit(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD, baseEntityId);

        String dobString = Utils.getDuration(Utils.getValue(pClient.getColumnmaps(), DBConstants.KEY.DOB, false));

        final ChildVisit childVisit = ChildUtils.getChildVisitStatus(dobString, childHomeVisit.getLastHomeVisitDate(), childHomeVisit.getVisitNotDoneDate(), childHomeVisit.getDateCreated());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.updateChildVisit(childVisit);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void refreshUpcomingServiceAndFamilyDue(String familyId, String baseEntityId, final ChildProfileContract.InteractorCallBack callback) {
        if (getpClient() == null) return;
        updateUpcomingServices()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChildService>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ChildService childService) {
                        callback.updateChildService(childService);
                        callback.hideProgressBar();

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.hideProgressBar();

                    }

                    @Override
                    public void onComplete() {
                    }
                });
        updateFamilyDueStatus(baseEntityId, familyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        callback.updateFamilyMemberServiceDue(s);

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.hideProgressBar();
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

    private Observable<ChildService> updateUpcomingServices() {
        return Observable.create(new ObservableOnSubscribe<ChildService>() {
            @Override
            public void subscribe(final ObservableEmitter<ChildService> e) throws Exception {
                final HomeVisitImmunizationPresenter presenter = new HomeVisitImmunizationPresenter();
                presenter.setChildClient(getpClient());
                presenter.updateImmunizationState(new HomeVisitImmunizationContract.InteractorCallBack() {
                    @Override
                    public void immunizationState(List<Alert> alerts, List<Vaccine> vaccines, Map<String, Date> receivedVaccine, List<Map<String, Object>> sch) {
                        vaccineList = receivedVaccine;
                        presenter.createAllVaccineGroups(alerts, vaccines, sch);
                        presenter.getVaccinesNotGivenLastVisit();
                        presenter.calculateCurrentActiveGroup();
                        ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetailsList = presenter.getAllgroups();
                        String dueDate = "", vaccineName = "";
                        ImmunizationState state = ImmunizationState.UPCOMING;
                        for (HomeVisitVaccineGroup homeVisitVaccineGroupDetail : homeVisitVaccineGroupDetailsList) {
                            if (homeVisitVaccineGroupDetail.getAlert().equals(ImmunizationState.DUE)
                                    || homeVisitVaccineGroupDetail.getAlert().equals(ImmunizationState.OVERDUE)
                                    || homeVisitVaccineGroupDetail.getAlert().equals(ImmunizationState.UPCOMING)) {
                                if (homeVisitVaccineGroupDetail.getNotGivenVaccines().size() > 0) {
                                    dueDate = homeVisitVaccineGroupDetail.getDueDisplayDate();
                                    VaccineRepo.Vaccine vaccine = homeVisitVaccineGroupDetail.getNotGivenVaccines().get(0);
                                    vaccineName = fixVaccineCasing(vaccine.display());
                                    state = homeVisitVaccineGroupDetail.getAlert();
                                    break;
                                }
                            }
                        }

                        if (!TextUtils.isEmpty(vaccineName) && !TextUtils.isEmpty(dueDate)) {
                            ChildService childService = new ChildService();
                            childService.setServiceName(vaccineName);
                            if (childService.getServiceName().contains("MEASLES")) {
                                childService.setServiceName(childService.getServiceName().replace("MEASLES", "MCV"));
                            }
                            //String duedateString = DateUtil.formatDate(dueDate, "dd MMM yyyy");
                            childService.setServiceDate(dueDate);
                            if (state.equals(ImmunizationState.DUE)) {
                                childService.setServiceStatus(ServiceType.DUE.name());
                            } else if (state.equals(ImmunizationState.OVERDUE)) {
                                childService.setServiceStatus(ServiceType.OVERDUE.name());
                            } else {
                                childService.setServiceStatus(ServiceType.UPCOMING.name());
                            }
                            e.onNext(childService);
                        } else {
                            //fetch service data
                            final HomeVisitGrowthNutritionInteractor homeVisitGrowthNutritionInteractor = new HomeVisitGrowthNutritionInteractor();
                            homeVisitGrowthNutritionInteractor.parseRecordServiceData(getpClient(), new HomeVisitGrowthNutritionContract.InteractorCallBack() {
                                @Override
                                public void updateGivenRecordVisitData(Map<String, ServiceWrapper> stringServiceWrapperMap) {
                                    try {
                                        ChildService childService = null;
                                        ArrayList<GrowthServiceData> growthServiceDataList = homeVisitGrowthNutritionInteractor.getAllDueService(stringServiceWrapperMap);
                                        if (growthServiceDataList.size() > 0) {
                                            childService = new ChildService();
                                            GrowthServiceData growthServiceData = growthServiceDataList.get(0);
                                            childService.setServiceName(growthServiceData.getDisplayName());
                                            childService.setServiceDate(growthServiceData.getDisplayAbleDate());
                                            ImmunizationState state1 = ChildUtils.getDueStatus(growthServiceData.getDate());
                                            if (state1.equals(ImmunizationState.DUE)) {
                                                childService.setServiceStatus(ServiceType.DUE.name());
                                            } else if (state1.equals(ImmunizationState.OVERDUE)) {
                                                childService.setServiceStatus(ServiceType.OVERDUE.name());
                                            } else {
                                                childService.setServiceStatus(ServiceType.UPCOMING.name());
                                            }

                                        }
                                        e.onNext(childService);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void updateNotGivenRecordVisitData(Map<String, ServiceWrapper> stringServiceWrapperMap) {
                                    //No need to handle not given service
                                }
                            });
                        }


                    }
                });

            }
        });
    }

    private Observable<String> updateFamilyDueStatus(final String childId, final String familyId) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                ImmunizationState familyImmunizationState = ImmunizationState.NO_ALERT;
                String serviceDueStatus;
                String query = ChildUtils.getChildListByFamilyId(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD, familyId, childId);
                Cursor cursor = org.smartregister.family.util.Utils.context().commonrepository(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD).queryTable(query);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        CommonPersonObject personObject = org.smartregister.family.util.Utils.context().commonrepository(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD).findByBaseEntityId(cursor.getString(1));
                        if (!personObject.getCaseId().equalsIgnoreCase(childId)) {
                            String dobString = org.smartregister.util.Utils.getValue(personObject.getColumnmaps(), DBConstants.KEY.DOB, false);
                            String visitNotDoneStr = org.smartregister.util.Utils.getValue(personObject.getColumnmaps(), ChildDBConstants.KEY.VISIT_NOT_DONE, false);
                            String lastHomeVisitStr = org.smartregister.util.Utils.getValue(personObject.getColumnmaps(), ChildDBConstants.KEY.LAST_HOME_VISIT, false);
                            String strDateCreated = org.smartregister.family.util.Utils.getValue(personObject.getColumnmaps(), ChildDBConstants.KEY.DATE_CREATED, false);
                            long lastHomeVisit = TextUtils.isEmpty(lastHomeVisitStr) ? 0 : Long.parseLong(lastHomeVisitStr);
                            long visitNotDone = TextUtils.isEmpty(visitNotDoneStr) ? 0 : Long.parseLong(visitNotDoneStr);

                            long dateCreated = 0;
                            if (!TextUtils.isEmpty(strDateCreated)) {
                                dateCreated = org.smartregister.family.util.Utils.dobStringToDateTime(strDateCreated).getMillis();
                            }
                            final ChildVisit childVisit = ChildUtils.getChildVisitStatus(dobString, lastHomeVisit, visitNotDone, dateCreated);
                            if (childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.OVERDUE.name())
                                    || childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.DUE.name()))
                                if (familyImmunizationState != null && !familyImmunizationState.equals(ImmunizationState.OVERDUE)) {
                                    familyImmunizationState = getImmunizationStatus(childVisit.getVisitStatus());
                                }
                        }


                    } while (cursor.moveToNext());
                    cursor.close();
                }
                if (familyImmunizationState.equals(ImmunizationState.DUE)) {
                    serviceDueStatus = FamilyServiceType.DUE.name();
                } else if (familyImmunizationState.equals(ImmunizationState.OVERDUE)) {
                    serviceDueStatus = FamilyServiceType.OVERDUE.name();
                } else {
                    serviceDueStatus = FamilyServiceType.NOTHING.name();
                }
                e.onNext(serviceDueStatus);
            }
        });

    }

    private ImmunizationState getImmunizationStatus(String visitStatus) {
        if (visitStatus.equalsIgnoreCase(ChildProfileInteractor.VisitType.OVERDUE.name())) {
            return ImmunizationState.OVERDUE;
        }
        if (visitStatus.equalsIgnoreCase(ChildProfileInteractor.VisitType.DUE.name())) {
            return ImmunizationState.DUE;
        }
        return ImmunizationState.NO_ALERT;
    }

    @Override
    public void updateChildCommonPerson(String baseEntityId) {
        String query = ChildUtils.mainSelect(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD, org.smartregister.chw.util.Constants.TABLE_NAME.FAMILY, org.smartregister.chw.util.Constants.TABLE_NAME.FAMILY_MEMBER, baseEntityId);

        Cursor cursor = getCommonRepository(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD).rawCustomQueryForAdapter(query);
        if (cursor != null && cursor.moveToFirst()) {
            CommonPersonObject personObject = getCommonRepository(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD).readAllcommonforCursorAdapter(cursor);
            pClient = new CommonPersonObjectClient(personObject.getCaseId(),
                    personObject.getDetails(), "");
            pClient.setColumnmaps(personObject.getColumnmaps());
            cursor.close();
        }
    }

    /**
     * Refreshes family view based on the child id
     *
     * @param baseEntityId
     * @param isForEdit
     * @param callback
     */
    @Override
    public void refreshProfileView(final String baseEntityId, final boolean isForEdit, final ChildProfileContract.InteractorCallBack callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String query = ChildUtils.mainSelect(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD, org.smartregister.chw.util.Constants.TABLE_NAME.FAMILY, org.smartregister.chw.util.Constants.TABLE_NAME.FAMILY_MEMBER, baseEntityId);

                Cursor cursor = getCommonRepository(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD).rawCustomQueryForAdapter(query);
                if (cursor != null && cursor.moveToFirst()) {
                    CommonPersonObject personObject = getCommonRepository(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD).readAllcommonforCursorAdapter(cursor);
                    pClient = new CommonPersonObjectClient(personObject.getCaseId(),
                            personObject.getDetails(), "");
                    pClient.setColumnmaps(personObject.getColumnmaps());
                    final String familyId = Utils.getValue(pClient.getColumnmaps(), ChildDBConstants.KEY.RELATIONAL_ID, false);

                    final CommonPersonObject familyPersonObject = getCommonRepository(Utils.metadata().familyRegister.tableName).findByBaseEntityId(familyId);
                    final CommonPersonObjectClient client = new CommonPersonObjectClient(familyPersonObject.getCaseId(), familyPersonObject.getDetails(), "");
                    client.setColumnmaps(familyPersonObject.getColumnmaps());

                    final String primaryCaregiverID = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.PRIMARY_CAREGIVER, false);
                    final String familyHeadID = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FAMILY_HEAD, false);
                    final String familyName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FIRST_NAME, false);


                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {

                            callback.setFamilyHeadID(familyHeadID);
                            callback.setFamilyID(familyId);
                            callback.setPrimaryCareGiverID(primaryCaregiverID);
                            callback.setFamilyName(familyName);

                            if (isForEdit) {
                                callback.startFormForEdit("", pClient);
                            } else {
                                callback.refreshProfileTopSection(pClient);
                            }
                        }
                    });
                    cursor.close();
                }


            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public JSONObject getAutoPopulatedJsonEditFormString(String formName, String title, Context context, CommonPersonObjectClient client) {
        try {
            JSONObject form = FormUtils.getInstance(context).getFormJson(formName);
            LocationPickerView lpv = new LocationPickerView(context);
            lpv.init();
            if (form != null) {
                form.put(org.smartregister.family.util.JsonFormUtils.ENTITY_ID, client.getCaseId());
                form.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE, Constants.EventType.UPDATE_CHILD_REGISTRATION);

                JSONObject metadata = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.METADATA);
                String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());

                metadata.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);

                form.put(org.smartregister.family.util.JsonFormUtils.CURRENT_OPENSRP_ID, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false));

                JSONObject stepOne = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP1);

                if (StringUtils.isNotBlank(title)) {
                    stepOne.put(org.smartregister.chw.util.JsonFormUtils.TITLE, title);
                }
                JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    processPopulatableFields(client, jsonObject, jsonArray);

                }

                return form;
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return null;
    }

    @Override
    public void saveRegistration(final Pair<Client, Event> pair, final String jsonString, final boolean isEditMode, final ChildProfileContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                saveRegistration(pair, jsonString, isEditMode);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onRegistrationSaved(isEditMode);
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    private void processPopulatableFields(CommonPersonObjectClient client, JSONObject jsonObject, JSONArray jsonArray) throws JSONException {

        switch (jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).toLowerCase()) {
            case org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN:
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, false);
                JSONObject optionsObject = jsonObject.getJSONArray(org.smartregister.family.util.Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
                optionsObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN, false));

                break;
            case "age": {

                String dobString = org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
                dobString = org.smartregister.family.util.Utils.getDuration(dobString);
                dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : "0";
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Integer.valueOf(dobString));
            }
            break;
            case DBConstants.KEY.DOB:

                String dobString = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
                if (StringUtils.isNotBlank(dobString)) {
                    Date dob = Utils.dobStringToDate(dobString);
                    if (dob != null) {
                        jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, org.smartregister.chw.util.JsonFormUtils.dd_MM_yyyy.format(dob));
                    }
                }

                break;

            case org.smartregister.family.util.Constants.KEY.PHOTO:

                Photo photo = ImageUtils.profilePhotoByClientID(client.getCaseId(), Utils.getProfileImageResourceIDentifier());
                if (StringUtils.isNotBlank(photo.getFilePath())) {
                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, photo.getFilePath());
                }

                break;

            case DBConstants.KEY.UNIQUE_ID:

                String uniqueId = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false);
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, uniqueId.replace("-", ""));

                break;

            case org.smartregister.chw.util.Constants.JsonAssets.FAM_NAME:

                final String SAME_AS_FAM_NAME = "same_as_fam_name";
                final String SURNAME = "surname";

                String familyName = Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.FAMILY_FIRST_NAME, false);
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, familyName);

                String lastName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, false);

                JSONObject sameAsFamName = getFieldJSONObject(jsonArray, SAME_AS_FAM_NAME);
                JSONObject sameOptions = sameAsFamName.getJSONArray(org.smartregister.family.util.Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);

                if (familyName.equals(lastName)) {
                    sameOptions.put(org.smartregister.family.util.JsonFormUtils.VALUE, true);
                } else {
                    sameOptions.put(org.smartregister.family.util.JsonFormUtils.VALUE, false);
                }

                JSONObject surname = getFieldJSONObject(jsonArray, SURNAME);
                if (!familyName.equals(lastName)) {
                    surname.put(org.smartregister.family.util.JsonFormUtils.VALUE, lastName);
                } else {
                    surname.put(org.smartregister.family.util.JsonFormUtils.VALUE, "");
                }

                break;

            case DBConstants.KEY.GPS:

                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.GPS, false));

                break;

            default:
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY), false));

                //Log.e(TAG, "ERROR:: Unprocessed Form Object Key " + jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY));

                break;

        }
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

    }

    public enum VisitType {DUE, OVERDUE, LESS_TWENTY_FOUR, VISIT_THIS_MONTH, NOT_VISIT_THIS_MONTH, EXPIRY}

    public enum ServiceType {DUE, OVERDUE, UPCOMING}

    public enum FamilyServiceType {DUE, OVERDUE, NOTHING}
}
