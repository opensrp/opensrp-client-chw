package org.smartregister.chw.core.interactor;

import android.content.Context;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.interactor.BaseAncUpcomingServicesInteractor;
import org.smartregister.chw.anc.model.BaseUpcomingService;

import java.util.List;

public class CoreAncUpcomingServicesInteractor extends BaseAncUpcomingServicesInteractor {

    private Flavor flavor;

    public Flavor getFlavor() {
        return flavor;
    }

    public void setFlavor(Flavor flavor) {
        this.flavor = flavor;
    }

    @Override
    protected List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject) {
        return flavor.getMemberServices(context, memberObject);
    }

    public interface Flavor {
        List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject);
    }
}
