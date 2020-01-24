package org.smartregister.chw.interactor;

import android.content.Context;

import androidx.annotation.VisibleForTesting;

import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.util.AppExecutors;
import org.smartregister.chw.contract.SickFormMedicalHistoryContract;
import org.smartregister.chw.core.utils.CoreConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SickFormMedicalHistoryInteractor implements SickFormMedicalHistoryContract.Interactor {

    protected AppExecutors appExecutors;

    @VisibleForTesting
    SickFormMedicalHistoryInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public SickFormMedicalHistoryInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void getUpComingServices(final MemberObject memberObject, Context context, SickFormMedicalHistoryContract.InteractorCallBack callBack) {
        Runnable runnable = () -> {
            // save it
            final List<String> services = new ArrayList<>();
            try {
                List<Visit> visits = AncLibrary.getInstance().visitRepository().getVisits(memberObject.getBaseEntityId(), CoreConstants.EventType.SICK_CHILD);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

                for (Visit v : visits) {
                    services.add(sdf.format(v.getDate()));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            appExecutors.mainThread().execute(() -> callBack.onDataFetched(services));
        };
        appExecutors.diskIO().execute(runnable);
    }
}
