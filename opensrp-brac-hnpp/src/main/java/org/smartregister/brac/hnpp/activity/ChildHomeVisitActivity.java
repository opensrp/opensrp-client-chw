package org.smartregister.brac.hnpp.activity;

import org.smartregister.chw.anc.presenter.BaseAncHomeVisitPresenter;
import org.smartregister.chw.core.activity.CoreChildHomeVisitActivity;
import org.smartregister.chw.core.interactor.CoreChildHomeVisitInteractor;
import org.smartregister.brac.hnpp.interactor.HFChwChildHomeVisitInteractor;

public class ChildHomeVisitActivity extends CoreChildHomeVisitActivity {

    @Override
    protected void registerPresenter() {
        presenter = new BaseAncHomeVisitPresenter(memberObject, this, new CoreChildHomeVisitInteractor(new HFChwChildHomeVisitInteractor()));
    }

}
