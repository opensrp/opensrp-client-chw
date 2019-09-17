package org.smartregister.chw.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;

import org.smartregister.chw.activity.FamilyProfileActivity;
import org.smartregister.chw.core.fragment.CoreFamilyProfileMemberFragment;
import org.smartregister.chw.model.FamilyProfileMemberModel;
import org.smartregister.chw.presenter.FamilyProfileDuePresenter;
import org.smartregister.chw.presenter.FamilyProfileMemberPresenter;
import org.smartregister.chw.provider.ChwMemberRegisterProvider;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.Constants;

import java.util.Set;

public class FamilyProfileMemberFragment extends CoreFamilyProfileMemberFragment {
    private int dueCount = 0;

    public static BaseFamilyProfileMemberFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        BaseFamilyProfileMemberFragment fragment = new FamilyProfileMemberFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns, String familyHead, String primaryCaregiver) {
        ChwMemberRegisterProvider chwMemberRegisterProvider = new ChwMemberRegisterProvider(this.getActivity(), this.commonRepository(), visibleColumns, this.registerActionHandler, this.paginationViewHandler, familyHead, primaryCaregiver);
        this.clientAdapter = new RecyclerViewPaginatedAdapter(null, chwMemberRegisterProvider, this.context().commonrepository(this.tablename));
        this.clientAdapter.setCurrentlimit(20);
        this.clientsView.setAdapter(this.clientAdapter);
    }

    public FamilyProfileMemberPresenter getPresenter() {
        return (FamilyProfileMemberPresenter) presenter;
    }

    @Override
    protected void initializePresenter() {
        Bundle bundle = getArguments();
        if (bundle == null)
            return;

        String familyBaseEntityId = bundle.getString(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        String familyHead = bundle.getString(Constants.INTENT_KEY.FAMILY_HEAD);
        String primaryCareGiver = bundle.getString(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
        presenter = new FamilyProfileMemberPresenter(this, new FamilyProfileMemberModel(), null, familyBaseEntityId, familyHead, primaryCareGiver);
    }

    @Override
    public void countExecute() {
        final int count = getPresenter().getDueCount();
        clientAdapter.setTotalcount(count);

        if (getActivity() != null && count != dueCount) {
            dueCount = count;
            ((FamilyProfileActivity) getActivity()).updateDueCount(dueCount);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case LOADER_ID:
                // Returns a new CursorLoader
                return new CursorLoader(getActivity()) {
                    @Override
                    public Cursor loadInBackground() {
                        // Count query
                        if (args != null && args.getBoolean("count_execute")) {
                            countExecute();
                        }
                        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);
                        sqb.addCondition(filters);
                        sqb.addCondition(((FamilyProfileMemberPresenter) presenter()).getChildFilter());
                        String query = sqb.orderbyCondition(Sortqueries);
                        query = sqb.Endquery(sqb.addlimitandOffset(query, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset()));
                        return commonRepository().rawCustomQueryForAdapter(query);
                    }
                };
            default:
                // An invalid id was passed in
                return null;
        }
    }
}
