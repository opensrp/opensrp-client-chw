package org.smartregister.chw.core.presenter;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.contract.BaseReferralRegisterFragmentContract;
import org.smartregister.chw.core.interactor.CoreReferralInteractor;
import org.smartregister.chw.core.model.BaseReferralModel;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.HashSet;

public class BaseReferralFragmentPresenter implements BaseReferralRegisterFragmentContract.Presenter, BaseReferralRegisterFragmentContract.InteractorCallBack {
    protected String baseEntityId;
    protected BaseReferralRegisterFragmentContract.View view;
    protected BaseReferralRegisterFragmentContract.Model model;
    protected BaseReferralRegisterFragmentContract.Interactor interactor;
    protected String taskFocus;

    public BaseReferralFragmentPresenter(BaseReferralRegisterFragmentContract.View view) {
        this.view = view;
        interactor = new CoreReferralInteractor();
        model = new BaseReferralModel();
    }

    @Override
    public void processViewConfigurations() {
        if (view != null) {
            view.updateSearchBarHint(view.getContext().getString(R.string.search_name_or_id));
        }
    }

    @Override
    public void initializeQueries(String mainCondition) {
        String countSelect = model.countSelect(CoreConstants.TABLE_NAME.TASK, mainCondition);
        String mainSelect = model.mainSelect(CoreConstants.TABLE_NAME.TASK, CoreConstants.TABLE_NAME.FAMILY_MEMBER, mainCondition);

        view.initializeQueryParams(CoreConstants.TABLE_NAME.FAMILY_MEMBER, countSelect, mainSelect);
        view.initializeAdapter(new HashSet<>(), CoreConstants.TABLE_NAME.TASK);

        view.countExecute();
        view.filterandSortInInitializeQueries();
    }

    @Override
    public void startSync() {
        //// TODO: 15/08/19
    }

    @Override
    public void searchGlobally(String s) {
        //// TODO: 15/08/19
    }

    @Override
    public void fetchClient() {
        interactor.getClientDetails(getBaseEntityId(), this, "");
    }

    @Override
    public void setTasksFocus(String taskFocus) {
        this.taskFocus = taskFocus;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getTaskFocus() {
        return taskFocus;
    }

    @Override
    public void clientDetails(CommonPersonObjectClient client) {
        view.setClient(client);
    }
}
