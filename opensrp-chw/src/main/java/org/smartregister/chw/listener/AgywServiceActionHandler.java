package org.smartregister.chw.listener;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import org.smartregister.chw.activity.AgywServicesFormActivity;
import org.smartregister.chw.agyw.domain.MemberObject;
import org.smartregister.chw.agyw.domain.ServiceCard;
import org.smartregister.chw.agyw.handlers.BaseServiceActionHandler;

public class AgywServiceActionHandler extends BaseServiceActionHandler {

    @Override
    protected void startVisitActivity(Context context, ServiceCard serviceCard, MemberObject memberObject) {
        if (serviceCard.getFormName() != null) {
            try {
                AgywServicesFormActivity.startMe((Activity) context, serviceCard.getFormName(), memberObject.getBaseEntityId(), memberObject.getAge());
            } catch (Exception e) {
                Toast.makeText(context, "Something happened!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
