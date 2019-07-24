package org.smartregister.chw.presenter;

import android.database.Cursor;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.smartregister.chw.R;
import org.smartregister.chw.fragment.FamilyProfileActivityFragment;
import org.smartregister.chw.model.WashCheckModel;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.Utils;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.contract.FamilyProfileActivityContract;
import org.smartregister.family.presenter.BaseFamilyProfileActivityPresenter;
import org.smartregister.family.util.DBConstants;

import java.text.MessageFormat;
import java.util.ArrayList;

import timber.log.Timber;

public class FamilyProfileActivityPresenter extends BaseFamilyProfileActivityPresenter {

    public FamilyProfileActivityPresenter(FamilyProfileActivityContract.View view, FamilyProfileActivityContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId);
    }

    @Override
    public String getMainCondition() {
        return String.format(" %s = '%s' and %s is null and ( %s is null OR %s != '0') ", Constants.TABLE_NAME.CHILD_ACTIVITY + ".relational_id", this.familyBaseEntityId, Constants.TABLE_NAME.CHILD_ACTIVITY + "." + DBConstants.KEY.DATE_REMOVED, Constants.TABLE_NAME.CHILD_ACTIVITY + "." + DBConstants.KEY.DATE_VISIT_NOT_DONE, Constants.TABLE_NAME.CHILD_ACTIVITY + "." + DBConstants.KEY.DATE_VISIT_NOT_DONE);
    }

    @Override
    public String getDefaultSortQuery() {
        return Constants.TABLE_NAME.CHILD_ACTIVITY + "." + ChildDBConstants.KEY.EVENT_DATE + " DESC";
    }
    public void fetchLastWashCheck(String familyId) {
        ArrayList<WashCheckModel> washCheckModelArrayList = new ArrayList<>();
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(Constants.TABLE_NAME.WASH_CHECK_LOG, new String[]{DBConstants.KEY.BASE_ENTITY_ID,"last_visit","details_info"});
        queryBUilder.mainCondition(MessageFormat.format("{0}.{1} = ''{2}''", Constants.TABLE_NAME.WASH_CHECK_LOG, DBConstants.KEY.BASE_ENTITY_ID, familyId));
        String query = queryBUilder.orderbyCondition("last_visit" + " DESC");
        Cursor cursor = null;
        try {
            CommonRepository commonRepository = Utils.context().commonrepository(Constants.TABLE_NAME.WASH_CHECK_LOG);
            cursor = commonRepository.rawCustomQueryForAdapter(query);
            if (cursor != null && cursor.moveToFirst()) {
                WashCheckModel washCheckModel= new WashCheckModel();
                String lastVisitStr = cursor.getString(cursor.getColumnIndex("last_visit"));
                String detailsInfo =cursor.getString(cursor.getColumnIndex("details_info"));
                washCheckModel.setLastVisit(Long.parseLong(lastVisitStr));
                washCheckModel.setDetailsJson(detailsInfo);
                washCheckModel.setFamilyBaseEntityId(familyId);
                washCheckModel.setLastVisitDate(getLastVisitDays(washCheckModel.getLastVisit()));
                washCheckModelArrayList.add(washCheckModel);
            }
        } catch (Exception ex) {
            Timber.e(ex.toString());
        } finally {
            if (cursor != null)
                cursor.close();
        }
        if(getView() instanceof FamilyProfileActivityFragment){
            FamilyProfileActivityFragment familyProfileDueFragment = (FamilyProfileActivityFragment)getView();
            familyProfileDueFragment.updateWashCheckBar(washCheckModelArrayList);
        }
    }
    private String getLastVisitDays(long lastVisit){
        LocalDate lastVisitDate = new LocalDate(lastVisit);
        LocalDate todayDate = new LocalDate();
        int daysDiff = Days.daysBetween(lastVisitDate, todayDate).getDays();
        return daysDiff+" "+getView().getContext().getString(R.string.days);
    }
}
