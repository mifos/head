package org.mifos.framework.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class CompositeUpgrade extends Upgrade {
	
	private final Upgrade[] upgrades;

	public static int findVersion(Upgrade... upgrades) {
		if (upgrades.length == 0) {
			throw new RuntimeException("must specify at least one upgrade");
		}
		int version = upgrades[0].higherVersion();
		for (int i = 1; i < upgrades.length; ++i) {
			int thisVersion = upgrades[i].higherVersion();
			if (thisVersion != version) {
				throw new RuntimeException(
					"got upgrades to " + version + " and " + thisVersion + 
					" but expected matching versions");
			}
		}
		return version;
	}

	protected CompositeUpgrade(Upgrade... upgrades) {
		super(findVersion(upgrades));
		this.upgrades = upgrades;
	}

	@Override
	public void downgrade(Connection connection) throws IOException,
			SQLException {
		for (int i = upgrades.length - 1; i >= 0; --i) {
			upgrades[i].downgrade(connection);
		}
	}

	@Override
	public void upgrade(Connection connection) throws IOException, SQLException {
		for (Upgrade upgrade : upgrades) {
			upgrade.upgrade(connection);
		}
	}

}
