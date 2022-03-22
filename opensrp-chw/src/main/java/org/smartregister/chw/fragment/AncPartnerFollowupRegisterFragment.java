package org.smartregister.chw.fragment;

import android.app.Activity;
import android.database.Cursor;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.AncHomeVisitActivity;
import org.smartregister.chw.activity.AncPartnerFollowupReferralProfileActivity;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.core.fragment.CoreAncRegisterFragment;
import org.smartregister.chw.core.provider.ChwAncRegisterProvider;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.model.AncPartnerFollowupRegisterFragmentModel;
import org.smartregister.chw.presenter.ChwAncPartnerFollowupRegisterFragmentPresenter;
import org.smartregister.chw.provider.AncFollowupRegisterProvider;
import org.smartregister.chw.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.Set;

import timber.log.Timber;

public class AncPartnerFollowupRegisterFragment extends CoreAncRegisterFragment {
    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        ChwAncRegisterProvider provider = new AncFollowupRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, provider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new ChwAncPartnerFollowupRegisterFragmentPresenter(this, new AncPartnerFollowupRegisterFragmentModel(), null);
    }

    @Override
    protected void openProfile(CommonPersonObjectClient client) {
        AncPartnerFollowupReferralProfileActivity.startMe(getActivity(), client.getCaseId(),client.getColumnmaps().get(Constants.PartnerRegistrationConstants.FormSubmissionId));
    }

    @Override
    protected void openHomeVisit(CommonPersonObjectClient client) {
        Activity activity = getActivity();
        if (activity == null)
            return;

        AncHomeVisitActivity.startMe(activity, client.getCaseId(), false);
    }

    @Override
    public void countExecute() {
        Cursor cursor = null;
        try {

            String query = "select count(*) from " + presenter().getMainTable() +
                    " inner join " + CoreConstants.TABLE_NAME.FAMILY_MEMBER + " on " + presenter().getMainTable() + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " + CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.BASE_ENTITY_ID +
                    " inner join " + CoreConstants.TABLE_NAME.ANC_PARTNER_FOLLOWUP + " on " + presenter().getMainTable() + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " + CoreConstants.TABLE_NAME.ANC_PARTNER_FOLLOWUP + ".entity_id" +
                    " where " + presenter().getMainCondition();

            if (StringUtils.isNotBlank(filters))
                query = query + getFilterString();

            cursor = commonRepository().rawCustomQueryForAdapter(query);
            cursor.moveToFirst();
            clientAdapter.setTotalcount(cursor.getInt(0));
            Timber.v("total count here %d", clientAdapter.getTotalcount());

            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);


        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void setupViews(android.view.View view) {
        super.setupViews(view);
        titleLabelView.setText(R.string.action_received_anc_partner_followup_referrals);
    }
}
