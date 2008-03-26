package org.mifos.application.reports.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.mifos.application.reports.business.ReportsBO;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.Upgrade;

public class AddReport extends Upgrade {

	private final short newId;
	private final short category;
	private final String name;
	private final String identifier;
	private final String design;

	public AddReport(int higherVersion, short newId, short category, 
		String name, String identifier, String design) {
		super(higherVersion);
		this.newId = newId;
		this.category = category;
		this.name = name;
		this.identifier = identifier;
		this.design = design;
	}

	@Override
	public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) 
	throws IOException, SQLException {
		insertIntoReport(connection);
		insertIntoReportJasperMap(connection);
	}

	private void insertIntoReport(Connection connection) 
	throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"INSERT INTO REPORT(REPORT_ID,REPORT_CATEGORY_ID," +
			"REPORT_NAME,REPORT_IDENTIFIER, REPORT_ACTIVE)" +
			"VALUES(?,?,?,?,?)");
		statement.setShort(1, newId);
		statement.setShort(2, category);
		statement.setString(3, name);
		statement.setString(4, identifier);
		statement.setShort(5, ReportsBO.ACTIVE);
		statement.executeUpdate();
		statement.close();
	}

	private void insertIntoReportJasperMap(Connection connection) 
	throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID," +
			"REPORT_NAME, REPORT_IDENTIFIER, REPORT_JASPER) " +
			"VALUES (?,?,?,?,?)");
		statement.setShort(1, newId);
		statement.setShort(2, category);
		statement.setString(3, name);
		statement.setString(4, identifier);
		statement.setString(5, design);
		statement.executeUpdate();
		statement.close();
	}

	@Override
	public void downgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) 
	throws IOException, SQLException {
		deleteFromReport(connection);
		deleteFromReportJasperMap(connection);
	}

	private void deleteFromReport(Connection connection) 
	throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"DELETE FROM REPORT where report_id = ?");
		statement.setShort(1, newId);
		statement.executeUpdate();
		statement.close();
	}

	private void deleteFromReportJasperMap(Connection connection) 
	throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"DELETE FROM report_jasper_map where report_id = ?");
		statement.setShort(1, newId);
		statement.executeUpdate();
		statement.close();
	}

}
