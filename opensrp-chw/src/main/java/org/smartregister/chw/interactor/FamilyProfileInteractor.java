package org.smartregister.chw.interactor;

import android.support.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.contract.FamilyProfileExtendedContract;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.util.Map;

public class FamilyProfileInteractor extends org.smartregister.family.interactor.FamilyProfileInteractor {

    private AppExecutors appExecutors;

    @VisibleForTesting
    FamilyProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public FamilyProfileInteractor() {
        this(new AppExecutors());
    }

    public void verifyHasPhone(final String familyID, final FamilyProfileExtendedContract.PresenterCallBack profilePresenter) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final boolean hasPhone = hasPhone(familyID);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        profilePresenter.notifyHasPhone(hasPhone);
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    private boolean hasPhone(String familyBaseEntityId) {

        final CommonPersonObject personObject = getCommonRepository(Utils.metadata().familyRegister.tableName).findByBaseEntityId(familyBaseEntityId);
        final CommonPersonObjectClient client = new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");
        client.setColumnmaps(personObject.getColumnmaps());

        String primaryCaregiverID = getValue(client.getColumnmaps(), DBConstants.KEY.PRIMARY_CAREGIVER);
        String familyHeadID = getValue(client.getColumnmaps(), DBConstants.KEY.FAMILY_HEAD);

        return hasNumber(primaryCaregiverID) || hasNumber(familyHeadID);
    }

    private boolean hasNumber(String baseID) {
        final CommonPersonObject personObject = getCommonRepository(Utils.metadata().familyMemberRegister.tableName).findByBaseEntityId(baseID);
        try {
            final CommonPersonObjectClient client = new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");
            client.setColumnmaps(personObject.getColumnmaps());
            client.setColumnmaps(personObject.getColumnmaps());
            return StringUtils.isNotBlank(Utils.getValue(client.getColumnmaps(), DBConstants.KEY.PHONE_NUMBER, false));
        } catch (Exception e) {
            return false;
        }
    }

    public String getValue(Map<String, String> map, String field) {
        return Utils.getValue(map, field, false);
    }

}
