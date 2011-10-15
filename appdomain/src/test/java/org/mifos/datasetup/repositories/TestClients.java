package org.mifos.datasetup.repositories;

import org.hibernate.Session;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.ClientDetailEntity;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.factories.MeetingFactory;
import org.mifos.security.util.UserContext;

public class TestClients extends TestEntities {
    private TestPersonnels testPersonnels;

    public TestClients(Session session) {
        super(session, PersonnelBO.class);
        this.testPersonnels = new TestPersonnels(session);
    }

    public ClientBO add(String firstName, OfficeBO office, MeetingBO meeting, PersonnelBO loanOfficer, short groupFlag) {
        UserContext userContext = new UserContext();
        ClientDetailEntity clientDetailEntity = new ClientDetailEntity();
        ClientBO clientBO = new ClientBO(userContext, firstName, CustomerStatus.CLIENT_ACTIVE, TestDates.RECENT, office, meeting, loanOfficer,
                testPersonnels.any(), TestDates.OLD_ENOUGH, "123456", true, TestDates.RECENT, groupFlag, firstName, "Brown", "James", clientDetailEntity);
        session.saveOrUpdate(clientBO);
        return clientBO;
    }

    public ClientBO addAny(String displayId, OfficeBO office, PersonnelBO loanOfficer) {
        return add(displayId, office, MeetingFactory.create(), loanOfficer, YesNoFlag.YES.getValue());
    }
}
