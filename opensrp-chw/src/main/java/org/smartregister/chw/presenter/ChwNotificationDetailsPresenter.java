package org.smartregister.chw.presenter;

import android.app.Activity;

import org.smartregister.chw.core.contract.ChwNotificationDetailsContract;
import org.smartregister.chw.core.presenter.BaseChwNotificationDetailsPresenter;
import org.smartregister.chw.task.ChwGoToMemberProfileBasedOnRegisterTask;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class ChwNotificationDetailsPresenter extends BaseChwNotificationDetailsPresenter {
    public ChwNotificationDetailsPresenter(ChwNotificationDetailsContract.View view) {
        super(view);
    }

}
