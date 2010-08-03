package org.mifos.application.master.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.mifos.framework.persistence.Upgrade;
import org.mifos.security.AddActivity;
import org.mifos.security.util.SecurityConstants;

public class Upgrade1280721170 extends Upgrade {

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {
        new AddActivity("Permissions-CanViewActiveSessions", SecurityConstants.CAN_VIEW_ACTIVE_SESSIONS,
                SecurityConstants.SYSTEM_INFORMATION).upgrade(connection);
        new AddActivity("Permissions-CanStartMifosShutDown", SecurityConstants.CAN_SHUT_DOWN_MIFOS,
                SecurityConstants.SYSTEM_INFORMATION).upgrade(connection);
    }

}
