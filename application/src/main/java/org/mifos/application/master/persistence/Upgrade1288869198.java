package org.mifos.application.master.persistence;

import org.mifos.framework.persistence.Upgrade;
import org.mifos.security.AddActivity;
import org.mifos.security.util.SecurityConstants;

import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;

public class Upgrade1288869198 extends Upgrade {
    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {
        new AddActivity("Permissions-CanActivateQuestionGroups", SecurityConstants.CAN_ACTIVATE_QUESTION_GROUPS,
                SecurityConstants.CONFIGURATION_MANAGEMENT).upgrade(connection);
    }
}
