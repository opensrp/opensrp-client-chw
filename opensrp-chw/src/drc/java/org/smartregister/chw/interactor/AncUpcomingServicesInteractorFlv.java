package org.smartregister.chw.interactor;

import android.content.Context;

import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class AncUpcomingServicesInteractorFlv extends DefaultAncUpcomingServicesInteractorFlv {
    @Override
    public List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject) {
        List<BaseUpcomingService> services = new ArrayList<>();

        Date createDate = null;
        try {
            createDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(memberObject.getDateCreated());
        } catch (ParseException e) {
            Timber.e(e);
        }
        if (createDate == null) {
            return services;
        }

        // anc card
        evaluateANCCard(services, memberObject, context, createDate);
        evaluateDeliveryKit(services, memberObject, context, createDate);
        evaluateHealthFacility(services, memberObject, context);
        evaluateTT(services, memberObject, context);
        evaluateIPTP(services, memberObject, context);

        return services;
    }

    protected void evaluateDeliveryKit(List<BaseUpcomingService> services, MemberObject memberObject, Context context, Date createDate) {
        if (memberObject.getDeliveryKit() != null && !memberObject.getDeliveryKit().equalsIgnoreCase("Yes")) {
            BaseUpcomingService cardService = new BaseUpcomingService();
            cardService.setServiceName(context.getString(R.string.delivery_kit));
            cardService.setServiceDate(createDate);
            services.add(cardService);
        }
    }
}
