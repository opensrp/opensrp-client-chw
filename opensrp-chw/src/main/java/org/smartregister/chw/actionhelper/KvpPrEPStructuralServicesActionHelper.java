package org.smartregister.chw.actionhelper;

import android.content.Context;

import org.smartregister.chw.kvp.domain.VisitDetail;
import org.smartregister.chw.kvp.model.BaseKvpVisitAction;

import java.util.List;
import java.util.Map;

public class KvpPrEPStructuralServicesActionHelper implements BaseKvpVisitAction.KvpVisitActionHelper {

    private final Context context;

    public KvpPrEPStructuralServicesActionHelper(Context context) {
        this.context = context;
    }

    @Override
    public void onJsonFormLoaded(String s, Context context, Map<String, List<VisitDetail>> map) {

    }

    @Override
    public String getPreProcessed() {
        return null;
    }

    @Override
    public void onPayloadReceived(String s) {

    }

    @Override
    public BaseKvpVisitAction.ScheduleStatus getPreProcessedStatus() {
        return null;
    }

    @Override
    public String getPreProcessedSubTitle() {
        return null;
    }

    @Override
    public String postProcess(String s) {
        return null;
    }

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseKvpVisitAction.Status evaluateStatusOnPayload() {
        return null;
    }

    @Override
    public void onPayloadReceived(BaseKvpVisitAction baseKvpVisitAction) {

    }
}
