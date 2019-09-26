package org.smartregister.chw.hf.fragment;

import android.database.Cursor;
import android.os.Handler;
import android.view.View;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.fragment.BaseReferralRegisterFragment;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.chw.hf.HealthFacilityApplication;
import org.smartregister.chw.hf.activity.ReferralTaskViewActivity;
import org.smartregister.chw.hf.presenter.ReferralFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.Task;
import org.smartregister.family.util.DBConstants;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class ReferralRegisterFragment extends BaseReferralRegisterFragment {
    public Handler handler = new Handler();
    private ReferralFragmentPresenter referralFragmentPresenter;
    private CommonPersonObjectClient commonPersonObjectClient;

    @Override
    public void setClient(CommonPersonObjectClient commonPersonObjectClient) {
        setCommonPersonObjectClient(commonPersonObjectClient);
    }

    @Override
    protected String getMainCondition() {
        return "task.status = '" + Task.TaskStatus.READY.name() + "' and  ec_family_member_search.date_removed is null";
    }

    @Override
    public CommonPersonObjectClient getCommonPersonObjectClient() {
        return commonPersonObjectClient;
    }

    @Override
    public void setCommonPersonObjectClient(CommonPersonObjectClient commonPersonObjectClient) {
        this.commonPersonObjectClient = commonPersonObjectClient;
    }

    @Override
    protected void initializePresenter() {
        referralFragmentPresenter = new ReferralFragmentPresenter(this);
        presenter = referralFragmentPresenter;

    }

    @Override
    protected void onViewClicked(View view) {
        CommonPersonObjectClient client = (CommonPersonObjectClient) view.getTag();
        referralFragmentPresenter.setBaseEntityId(Utils.getValue(client.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false));
        referralFragmentPresenter.fetchClient();

        Task task = getTask(Utils.getValue(client.getColumnmaps(), "_id", false));
        referralFragmentPresenter.setTasksFocus(task.getFocus());
        if (CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS.equals(task.getFocus()) ||
                CoreConstants.TASKS_FOCUS.PNC_DANGER_SIGNS.equals(task.getFocus())) {
            goToPncOrAncReferralsDetails(client);
        } else {
            goToReferralsDetails(client);
        }
    }

    private Task getTask(String taskId) {
        return HealthFacilityApplication.getInstance().getTaskRepository().getTaskByIdentifier(taskId);
    }

    private void goToPncOrAncReferralsDetails(CommonPersonObjectClient client) {
        HashMap<String, String> detailsMap = CoreChwApplication.ancRegisterRepository().getFamilyNameAndPhone(Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FAMILY_HEAD, false));

        String familyName = "";
        String familyHeadPhone = "";
        if (detailsMap != null) {
            familyName = detailsMap.get(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_NAME);
            familyHeadPhone = detailsMap.get(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_PHONE);
        }
        String finalFamilyHeadPhone = familyHeadPhone;
        String finalFamilyName = familyName;


        handler.postDelayed(() -> {
            String baseEntityId = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false);
            getCommonPersonObjectClient().getColumnmaps().putAll(fetchAncDetails(baseEntityId));
            ReferralTaskViewActivity.startReferralTaskViewActivity(getActivity(), new MemberObject(getCommonPersonObjectClient()), finalFamilyName, finalFamilyHeadPhone, getCommonPersonObjectClient(), getTask(Utils.getValue(client.getColumnmaps(), "_id", false)), CoreConstants.REGISTERED_ACTIVITIES.REFERRALS_REGISTER_ACTIVITY);
        }, 100);
    }


    private void goToReferralsDetails(CommonPersonObjectClient client) {
        handler.postDelayed(() -> ReferralTaskViewActivity.startReferralTaskViewActivity(getActivity(), getCommonPersonObjectClient(), getTask(Utils.getValue(client.getColumnmaps(), "_id", false)), CoreConstants.REGISTERED_ACTIVITIES.REFERRALS_REGISTER_ACTIVITY), 100);
    }

    private Map<String, String> fetchAncDetails(String baseEntityId) {
        Map<String, String> details = new HashMap<>();
        String query = CoreReferralUtils.mainAncDetailsSelect(CoreConstants.TABLE_NAME.ANC_MEMBER, baseEntityId);
        Timber.d("The anc member query %s", query);
        try (Cursor cursor = getCommonRepository(CoreConstants.TABLE_NAME.ANC_MEMBER).rawCustomQueryForAdapter(query)) {
            if (cursor != null && cursor.moveToFirst()) {
                CommonPersonObject personObject = getCommonRepository(CoreConstants.TABLE_NAME.ANC_MEMBER).readAllcommonforCursorAdapter(cursor);
                details = personObject.getColumnmaps();
            }
        } catch (Exception e) {
            Timber.e(e, "CoreReferralInteractor --> fetchAncDetails");
        }

        return details;
    }

    private CommonRepository getCommonRepository(String tableName) {
        return Utils.context().commonrepository(tableName);
    }
}