package org.smartregister.chw.provider;

import android.content.Context;
import android.view.View;

import org.smartregister.chw.core.interactor.CoreFamilyRemoveMemberInteractor;
import org.smartregister.chw.core.provider.CoreFamilyRemoveMemberProvider;
import org.smartregister.chw.interactor.FamilyRemoveMemberInteractor;
import org.smartregister.commonregistry.CommonRepository;

import java.util.Set;

public class FamilyRemoveMemberProvider extends CoreFamilyRemoveMemberProvider {

    public FamilyRemoveMemberProvider(String familyID, Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener, String familyHead, String primaryCaregiver) {
        super(familyID, context, commonRepository, visibleColumns, onClickListener, paginationClickListener, familyHead, primaryCaregiver);
    }

    @Override
    protected CoreFamilyRemoveMemberInteractor getFamilyRemoveMemberInteractor() {
        return FamilyRemoveMemberInteractor.getInstance();
    }
}

