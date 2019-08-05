package com.opensrp.chw.hf.provider;

import android.content.Context;
import android.view.View;

import com.opensrp.chw.core.interactor.CoreFamilyRemoveMemberInteractor;
import com.opensrp.chw.core.provider.CoreFamilyRemoveMemberProvider;
import com.opensrp.chw.hf.interactor.FamilyRemoveMemberInteractor;

import org.smartregister.commonregistry.CommonRepository;

import java.util.Set;

public class HfFamilyRemoveMemberProvider extends CoreFamilyRemoveMemberProvider{

    public HfFamilyRemoveMemberProvider(String familyID, Context context, CommonRepository commonRepository,
                                        Set visibleColumns, View.OnClickListener onClickListener,
                                        View.OnClickListener paginationClickListener, String familyHead, String primaryCaregiver) {
        super(familyID, context, commonRepository, visibleColumns, onClickListener, paginationClickListener, familyHead, primaryCaregiver);
    }

    @Override
    protected CoreFamilyRemoveMemberInteractor getFamilyRemoveMemberInteractor() {
        return FamilyRemoveMemberInteractor.getInstance();
    }
}

