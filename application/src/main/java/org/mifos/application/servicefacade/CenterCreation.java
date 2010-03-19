package org.mifos.application.servicefacade;

import java.util.Locale;

public class CenterCreation {

    private final Short officeId;
    private final Short loggedInUserId;
    private final Short loggedInUserLevelId;
    private final Locale locale;

    public CenterCreation(Short officeId, Short userId, Short userLevelId, Locale locale) {
        this.officeId = officeId;
        this.loggedInUserId = userId;
        this.loggedInUserLevelId = userLevelId;
        this.locale = locale;
    }

    public Short getOfficeId() {
        return this.officeId;
    }

    public Short getLoggedInUserId() {
        return this.loggedInUserId;
    }

    public Short getLoggedInUserLevelId() {
        return this.loggedInUserLevelId;
    }

    public Locale getLocale() {
        return this.locale;
    }
}

