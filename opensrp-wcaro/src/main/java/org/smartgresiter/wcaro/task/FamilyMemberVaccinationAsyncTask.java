package org.smartgresiter.wcaro.task;

import android.database.Cursor;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.joda.time.DateTime;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.interactor.ChildProfileInteractor;
import org.smartgresiter.wcaro.listener.FamilyMemberImmunizationListener;
import org.smartgresiter.wcaro.util.ChildDBConstants;
import org.smartgresiter.wcaro.util.ChildUtils;
import org.smartgresiter.wcaro.util.ChildVisit;
import org.smartgresiter.wcaro.util.ImmunizationState;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.ServiceSchedule;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.util.VaccinateActionUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.smartgresiter.wcaro.util.Constants.IMMUNIZATION_CONSTANT.DATE;
import static org.smartregister.immunization.util.VaccinatorUtils.generateScheduleList;
import static org.smartregister.immunization.util.VaccinatorUtils.nextVaccineDue;
import static org.smartregister.immunization.util.VaccinatorUtils.receivedVaccines;

public class FamilyMemberVaccinationAsyncTask extends AsyncTask {

    private String childId, familyId;
    private FamilyMemberImmunizationListener immunizationStateChangeListener;
    private Map<String, Date> vaccines = new HashMap<>();
    private ImmunizationState childServiceState;
    private Map<String, Object> nv;

    public FamilyMemberVaccinationAsyncTask(String childId, String familyId, FamilyMemberImmunizationListener immunizationStateChangeListener) {
        this.childId = childId;
        this.familyId = familyId;
        this.immunizationStateChangeListener = immunizationStateChangeListener;
    }
    //TODO need to performance improvement

    @Override
    protected Object doInBackground(Object[] objects) {
        Log.v("PROFILE_UPDATE", "doInBackground>>>FamilyMemberVaccinationAsyncTask");
        ImmunizationState state = null;
        ImmunizationState familyImmunizationState=ImmunizationState.NO_ALERT;
        String query = ChildUtils.getChildListByFamilyId(org.smartgresiter.wcaro.util.Constants.TABLE_NAME.CHILD, familyId, childId);
        Cursor cursor = Utils.context().commonrepository(org.smartgresiter.wcaro.util.Constants.TABLE_NAME.CHILD).queryTable(query);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                CommonPersonObject personObject = Utils.context().commonrepository(org.smartgresiter.wcaro.util.Constants.TABLE_NAME.CHILD).findByBaseEntityId(cursor.getString(1));
                CommonPersonObjectClient pClient = new CommonPersonObjectClient(personObject.getCaseId(),
                        personObject.getDetails(), "");
                // pClient.setColumnmaps(personObject.getColumnmaps());
                String dobString = org.smartregister.util.Utils.getValue(personObject.getColumnmaps(), DBConstants.KEY.DOB, false);
                String visitNotDoneStr=org.smartregister.util.Utils.getValue(personObject.getColumnmaps(), ChildDBConstants.KEY.VISIT_NOT_DONE, false);
                String lastHomeVisitStr=org.smartregister.util.Utils.getValue(personObject.getColumnmaps(), ChildDBConstants.KEY.LAST_HOME_VISIT, false);
                long lastHomeVisit=TextUtils.isEmpty(lastHomeVisitStr)?0:Long.parseLong(lastHomeVisitStr);
                long visitNotDone=TextUtils.isEmpty(visitNotDoneStr)?0:Long.parseLong(visitNotDoneStr);
                DateTime dob = org.smartgresiter.wcaro.util.Utils.dobStringToDateTime(dobString);
                if (dob == null) {
                    dob = new DateTime();
                }

                if (pClient.getCaseId().equalsIgnoreCase(childId)) {
                    if (!TextUtils.isEmpty(dobString)) {
                        DateTime dateTime = new DateTime(dobString);
                        VaccineSchedule.updateOfflineAlerts(childId, dateTime, "child");
                        ServiceSchedule.updateOfflineAlerts(childId, dateTime);
                    }
                }

                List<Alert> alerts = WcaroApplication.getInstance().getContext().alertService().findByEntityIdAndAlertNames(pClient.getCaseId(), VaccinateActionUtils.allAlertNames("child"));
                List<Vaccine> vaccines = WcaroApplication.getInstance().vaccineRepository().findByEntityId(pClient.getCaseId());
                Map<String, Date> recievedVaccines = receivedVaccines(vaccines);
                List<Map<String, Object>> sch = generateScheduleList("child",
                        dob, recievedVaccines, alerts);

                List<VaccineRepo.Vaccine> vList = Arrays.asList(VaccineRepo.Vaccine.values());
                Map<String, Object> nv = nextVaccineDue(sch, vList);

                if (nv != null) {
                    DateTime dueDate = (DateTime) nv.get(DATE);
                    String ALERT = "alert";
                    if (nv.get(ALERT) == null) {
                        state = ImmunizationState.NO_ALERT;
                    } else if (((Alert) nv.get(ALERT)).status().value().equalsIgnoreCase(ImmunizationState.NORMAL.name())) {
                        state = ImmunizationState.DUE;
                    } else if (((Alert) nv.get(ALERT)).status().value().equalsIgnoreCase(ImmunizationState.UPCOMING.name())) {
                        Calendar today = Calendar.getInstance();
                        today.set(Calendar.HOUR_OF_DAY, 0);
                        today.set(Calendar.MINUTE, 0);
                        today.set(Calendar.SECOND, 0);
                        today.set(Calendar.MILLISECOND, 0);

                        if (dueDate.getMillis() >= (today.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)) && dueDate.getMillis() < (today.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS))) {
                            state = ImmunizationState.UPCOMING_NEXT_7_DAYS;
                        } else {
                            state = ImmunizationState.UPCOMING;
                        }
                    } else if (((Alert) nv.get(ALERT)).status().value().equalsIgnoreCase(ImmunizationState.URGENT.name())) {
                        state = ImmunizationState.OVERDUE;
                    } else if (((Alert) nv.get(ALERT)).status().value().equalsIgnoreCase(ImmunizationState.EXPIRED.name())) {
                        state = ImmunizationState.EXPIRED;
                    }
                } else {
                    state = ImmunizationState.WAITING;
                }
                if (pClient.getCaseId().equalsIgnoreCase(childId)) {
                    this.vaccines = recievedVaccines;
                    this.nv = nv;
                    this.childServiceState = state;
                }else{
                    final ChildVisit childVisit = ChildUtils.getChildVisitStatus(dobString,lastHomeVisit,visitNotDone);
                    if(childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.OVERDUE.name())
                            || childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.DUE.name()))
                        if( familyImmunizationState!=null && !familyImmunizationState.equals(ImmunizationState.OVERDUE)){
                                familyImmunizationState=state;
                       }
                }

            } while (cursor.moveToNext());
            cursor.close();
        }


        return familyImmunizationState;
    }


    @Override
    protected void onPostExecute(Object o) {
        if (o instanceof ImmunizationState) {
            ImmunizationState state = (ImmunizationState) o;
            immunizationStateChangeListener.onFamilyMemberState(state);
        }
        immunizationStateChangeListener.onSelfStatus(vaccines, nv, childServiceState);

    }


}
