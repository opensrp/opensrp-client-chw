package org.smartgresiter.wcaro.presenter;

import org.json.JSONObject;
import org.smartgresiter.wcaro.contract.FamilyRemoveMemberContract;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;

import java.lang.ref.WeakReference;

public class FamilyRemoveMemberPresenter extends FamilyProfileMemberPresenter implements FamilyRemoveMemberContract.Presenter {

    FamilyRemoveMemberContract.Model model;
    protected WeakReference<FamilyRemoveMemberContract.View> viewReference;

    public FamilyRemoveMemberPresenter(FamilyRemoveMemberContract.View view, FamilyRemoveMemberContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId, String familyHead, String primaryCaregiver) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId, familyHead, primaryCaregiver);

        this.model = model;
        this.viewReference = new WeakReference<>(view);
    }


    @Override
    public void removeMember(CommonPersonObjectClient client) {
        model.prepareJsonForm(client);
        JSONObject form = model.prepareJsonForm(client);
        if (form != null) {
            String baseEntityId = client.getColumnmaps().get(DBConstants.KEY.BASE_ENTITY_ID);


            viewReference.get().startJsonActivity(form);
        }
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
}