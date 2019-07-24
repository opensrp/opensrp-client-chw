package org.smartregister.chw.presenter;

import android.database.Cursor;

import org.smartregister.chw.fragment.FamilyProfileDueFragment;
import org.smartregister.chw.interactor.ChildProfileInteractor;
import org.smartregister.chw.model.WashCheckModel;
import org.smartregister.chw.rule.HomeAlertRule;
import org.smartregister.chw.rule.WashCheckAlertRule;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.ChildVisit;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.ImmunizationState;
import org.smartregister.chw.util.Utils;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.contract.FamilyProfileDueContract;
import org.smartregister.family.presenter.BaseFamilyProfileDuePresenter;
import org.smartregister.family.util.DBConstants;

import java.text.MessageFormat;
import java.util.Date;

import timber.log.Timber;

public class FamilyProfileDuePresenter extends BaseFamilyProfileDuePresenter {

    public FamilyProfileDuePresenter(FamilyProfileDueContract.View view, FamilyProfileDueContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId);
    }

    @Override
    public String getMainCondition() {
        return String.format(" %s AND %s AND %s ", super.getMainCondition(), ChildDBConstants.childDueFilter(), ChildDBConstants.childAgeLimitFilter());
    }

    @Override
    public String getDefaultSortQuery() {
        return ChildDBConstants.KEY.LAST_HOME_VISIT + ", " + ChildDBConstants.KEY.VISIT_NOT_DONE + " ASC ";
    }

    public void fetchLastWashCheck(String familyId,long dateCreatedFamily) {
        WashCheckModel washCheckModel = null;
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(Constants.TABLE_NAME.WASH_CHECK_LOG, new String[]{DBConstants.KEY.BASE_ENTITY_ID,"last_visit","details_info"});
        queryBUilder.mainCondition(MessageFormat.format("{0}.{1} = ''{2}''", Constants.TABLE_NAME.WASH_CHECK_LOG, DBConstants.KEY.BASE_ENTITY_ID, familyId));
        String query = queryBUilder.orderbyCondition("last_visit" + " DESC limit 1");
        Cursor cursor = null;
        try {
            CommonRepository commonRepository = Utils.context().commonrepository(Constants.TABLE_NAME.WASH_CHECK_LOG);
            cursor = commonRepository.rawCustomQueryForAdapter(query);
            if (cursor != null && cursor.moveToFirst()) {
                washCheckModel= new WashCheckModel();
                String lastVisitStr = cursor.getString(cursor.getColumnIndex("last_visit"));
                String detailsInfo =cursor.getString(cursor.getColumnIndex("details_info"));
                washCheckModel.setLastVisit(Long.parseLong(lastVisitStr));
                washCheckModel.setDetailsJson(detailsInfo);
                washCheckModel.setFamilyBaseEntityId(familyId);
            }
        } catch (Exception ex) {
            Timber.e(ex.toString());
        } finally {
            if (cursor != null)
                cursor.close();
        }
        if(washCheckModel != null){
            WashCheckAlertRule washCheckAlertRule = new WashCheckAlertRule(getView().getContext(),washCheckModel.getLastVisit(),dateCreatedFamily);
            if(washCheckAlertRule.isDueWithinMonth()){
                washCheckModel.setStatus(ChildProfileInteractor.VisitType.DUE.name());
            }
            else if(washCheckAlertRule.isOverdueWithinMonth(1)){
                washCheckModel.setStatus(ChildProfileInteractor.VisitType.OVERDUE.name());
            }
            else {
                washCheckModel.setStatus(ImmunizationState.NO_ALERT.name());
            }
            washCheckModel.setLastVisitDate(washCheckAlertRule.noOfDayDue);
        }
        if(getView() instanceof FamilyProfileDueFragment){
            FamilyProfileDueFragment familyProfileDueFragment = (FamilyProfileDueFragment)getView();
            familyProfileDueFragment.updateWashCheckBar(washCheckModel);
        }
    }
}
