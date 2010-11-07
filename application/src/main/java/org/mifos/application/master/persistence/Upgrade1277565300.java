package org.mifos.application.master.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifos.framework.persistence.SqlUpgrade;
import org.mifos.framework.persistence.Upgrade;
import org.mifos.framework.util.SqlUpgradeScriptFinder;

public class Upgrade1277565300 extends Upgrade {

    @Override
    @SuppressWarnings("PMD.CloseResource") // resource gets closed
    public void upgrade(Connection connection) throws IOException, SQLException {

        if (alreadyUpgraded(connection)) {
            return;
        }

        ResultSet rs;
        PreparedStatement updateSpouseFatherLookup = connection
                .prepareStatement("update spouse_father_lookup set lookup_id=? where spouse_father_id = ?");


        SqlUpgrade upgrade = SqlUpgradeScriptFinder.findUpgradeScript("upgrade1277565300_conditional.sql");
        upgrade.runScript(connection);

        connection.createStatement().execute("set foreign_key_checks=0");

        // delete Hungarian lookupValue
        rs = connection.createStatement().executeQuery("select lookup_id from language where lang_name='Hungarian'");
        rs.first();
        int hungarianLookupId = rs.getInt(1);
        deleteFromLookupValue(connection, hungarianLookupId);

        // delete child lookupValue
        rs = connection.createStatement().executeQuery(
                "select lookup_id from spouse_father_lookup where spouse_father_id=5");
        rs.first();
        int childLookupId = rs.getInt(1);
        deleteFromLookupValue(connection, childLookupId);

        // delete mother lookupValue
        rs = connection.createStatement().executeQuery(
                "select lookup_id from spouse_father_lookup where spouse_father_id=4");
        rs.first();
        int motherLookupId = rs.getInt(1);
        deleteFromLookupValue(connection, motherLookupId);

        // delete NotTogether lookupValue
        rs = connection.createStatement().executeQuery(
                "select lookup_id from lookup_value where lookup_name='NotTogether'");
        rs.first();
        int notTogetherLookupId = rs.getInt(1);
        deleteFromLookupValue(connection, notTogetherLookupId);

        // delete together lookupValue
        rs = connection.createStatement().executeQuery(
                "select lookup_id from lookup_value where lookup_name='Together'");
        rs.first();
        int togetherLookupId = rs.getInt(1);
        deleteFromLookupValue(connection, togetherLookupId);

        // insert together lookupValue
        insertLookupValue(connection, 92, "Together");
        insertLookupValue(connection, 92, "NotTogether");

        // insert mother lookupValue
        motherLookupId = insertLookupValue(connection, 52, "Mother");
        updateSpouseFatherLookup.setInt(1, motherLookupId);
        updateSpouseFatherLookup.setInt(2, 4);
        updateSpouseFatherLookup.execute();

        // insert child lookupValue
        childLookupId = insertLookupValue(connection, 52, "Child");
        updateSpouseFatherLookup.setInt(1, childLookupId);
        updateSpouseFatherLookup.setInt(2, 5);
        updateSpouseFatherLookup.execute();

        // insert Hungarian lookupValue
        hungarianLookupId = insertLookupValue(connection, 74, "Language-Hungarian");
        connection.createStatement()
                .execute("update language set lookup_id=" + hungarianLookupId + " where lang_id=11");

        rs.close();
        connection.createStatement().execute("set foreign_key_checks=1");
    }

    @SuppressWarnings("PMD.CloseResource") // resource gets closed
    private boolean alreadyUpgraded(Connection connection) throws SQLException {
        // upgrade not necessary if upgrade1277587947 has been applied or database is at version 244
        // see GIT commit 85908349d85f470dca10261e4dbe7545b0cbebce
        boolean upgraded = false;
        ResultSet rs = connection.createStatement().executeQuery(
                "select upgrade_id from applied_upgrades where upgrade_id=1277587947");
        if (rs.first()) {
            upgraded = true;
        }
        rs.close();
        return upgraded;
    }

}
