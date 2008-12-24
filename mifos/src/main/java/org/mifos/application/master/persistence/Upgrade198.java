package org.mifos.application.master.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.SqlUpgrade;
import org.mifos.framework.persistence.Upgrade;


	/**
	 * Upgrade198 is a conditional upgrade that removes 7 orphan lookup values 
	 * (DBUpgrade.OfficeLevels.Unsued,DBUpgrade.PrdApplicableMaster.Unused, DBUpgrade.InterestCalcRule.Unused,
	 *  DBUpgrade.Address3.Unused, DBUpgrade.City.Unused,DBUpgrade.LoanPurposes1.Unused,DBUpgrade.LoanPurposes2.Unused) 
	 * from the database if the database is clean (the proxy for "clean" is 
	 * that no other offices than the default head offices exist in the database)
	 */
	public class Upgrade198 extends Upgrade {

		public Upgrade198() {
			super(198);
		}

			
		/**
		 * Don't apply the upgrade if there is any data in the database.  
		 * This upgrade is used to keep the testing framework consistent in
		 * being able to upgrade and downgrade a "clean" database.
		 */
		@Override
		public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) throws IOException, SQLException {
			if (noOfficesHaveBeenCreatedByEndUsers(connection)) {
				SqlUpgrade upgrade = databaseVersionPersistence.findUpgradeScript(this.higherVersion(),"upgrade_to_198_conditional.sql");
				upgrade.runScript(connection);
			}
			upgradeVersion(connection);
		}


}
