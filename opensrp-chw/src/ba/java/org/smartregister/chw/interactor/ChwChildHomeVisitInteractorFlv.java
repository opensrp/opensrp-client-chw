package org.smartregister.chw.interactor;

import timber.log.Timber;

public class ChwChildHomeVisitInteractorFlv extends DefaultChwChildHomeVisitInteractor {
    @Override
    protected void evaluateMNP() {
        Timber.v("evaluateMNP");
    }

    @Override
    protected void evaluateMUAC() {
        Timber.v("evaluateMUAC");
    }

    @Override
    protected void evaluateLLITN() {
        Timber.v("evaluateLLITN");
    }

    @Override
    protected void evaluateECD() {
        Timber.v("evaluateECD");
    }
}
