package org.smartregister.chw.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.smartregister.chw.listener.UpdateServiceListener;
import org.smartregister.chw.model.RecurringServiceModel;
import org.smartregister.chw.util.RecurringServiceUtil;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Photo;
import org.smartregister.immunization.domain.ServiceType;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.immunization.util.ImageUtils;
import org.smartregister.util.Utils;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.util.Utils.getName;
import static org.smartregister.util.Utils.getValue;

public class UpdateServiceTask extends AsyncTask<Void, Void, RecurringServiceModel> {
    private CommonPersonObjectClient childDetails;
    private Map<String, ServiceWrapper> displayServiceWrapper;
    private UpdateServiceListener listener;

    public UpdateServiceTask(CommonPersonObjectClient childDetails, UpdateServiceListener listener) {
        this.childDetails = childDetails;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        displayServiceWrapper = new LinkedHashMap<>();
    }

    @Override
    protected void onPostExecute(RecurringServiceModel map) {

        Map<String, List<ServiceType>> foundServiceTypeMap = RecurringServiceUtil.getServiceGroup(map);

        if (foundServiceTypeMap.isEmpty()) {
            listener.onUpdateServiceList(displayServiceWrapper);
            return;
        }

        for (String type : foundServiceTypeMap.keySet()) {
            ServiceWrapper serviceWrapper = new ServiceWrapper();
            serviceWrapper.setId(childDetails.entityId());
            serviceWrapper.setGender(childDetails.getDetails().get("gender"));
            serviceWrapper.setDefaultName(type);

            String dobString = getValue(childDetails.getColumnmaps(), "dob", false);
            if (StringUtils.isNotBlank(dobString)) {
                Calendar dobCalender = Calendar.getInstance();
                DateTime dateTime = new DateTime(dobString);
                dobCalender.setTime(dateTime.toDate());
                serviceWrapper.setDob(new DateTime(dobCalender.getTime()));
            }

            Photo photo = ImageUtils.profilePhotoByClient(childDetails);
            serviceWrapper.setPhoto(photo);

            String zeirId = getValue(childDetails.getColumnmaps(), "zeir_id", false);
            serviceWrapper.setPatientNumber(zeirId);

            String firstName = getValue(childDetails.getColumnmaps(), "first_name", true);
            String lastName = getValue(childDetails.getColumnmaps(), "last_name", true);
            String childName = getName(firstName, lastName);
            serviceWrapper.setPatientName(childName.trim());

            DateTime dob = new DateTime(dobString);
            RecurringServiceUtil.updateWrapperStatus(map.getServiceRecords(), map.getAlerts(), serviceWrapper, dob, foundServiceTypeMap.get(type));
            RecurringServiceUtil.updateWrapper(serviceWrapper, map.getServiceRecords());
            displayServiceWrapper.put(type, serviceWrapper);
        }
        Timber.v("Service_wrapper %s", displayServiceWrapper);
        listener.onUpdateServiceList(displayServiceWrapper);
    }

    @Override
    protected RecurringServiceModel doInBackground(Void... voids) {
        String dobString = Utils.getValue(childDetails.getColumnmaps(), "dob", false);
        DateTime dateOfBirth = null;
        if (!TextUtils.isEmpty(dobString)) {
            dateOfBirth = new DateTime(dobString);
        }

        return RecurringServiceUtil.getServiceModel(childDetails.entityId(), dateOfBirth, "child");
    }

}


