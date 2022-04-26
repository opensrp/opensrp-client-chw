package org.smartregister.chw.fragment;

import android.database.Cursor;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.activity.PncHomeVisitActivity;
import org.smartregister.chw.activity.PncMemberProfileActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.core.fragment.CorePncRegisterFragment;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.QueryGenerator;
import org.smartregister.chw.model.ChwPncRegisterFragmentModel;
import org.smartregister.chw.presenter.PncRegisterFragmentPresenter;
import org.smartregister.chw.provider.ChwPncRegisterProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.text.MessageFormat;
import java.util.Set;

import timber.log.Timber;

public class PncRegisterFragment extends CorePncRegisterFragment {

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        ChwPncRegisterProvider provider = new ChwPncRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, provider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void openHomeVisit(CommonPersonObjectClient client) {
        PncHomeVisitActivity.startMe(getActivity(), new MemberObject(client), false);
    }

    @Override
    protected void openPncMemberProfile(CommonPersonObjectClient client) {
        MemberObject memberObject = new MemberObject(client);
        PncMemberProfileActivity.startMe(getActivity(), memberObject.getBaseEntityId());
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new PncRegisterFragmentPresenter(this, new ChwPncRegisterFragmentModel(), null);
    }


    @Override
    public void countExecute() {
        Cursor cursor = null;
        try {
            String mainTable = presenter().getMainTable();

            QueryGenerator generator = new QueryGenerator()
                    .withMainTable(mainTable)
                    .withColumn("count(*)")
                    .withJoinClause("INNER JOIN " + CoreConstants.TABLE_NAME.FAMILY_MEMBER + " ON "
                            + mainTable + "." + DBConstants.KEY.BASE_ENTITY_ID + " = "
                            + CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.BASE_ENTITY_ID)

                    .withWhereClause(getCountMainCondition());

            if (dueFilterActive)
                generator.withWhereClause(getDueCondition());

            if (StringUtils.isNotBlank(filters))
                generator.withWhereClause(getSearchFilter(filters));

            cursor = commonRepository().rawCustomQueryForAdapter(generator.generateQuery());
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

    private String getSearchFilter(String search) {
        return MessageFormat.format(" {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.FIRST_NAME, search) +
                MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.LAST_NAME, search) +
                MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.MIDDLE_NAME, search) +
                MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.UNIQUE_ID, search);
    }

    private String getCountMainCondition() {
        return " " + CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.DATE_REMOVED + " is null ";
    }
}
