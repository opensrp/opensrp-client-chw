package org.smartregister.chw.interactor;

import android.content.Context;
import android.content.res.AssetManager;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.repository.VisitRepository;

import java.util.LinkedHashMap;
import java.util.Locale;


@RunWith(MockitoJUnitRunner.class)
public abstract class BaseHomeVisitInteractorFlvTest {

    @Mock
    protected MemberObject memberObject;

    @Mock
    protected LinkedHashMap<String, BaseAncHomeVisitAction> actionList;

    @Mock
    protected Context context;

    protected Locale locale = Locale.ENGLISH;

    @Mock
    protected AssetManager assetManager;

    protected final String title = "Sample Title";

    @Mock
    protected BaseAncHomeVisitAction.Builder builder;

    @Mock
    protected BaseAncHomeVisitAction ancHomeVisitAction;

    @Mock
    protected VisitRepository visitRepository;

    @Mock
    protected BaseAncHomeVisitContract.View view;

    @Mock
    protected BaseAncHomeVisitContract.InteractorCallBack callBack;
}
