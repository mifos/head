package org.mifos.application.master.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifos.framework.persistence.SqlUpgrade;
import org.mifos.framework.persistence.Upgrade;
import org.mifos.framework.util.SqlUpgradeScriptFinder;

public class Upgrade1277565300 extends Upgrade{

    public Upgrade1277565300() {
        super();
    }

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {
        if (!alreadyUpgraded(connection)) {
            SqlUpgrade upgrade = SqlUpgradeScriptFinder.findUpgradeScript(
                    "upgrade1277565300_conditional.sql");
            upgrade.runScript(connection);
        }

    }

    private boolean alreadyUpgraded(Connection connection) throws SQLException  {
        // upgrade not necessary if 1277587947 or version 244 has been applied
        // see git commit 85908349d85f470dca10261e4dbe7545b0cbebce
        ResultSet rs = connection.createStatement().executeQuery("select upgrade_id from applied_upgrades where upgrade_id=1277587947");
        if (rs.first()){
            return true;
        }

        rs = connection.createStatement().executeQuery("select lookup_id, entity_id, lookup_name from lookup_value where lookup_id=620");
        Boolean skipUpdate = Boolean.FALSE;
        if (rs.next() ==false) {
            return false;
        }
        rs.first();

        if (rs.getInt(1) ==620 && rs.getInt(2) == 92 && rs.getString(3).equals("Together")) {
                skipUpdate = true;
        }
        return skipUpdate;
    }



}
