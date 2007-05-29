package org.mifos.framework.persistence;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mifos.framework.hibernate.helper.HibernateUtil;

public class DatabaseVersionPersistence {

	public static final int APPLICATION_VERSION = 119;
	public static final int FIRST_NUMBERED_VERSION = 100;

	private final Connection connection;
	
	public DatabaseVersionPersistence() {
		this(HibernateUtil.getOrCreateSessionHolder().getSession().connection());
	}

	public DatabaseVersionPersistence(Connection connection) {
		this.connection = connection;
	}
	
	private Connection getConnection() {
		return connection;
	}

	public int read() throws SQLException {
		return read(getConnection());
	}

	public int read(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery(
			"select DATABASE_VERSION from DATABASE_VERSION");
		if (results.next()) {
			int version = results.getInt("DATABASE_VERSION");
			if (results.next()) {
				throw new RuntimeException("too many rows in DATABASE_VERSION");
			}
			statement.close();
			return version;
		}
		else {
			throw new RuntimeException("No row in DATABASE_VERSION");
		}
	}
	
	public void write(int version) throws SQLException {
		Connection connection = getConnection();
		Statement statement = connection.createStatement();
		int rows = statement.executeUpdate(
			"update DATABASE_VERSION set DATABASE_VERSION = " + version);
		statement.close();
		if (rows != 1) {
			throw new RuntimeException(
				"Unable to update database version (" + rows + " rows updated)");
		}
		connection.commit();
	}
	
	public boolean isVersioned() throws SQLException{
		return isVersioned(getConnection());
	}

	boolean isVersioned(Connection conn) throws SQLException{
		ResultSet results = conn.getMetaData().getColumns(
				null, null, "DATABASE_VERSION", "DATABASE_VERSION");
		boolean foundColumns = results.next();
		results.close();
		return foundColumns;
	}

	public List<Upgrade> scripts(int applicationVersion, int databaseVersion) {
		if (applicationVersion < databaseVersion) {
			/*
			   Automatically applying downgrades would be a mistake because
			   a downgrade is likely to destroy data (for example, if the
			   upgrade had added a column and the application had put some
			   data into that column prior to the downgrade).
			 */
			throw new UnsupportedOperationException(
				"your database needs to be downgraded from " +
				databaseVersion + " to " + applicationVersion);
		}
		
		if (applicationVersion == databaseVersion) {
			return Collections.emptyList();
		}
		
		List<Upgrade> upgrades = new ArrayList<Upgrade>(
			applicationVersion - databaseVersion);
		for (int i = databaseVersion; i < applicationVersion; i++){
			Upgrade upgrade = findUpgrade(i);
			upgrades.add(upgrade);
		}
		return Collections.unmodifiableList(upgrades);
	}

	Upgrade findUpgrade(int fromVersion) {
		int higherVersion = fromVersion + 1;
		String name = "upgrade_to_" + higherVersion + ".sql";
		URL url = lookup(name);
		Upgrade upgrade;
		if (url == null) {
			String location;
			try {
				location = " in " + 
				    getClass().getProtectionDomain()
				    .getCodeSource().getLocation().toString();
			} catch (Throwable e) {
				location = "";
			}
			throw new IllegalStateException("WAR built without "+name+" next to "+
					getClass().getName()+
					location);
		}
		else {
			upgrade = new SqlUpgrade(url, higherVersion);
		}
		return upgrade;
	}

	URL lookup(String name) {
		return getClass().getResource(name);
	}

	public List<Upgrade> downgrades(
		int downgradeTo, int databaseVersion) {
		List<Upgrade> downgrades = new ArrayList<Upgrade>();
		for (int i = databaseVersion; i > downgradeTo; --i) {
			downgrades.add(new SqlUpgrade(null, i));
		}
		return Collections.unmodifiableList(downgrades);
	}
	
	void execute(Upgrade upgrade, Connection conn) throws IOException, SQLException {
		upgrade.upgrade(conn);
	}
	
	void execute(List<Upgrade> scripts, Connection conn) 
	throws IOException, SQLException {
		for (Upgrade upgrade : scripts) {
			execute(upgrade, conn);
		}
	}
	
	public void upgradeDatabase() throws Exception {
		Connection conn = getConnection();
		try {
			upgradeDatabase(conn, APPLICATION_VERSION);
			conn.commit();
		}
		catch (Exception e) {
			try {
				conn.rollback();
			}
			catch (SQLException rollbackException){
				rollbackException.printStackTrace();
			}
			throw e;
		}
	}
	
	void upgradeDatabase(Connection connection, int upgradeTo) 
	throws Exception {
		if (!isVersioned(connection)) {
			throw new RuntimeException(
				"Database version is too old to be upgraded automatically");
		}

		int version = read(connection);
		for (Upgrade upgrade : scripts(upgradeTo, version)){
			execute(upgrade, connection);
			int upgradedVersion = read(connection);
			if(upgradedVersion != version + 1) {
				throw new RuntimeException("upgrade script from " + version +
					" did not end up at " + (version + 1) + "(was instead " + upgradedVersion + ")");
			}
			version = upgradedVersion;
		}
	}

}
