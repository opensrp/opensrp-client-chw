package org.smartregister.chw.interactor;

import android.content.Context;
import android.content.res.AssetManager;

import com.opensrp.chw.core.utils.Constants;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.smartregister.chw.activity.FamilyProfileActivity;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.presenter.FamilyProfilePresenter;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.activity.FamilyWizardFormActivity;
import org.smartregister.family.domain.FamilyMetadata;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.Executor;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class, ChwApplication.class})
public class FamilyProfileInteractorTest {

    private FamilyProfileInteractor interactor;

    @Mock
    private HashMap<String, String> details;

    @Mock
    private FamilyProfilePresenter profilePresenter;

    @Mock
    private ChwApplication chwApplication;

    @Mock
    private Context context;

    @Mock
    private AssetManager assetManager;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(ChwApplication.class);

        /* Mock reading of assets*/
        PowerMockito.when(ChwApplication.getInstance()).thenReturn(chwApplication);
        PowerMockito.when(ChwApplication.getCurrentLocale()).thenReturn(new Locale("fr", "FR"));
        PowerMockito.when(chwApplication.getApplicationContext()).thenReturn(context);
        PowerMockito.when(context.getAssets()).thenReturn(assetManager);

        String sampleJson = " " +
                "{ " +
                "  \"validate_on_submit\": true,\n" +
                "  \"show_errors_on_submit\": false,\n" +
                "  \"count\": \"2\",\n" +
                "  \"encounter_type\": \"Family Registration\",\n" +
                "  \"entity_id\": \"\"" +
                "}";

        InputStream inputStream = IOUtils.toInputStream(sampleJson);
        Mockito.when(assetManager.open(Mockito.anyString())).thenReturn(inputStream);

        AppExecutors appExecutors = Mockito.spy(AppExecutors.class);
        Executor executor = Mockito.mock(Executor.class);
        implementAsDirectExecutor(executor);

        interactor = Mockito.spy(FamilyProfileInteractor.class);
        FamilyMetadata metadata = getMetadata();
        CommonRepository commonRepository = Mockito.mock(CommonRepository.class);

        // stub all executor threads with the main thread
        Whitebox.setInternalState(appExecutors, "diskIO", executor);
        Whitebox.setInternalState(appExecutors, "networkIO", executor);
        Whitebox.setInternalState(appExecutors, "mainThread", executor);

        Whitebox.setInternalState(interactor, "appExecutors", appExecutors);

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.metadata()).thenReturn(metadata);

        /*
        PowerMockito.when(
                Utils.getValue(Mockito.anyMapOf(String.class,String.class), Mockito.anyString(), Mockito.anyBoolean())
        ).thenReturn(familyID);
        */

        Mockito.doReturn(commonRepository).when(interactor).getCommonRepository(Mockito.anyString());
        // Mockito.doReturn(familyID).when(getDetails()).get(Mockito.anyString());
        Mockito.doReturn("123Test").when(details).get(Mockito.anyString());

        CommonPersonObject personObject = new CommonPersonObject(null, null, details, null);
        personObject.setColumnmaps(details);

        Mockito.doReturn(personObject).when(commonRepository).findByBaseEntityId(Mockito.anyString());
        Mockito.doReturn(personObject).when(commonRepository).findByBaseEntityId(null);
    }

    private void implementAsDirectExecutor(Executor executor) {
        Mockito.doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                ((Runnable) invocation.getArguments()[0]).run();
                return null;
            }
        }).when(executor).execute(Mockito.any(Runnable.class));
    }

    @Test
    public void testVerifyHasPhone() {

        String familyID = "12345";
        interactor.verifyHasPhone(familyID, profilePresenter);
        // verify that calls are sent to the database

        // verify that the interactor update that view
        Mockito.verify(profilePresenter).notifyHasPhone(Mockito.anyBoolean());
    }

    private FamilyMetadata getMetadata() {
        FamilyMetadata metadata = new FamilyMetadata(FamilyWizardFormActivity.class, FamilyWizardFormActivity.class, FamilyProfileActivity.class, Constants.IDENTIFIER.UNIQUE_IDENTIFIER_KEY, false);
        metadata.updateFamilyRegister(Constants.JSON_FORM.getFamilyRegister(), Constants.TABLE_NAME.FAMILY, Constants.EventType.FAMILY_REGISTRATION, Constants.EventType.UPDATE_FAMILY_REGISTRATION, Constants.CONFIGURATION.FAMILY_REGISTER, Constants.RELATIONSHIP.FAMILY_HEAD, Constants.RELATIONSHIP.PRIMARY_CAREGIVER);
        metadata.updateFamilyMemberRegister(Constants.JSON_FORM.getFamilyMemberRegister(), Constants.TABLE_NAME.FAMILY_MEMBER, Constants.EventType.FAMILY_MEMBER_REGISTRATION, Constants.EventType.UPDATE_FAMILY_MEMBER_REGISTRATION, Constants.CONFIGURATION.FAMILY_MEMBER_REGISTER, Constants.RELATIONSHIP.FAMILY);
        metadata.updateFamilyDueRegister(Constants.TABLE_NAME.CHILD, Integer.MAX_VALUE, false);
        metadata.updateFamilyActivityRegister(Constants.TABLE_NAME.CHILD_ACTIVITY, Integer.MAX_VALUE, false);
        metadata.updateFamilyOtherMemberRegister(Constants.TABLE_NAME.FAMILY_MEMBER, Integer.MAX_VALUE, false);
        return metadata;
    }
}
