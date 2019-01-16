package org.smartgresiter.wcaro.task;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;

import org.joda.time.DateTime;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.service.AlertService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raihan on 1/16/19.
 */
public class UndoVaccineTask extends AsyncTask<Void, Void, Void> {

    private ArrayList<VaccineWrapper> tag;
    private final VaccineRepository vaccineRepository;
    private final AlertService alertService;
    private List<Vaccine> vaccineList;
    private List<Alert> alertList;
    private List<String> affectedVaccines;
    private CommonPersonObjectClient childDetails;

    public UndoVaccineTask(ArrayList<VaccineWrapper> tag,CommonPersonObjectClient childDetails) {
        this.childDetails = childDetails;
        this.tag = tag;
        vaccineRepository = ImmunizationLibrary.getInstance().vaccineRepository();
        alertService = ImmunizationLibrary.getInstance().context().alertService();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        for(VaccineWrapper tag : this.tag) {
            if (tag != null) {

                if (tag.getDbKey() != null) {
                    Long dbKey = tag.getDbKey();
                    vaccineRepository.deleteVaccine(dbKey);
                    String dobString = org.smartregister.util.Utils.getValue(childDetails.getColumnmaps(), "dob", false);
                    if (!TextUtils.isEmpty(dobString)) {
                        DateTime dateTime = new DateTime(dobString);
                        affectedVaccines = VaccineSchedule.updateOfflineAlerts(childDetails.entityId(), dateTime, "child");
                        vaccineList = vaccineRepository.findByEntityId(childDetails.entityId());
                        alertList = alertService.findByEntityIdAndAlertNames(childDetails.entityId(),
                                VaccinateActionUtils.allAlertNames("child"));
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void params) {
        super.onPostExecute(params);

    }
}