package org.smartgresiter.wcaro.interactor;

import android.content.Context;

import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartgresiter.wcaro.domain.FamilyMember;
import org.smartgresiter.wcaro.presenter.FamilyChangeContractPresenter;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.family.util.AppExecutors;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;

public class FamilyChangeContractInteractorTest {

    @Test
    public void testUpdateFamilyMember() throws Exception {

        FamilyChangeContractInteractor interactor = Mockito.spy(FamilyChangeContractInteractor.class);

        Whitebox.setInternalState(interactor, "appExecutors", new AppExecutors());

        HashMap<String, String> familyMember = new HashMap<>();
        familyMember.put(Constants.PROFILE_CHANGE_ACTION.ACTION_TYPE, Constants.PROFILE_CHANGE_ACTION.PRIMARY_CARE_GIVER);

        String familyID = "";
        String lastLocationId = "";
        FamilyChangeContractPresenter presenter = Mockito.mock(FamilyChangeContractPresenter.class);
        Context context = Mockito.mock(Context.class);

        Mockito.doNothing().when(interactor).updateFamilyRelations(Mockito.eq(context), any(FamilyMember.class), Mockito.eq(lastLocationId));

        interactor.updateFamilyMember(context, familyMember, familyID, lastLocationId, presenter);

        // update relations was called
        Mockito.verify(interactor).updateFamilyRelations(Mockito.eq(context), any(FamilyMember.class), Mockito.eq(lastLocationId));
    }
}
