package org.smartregister.chw.activity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.presenter.BaseAncHomeVisitPresenter;
import org.smartregister.chw.core.activity.CoreChildHomeVisitActivity;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.dao.VisitDao;
import org.smartregister.chw.core.interactor.CoreChildHomeVisitInteractor;
import org.smartregister.chw.interactor.ChildHomeVisitInteractorFlv;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.family.util.DBConstants;

import java.util.List;

public class ChildHomeVisitActivity extends CoreChildHomeVisitActivity {
    @Override
    protected void registerPresenter() {
        presenter = new BaseAncHomeVisitPresenter(memberObject, this, new CoreChildHomeVisitInteractor(new ChildHomeVisitInteractorFlv()));
    }


    @Override
    public void submittedAndClose() {
        super.submittedAndClose();

        // updating birth certification
        String baseEntityID = memberObject.getBaseEntityId();
        List<Visit> summaryMap = VisitDao.getVisitsByMemberID(baseEntityID);
        if (summaryMap.size() > 0) {
            for (int i = 0; i < summaryMap.size(); i++) {
                if (summaryMap.get(i).getVisitType().equals("Birth Certification")) {
                    String birthCertification = summaryMap.get(i).getJson();
                    updateBirthRegistration(birthCertification, baseEntityID);
                    break;
                }
            }
        }

        ChildProfileActivity.startMe(this, memberObject, ChildProfileActivity.class);
    }

    private void updateBirthRegistration(String birthCertification, String baseEntityID) {
        AllCommonsRepository allCommonsRepository = CoreChwApplication.getInstance().getAllCommonsRepository("ec_child");
        String tableName = "ec_child";
        try {
            JSONObject response = new JSONObject(birthCertification);
            JSONArray array = response.getJSONArray("obs");
            JSONObject birthCert = array.getJSONObject(0);
            if (birthCert.getJSONArray("humanReadableValues").get(0).toString().equals("Yes")){
                String dateOfBirthCertificate = array.getJSONObject(1).getJSONArray("values").get(0).toString();
                String birthCertNum = array.getJSONObject(2).getJSONArray("values").get(0).toString();
                String sql = "UPDATE ec_child SET birth_cert = ?, birth_cert_issue_date = ?, birth_cert_num = ? WHERE id = ?";
                String[] selectionArgs = {"Yes", dateOfBirthCertificate, birthCertNum, baseEntityID};
                allCommonsRepository.customQuery(sql, selectionArgs, tableName);
            }else if (array.getJSONObject(1).getString("fieldCode").equals("birthRegistration")){
                String registration = "";
                try{
                    registration = array.getJSONObject(1).getJSONArray("humanReadableValues").get(0).toString();
                }catch (Exception e){
                    registration = "No";
                    e.printStackTrace();
                }
                if (registration.equals("Yes")) {
                    String sql = "UPDATE ec_child SET birth_cert = ?, birth_registration = ?, birth_notification = ? WHERE id = ?";
                    String[] selectionArgs = {"No", "Yes", "No", baseEntityID};
                    allCommonsRepository.customQuery(sql, selectionArgs, tableName);
                } else {
                    String notification = "";
                    try{
                        notification = array.getJSONObject(2).getJSONArray("humanReadableValues").get(0).toString();
                    }catch (Exception e){
                        notification = "No";
                        e.printStackTrace();
                    }
                    String sql = "UPDATE ec_child SET birth_cert = ?, birth_registration = ?, birth_notification = ? WHERE id = ?";
                    String[] selectionArgs = {"No", "No", notification, getIntent().getStringExtra(DBConstants.KEY.BASE_ENTITY_ID).toLowerCase()};
                    allCommonsRepository.customQuery(sql, selectionArgs, tableName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
