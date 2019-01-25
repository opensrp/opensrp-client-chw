package org.smartgresiter.wcaro.presenter;

import org.json.JSONObject;
import org.smartgresiter.wcaro.contract.FamilyRemoveMemberContract;
import org.smartgresiter.wcaro.interactor.FamilyRemoveMemberInteractor;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.view.LocationPickerView;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

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

        String memberID = client.getColumnmaps().get(DBConstants.KEY.BASE_ENTITY_ID);
        if (memberID.equalsIgnoreCase(familyHead) ||
                memberID.equalsIgnoreCase(primaryCaregiver)) {

            interactor.processFamilyMember(familyBaseEntityId, client, this);

        } else {

            JSONObject form = model.prepareJsonForm(client, getForm(client));
            if (form != null) {
                viewReference.get().startJsonActivity(form);
            }
        }
    }

    @Override
    public void processMember(Map<String, String> familyDetails, CommonPersonObjectClient client) {
        String memberID = client.getColumnmaps().get(DBConstants.KEY.BASE_ENTITY_ID);
        String currentFamilyHead = familyDetails.get(Constants.RELATIONSHIP.FAMILY_HEAD);
        String currentCareGiver = familyDetails.get(Constants.RELATIONSHIP.PRIMARY_CAREGIVER);

        if (memberID != null) {
            if (memberID.equalsIgnoreCase(currentFamilyHead)) {

                if (viewReference.get() != null) {
                    viewReference.get().displayChangeFamilyHeadDialog(client, memberID);
                }

            } else if (memberID.equalsIgnoreCase(currentCareGiver)) {

                if (viewReference.get() != null) {
                    viewReference.get().displayChangeCareGiverDialog(client, memberID);
                }

            } else {

                JSONObject form = model.prepareJsonForm(client, getForm(client));
                if (form != null) {
                    viewReference.get().startJsonActivity(form);
                }
            }
        }
    }

    private String getForm(CommonPersonObjectClient client) {
        Date dob = Utils.dobStringToDate(Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false));
        return ((dob != null && getDiffYears(dob, new Date()) >= 5) ? Constants.JSON_FORM.FAMILY_DETAILS_REMOVE_MEMBER : Constants.JSON_FORM.FAMILY_DETAILS_REMOVE_CHILD);
    }

    private int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }

    private Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    @Override
    public void removeEveryone(String details) {

        JSONObject form = model.prepareFamilyRemovalForm(familyBaseEntityId, details);
        if (form != null) {
            viewReference.get().startJsonActivity(form);
        }

    }

    @Override
    public void onFamilyRemoved(Boolean success) {
        if (success) {
            // close
            if (viewReference.get() != null) {
                viewReference.get().onEveryoneRemoved();
            }
        }
    }

    @Override
    public void processRemoveForm(JSONObject jsonObject) {

        LocationPickerView lpv = new LocationPickerView(viewReference.get().getContext());
        lpv.init();
        String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());

        interactor.removeMember(familyBaseEntityId, lastLocationId, jsonObject, this);
    }

    @Override
    public void memberRemoved(String removalType) {
        if (viewReference.get() != null) {
            viewReference.get().onMemberRemoved(removalType);
        }
    }

    @Override
    public String getDefaultSortQuery() {
        return String.format(" %s ASC ", DBConstants.KEY.DOB);
    }

    @Override
    public String getMainCondition() {
        return String.format(" %s = '%s' and %s is null and %s is null ",
                DBConstants.KEY.OBJECT_RELATIONAL_ID, familyBaseEntityId,
                DBConstants.KEY.DATE_REMOVED ,
                DBConstants.KEY.DOD
        );
    }
}