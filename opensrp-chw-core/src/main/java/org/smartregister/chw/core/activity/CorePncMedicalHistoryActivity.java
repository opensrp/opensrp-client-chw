package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import org.smartregister.chw.anc.contract.BaseAncMedicalHistoryContract;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.presenter.BaseAncMedicalHistoryPresenter;
import org.smartregister.chw.pnc.activity.BasePncMedicalHistoryActivity;

import java.util.List;

public abstract class CorePncMedicalHistoryActivity extends BasePncMedicalHistoryActivity {

    @Override
    public void initializePresenter() {
        presenter = new BaseAncMedicalHistoryPresenter(getPncMedicalHistoryInteractor(), this, memberObject.getBaseEntityId());
    }

    protected abstract BaseAncMedicalHistoryContract.Interactor getPncMedicalHistoryInteractor();

    public interface Flavor {

        View bindViews(Activity activity);

        void processViewData(List<Visit> visits, Context context);
    }

}
