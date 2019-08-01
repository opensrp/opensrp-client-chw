package com.opensrp.chw.hf.interactor;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.opensrp.chw.core.contract.CoreChildProfileContract;
import com.opensrp.chw.core.interactor.CoreChildProfileInteractor;
import com.opensrp.chw.core.model.ChildVisit;
import com.opensrp.chw.core.utils.ChildDBConstants;
import com.opensrp.chw.core.utils.ChildHomeVisit;

import com.opensrp.chw.core.utils.CoreChildUtils;
import com.opensrp.chw.core.utils.CoreConstants;
import com.opensrp.chw.core.utils.CoreJsonFormUtils;
import com.opensrp.chw.core.utils.Utils;
import com.opensrp.chw.hf.utils.HfChildUtils;

import org.json.JSONObject;
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
        ChildHomeVisit childHomeVisit = CoreChildUtils.getLastHomeVisit(CoreConstants.TABLE_NAME.CHILD, baseEntityId);

        String dobString = Utils.getDuration(Utils.getValue(getpClient().getColumnmaps(), DBConstants.KEY.DOB, false));

        final ChildVisit childVisit = HfChildUtils.getChildVisitStatus(context, dobString, childHomeVisit.getLastHomeVisitDate(), childHomeVisit.getVisitNotDoneDate(), childHomeVisit.getDateCreated());

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
        HfFamilyInteractor hfFamilyInteractor = new HfFamilyInteractor();
        hfFamilyInteractor.updateFamilyDueStatus(context, baseEntityId, familyId)
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
                HfChildUtils.processClientProcessInBackground();
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
                final String homeVisitId = CoreJsonFormUtils.generateRandomUUIDString();

                Map<String, JSONObject> fields = new HashMap<>();
                HfChildUtils.updateHomeVisitAsEvent(getpClient().entityId(), CoreConstants.EventType.CHILD_VISIT_NOT_DONE, CoreConstants.TABLE_NAME.CHILD,
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
