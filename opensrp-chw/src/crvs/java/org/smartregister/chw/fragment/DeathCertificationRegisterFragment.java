package org.smartregister.chw.fragment;

import static org.smartregister.AllConstants.CLIENT_TYPE;
import static org.smartregister.chw.core.utils.CoreConstants.ACTION.START_DEATH_CERTIFICATION_UPDATE;
import static org.smartregister.chw.core.utils.CoreConstants.FORM_CONSTANTS.REMOVE_MEMBER_FORM.DEATH_CERTIFICATE_ISSUE_DATE;
import static org.smartregister.chw.core.utils.CoreConstants.FORM_CONSTANTS.REMOVE_MEMBER_FORM.DEATH_CERTIFICATE_NUMBER;
import static org.smartregister.chw.core.utils.CoreConstants.FORM_CONSTANTS.REMOVE_MEMBER_FORM.DEATH_NOTIFICATION_DONE;
import static org.smartregister.chw.core.utils.CoreConstants.FORM_CONSTANTS.REMOVE_MEMBER_FORM.RECEIVED_DEATH_CERTIFICATE;
import static org.smartregister.chw.util.Constants.BASE_ENTITY_ID;
import static org.smartregister.chw.util.Constants.INFORMANT_ADDRESS;
import static org.smartregister.chw.util.Constants.INFORMANT_NAME;
import static org.smartregister.chw.util.Constants.INFORMANT_PHONE;
import static org.smartregister.chw.util.Constants.INFORMANT_RELATIONSHIP;
import static org.smartregister.chw.util.Constants.OFFICIAL_ADDRESS;
import static org.smartregister.chw.util.Constants.OFFICIAL_NAME;
import static org.smartregister.chw.util.Constants.OFFICIAL_NUMBER;
import static org.smartregister.chw.util.Constants.OFFICIAL_POSITION;
import static org.smartregister.chw.util.CrvsConstants.DOB;
import static org.smartregister.chw.util.CrvsConstants.OFFICIAL_ID;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;

import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.jetbrains.annotations.NotNull;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.DeathCertificationRegisterActivity;
import org.smartregister.chw.core.fragment.CoreCertificationRegisterFragment;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.model.DeathCertificationRegisterFragmentModel;
import org.smartregister.chw.presenter.DeathCertificationRegisterFragmentPresenter;
import org.smartregister.chw.provider.DeathCertificationRegisterProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.Set;

import timber.log.Timber;

public class DeathCertificationRegisterFragment extends CoreCertificationRegisterFragment {

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        DeathCertificationRegisterProvider registerProvider = new DeathCertificationRegisterProvider(getActivity(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public void setupViews(android.view.View view) {
        super.setupViews(view);
        dueOnlyFilter = view.findViewById(org.smartregister.chw.core.R.id.due_only_text_view);
        dueOnlyFilter.setText(getResources().getString(R.string.death_summary_toggle));
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new DeathCertificationRegisterFragmentPresenter(this, new DeathCertificationRegisterFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    protected int getToolBarTitle() {
        return R.string.death_certification;
    }

    @Override
    public Intent getUpdateIntent(CommonPersonObjectClient client) {
        if (getActivity() == null || client == null)
            return null;

        Intent intent = new Intent(getActivity(), DeathCertificationRegisterActivity.class);
        intent.putExtra(CoreConstants.ACTIVITY_PAYLOAD.ACTION, START_DEATH_CERTIFICATION_UPDATE);
        intent.putExtra(DOB, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, true));
        intent.putExtra(BASE_ENTITY_ID, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false));
        intent.putExtra(CLIENT_TYPE, Utils.getValue(client.getColumnmaps(), CLIENT_TYPE, false));
        intent.putExtra(RECEIVED_DEATH_CERTIFICATE, Utils.getValue(client.getColumnmaps(), RECEIVED_DEATH_CERTIFICATE, false));
        intent.putExtra(DEATH_NOTIFICATION_DONE, Utils.getValue(client.getColumnmaps(), DEATH_NOTIFICATION_DONE, false));
        intent.putExtra(DEATH_CERTIFICATE_ISSUE_DATE, Utils.getValue(client.getColumnmaps(), DEATH_CERTIFICATE_ISSUE_DATE, false));
        intent.putExtra(DEATH_CERTIFICATE_NUMBER, Utils.getValue(client.getColumnmaps(), DEATH_CERTIFICATE_NUMBER, false));
        intent.putExtra(OFFICIAL_ID, Utils.getValue(client.getColumnmaps(), OFFICIAL_ID, false));
        intent.putExtra(INFORMANT_NAME, Utils.getValue(client.getColumnmaps(), INFORMANT_NAME, false));
        intent.putExtra(INFORMANT_RELATIONSHIP, Utils.getValue(client.getColumnmaps(), INFORMANT_RELATIONSHIP, false));
        intent.putExtra(INFORMANT_ADDRESS, Utils.getValue(client.getColumnmaps(), INFORMANT_ADDRESS, false));
        intent.putExtra(INFORMANT_PHONE, Utils.getValue(client.getColumnmaps(), INFORMANT_PHONE, false));
        intent.putExtra(OFFICIAL_NAME, Utils.getValue(client.getColumnmaps(), OFFICIAL_NAME, false));
        intent.putExtra(OFFICIAL_ID, Utils.getValue(client.getColumnmaps(), OFFICIAL_ID, false));
        intent.putExtra(OFFICIAL_POSITION, Utils.getValue(client.getColumnmaps(), OFFICIAL_POSITION, false));
        intent.putExtra(OFFICIAL_ADDRESS, Utils.getValue(client.getColumnmaps(), OFFICIAL_ADDRESS, false));
        intent.putExtra(OFFICIAL_NUMBER, Utils.getValue(client.getColumnmaps(), OFFICIAL_NUMBER, false));

        return intent;
    }

    @NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID) {// Returns a new CursorLoader
            return new CursorLoader(requireActivity()) {
                @Override
                public Cursor loadInBackground() {
                    // Count query
                    final String COUNT = "count_execute";
                    if (args != null && args.getBoolean(COUNT)) {
                        countExecute();
                    }
                    String query = filterAndSortQuery();
                    return commonRepository().rawCustomQueryForAdapter(query);
                }
            };
        }// An invalid id was passed in
        return null;
    }

    @Override
    public void countExecute() {
        Cursor c = null;
        try {
            c = commonRepository().rawCustomQueryForAdapter(getCountSelect());
            c.moveToFirst();
            clientAdapter.setTotalcount(c.getInt(0));

            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private String getCountSelect() {
        String query = "";

        try {
            query = ((DeathCertificationRegisterFragmentPresenter) presenter()).
                    getCustomSelectString(getMainCondition(), filters, presenter().getOutOfCatchmentSortQueries(), dueFilterActive);
        } catch (SQLException e) {
            Timber.e(e);
        }

        return "SELECT COUNT(*) FROM (" + query + ") AS cnt;";
    }

    private String filterAndSortQuery() {

        String query = "";

        try {
            query = ((DeathCertificationRegisterFragmentPresenter) presenter()).
                    getCustomSelectString(getMainCondition(), filters, presenter().getOutOfCatchmentSortQueries(), dueFilterActive);
            SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder(query);
            return queryBuilder.addlimitandOffset(queryBuilder.toString(), clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset());

        } catch (SQLException e) {
            Timber.e(e);
        }

        return query;
    }

}
