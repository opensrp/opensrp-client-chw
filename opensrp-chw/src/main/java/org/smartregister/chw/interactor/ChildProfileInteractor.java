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
import org.smartregister.chw.contract.ImmunizationContact;
import org.smartregister.chw.presenter.ImmunizationViewPresenter;
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
import org.smartregister.domain.Photo;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.ServiceWrapper;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

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
            Timber.e(Log.getStackTraceString(e));
        }
    }

    @Override
    public void updateVisitNotDone(final long value, final ChildProfileContract.InteractorCallBack callback) {

        updateHomeVisitAsEvent(value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        if (value == 0) {
                            callback.undoVisitNotDone();
                        } else {
                            callback.updateVisitNotDone();
                        }
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

    @Override
    public void refreshChildVisitBar(Context context, String baseEntityId, final ChildProfileContract.InteractorCallBack callback) {
        ChildHomeVisit childHomeVisit = ChildUtils.getLastHomeVisit(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD, baseEntityId);

        String dobString = Utils.getDuration(Utils.getValue(pClient.getColumnmaps(), DBConstants.KEY.DOB, false));

        final ChildVisit childVisit = ChildUtils.getChildVisitStatus(context, dobString, childHomeVisit.getLastHomeVisitDate(), childHomeVisit.getVisitNotDoneDate(), childHomeVisit.getDateCreated());

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
    public void refreshUpcomingServiceAndFamilyDue(Context context, String familyId, String baseEntityId, final ChildProfileContract.InteractorCallBack callback) {
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

        FamilyInteractor.updateFamilyDueStatus(context, baseEntityId, familyId)
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

    @Override
    public void processBackGroundEvent(final ChildProfileContract.InteractorCallBack callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ChildUtils.processClientProcessInBackground();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.updateAfterBackGroundProcessed();
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    private Observable<Object> updateHomeVisitAsEvent(final long value) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                final String homeVisitId = org.smartregister.chw.util.JsonFormUtils.generateRandomUUIDString();

                Map<String, JSONObject> fields = new HashMap<>();
                ChildUtils.updateHomeVisitAsEvent(getpClient().entityId(), Constants.EventType.CHILD_VISIT_NOT_DONE, Constants.TABLE_NAME.CHILD,
                        fields, ChildDBConstants.KEY.VISIT_NOT_DONE, value + "", homeVisitId);
                e.onNext("");
            }
        });
    }

    private Observable<ChildService> updateUpcomingServices() {
        return Observable.create(new ObservableOnSubscribe<ChildService>() {
            @Override
            public void subscribe(final ObservableEmitter<ChildService> e) throws Exception {
                final ImmunizationViewPresenter presenter = new ImmunizationViewPresenter();
                presenter.upcomingServiceFetch(getpClient(), new ImmunizationContact.InteractorCallBack() {

                    @Override
                    public void updateData(ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetails, Map<String, Date> vaccines) {
                        String dueDate = "", vaccineName = "";
                        vaccineList = vaccines;
                        ImmunizationState state = ImmunizationState.UPCOMING;
                        for (HomeVisitVaccineGroup homeVisitVaccineGroupDetail : homeVisitVaccineGroupDetails) {
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

                                @Override
                                public void allDataLoaded() {

                                }
                            });
                        }

                    }

                    @Override
                    public void updateEditData(ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetails) {

                        //TODO
                        Timber.v("updateEditData");
                    }
                });


            }
        });
    }

    @Override
    public void updateChildCommonPerson(String baseEntityId) {
        String query = ChildUtils.mainSelect(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD, org.smartregister.chw.util.Constants.TABLE_NAME.FAMILY, org.smartregister.chw.util.Constants.TABLE_NAME.FAMILY_MEMBER, baseEntityId);

        Cursor cursor = null;
        try {
            cursor = getCommonRepository(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD).rawCustomQueryForAdapter(query);
            if (cursor != null && cursor.moveToFirst()) {
                CommonPersonObject personObject = getCommonRepository(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD).readAllcommonforCursorAdapter(cursor);
                pClient = new CommonPersonObjectClient(personObject.getCaseId(),
                        personObject.getDetails(), "");
                pClient.setColumnmaps(personObject.getColumnmaps());
            }
        } catch (Exception ex) {
            Timber.e(ex.toString());
        } finally {
            if (cursor != null)
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

                Cursor cursor = null;
                try {
                    cursor = getCommonRepository(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD).rawCustomQueryForAdapter(query);
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
                    }
                } catch (Exception ex) {
                    Timber.e(ex.toString());
                } finally {
                    if (cursor != null)
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
                form.put(JsonFormUtils.ENTITY_ID, client.getCaseId());
                form.put(JsonFormUtils.ENCOUNTER_TYPE, Constants.EventType.UPDATE_CHILD_REGISTRATION);

                JSONObject metadata = form.getJSONObject(JsonFormUtils.METADATA);
                String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());

                metadata.put(JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);

                form.put(JsonFormUtils.CURRENT_OPENSRP_ID, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false));

                JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);

                if (StringUtils.isNotBlank(title)) {
                    stepOne.put(org.smartregister.chw.util.JsonFormUtils.TITLE, title);
                }
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
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

        switch (jsonObject.getString(JsonFormUtils.KEY).toLowerCase()) {
            case org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN:
                jsonObject.put(JsonFormUtils.READ_ONLY, false);
                JSONObject optionsObject = jsonObject.getJSONArray(org.smartregister.family.util.Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
                optionsObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN, false));

                break;
            case "age": {

                String dobString = org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
                dobString = org.smartregister.family.util.Utils.getDuration(dobString);
                dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : "0";
                jsonObject.put(JsonFormUtils.VALUE, Integer.valueOf(dobString));
            }
            break;
            case DBConstants.KEY.DOB:

                String dobString = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
                if (StringUtils.isNotBlank(dobString)) {
                    Date dob = Utils.dobStringToDate(dobString);
                    if (dob != null) {
                        jsonObject.put(JsonFormUtils.VALUE, org.smartregister.chw.util.JsonFormUtils.dd_MM_yyyy.format(dob));
                    }
                }

                break;

            case org.smartregister.family.util.Constants.KEY.PHOTO:

                Photo photo = ImageUtils.profilePhotoByClientID(client.getCaseId(), Utils.getProfileImageResourceIDentifier());
                if (StringUtils.isNotBlank(photo.getFilePath())) {
                    jsonObject.put(JsonFormUtils.VALUE, photo.getFilePath());
                }

                break;

            case DBConstants.KEY.UNIQUE_ID:

                String uniqueId = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false);
                jsonObject.put(JsonFormUtils.VALUE, uniqueId.replace("-", ""));

                break;

            case org.smartregister.chw.util.Constants.JsonAssets.FAM_NAME:

                final String SAME_AS_FAM_NAME = "same_as_fam_name";
                final String SURNAME = "surname";

                String familyName = Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.FAMILY_FIRST_NAME, false);
                jsonObject.put(JsonFormUtils.VALUE, familyName);

                String lastName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, false);

                JSONObject sameAsFamName = getFieldJSONObject(jsonArray, SAME_AS_FAM_NAME);
                JSONObject sameOptions = sameAsFamName.getJSONArray(org.smartregister.family.util.Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);

                if (familyName.equals(lastName)) {
                    sameOptions.put(JsonFormUtils.VALUE, true);
                } else {
                    sameOptions.put(JsonFormUtils.VALUE, false);
                }

                JSONObject surname = getFieldJSONObject(jsonArray, SURNAME);
                if (!familyName.equals(lastName)) {
                    surname.put(JsonFormUtils.VALUE, lastName);
                } else {
                    surname.put(JsonFormUtils.VALUE, "");
                }

                break;

            case Constants.JsonAssets.INSURANCE_PROVIDER:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.INSURANCE_PROVIDER, false));
                break;
            case Constants.JsonAssets.INSURANCE_PROVIDER_NUMBER:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.INSURANCE_PROVIDER_NUMBER, false));
                break;
            case Constants.JsonAssets.INSURANCE_PROVIDER_OTHER:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.INSURANCE_PROVIDER_OTHER, false));
                break;
            case Constants.JsonAssets.DISABILITIES:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.CHILD_PHYSICAL_CHANGE, false));
                break;
            case Constants.JsonAssets.DISABILITY_TYPE:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.TYPE_OF_DISABILITY, false));
                break;

            case Constants.JsonAssets.BIRTH_CERT_AVAILABLE:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.BIRTH_CERT, false));
                break;
            case Constants.JsonAssets.BIRTH_REGIST_NUMBER:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.BIRTH_CERT_NUMBER, false));
                break;
            case Constants.JsonAssets.RHC_CARD:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.RHC_CARD, false));
                break;
            case Constants.JsonAssets.NUTRITION_STATUS:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.NUTRITION_STATUS, false));
                break;

            case DBConstants.KEY.GPS:

                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.GPS, false));

                break;

            default:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), jsonObject.getString(JsonFormUtils.KEY), false));

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
