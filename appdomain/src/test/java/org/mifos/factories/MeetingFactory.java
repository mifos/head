package org.mifos.factories;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;

import java.util.Date;

public class MeetingFactory {
    public static MeetingBO create() {
        return new MeetingBO(MeetingType.LOAN_INSTALLMENT, new Date(), "home");
    }
}
