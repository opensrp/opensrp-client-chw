package org.smartregister.chw.presenter;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.chw.core.contract.CorePmtctProfileContract;
import org.smartregister.chw.core.presenter.CorePmtctMemberProfilePresenter;
import org.smartregister.chw.pmtct.domain.MemberObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;

public class PmtctProfilePresenter extends CorePmtctMemberProfilePresenter implements FamilyProfileContract.InteractorCallBack {
    public PmtctProfilePresenter(CorePmtctProfileContract.View view, CorePmtctProfileContract.Interactor interactor, MemberObject memberObject) {
        super(view, interactor, memberObject);
    }

    @Override
    public void startFormForEdit(CommonPersonObjectClient commonPersonObjectClient) {
        //implement
    }

    @Override
    public void refreshProfileTopSection(CommonPersonObjectClient commonPersonObjectClient) {
        //implement
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String s) {
        //implement
    }

    @Override
    public void onNoUniqueId() {
        //implement
    }

    @Override
    public void onRegistrationSaved(boolean b, boolean b1, FamilyEventClient familyEventClient) {
        //implement
    }
}
