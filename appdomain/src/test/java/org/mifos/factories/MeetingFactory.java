package org.mifos.factories;

import java.util.Date;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;

public class MeetingFactory {
    public static MeetingBO create() {
        return new MeetingBO(MeetingType.LOAN_INSTALLMENT, new Date(), "home");
    }
}
