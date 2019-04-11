package org.smartregister.chw.presenter;

import android.util.Log;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.chw.contract.FamilyOtherMemberProfileExtendedContract;
import org.smartregister.chw.contract.FamilyProfileExtendedContract;
import org.smartregister.chw.interactor.FamilyInteractor;
import org.smartregister.chw.interactor.FamilyProfileInteractor;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.contract.FamilyOtherMemberContract;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.model.BaseFamilyProfileModel;
import org.smartregister.family.presenter.BaseFamilyOtherMemberProfileActivityPresenter;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static org.smartregister.util.Utils.getName;

public class FamilyOtherMemberActivityPresenter extends BaseFamilyOtherMemberProfileActivityPresenter implements FamilyOtherMemberProfileExtendedContract.Presenter, FamilyProfileContract.InteractorCallBack, FamilyProfileExtendedContract.PresenterCallBack {
    private static final String TAG = FamilyOtherMemberActivityPresenter.class.getCanonicalName();

    private WeakReference<FamilyOtherMemberProfileExtendedContract.View> viewReference;
    private String familyBaseEntityId;
    private String familyName;

    private FamilyProfileContract.Interactor profileInteractor;
    private FamilyProfileContract.Model profileModel;

    public FamilyOtherMemberActivityPresenter(FamilyOtherMemberProfileExtendedContract.View view, FamilyOtherMemberContract.Model model,
                                              String viewConfigurationIdentifier, String familyBaseEntityId, String baseEntityId,
                                              String familyHead, String primaryCaregiver, String villageTown, String familyName) {
        super(view, model, viewConfigurationIdentifier, baseEntityId, familyHead, primaryCaregiver, villageTown);
        viewReference = new WeakReference<>(view);
        this.familyBaseEntityId = familyBaseEntityId;
        this.familyName = familyName;

        this.profileInteractor = new FamilyProfileInteractor();
        this.profileModel = new BaseFamilyProfileModel(familyName);

        verifyHasPhone();
        initializeServiceStatus();
    }

    private void initializeServiceStatus() {
        FamilyInteractor.updateFamilyDueStatus("", familyBaseEntityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.v(TAG, "initializeServiceStatus onSubscribe");
                    }

                    @Override
                    public void onNext(String s) {
                        updateFamilyMemberServiceDue(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "initializeServiceStatus " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.v(TAG, "initializeServiceStatus onComplete");
                    }
                });
    }

    public String getFamilyBaseEntityId() {
        return familyBaseEntityId;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void updateFamilyMember(String jsonString) {

        try {
            getView().showProgressDialog(org.smartregister.family.R.string.saving_dialog_title);

            FamilyEventClient familyEventClient = profileModel.processUpdateMemberRegistration(jsonString, familyBaseEntityId);
            if (familyEventClient == null) {
                return;
            }

            profileInteractor.saveRegistration(familyEventClient, jsonString, true, this);
        } catch (Exception e) {
            getView().hideProgressDialog();
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void refreshProfileTopSection(CommonPersonObjectClient client) {
        super.refreshProfileTopSection(client);
        if (client != null && client.getColumnmaps() != null) {
            String firstName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
            String middleName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
            String lastName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
            int age = Utils.getAgeFromDate(Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, true));

            this.getView().setProfileName(MessageFormat.format("{0}, {1}", getName(getName(firstName, middleName), lastName), age));
        }
    }

    public void startFormForEdit(CommonPersonObjectClient commonPersonObject) {
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        //TODO Implement
        Log.d(TAG, "onUniqueIdFetched unimplemented");
    }

    @Override
    public void onNoUniqueId() {
        //TODO Implement
        Log.d(TAG, "onNoUniqueId unimplemented");
    }

    @Override
    public void onRegistrationSaved(boolean isEditMode) {
        if (isEditMode) {
            getView().hideProgressDialog();

            refreshProfileView();

            getView().refreshList();
        }
    }

    @Override
    public void verifyHasPhone() {
        ((FamilyProfileInteractor) profileInteractor).verifyHasPhone(familyBaseEntityId, this);
    }

    public FamilyOtherMemberProfileExtendedContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }

    @Override
    public void notifyHasPhone(boolean hasPhone) {
        if (viewReference.get() != null) {
            viewReference.get().updateHasPhone(hasPhone);
        }
    }

    @Override
    public void updateFamilyMemberServiceDue(String serviceDueStatus) {
        if (getView() != null) {
            getView().setFamilyServiceStatus(serviceDueStatus);
        }

    }
}
