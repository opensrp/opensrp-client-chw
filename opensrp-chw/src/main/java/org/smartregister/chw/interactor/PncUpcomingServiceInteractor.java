package org.smartregister.chw.interactor;

import android.content.Context;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.interactor.BaseAncUpcomingServicesInteractor;
import org.smartregister.chw.anc.model.BaseUpcomingService;

import java.util.List;

public class PncUpcomingServiceInteractor extends BaseAncUpcomingServicesInteractor {

    private Flavor flavor = new PncUpcomingServicesInteractorFlv();

    @Override
    protected List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject) {
        return flavor.getMemberServices(context, memberObject);
    }

    public interface Flavor {
        List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject);
    }
}


