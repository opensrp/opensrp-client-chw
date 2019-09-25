package org.smartregister.chw.hf.presenter;


import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.presenter.BaseAncMemberProfilePresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.contract.PncMemberProfileContract;
import org.smartregister.domain.Task;

import java.util.Set;

public class PncMemberProfilePresenter extends BaseAncMemberProfilePresenter implements
        PncMemberProfileContract.Presenter, PncMemberProfileContract.InteractorCallback {

    private PncMemberProfileContract.Interactor interactor;
    private String entityId;

    public PncMemberProfilePresenter(BaseAncMemberProfileContract.View view, BaseAncMemberProfileContract.Interactor interactor,
                                     MemberObject memberObject) {
        super(view, interactor, memberObject);
        setEntityId(memberObject.getBaseEntityId());
        this.interactor = (PncMemberProfileContract.Interactor) interactor;
    }

    @Override
    public void fetchReferralTasks() {
        interactor.getReferralTasks(CoreConstants.REFERRAL_PLAN_ID, getEntityId(), this);
    }

    @Override
    public void updateReferralTasks(Set<Task> taskList) {
        ((PncMemberProfileContract.View) getView()).setReferralTasks(taskList);
    }

    public String getEntityId() {
        return entityId;
    }

    private void setEntityId(String entityId) {
        this.entityId = entityId;
    }
}


