package com.puthuvaazhvu.mapping.application;

import com.puthuvaazhvu.mapping.application.modal.ApplicationData;
import com.puthuvaazhvu.mapping.modals.Survey;

/**
 * Created by muthuveerappans on 12/21/17.
 */

// Contains the global data for the application
public class GlobalContext {
    private static GlobalContext globalContext;
    private ApplicationData applicationData;

    public static GlobalContext getInstance() {
        if (globalContext == null) {
            globalContext = new GlobalContext();
        }
        return globalContext;
    }

    private GlobalContext() {
        applicationData = ApplicationData.getInstance();
    }

    public ApplicationData getApplicationData() {
        return applicationData;
    }
}
