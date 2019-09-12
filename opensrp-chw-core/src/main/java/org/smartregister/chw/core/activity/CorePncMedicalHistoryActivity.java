package org.smartregister.chw.core.activity;

import org.smartregister.chw.anc.contract.BaseAncMedicalHistoryContract;
import org.smartregister.chw.anc.presenter.BaseAncMedicalHistoryPresenter;
import org.smartregister.chw.pnc.activity.BasePncMedicalHistoryActivity;

public abstract class CorePncMedicalHistoryActivity extends BasePncMedicalHistoryActivity {

    @Override
    public void initializePresenter() {
        presenter = new BaseAncMedicalHistoryPresenter(getPncMedicalHistoryInteractor(), this, memberObject.getBaseEntityId());
    }

    protected abstract BaseAncMedicalHistoryContract.Interactor getPncMedicalHistoryInteractor();

}
