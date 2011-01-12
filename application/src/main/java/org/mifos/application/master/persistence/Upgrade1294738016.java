package org.mifos.application.master.persistence;

import org.mifos.framework.persistence.Upgrade;
import org.mifos.security.AddActivity;
import org.mifos.security.util.SecurityConstants;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class Upgrade1294738016 extends Upgrade {
    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {
        new AddActivity("Permissions-CanAdjustBackDatedTransactions", SecurityConstants.LOAN_ADJUST_BACK_DATED_TRXNS,
                SecurityConstants.LOAN_MANAGEMENT).upgrade(connection);
    }
}
