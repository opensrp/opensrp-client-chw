package org.smartregister.chw.hf.fragment;

import android.database.Cursor;

import org.smartregister.chw.core.fragment.CoreAncRegisterFragment;
import org.smartregister.chw.core.presenter.AncRegisterFragmentPresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.chw.hf.activity.AncMemberProfileActivity;
import org.smartregister.chw.hf.model.AncRegisterFragmentModel;
import org.smartregister.chw.hf.provider.HfAncRegisterProvider;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.family.util.DBConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

public class AncRegisterFragment extends CoreAncRegisterFragment {

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        HfAncRegisterProvider provider = new HfAncRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, provider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new AncRegisterFragmentPresenter(this, new AncRegisterFragmentModel(), null);
    }

    @Override
    protected void openProfile(CommonPersonObjectClient client) {
        String careGiverId = org.smartregister.util.Utils.getValue(client.getColumnmaps(), DBConstants.KEY.PRIMARY_CAREGIVER, false);
        client.getColumnmaps().putAll(fetchCareGiverDetails(careGiverId));

        AncMemberProfileActivity.startMe(getActivity(), client.getCaseId(), client);
    }

    @Override
    protected void openHomeVisit(CommonPersonObjectClient client) {
        //Not needed on HF
    }

    private Map<String, String> fetchCareGiverDetails(String careGiverId) {
        Map<String, String> details = new HashMap<>();

        String query = CoreReferralUtils.mainCareGiverSelect(CoreConstants.TABLE_NAME.FAMILY_MEMBER, careGiverId);
        Timber.d("The caregiver query %s", query);
        try (Cursor cursor = getCommonRepository(CoreConstants.TABLE_NAME.FAMILY_MEMBER).rawCustomQueryForAdapter(query)) {
            if (cursor != null && cursor.moveToFirst()) {
                CommonPersonObject personObject = getCommonRepository(CoreConstants.TABLE_NAME.FAMILY_MEMBER).readAllcommonforCursorAdapter(cursor);
                details = personObject.getColumnmaps();
                //pClient.getColumnmaps().putAll(personObject.getColumnmaps());
            }
        } catch (Exception e) {
            Timber.e(e, "AncRegisterFragment --> fetchCareGiverDetails");
        }

        return details;
    }

    private CommonRepository getCommonRepository(String tableName) {
        return Utils.context().commonrepository(tableName);
    }
}
