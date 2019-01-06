package org.smartgresiter.wcaro.task;

import android.database.Cursor;
import android.os.AsyncTask;

import org.joda.time.DateTime;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.listener.ImmunizationStateChangeListener;
import org.smartgresiter.wcaro.util.ImmunizationState;
import org.smartregister.domain.Alert;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.util.VaccinateActionUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.smartgresiter.wcaro.util.Constants.IMMUNIZATION_CONSTANT.DATE;
import static org.smartgresiter.wcaro.util.Constants.IMMUNIZATION_CONSTANT.VACCINE;
import static org.smartregister.immunization.util.VaccinatorUtils.generateScheduleList;
import static org.smartregister.immunization.util.VaccinatorUtils.nextVaccineDue;
import static org.smartregister.immunization.util.VaccinatorUtils.receivedVaccines;

public class VaccinationAsyncTask extends AsyncTask {
    private List<Vaccine> vaccines = new ArrayList<>();
    private List<Alert> alerts = new ArrayList<>();
    private String entityId;
    private String stateKey = "";
    private ImmunizationState state;
    private Map<String, Object> nv;
    private ImmunizationStateChangeListener immunizationStateChangeListener;
    private Map<String,String> getColumnMaps;

    public VaccinationAsyncTask(String entityId,Map<String,String> getColumnMaps, ImmunizationStateChangeListener immunizationStateChangeListener) {
        this.entityId=entityId;
        this.getColumnMaps=getColumnMaps;
        this.immunizationStateChangeListener=immunizationStateChangeListener;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        alerts = WcaroApplication.getInstance().getContext().alertService().findByEntityIdAndAlertNames(entityId, VaccinateActionUtils.allAlertNames("child"));
        vaccines = WcaroApplication.getInstance().vaccineRepository().findByEntityId(entityId);
        Map<String, Date> recievedVaccines = receivedVaccines(vaccines);

        List<Map<String, Object>> sch = generateScheduleList("child",
                new DateTime(org.smartregister.family.util.Utils.getValue(getColumnMaps, DBConstants.KEY.DOB, false)), recievedVaccines, alerts);

        List<VaccineRepo.Vaccine> vList = Arrays.asList(VaccineRepo.Vaccine.values());
        nv = nextVaccineDue(sch, vList);
        if (nv != null) {
            DateTime dueDate = (DateTime) nv.get(DATE);
            VaccineRepo.Vaccine vaccine = (VaccineRepo.Vaccine) nv.get(VACCINE);
            stateKey = VaccinateActionUtils.stateKey(vaccine);
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




        return null;
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        immunizationStateChangeListener.onImmunicationStateChange(vaccines,stateKey,nv,state);
        //ImmunizationState(vaccines,stateKey,nv,state);

    }


}
