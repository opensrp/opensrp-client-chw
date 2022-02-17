package org.smartregister.chw.fragment;

import static org.smartregister.AllConstants.CLIENT_TYPE;
import static org.smartregister.chw.core.utils.CoreConstants.ACTION.START_DEATH_CERTIFICATION_UPDATE;
import static org.smartregister.chw.core.utils.CoreConstants.FORM_CONSTANTS.REMOVE_MEMBER_FORM.DEATH_CERTIFICATE_ISSUE_DATE;
import static org.smartregister.chw.core.utils.CoreConstants.FORM_CONSTANTS.REMOVE_MEMBER_FORM.DEATH_CERTIFICATE_NUMBER;
import static org.smartregister.chw.core.utils.CoreConstants.FORM_CONSTANTS.REMOVE_MEMBER_FORM.RECEIVED_DEATH_CERTIFICATE;
import static org.smartregister.chw.util.Constants.BASE_ENTITY_ID;
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
        intent.putExtra(BASE_ENTITY_ID, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, true));
        intent.putExtra(CLIENT_TYPE, Utils.getValue(client.getColumnmaps(), CLIENT_TYPE, true));
        intent.putExtra(RECEIVED_DEATH_CERTIFICATE, Utils.getValue(client.getColumnmaps(), RECEIVED_DEATH_CERTIFICATE, false));
        intent.putExtra(DEATH_CERTIFICATE_ISSUE_DATE, Utils.getValue(client.getColumnmaps(), DEATH_CERTIFICATE_ISSUE_DATE, false));
        intent.putExtra(DEATH_CERTIFICATE_NUMBER, Utils.getValue(client.getColumnmaps(), DEATH_CERTIFICATE_NUMBER, false));
        intent.putExtra(OFFICIAL_ID, Utils.getValue(client.getColumnmaps(), OFFICIAL_ID, false));
        intent.putExtra(DOB, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, true));

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
                    getCustomSelectString(getMainCondition(), filters, presenter().getDefaultSortQuery(), dueFilterActive);

        } catch (SQLException e) {
            Timber.e(e);
        }

        return query;
    }

}
