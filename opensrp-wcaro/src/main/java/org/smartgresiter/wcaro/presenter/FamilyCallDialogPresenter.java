package org.smartgresiter.wcaro.presenter;

import org.smartgresiter.wcaro.contract.FamilyCallDialogContract;
import org.smartgresiter.wcaro.interactor.FamilyCallDialogInteractor;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FamilyCallDialogPresenter implements FamilyCallDialogContract.Presenter {

    static String TAG = FamilyCallDialogPresenter.class.getCanonicalName();

    WeakReference<FamilyCallDialogContract.View> mView;
    FamilyCallDialogContract.Interactor mInteractor;
    ExecutorService executorService;
    int THREAD_POOL = 2;

    public FamilyCallDialogPresenter(FamilyCallDialogContract.View view) {
        mView = new WeakReference<>(view);
        executorService = Executors.newFixedThreadPool(THREAD_POOL);
        mInteractor = new FamilyCallDialogInteractor(executorService);
        initalize();
    }

    @Override
    public void updateHeadOfFamily(FamilyCallDialogContract.Model model) {
        if(mView.get() != null){
            mView.get().refreshHeadOfFamilyView(model);
        }
    }

    @Override
    public void updateCareGiver(FamilyCallDialogContract.Model model) {
        if(mView.get() != null){
            mView.get().refreshCareGiverView(model);
        }
    }

    @Override
    public void initalize() {
        mInteractor.getCareGiver(this);
        mInteractor.getHeadOfFamily(this);
    }
}
