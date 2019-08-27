package org.smartregister.chw.hf.presenter;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.presenter.CoreAncMemberProfilePresenter;
import org.smartregister.chw.hf.contract.AncMemberProfileContract;
import org.smartregister.chw.hf.interactor.HfAncMemberProfileInteractor;
import org.smartregister.domain.Task;

import java.lang.ref.WeakReference;
import java.util.Set;

public class AncMemberProfilePresenter extends CoreAncMemberProfilePresenter implements AncMemberProfileContract.Presenter, AncMemberProfileContract.InteractorCallBack {
    private String entityId;
    private WeakReference<AncMemberProfileContract.View> view;
    private AncMemberProfileContract.Interactor interactor;

    public AncMemberProfilePresenter(AncMemberProfileContract.View view, AncMemberProfileContract.Interactor interactor, MemberObject memberObject) {
        super(view, interactor, memberObject);
        setEntityId(memberObject.getBaseEntityId());
        this.view = new WeakReference<>(view);
        this.interactor = new HfAncMemberProfileInteractor(view.getContext());
    }

    @Override
    public void fetchTasks() {
        interactor.getClientTasks("5270285b-5a3b-4647-b772-c0b3c52e2b71", getEntityId(), this);
    }

    public String getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public void setClientTasks(Set<Task> taskList) {
        if (getView() != null) {
            getView().setClientTasks(taskList);
        }
    }

    @Override
    public AncMemberProfileContract.View getView() {
        if (view != null) {
            return view.get();
        } else {
            return null;
        }
    }
}
