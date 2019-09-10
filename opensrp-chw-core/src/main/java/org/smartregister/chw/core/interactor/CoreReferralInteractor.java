package org.smartregister.chw.core.interactor;

import android.database.Cursor;

import org.smartregister.chw.core.contract.BaseReferralRegisterFragmentContract;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;

import timber.log.Timber;

public class CoreReferralInteractor implements BaseReferralRegisterFragmentContract.Interactor {
    private AppExecutors appExecutors;
    private CommonPersonObjectClient pClient;

    public CoreReferralInteractor() {
        this(new AppExecutors());
    }

    public CoreReferralInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public void getClientDetails(String baseEntityId, BaseReferralRegisterFragmentContract.InteractorCallBack callback, String taskFocus) {
        Runnable runnable = () -> {
            String query = CoreReferralUtils.mainSelect(CoreConstants.TABLE_NAME.FAMILY_MEMBER, CoreConstants.TABLE_NAME.FAMILY, baseEntityId);

            try (Cursor cursor = CoreReferralUtils.getCommonRepository(CoreConstants.TABLE_NAME.FAMILY_MEMBER).rawCustomQueryForAdapter(query)) {
                if (cursor != null && cursor.moveToFirst()) {
                    CommonPersonObject personObject = CoreReferralUtils.getCommonRepository(CoreConstants.TABLE_NAME.FAMILY_MEMBER).readAllcommonforCursorAdapter(cursor);
                    pClient = new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");
                    pClient.setColumnmaps(personObject.getColumnmaps());

                    final String caregiverId = Utils.getValue(pClient.getColumnmaps(), ChildDBConstants.KEY.PRIMARY_CAREGIVER, false);
                    fetchCareGiverDetails(caregiverId);

                    if (CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS.equals(taskFocus)) {
                        final String familyAncMemberId = Utils.getValue(pClient.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false);
                        fetchAncDetails(familyAncMemberId);
                    }

                    appExecutors.mainThread().execute(() -> callback.clientDetails(pClient));
                }
            } catch (Exception e) {
                Timber.e(e, "CoreReferralInteractor --> getClientDetails");
            }

        };

        appExecutors.diskIO().execute(runnable);
    }


    private void fetchCareGiverDetails(String careGiverId) {
        String query = CoreReferralUtils.mainCareGiverSelect(CoreConstants.TABLE_NAME.FAMILY_MEMBER, careGiverId);
        Timber.d("The caregiver query %s", query);
        try (Cursor cursor = CoreReferralUtils.getCommonRepository(CoreConstants.TABLE_NAME.FAMILY_MEMBER).rawCustomQueryForAdapter(query)) {
            if (cursor != null && cursor.moveToFirst()) {
                CommonPersonObject personObject = CoreReferralUtils.getCommonRepository(CoreConstants.TABLE_NAME.FAMILY_MEMBER).readAllcommonforCursorAdapter(cursor);
                pClient.getColumnmaps().putAll(personObject.getColumnmaps());
            }
        } catch (Exception e) {
            Timber.e(e, "CoreReferralInteractor --> fetchCareGiverDetails");
        }
    }

    private void fetchAncDetails(String familyMemberId) {
        String query = CoreReferralUtils.mainAncDetailsSelect(CoreConstants.TABLE_NAME.ANC_MEMBER, familyMemberId);
        Timber.d("The anc member query %s", query);
        try (Cursor cursor = CoreReferralUtils.getCommonRepository(CoreConstants.TABLE_NAME.ANC_MEMBER).rawCustomQueryForAdapter(query)) {
            if (cursor != null && cursor.moveToFirst()) {
                CommonPersonObject personObject = CoreReferralUtils.getCommonRepository(CoreConstants.TABLE_NAME.ANC_MEMBER).readAllcommonforCursorAdapter(cursor);
                pClient.getColumnmaps().putAll(personObject.getColumnmaps());
            }
        } catch (Exception e) {
            Timber.e(e, "CoreReferralInteractor --> fetchAncDetails");
        }
    }
}
