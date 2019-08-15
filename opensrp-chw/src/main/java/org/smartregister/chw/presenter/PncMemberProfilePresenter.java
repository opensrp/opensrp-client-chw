package org.smartregister.chw.presenter;


import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.presenter.BaseAncMemberProfilePresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.contract.FamilyProfileContract;

import timber.log.Timber;

public class PncMemberProfilePresenter extends BaseAncMemberProfilePresenter implements FamilyProfileContract.InteractorCallBack {

    public PncMemberProfilePresenter(BaseAncMemberProfileContract.View view, BaseAncMemberProfileContract.Interactor interactor, MemberObject memberObject) {
        super(view, interactor, memberObject);
    }

    public void startFormForEdit(CommonPersonObjectClient commonPersonObject) {
//        TODO Implement
    }

    @Override
    public void refreshProfileTopSection(CommonPersonObjectClient client) {
//        TODO Implement
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
//        TODO Implement
        Timber.d("onUniqueIdFetched unimplemented");
    }

    @Override
    public void onNoUniqueId() {
//        TODO Implement
        Timber.d("onNoUniqueId unimplemented");
    }

    @Override
    public void onRegistrationSaved(boolean isEditMode) {
//     TODO Implement
        Timber.d("onRegistrationSaved unimplemented");
    }

    public BaseAncMemberProfileContract.View getView() {
        if (view != null) {
            return view.get();
        } else {
            return null;
        }
    }
}


