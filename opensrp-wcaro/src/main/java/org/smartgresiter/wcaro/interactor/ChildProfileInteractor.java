package org.smartgresiter.wcaro.interactor;

import android.database.Cursor;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.contract.ChildProfileContract;
import org.smartgresiter.wcaro.listener.FamilyMemberImmunizationListener;
import org.smartgresiter.wcaro.listener.ImmunizationStateChangeListener;
import org.smartgresiter.wcaro.task.FamilyMemberVaccinationAsyncTask;
import org.smartgresiter.wcaro.task.VaccinationAsyncTask;
import org.smartgresiter.wcaro.util.ChildDBConstants;
import org.smartgresiter.wcaro.util.ChildService;
import org.smartgresiter.wcaro.util.ChildUtils;
import org.smartgresiter.wcaro.util.ChildVisit;
import org.smartgresiter.wcaro.util.ImmunizationState;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.UniqueId;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.DateUtil;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.smartgresiter.wcaro.util.Constants.IMMUNIZATION_CONSTANT.DATE;
import static org.smartgresiter.wcaro.util.Constants.IMMUNIZATION_CONSTANT.VACCINE;
import static org.smartregister.util.Utils.startAsyncTask;

public class ChildProfileInteractor implements ChildProfileContract.Interactor {
    public static final String TAG = ChildProfileInteractor.class.getName();
    private AppExecutors appExecutors;
    private CommonPersonObjectClient pClient;
    private String familyId;
    private FamilyMemberVaccinationAsyncTask familyMemberVaccinationAsyncTask;
    private Map<String, Date> vaccineList;
    private String serviceDueStatus=FamilyServiceType.NOTHING.name();
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
    public String getFamilyId(){
        return familyId;
    }
    public Map<String, Date> getVaccineList(){
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
                if (baseClient != null) {
                    String newOpenSRPId = baseClient.getIdentifier(DBConstants.KEY.UNIQUE_ID).replace("-", "");
                    String currentOpenSRPId = JsonFormUtils.getString(jsonString, JsonFormUtils.CURRENT_OPENSRP_ID).replace("-", "");
                    if (!newOpenSRPId.equals(currentOpenSRPId)) {
                        //OPENSRP ID was changed
                        getUniqueIdRepository().open(currentOpenSRPId);
                    }
                }

            } else {
                if (baseClient != null) {
                    String opensrpId = baseClient.getIdentifier(DBConstants.KEY.UNIQUE_ID);

                    //mark OPENSRP ID as used
                    getUniqueIdRepository().close(opensrpId);
                }
            }

            if (baseClient != null || baseEvent != null) {
                String imageLocation = JsonFormUtils.getFieldValue(jsonString, Constants.KEY.PHOTO);
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
        ChildUtils.updateClientStatusAsEvent(getpClient().entityId(),"Visit not done","visit_not_done",""+value,"ec_child");

    }

    @Override
    public void refreshChildVisitBar(String baseEntityId, final ChildProfileContract.InteractorCallBack callback) {
        String query=ChildUtils.getLastHomeVisit(org.smartgresiter.wcaro.util.Constants.TABLE_NAME.CHILD,baseEntityId);
        long lastVisit=0;
        long visitNot=0;
        Cursor cursor=getCommonRepository(org.smartgresiter.wcaro.util.Constants.TABLE_NAME.CHILD).queryTable(query);
        if(cursor!=null && cursor.moveToFirst()){
            String lastVisitStr=cursor.getString(1);
            if(!TextUtils.isEmpty(lastVisitStr)) {
                try {
                    lastVisit = Long.parseLong(lastVisitStr);
                } catch (Exception e) {

                }
            }
            String visitNotDoneStr=cursor.getString(2);
            if(!TextUtils.isEmpty(visitNotDoneStr)) {
                try {
                    visitNot = Long.parseLong(visitNotDoneStr);
                } catch (Exception e) {

                }
            }
            cursor.close();
        }
        final   ChildVisit childVisit=ChildUtils.getChildVisitStatus(lastVisit,visitNot);

       Runnable runnable=new Runnable() {
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
    public void refreshFamilyMemberServiceDue(String familyId,String baseEntityId,final ChildProfileContract.InteractorCallBack callback) {
        if(getpClient()==null) return;
        Log.v("PROFILE_UPDATE","refreshFamilyMemberServiceDue");
//        if(familyMemberVaccinationAsyncTask!=null && !familyMemberVaccinationAsyncTask.isCancelled()){
//            familyMemberVaccinationAsyncTask.cancel(true);
//        }
        familyMemberVaccinationAsyncTask=new FamilyMemberVaccinationAsyncTask(baseEntityId, familyId,new FamilyMemberImmunizationListener() {
            @Override
            public void onFamilyMemberState(ImmunizationState state) {

                if(state.equals(ImmunizationState.DUE)) {
                    serviceDueStatus=FamilyServiceType.DUE.name();
                }else if(state.equals(ImmunizationState.OVERDUE)){
                    serviceDueStatus=FamilyServiceType.OVERDUE.name();
                }else {
                    serviceDueStatus=FamilyServiceType.NOTHING.name();
                }
                Runnable runnable=new Runnable() {
                    @Override
                    public void run() {
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.updateFamilyMemberServiceDue(serviceDueStatus);
                            }
                        });
                    }
                };
                appExecutors.diskIO().execute(runnable);

            }

            @Override
            public void onSelfStatus(Map<String, Date> vaccines, Map<String, Object> nv, ImmunizationState state) {
                vaccineList=vaccines;
                VaccineRepo.Vaccine vaccine = (VaccineRepo.Vaccine) nv.get(VACCINE);
                final ChildService childService=new ChildService();
                childService.setServiceName(vaccine.display());
                DateTime dueDate = (DateTime) nv.get(DATE);
                String duedateString = DateUtil.formatDate(dueDate.toLocalDate(),"dd MMM yyyy");
                childService.setServiceDate(duedateString);
                if(state.equals(ImmunizationState.DUE)) {
                    childService.setServiceStatus(ServiceType.DUE.name());
                }else if(state.equals(ImmunizationState.OVERDUE)){
                    childService.setServiceStatus(ServiceType.OVERDUE.name());
                }else {
                    childService.setServiceStatus(ServiceType.UPCOMING.name());
                }
                Runnable runnable=new Runnable() {
                    @Override
                    public void run() {
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.updateChildService(childService);
                            }
                        });
                    }
                };
                appExecutors.diskIO().execute(runnable);
            }


        });

        startAsyncTask(familyMemberVaccinationAsyncTask,null);

    }

    @Override
    public void refreshProfileView(final String baseEntityId, final boolean isForEdit, final ChildProfileContract.InteractorCallBack callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String query=ChildUtils.mainSelect(org.smartgresiter.wcaro.util.Constants.TABLE_NAME.CHILD,org.smartgresiter.wcaro.util.Constants.TABLE_NAME.FAMILY,org.smartgresiter.wcaro.util.Constants.TABLE_NAME.FAMILY_MEMBER,baseEntityId);

                Cursor cursor=getCommonRepository(org.smartgresiter.wcaro.util.Constants.TABLE_NAME.CHILD).rawCustomQueryForAdapter(query);
                if(cursor!=null && cursor.moveToFirst()){
                    CommonPersonObject personObject = getCommonRepository(org.smartgresiter.wcaro.util.Constants.TABLE_NAME.CHILD).readAllcommonforCursorAdapter(cursor);
                    pClient = new CommonPersonObjectClient(personObject.getCaseId(),
                        personObject.getDetails(), "");
                        pClient.setColumnmaps(personObject.getColumnmaps());
                    familyId= Utils.getValue(pClient.getColumnmaps(), ChildDBConstants.KEY.RELATIONAL_ID, false);
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (isForEdit) {
                                callback.startFormForEdit(pClient);
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
    public void getNextUniqueId(final Triple<String, String, String> triple,final ChildProfileContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                UniqueId uniqueId = getUniqueIdRepository().getNextUniqueId();
                final String entityId = uniqueId != null ? uniqueId.getOpenmrsId() : "";
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (StringUtils.isBlank(entityId)) {
                            callBack.onNoUniqueId();
                        } else {
                            callBack.onUniqueIdFetched(triple, entityId);
                        }
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveRegistration(final Pair<Client, Event> pair,final String jsonString,final boolean isEditMode,final ChildProfileContract.InteractorCallBack callBack) {
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

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

    }
    public enum VisitType {DUE, OVERDUE,LESS_TWENTY_FOUR, VISIT_THIS_MONTH,NOT_VISIT_THIS_MONTH}
    public enum ServiceType {DUE, OVERDUE, UPCOMING}
    public enum FamilyServiceType {DUE, OVERDUE, NOTHING}
}
