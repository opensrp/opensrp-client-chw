package org.smartregister.chw.core.fragment;

import org.smartregister.configurableviews.model.View;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;

import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;

public abstract class CoreFamilyProfileMemberFragment extends BaseFamilyProfileMemberFragment {

    @Override
    public abstract void initializeAdapter(Set<View> visibleColumns, String familyHead, String primaryCaregiver);

    @Override
    protected abstract void initializePresenter();

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        Timber.v("setAdvancedSearchFormData");
    }

}
