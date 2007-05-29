package org.mifos.framework.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class Upgrade {

	private final int higherVersion;

	protected Upgrade(int higherVersion) {
		this.higherVersion = higherVersion;
	}

	abstract public void upgrade(Connection conn) 
	throws IOException, SQLException;

	abstract public void downgrade(Connection conn) 
	throws IOException, SQLException;

	public int higherVersion() {
		return higherVersion;
	}

	public int downgradeTo() {
		return higherVersion - 1;
	}

}
