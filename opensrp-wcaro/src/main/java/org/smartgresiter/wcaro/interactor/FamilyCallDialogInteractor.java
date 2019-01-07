package org.smartgresiter.wcaro.interactor;

import android.database.Cursor;
import android.support.annotation.VisibleForTesting;

import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.contract.FamilyCallDialogContract;
import org.smartgresiter.wcaro.model.FamilyCallDialogModel;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
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
                //TODO  replace this with actual query info for the HOF



                // Select phone_number , is_family_head , is_primary_caregiver , first_name , last_name from ec_family_member where family_id = ? and is_family_head = 1;

                SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
                sqb.SelectInitiateMainTable(Constants.TABLE_NAME.FAMILY_MEMBER, mainColumns(Constants.TABLE_NAME.FAMILY_MEMBER));
                sqb.mainCondition(String.format(" %s = '%s'" , DBConstants.KEY.RELATIONAL_ID , familyBaseEntityId));
                // sqb.addCondition("is_family_head = 1"); replace with db logic

                Cursor cursor = getCommonRepository(org.smartgresiter.wcaro.util.Constants.TABLE_NAME.CHILD).rawCustomQueryForAdapter(sqb.getSelectquery());

                if(cursor!=null && cursor.moveToFirst()){

                    final FamilyCallDialogModel model = new FamilyCallDialogModel();
                    model.setPhoneNumber("+2547112233");
                    model.setName("ReplaceWith RealName");
                    model.setRole("Head of Family , Caregiver");
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            presenter.updateHeadOfFamily((model == null || model.getPhoneNumber() == null) ? null : model);
                        }
                    });
                }
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getCareGiver(final FamilyCallDialogContract.Presenter presenter) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //TODO  replace this with actual query info for the HOF

                final FamilyCallDialogModel model = null;
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        presenter.updateCareGiver((model == null || model.getPhoneNumber() == null) ? null : model);
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    public CommonRepository getCommonRepository(String tableName) {
        return Utils.context().commonrepository(tableName);
    }

    private static String[] mainColumns(String tableName) {

        String[] columns = new String[]{
                tableName + "." + DBConstants.KEY.RELATIONAL_ID +" as " + DBConstants.KEY.RELATIONAL_ID,
                tableName + "." + DBConstants.KEY.BASE_ENTITY_ID,
                tableName + "." + DBConstants.KEY.FIRST_NAME,
                tableName + "." + DBConstants.KEY.LAST_NAME
        };
        return columns;
    }
}
