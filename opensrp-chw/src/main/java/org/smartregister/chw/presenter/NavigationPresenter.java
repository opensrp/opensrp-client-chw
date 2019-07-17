package org.smartregister.chw.presenter;

import android.app.Activity;

import org.smartregister.chw.contract.NavigationContract;
import org.smartregister.chw.interactor.NavigationInteractor;
import org.smartregister.chw.job.VaccineRecurringServiceJob;
import org.smartregister.chw.model.NavigationModel;
import org.smartregister.chw.model.NavigationOption;
import org.smartregister.chw.util.Constants;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.SyncServiceJob;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class NavigationPresenter implements NavigationContract.Presenter {

    private NavigationContract.Model mModel;
    private NavigationContract.Interactor mInteractor;
    private WeakReference<NavigationContract.View> mView;

    private HashMap<String, String> tableMap = new HashMap<>();

    public NavigationPresenter(NavigationContract.View view) {
        mView = new WeakReference<>(view);
        mInteractor = NavigationInteractor.getInstance();
        mModel = NavigationModel.getInstance();
        initialize();
    }

    private void initialize() {
        tableMap.put(Constants.DrawerMenu.ALL_FAMILIES, Constants.TABLE_NAME.FAMILY);
        tableMap.put(Constants.DrawerMenu.CHILD_CLIENTS, Constants.TABLE_NAME.CHILD);
        tableMap.put(Constants.DrawerMenu.ANC_CLIENTS, Constants.TABLE_NAME.ANC_MEMBER);
        tableMap.put(Constants.DrawerMenu.ANC, Constants.TABLE_NAME.ANC_MEMBER);
        tableMap.put(Constants.DrawerMenu.PNC,Constants.TABLE_NAME.ANC_PREGNANCY_OUTCOME);
    }

    @Override
    public NavigationContract.View getNavigationView() {
        return mView.get();
    }

    @Override
    public void refreshNavigationCount(final Activity activity) {

        int x = 0;
        while (x < mModel.getNavigationItems().size()) {
            final int finalX = x;
            mInteractor.getRegisterCount(tableMap.get(mModel.getNavigationItems().get(x).getMenuTitle()), new NavigationContract.InteractorCallback<Integer>() {
                @Override
                public void onResult(Integer result) {
                    mModel.getNavigationItems().get(finalX).setRegisterCount(result);
                    getNavigationView().refreshCount();
                }

                @Override
                public void onError(Exception e) {
                    // getNavigationView().displayToast(activity, "Error retrieving count for " + tableMap.get(mModel.getNavigationItems().get(finalX).getMenuTitle()));
                    Timber.e("Error retrieving count for %s", tableMap.get(mModel.getNavigationItems().get(finalX).getMenuTitle()));
                }
            });
            x++;
        }

    }


    @Override
    public void refreshLastSync() {
        // get last sync date
        getNavigationView().refreshLastSync(mInteractor.Sync());
    }

    @Override
    public void displayCurrentUser() {
        getNavigationView().refreshCurrentUser(mModel.getCurrentUser());
    }

    @Override
    public void sync(Activity activity) {
        VaccineRecurringServiceJob.scheduleJobImmediately(VaccineRecurringServiceJob.TAG);
        ImageUploadServiceJob.scheduleJobImmediately(ImageUploadServiceJob.TAG);
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
    }

    @Override
    public List<NavigationOption> getOptions() {
        return mModel.getNavigationItems();
    }
}
