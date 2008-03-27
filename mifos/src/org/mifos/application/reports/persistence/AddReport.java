package org.mifos.application.reports.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mifos.application.reports.business.ReportsBO;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.Upgrade;

public class AddReport extends Upgrade {

	private  short newId;
	private final short category;
	private final String name;
	private final String identifier;
	private final String design;
	private Short activityId;

	public AddReport(int higherVersion, short newId, short category, 
		String name, String identifier, String design) {
		super(higherVersion);
		this.newId = newId;
		this.category = category;
		this.name = name;
		this.identifier = identifier;
		this.design = design;
		this.activityId = null;
	}
	
	public AddReport(int higherVersion, short newId, short category, 
			String name, String identifier, String design, Short activityId) {
			super(higherVersion);
			this.newId = newId;
			this.category = category;
			this.name = name;
			this.identifier = identifier;
			this.design = design;
			this.activityId = activityId;
		}

	@Override
	public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) 
	throws IOException, SQLException {
		insertIntoReport(connection);
		insertIntoReportJasperMap(connection);
	}
	
	private short getNextReportId(Connection connection) throws SQLException
	{
		Statement statement = connection.createStatement();
		String query = "SELECT MAX(REPORT_ID) FROM REPORT";
		short reportId = 0;
		ResultSet results = statement.executeQuery(query);
		if (results.next()) {
			reportId = results.getShort(1);
			reportId++;
			statement.close();
		}
		else {
			statement.close();
			throw new RuntimeException(
				"unable to find max report id to upgrade");
		}
	
		
		return reportId;
	}

	private void insertIntoReport(Connection connection) 
	throws SQLException {
		if (newId == 0)
			newId = getNextReportId(connection);
		PreparedStatement statement = connection.prepareStatement(
			"INSERT INTO REPORT(REPORT_ID, REPORT_CATEGORY_ID," +
			"REPORT_NAME,REPORT_IDENTIFIER, REPORT_ACTIVE, ACTIVITY_ID)" +
			"VALUES(?,?,?,?,?,?)");
		statement.setShort(1, newId);
		statement.setShort(2, category);
		statement.setString(3, name);
		statement.setString(4, identifier);
		statement.setShort(5, ReportsBO.ACTIVE);
		statement.setShort(6, activityId);
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
