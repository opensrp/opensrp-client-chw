package org.smartgresiter.wcaro.interactor;

import android.support.annotation.VisibleForTesting;

import org.smartgresiter.wcaro.contract.FamilyCallDialogContract;
import org.smartgresiter.wcaro.model.FamilyCallDialogModel;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

public class FamilyCallDialogInteractor implements FamilyCallDialogContract.Interactor {

    private AppExecutors appExecutors;
    String familyBaseEntityId;


    @VisibleForTesting
    FamilyCallDialogInteractor(AppExecutors appExecutors, String familyBaseEntityId) {
        this.appExecutors = appExecutors;
        this.familyBaseEntityId = familyBaseEntityId;
    }

    public FamilyCallDialogInteractor(String familyBaseEntityId) {
        this(new AppExecutors(), familyBaseEntityId);
    }

    @Override
    public void getHeadOfFamily(final FamilyCallDialogContract.Presenter presenter) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final CommonPersonObject personObject = getCommonRepository(Utils.metadata().familyRegister.tableName).findByBaseEntityId(familyBaseEntityId);
                final CommonPersonObjectClient client = new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");
                client.setColumnmaps(personObject.getColumnmaps());

                String primaryCaregiverID = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.PRIMARY_CAREGIVER, false);
                String familyHeadID = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FAMILY_HEAD, false);

                if (primaryCaregiverID != null) {
                    // load primary care giver
                    final FamilyCallDialogModel headModel = prepareModel(familyHeadID, primaryCaregiverID, true);
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            presenter.updateHeadOfFamily((headModel == null || headModel.getPhoneNumber() == null) ? null : headModel);
                        }
                    });
                }

                if (familyHeadID != null && !familyHeadID.equals(primaryCaregiverID)) {
                    // load family head
                    final FamilyCallDialogModel careGiverModel = prepareModel(familyHeadID, primaryCaregiverID, false);
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            presenter.updateCareGiver((careGiverModel == null || careGiverModel.getPhoneNumber() == null) ? null : careGiverModel);
                        }
                    });
                }
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    private FamilyCallDialogModel prepareModel(
            String familyHeadID, String primaryCaregiverID,
            Boolean isHead
    ) {

        if (primaryCaregiverID.toLowerCase().equals(familyHeadID.toLowerCase()) && !isHead) {
            return null;
        }

        String baseID = (isHead) ? familyHeadID : primaryCaregiverID;


        final CommonPersonObject personObject = getCommonRepository(Utils.metadata().familyMemberRegister.tableName).findByBaseEntityId(baseID);
        final CommonPersonObjectClient client = new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");
        client.setColumnmaps(personObject.getColumnmaps());

        String phoneNumber = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.PHONE_NUMBER, false);
        String firstName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FIRST_NAME, false);
        String lastName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, false);
        String middleName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, false);

        FamilyCallDialogModel model = new FamilyCallDialogModel();
        model.setPhoneNumber(phoneNumber);
        model.setName(
                String.format("%s %s",
                        String.format("%s %s", firstName, middleName).trim(),
                        lastName
                )
        );

        model.setRole((primaryCaregiverID.toLowerCase().equals(familyHeadID.toLowerCase())) ? "Head of Family , Caregiver" : (isHead ? "Head of Family" : "Caregiver"));

        return model;
    }

    public CommonRepository getCommonRepository(String tableName) {
        return Utils.context().commonrepository(tableName);
    }

}
