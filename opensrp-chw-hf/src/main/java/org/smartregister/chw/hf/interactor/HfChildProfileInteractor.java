package org.smartregister.chw.hf.interactor;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import org.json.JSONObject;
import org.smartregister.chw.core.contract.CoreChildProfileContract;
import org.smartregister.chw.core.interactor.CoreChildProfileInteractor;
import org.smartregister.chw.core.model.ChildVisit;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.ChildHomeVisit;
import org.smartregister.chw.core.utils.CoreChildService;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.chw.hf.utils.HfChildUtils;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HfChildProfileInteractor extends CoreChildProfileInteractor {
    private AppExecutors appExecutors;
    private Map<String, Date> vaccineList = new LinkedHashMap<>();

    public HfChildProfileInteractor() {
        this(new AppExecutors());
    }

    @VisibleForTesting
    HfChildProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public Map<String, Date> getVaccineList() {
        return vaccineList;
    }

    @Override
    public void setVaccineList(Map<String, Date> vaccineList) {
        this.vaccineList = vaccineList;
    }

    @Override
    public void updateVisitNotDone(final long value, final CoreChildProfileContract.InteractorCallBack callback) {
        updateHomeVisitAsEvent(value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //// TODO: 15/08/19
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
                        //// TODO: 15/08/19
                    }
                });
    }

    @Override
    public void refreshChildVisitBar(Context context, String baseEntityId, final CoreChildProfileContract.InteractorCallBack callback) {
        ChildHomeVisit childHomeVisit = CoreChildUtils.getLastHomeVisit(CoreConstants.TABLE_NAME.CHILD, baseEntityId);

        if (getpClient() != null) {
            String dobString = Utils.getDuration(Utils.getValue(getpClient().getColumnmaps(), DBConstants.KEY.DOB, false));

            final ChildVisit childVisit = HfChildUtils.getChildVisitStatus(context, dobString, childHomeVisit.getLastHomeVisitDate(), childHomeVisit.getVisitNotDoneDate(), childHomeVisit.getDateCreated());

            Runnable runnable = () -> appExecutors.mainThread().execute(() -> callback.updateChildVisit(childVisit));
            appExecutors.diskIO().execute(runnable);
        }
    }

    @Override
    public void refreshUpcomingServiceAndFamilyDue(Context context, String familyId, String baseEntityId, final CoreChildProfileContract.InteractorCallBack callback) {
        if (getpClient() == null) {
            return;
        }
        updateUpcomingServices(callback, context);
        updateFamilyDueStatus(context, familyId, baseEntityId, callback);

    }

    @Override
    public void processBackGroundEvent(final CoreChildProfileContract.InteractorCallBack callback) {
        Runnable runnable = () -> {
            HfChildUtils.processClientProcessInBackground();
            appExecutors.mainThread().execute(() -> callback.updateAfterBackGroundProcessed());
        };
        appExecutors.diskIO().execute(runnable);
    }

    private void updateUpcomingServices(final CoreChildProfileContract.InteractorCallBack callback, Context context) {
        updateUpcomingServices(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CoreChildService>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //// TODO: 15/08/19
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
                        //// TODO: 15/08/19
                    }
                });
    }

    private void updateFamilyDueStatus(Context context, String familyId, String baseEntityId, final CoreChildProfileContract.InteractorCallBack callback) {
        HfFamilyInteractor hfFamilyInteractor = new HfFamilyInteractor();
        hfFamilyInteractor.updateFamilyDueStatus(context, baseEntityId, familyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //// TODO: 15/08/19
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
                        //// TODO: 15/08/19
                    }
                });
    }

    private Observable<Object> updateHomeVisitAsEvent(final long value) {
        return Observable.create(objectObservableEmitter -> {
            final String homeVisitId = CoreJsonFormUtils.generateRandomUUIDString();

            Map<String, JSONObject> fields = new HashMap<>();
            HfChildUtils.updateHomeVisitAsEvent(getpClient().entityId(), CoreConstants.EventType.CHILD_VISIT_NOT_DONE, CoreConstants.TABLE_NAME.CHILD,
                    fields, ChildDBConstants.KEY.VISIT_NOT_DONE, value + "", homeVisitId);
            objectObservableEmitter.onNext("");
        });
    }
}
