package org.smartregister.chw.interactor;

import android.content.Context;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;

import java.util.List;

public class DefaultUpcomingServicesInteractorFlv implements FpUpcomingServicesInteractor.Flavor {
    @Override
    public List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject) {
        return null;
    }
}
