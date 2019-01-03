package org.smartgresiter.wcaro.interactor;

import android.os.Handler;
import android.os.Looper;

import org.smartgresiter.wcaro.contract.FamilyCallDialogContract;
import org.smartgresiter.wcaro.model.FamilyCallDialogModel;

import java.util.concurrent.ExecutorService;

public class FamilyCallDialogInteractor implements FamilyCallDialogContract.Interactor {

    ExecutorService executorService;
    Handler mainThread;

    public FamilyCallDialogInteractor(ExecutorService service) {
        executorService = service;

        mainThread = new Handler(Looper.getMainLooper());
        if (service == null)
            throw new IllegalStateException("Null executor service");
    }

    @Override
    public void getHeadOfFamily(final FamilyCallDialogContract.Presenter presenter) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                //TODO  replace this with actual query info for the HOF
                final FamilyCallDialogModel model = new FamilyCallDialogModel();
                model.setPhoneNumber("+2547112233");
                model.setName("ReplaceWith RealName");
                model.setRole("Head of Family , Caregiver");
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        // return only if the phone number is present
                        presenter.updateHeadOfFamily(model.getPhoneNumber() == null ? null : model);
                    }
                });
            }
        });
    }

    @Override
    public void getCareGiver(final FamilyCallDialogContract.Presenter presenter) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                //TODO  retrieve the and display the care giver
                final FamilyCallDialogModel model = new FamilyCallDialogModel();
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        // return only if the phone number is present
                        presenter.updateCareGiver(model.getPhoneNumber() == null ? null : model);
                    }
                });
            }
        });
    }

}
