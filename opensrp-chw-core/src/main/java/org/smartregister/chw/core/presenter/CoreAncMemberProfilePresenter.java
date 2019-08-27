package org.smartregister.chw.core.presenter;


import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.presenter.BaseAncMemberProfilePresenter;
import org.smartregister.chw.core.contract.AncMemberProfileContract;
import org.smartregister.chw.core.interactor.CoreAncMemberProfileInteractor;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Task;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.repository.AllSharedPreferences;

import java.lang.ref.WeakReference;
import java.util.Set;

import timber.log.Timber;

public class CoreAncMemberProfilePresenter extends BaseAncMemberProfilePresenter implements FamilyProfileContract.InteractorCallBack, AncMemberProfileContract.Presenter, AncMemberProfileContract.InteractorCallBack {
    private String entityId;
    private WeakReference<AncMemberProfileContract.View> view;
    private AncMemberProfileContract.Interactor interactor;

    public CoreAncMemberProfilePresenter(AncMemberProfileContract.View view, AncMemberProfileContract.Interactor interactor, MemberObject memberObject) {
        super(view, interactor, memberObject);
        setEntityId(memberObject.getBaseEntityId());
        this.view = new WeakReference<>(view);
        this.interactor = new CoreAncMemberProfileInteractor(view.getContext());
    }

    @Override
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
    public void createSickChildEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception {
        interactor.createSickChildEvent(allSharedPreferences, jsonString, getEntityId());
    }
}


