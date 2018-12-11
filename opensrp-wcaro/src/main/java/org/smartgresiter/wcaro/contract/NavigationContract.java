package org.smartgresiter.wcaro.contract;

import android.app.Activity;

import org.smartgresiter.wcaro.model.NavigationOption;

import java.util.Date;
import java.util.List;

public interface NavigationContract {

    interface Presenter {

        NavigationContract.View getNavigationView();

        void refreshNavigationCount(Activity activity);

        void refreshLastSync();

        void displayCurrentUser();

        void Sync(Activity activity);

        List<NavigationOption> getOptions(Activity activity);
    }

    interface View {

        void prepareViews(Activity activity);

        void refreshLastSync(Date lastSync);

        void refreshCurrentUser(String name);

        void logout(Activity activity);

        void refreshCount();

        void displayToast(Activity activity, String message);
    }

    interface Model {

        List<NavigationOption> getNavigationItems(Activity activity);

        String getCurrentUser();

        void setNavigationOptions(List<NavigationOption> navigationOptions);
    }

    interface Interactor {

        String getUser();

        Date getLastSync();

        void getFamilyCount(Activity activity, InteractorCallback<Integer> callback);

        void getChildrenCount(Activity activity, InteractorCallback<Integer> callback);

        Date Sync();

    }

    interface InteractorCallback<T> {
        void onResult(T result);

        void onError(Exception e);
    }

    interface SelectedAction {
        void onSelect();
    }


}
