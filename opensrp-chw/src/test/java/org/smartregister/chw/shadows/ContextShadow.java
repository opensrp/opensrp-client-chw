package org.smartregister.chw.shadows;

import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.Context;
import org.smartregister.repository.FormDataRepository;

/**
 * @author rkodev
 */

@Implements(Context.class)
public class ContextShadow {

    @Implementation
    public FormDataRepository formDataRepository() {
        return Mockito.mock(FormDataRepository.class);
    }
}
