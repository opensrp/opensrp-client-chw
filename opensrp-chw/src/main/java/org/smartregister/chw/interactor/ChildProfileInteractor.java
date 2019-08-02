package org.smartregister.chw.interactor;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.opensrp.chw.core.contract.CoreChildProfileContract;
import com.opensrp.chw.core.interactor.CoreChildProfileInteractor;
import com.opensrp.chw.core.model.ChildVisit;
import com.opensrp.chw.core.utils.ChildDBConstants;
import com.opensrp.chw.core.utils.ChildHomeVisit;
import com.opensrp.chw.core.utils.CoreChildService;

import org.json.JSONObject;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.Utils;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChildProfileInteractor extends CoreChildProfileInteractor {
    public static final String TAG = ChildProfileInteractor.class.getName();
    private AppExecutors appExecutors;
    private Map<String, Date> vaccineList = new LinkedHashMap<>();

    @VisibleForTesting
    ChildProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public ChildProfileInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void refreshChildVisitBar(Context context, String baseEntityId, final CoreChildProfileContract.InteractorCallBack callback) {
        ChildHomeVisit childHomeVisit = ChildUtils.getLastHomeVisit(Constants.TABLE_NAME.CHILD, baseEntityId);

        String dobString = Utils.getDuration(Utils.getValue(getpClient().getColumnmaps(), DBConstants.KEY.DOB, false));

        final ChildVisit childVisit = ChildUtils.getChildVisitStatus(context, dobString, childHomeVisit.getLastHomeVisitDate(), childHomeVisit.getVisitNotDoneDate(), childHomeVisit.getDateCreated());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.updateChildVisit(childVisit);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }


    @Override
    public void updateVisitNotDone(final long value, final CoreChildProfileContract.InteractorCallBack callback) {

        updateHomeVisitAsEvent(value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Object o) {
                        if (value == 0) {
                            callback.undoVisitNotDone();
                        } else {
                            callback.updateVisitNotDone();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.hideProgressBar();

                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void refreshUpcomingServiceAndFamilyDue(Context context, String familyId, String baseEntityId, final CoreChildProfileContract.InteractorCallBack callback) {
        if (getpClient() == null) return;
        updateUpcomingServices(callback);
        updateFamilyDueStatus(context, familyId, baseEntityId, callback);

    }

    private void updateUpcomingServices(final CoreChildProfileContract.InteractorCallBack callback) {
        updateUpcomingServices()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CoreChildService>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CoreChildService childService) {
                        callback.updateChildService(childService);
                        callback.hideProgressBar();

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.hideProgressBar();

                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void updateFamilyDueStatus(Context context, String familyId, String baseEntityId, final CoreChildProfileContract.InteractorCallBack callback) {
        FamilyInteractor familyInteractor = new FamilyInteractor();
        familyInteractor.updateFamilyDueStatus(context, baseEntityId, familyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        callback.updateFamilyMemberServiceDue(s);

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.hideProgressBar();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void processBackGroundEvent(final CoreChildProfileContract.InteractorCallBack callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ChildUtils.processClientProcessInBackground();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.updateAfterBackGroundProcessed();
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    private Observable<Object> updateHomeVisitAsEvent(final long value) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> objectObservableEmitter) throws Exception {
                final String homeVisitId = org.smartregister.chw.util.JsonFormUtils.generateRandomUUIDString();

                Map<String, JSONObject> fields = new HashMap<>();
                ChildUtils.updateHomeVisitAsEvent(getpClient().entityId(), Constants.EventType.CHILD_VISIT_NOT_DONE, Constants.TABLE_NAME.CHILD,
                        fields, ChildDBConstants.KEY.VISIT_NOT_DONE, value + "", homeVisitId);
                objectObservableEmitter.onNext("");
            }
        });
    }

    @Override
    public Map<String, Date> getVaccineList() {
        return vaccineList;
    }

    @Override
    public void setVaccineList(Map<String, Date> vaccineList) {
        this.vaccineList = vaccineList;
    }
}
