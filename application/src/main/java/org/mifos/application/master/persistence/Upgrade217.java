package org.mifos.application.master.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import net.sourceforge.mayfly.MayflySqlException;

import org.mifos.framework.persistence.SqlUpgrade;
import org.mifos.framework.persistence.Upgrade;
import org.mifos.framework.util.SqlUpgradeScriptFinder;

/**
 * Upgrade217 is a conditional upgrade because Mayfly doesn't understands the
 * syntax of sql script(Mysql specific ALTER statements) in this upgrade.
 */
public class Upgrade217 extends Upgrade {

    public Upgrade217() {
        super(217);
    }

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {
        try {
            SqlUpgrade upgrade = SqlUpgradeScriptFinder.findUpgradeScript(this.higherVersion(),
                    "upgrade_to_217_conditional.sql");
            upgrade.runScript(connection);

        } catch (MayflySqlException mayflySqlException) {
            getLogger().info(
                    "Upgrade217 is a conditional upgrade because Mayfly doesn't understands the"
                            + " syntax of sql script(Mysql specific ALTER statements) in this upgrade.");
        }
        upgradeVersion(connection);
    }

}
