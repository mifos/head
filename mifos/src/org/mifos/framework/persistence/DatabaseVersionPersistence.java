package org.mifos.framework.persistence;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.accounts.business.AddAccountAction;
import org.mifos.application.accounts.business.AddAccountStateFlag;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.holiday.persistence.Upgrade104;
import org.mifos.application.productsmix.persistence.Upgrade127;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.persistence.AddReport;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.components.fieldConfiguration.business.AddField;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.AddActivity;
import org.mifos.framework.security.util.resources.SecurityConstants;

public class DatabaseVersionPersistence {

	public static final int APPLICATION_VERSION = 146;
	public static final int FIRST_NUMBERED_VERSION = 100;

	public static void register(Map<Integer, Upgrade> register, Upgrade upgrade) {
		int higherVersion = upgrade.higherVersion();
		if (register.containsKey(higherVersion)) {
			throw new IllegalStateException("already have an upgrade to "
					+ higherVersion);
		}
		register.put(higherVersion, upgrade);
	}

	public static final short ENGLISH_LOCALE = 1;

	public static Map<Integer, Upgrade> masterRegister() {
		Map<Integer, Upgrade> register = new HashMap<Integer, Upgrade>();
		register101(register);
		register102(register);
		register103(register);
		register(register, new Upgrade104());
		register106(register);
		register115(register);
		register117(register);
		register118(register);
		register119(register);
		register120(register);
		register123(register);
		register124(register);
		register126(register);
		register(register, new Upgrade127());
		register130(register);
		register136(register);
		register141(register);
		register142(register);
		register143(register);
		register144(register);
		register145(register);
		register146(register);
		return Collections.unmodifiableMap(register);
	}

	private static void register101(Map<Integer, Upgrade> register) {
		register(register, new AddActivity(101,
				SecurityConstants.CAN_CREATE_MULTIPLE_LOAN_ACCOUNTS,
				SecurityConstants.BULK, ENGLISH_LOCALE,
				"Can create multiple Loan accounts"));
	}

	private static void register102(Map<Integer, Upgrade> register) {
		register(register, new CompositeUpgrade(new AddActivity(102,
				SecurityConstants.CAN_REVERSE_LOAN_DISBURSAL,
				SecurityConstants.LOAN_MANAGEMENT, ENGLISH_LOCALE,
				"Can reverse Loan disbursals"), new AddAccountStateFlag(102,
				AccountStateFlag.LOAN_REVERSAL.getValue(), "Loan reversal",
				ENGLISH_LOCALE, "Loan reversal"), new AddAccountAction(102,
				AccountActionTypes.LOAN_REVERSAL.getValue(), ENGLISH_LOCALE,
				"Loan Reversal"), new AddAccountAction(102,
				AccountActionTypes.LOAN_DISBURSAL_AMOUNT_REVERSAL.getValue(),
				ENGLISH_LOCALE, "Disrbursal amount Reversal")));
	}

	private static void register103(Map<Integer, Upgrade> register) {
		register(register, new CompositeUpgrade(new AddActivity(103,
				SecurityConstants.CONFIGURATION_MANAGEMENT, null,
				ENGLISH_LOCALE, "Configuration Management"), new AddActivity(
				103, SecurityConstants.CAN_DEFINE_LABELS,
				SecurityConstants.CONFIGURATION_MANAGEMENT, ENGLISH_LOCALE,
				"Can define labels")));
	}

	private static void register106(Map<Integer, Upgrade> register) {
		register(register, new CompositeUpgrade(new AddActivity(106,
				SecurityConstants.CAN_DEFINE_HIDDEN_MANDATORY_FIELDS,
				SecurityConstants.CONFIGURATION_MANAGEMENT, ENGLISH_LOCALE,
				"Can define hidden/mandatory fields"), new AddField(106, 74,
				"AssignClients", EntityType.CLIENT, false, false)));
	}

	private static void register115(Map<Integer, Upgrade> register) {
		register(register, new AddActivity(115,
				SecurityConstants.CAN_REMOVE_CLIENTS_FROM_GROUPS,
				SecurityConstants.CLIENTS, ENGLISH_LOCALE,
				"Can remove clients from groups"));
	}

	private static void register117(Map<Integer, Upgrade> register) {
		register(register, new CompositeUpgrade(new AddReport(117, (short) 28,
				ReportsCategoryBO.ANALYSIS,
				"Detailed Aging of Portfolio at Risk",
				"aging_portfolio_at_risk",
				"DetailedAgingPortfolioAtRisk.rptdesign"), new AddActivity(117,
				SecurityConstants.CAN_VIEW_DETAILED_AGING_OF_PORTFOLIO_AT_RISK,
				SecurityConstants.ANALYSIS, ENGLISH_LOCALE,
				"Can view Detailed Aging of Portfolio at Risk")));
	}

	private static void register118(Map<Integer, Upgrade> register) {
		register(register, new AddActivity(118,
				SecurityConstants.CAN_ADD_CLIENTS_TO_GROUPS,
				SecurityConstants.CLIENTS, ENGLISH_LOCALE,
				"Can add an existing client to a group"));
	}

	private static void register119(Map<Integer, Upgrade> register) {
		register(register, new CompositeUpgrade(new AddActivity(119,
				SecurityConstants.PRODUCT_MIX, null, ENGLISH_LOCALE,
				"Product mix"), new AddActivity(119,
				SecurityConstants.CAN_DEFINE_PRODUCT_MIX,
				SecurityConstants.PRODUCT_MIX, ENGLISH_LOCALE,
				"Can Define product mix"), new AddActivity(119,
				SecurityConstants.CAN_EDIT_PRODUCT_MIX,
				SecurityConstants.PRODUCT_MIX, ENGLISH_LOCALE,
				"Can Edit product mix")));
	}

	private static void register120(Map<Integer, Upgrade> register) {
		register(register, new CompositeUpgrade(new AddReport(120, (short) 29,
				ReportsCategoryBO.ANALYSIS, "Active Loans By Loan Officer",
				"active_loans_by_loan_officer",
				"ActiveLoansByLoanOfficer.rptdesign"), new AddActivity(120,
				SecurityConstants.CAN_VIEW_ACTIVE_LOANS_BY_LOAN_OFFICER,
				SecurityConstants.ANALYSIS, ENGLISH_LOCALE,
				"Can view Active Loans By Loan Officer")));
	}

	private static void register123(Map<Integer, Upgrade> register) {
		register(register, new AddActivity(123,
				SecurityConstants.CAN_DEFINE_LOOKUP_OPTIONS,
				SecurityConstants.CONFIGURATION_MANAGEMENT, ENGLISH_LOCALE,
				"Can Define Lookup Values"));
	}

	private static void register124(Map<Integer, Upgrade> register) {
		register(register, new AddActivity(124,
				SecurityConstants.UPLOAD_REPORT_TEMPLATE,
				SecurityConstants.REPORTS_MANAGEMENT, ENGLISH_LOCALE,
				"Can upload report template"));
	}

	private static void register126(Map<Integer, Upgrade> register) {
		register(register, new CompositeUpgrade(new AddActivity(126,
				SecurityConstants.VIEW_REPORTS,
				SecurityConstants.REPORTS_MANAGEMENT, ENGLISH_LOCALE,
				"Can view reports"), new AddActivity(126,
				SecurityConstants.EDIT_REPORT_INFORMATION,
				SecurityConstants.REPORTS_MANAGEMENT, ENGLISH_LOCALE,
				"Can edit report information")));
	}

	private static void register130(Map<Integer, Upgrade> register) {
		register(register, new AddActivity(130,
				SecurityConstants.CAN_ADJUST_PAYMENT_WHEN_OBLIGATION_MET,
				SecurityConstants.REPORTS_MANAGEMENT, // ???
				ENGLISH_LOCALE, "Can adjust payment when account status "
						+ "is \"closed-obligation met\""));
	}

	private static void register136(Map<Integer, Upgrade> register) {
		register(register, new AddActivity(136,
				SecurityConstants.CAN_REDO_LOAN_DISPURSAL,
				SecurityConstants.LOAN_MANAGEMENT, ENGLISH_LOCALE,
				"Can redo Loan disbursals"));
	}

	private static void register141(Map<Integer, Upgrade> register) {
		register(register, new AddActivity(141,
				SecurityConstants.CAN_DEFINE_ACCEPTED_PAYMENT_TYPE,
				SecurityConstants.ORGANIZATION_MANAGEMENT, ENGLISH_LOCALE,
				"Can define Accepted Payment Type"));
	}

	private static void register142(Map<Integer, Upgrade> register) {
		register(register, new AddActivity(142,
				SecurityConstants.DEFINE_REPORT_CATEGORY,
				SecurityConstants.REPORTS_MANAGEMENT, ENGLISH_LOCALE,
				"Can define new report category"));
	}

	private static void register143(Map<Integer, Upgrade> register) {
		register(register, new AddActivity(143,
				SecurityConstants.VIEW_REPORT_CATEGORY,
				SecurityConstants.REPORTS_MANAGEMENT, ENGLISH_LOCALE,
				"Can view report category"));
	}

	private static void register144(Map<Integer, Upgrade> register) {
		register(register, new AddActivity(144,
				SecurityConstants.DELETE_REPORT_CATEGORY,
				SecurityConstants.REPORTS_MANAGEMENT, ENGLISH_LOCALE,
				"Can delete report category"));
	}
	private static void register145(Map<Integer, Upgrade> register) {
		register(register, new AddActivity(145,
				SecurityConstants.DOWNLOAD_REPORT_TEMPLATE,
				SecurityConstants.REPORTS_MANAGEMENT, ENGLISH_LOCALE,
		"Can download report template"));
	}
	private static void register146(Map<Integer, Upgrade> register) {
		register(register, new AddActivity(146,
				SecurityConstants.CAN_DEFINE_CUSTOM_FIELD,
				SecurityConstants.CONFIGURATION_MANAGEMENT, ENGLISH_LOCALE,
				"Can define custom fields"));
	}

	private final Connection connection;
	private final Map<Integer, Upgrade> registeredUpgrades;

	public DatabaseVersionPersistence() {
		this(HibernateUtil.getOrCreateSessionHolder().getSession().connection());
	}

	public DatabaseVersionPersistence(Connection connection) {
		this(connection, masterRegister());
	}

	public DatabaseVersionPersistence(Connection connection,
			Map<Integer, Upgrade> registeredUpgrades) {
		this.connection = connection;
		this.registeredUpgrades = registeredUpgrades;
	}

	private Connection getConnection() {
		return connection;
	}

	public int read() throws SQLException {
		return read(getConnection());
	}

	public int read(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet results = statement
				.executeQuery("select DATABASE_VERSION from DATABASE_VERSION");
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
		int rows = statement
				.executeUpdate("update DATABASE_VERSION set DATABASE_VERSION = "
						+ version);
		statement.close();
		if (rows != 1) {
			throw new RuntimeException("Unable to update database version ("
					+ rows + " rows updated)");
		}
		connection.commit();
	}

	public boolean isVersioned() throws SQLException {
		return isVersioned(getConnection());
	}

	boolean isVersioned(Connection conn) throws SQLException {
		ResultSet results = conn.getMetaData().getColumns(null, null,
				"DATABASE_VERSION", "DATABASE_VERSION");
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
					"your database needs to be downgraded from "
							+ databaseVersion + " to " + applicationVersion);
		}

		if (applicationVersion == databaseVersion) {
			return Collections.emptyList();
		}

		List<Upgrade> upgrades = new ArrayList<Upgrade>(applicationVersion
				- databaseVersion);
		for (int i = databaseVersion; i < applicationVersion; i++) {
			Upgrade upgrade = findUpgrade(i + 1);
			upgrades.add(upgrade);
		}
		return Collections.unmodifiableList(upgrades);
	}

	Upgrade findUpgrade(int higherVersion) {
		boolean foundInJava = registeredUpgrades.containsKey(higherVersion);

		String name = "upgrade_to_" + higherVersion + ".sql";
		URL url = lookup(name);
		boolean foundInSql = url != null;

		if (foundInJava && foundInSql) {
			throw new IllegalStateException("Found upgrade to " + higherVersion
					+ " both in java and in " + name);
		}
		else if (foundInJava) {
			return registeredUpgrades.get(higherVersion);
		}
		else if (foundInSql) {
			return new SqlUpgrade(url, higherVersion);
		}
		else {
			String location;
			try {
				location = " in "
						+ getClass().getProtectionDomain().getCodeSource()
								.getLocation().toString();
			}
			catch (Throwable e) {
				location = "";
			}
			throw new IllegalStateException("Did not find upgrade to "
					+ higherVersion + " in java or in " + name + " next to "
					+ getClass().getName() + location);
		}
	}

	URL lookup(String name) {
		return getClass().getResource(name);
	}

	public List<Upgrade> downgrades(int downgradeTo, int databaseVersion) {
		List<Upgrade> downgrades = new ArrayList<Upgrade>();
		for (int higherVersion = databaseVersion; higherVersion > downgradeTo; --higherVersion) {
			downgrades.add(findUpgrade(higherVersion));
		}
		return Collections.unmodifiableList(downgrades);
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
			catch (SQLException rollbackException) {
				rollbackException.printStackTrace();
			}
			throw e;
		}
	}

	void upgradeDatabase(Connection connection, int upgradeTo) throws Exception {
		if (!isVersioned(connection)) {
			throw new RuntimeException(
					"Database version is too old to be upgraded automatically");
		}

		int version = read(connection);
		for (Upgrade upgrade : scripts(upgradeTo, version)) {

			try {
				upgrade.upgrade(connection);
			}
			catch (Exception e) {
				throw new RuntimeException("error in upgrading to "
						+ upgrade.higherVersion(), e);
			}

			int upgradedVersion = read(connection);
			if (upgradedVersion != version + 1) {
				throw new RuntimeException("upgrade script from " + version
						+ " did not end up at " + (version + 1)
						+ "(was instead " + upgradedVersion + ")");
			}
			version = upgradedVersion;
		}
	}
}
