package org.smartgresiter.wcaro.presenter;

import org.json.JSONObject;
import org.smartgresiter.wcaro.contract.FamilyRemoveMemberContract;
import org.smartgresiter.wcaro.interactor.FamilyRemoveMemberInteractor;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class FamilyRemoveMemberPresenter extends FamilyProfileMemberPresenter implements FamilyRemoveMemberContract.Presenter {

    FamilyRemoveMemberContract.Model model;
    protected WeakReference<FamilyRemoveMemberContract.View> viewReference;
    FamilyRemoveMemberContract.Interactor interactor;

    private String familyHead;
    private String primaryCaregiver;

    public FamilyRemoveMemberPresenter(FamilyRemoveMemberContract.View view, FamilyRemoveMemberContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId, String familyHead, String primaryCaregiver) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId, familyHead, primaryCaregiver);

        this.model = model;
        this.viewReference = new WeakReference<>(view);
        this.interactor = new FamilyRemoveMemberInteractor();
        this.familyHead = familyHead;
        this.primaryCaregiver = primaryCaregiver;
    }


    @Override
    public void removeMember(CommonPersonObjectClient client) {

        // read from the local database and verify if
        // 1. id HOF launch hof
        // 2. is PCG launch pcg
        // 3. none.. launch remove event

        String memberID = client.getColumnmaps().get(DBConstants.KEY.BASE_ENTITY_ID);
        if (memberID.equalsIgnoreCase(familyHead) ||
                memberID.equalsIgnoreCase(primaryCaregiver)) {

            // interactor.processFamilyMember(familyBaseEntityId, this);

        } else {

            model.prepareJsonForm(client);
            JSONObject form = model.prepareJsonForm(client);
            if (form != null) {
                String baseEntityId = client.getColumnmaps().get(DBConstants.KEY.BASE_ENTITY_ID);


                viewReference.get().startJsonActivity(form);
            }
        }
    }

    @Override
    public void processMember(Map<String, String> familyDetails, CommonPersonObject client) {

    }

    @Override
    public void displayChangeFamilyHeadDialog(CommonPersonObjectClient client) {

    }

    @Override
    public void displayChangeCareGiverDialog(CommonPersonObjectClient client) {

    }


    @Override
    public void changeCareGiver(String familyID, String memberID) {

    }

    @Override
    public void changeHeadOfFamily(String familyID, String memberID) {

    }

    @Override
    public void removeEveryone() {

    }

    @Override
    public void initialize() {

    }

    @Override
    public void processMember(HashMap<String, String> res) {

    }
}