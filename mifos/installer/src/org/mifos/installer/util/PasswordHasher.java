package org.mifos.installer.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import java.security.SecureRandom;
import java.security.MessageDigest;

/**
 * Utility class to hash and update the password in mifos schema
 */
public class PasswordHasher {
	//Default mifos user
	private static String MIFOS_USER = "mifos";
	//Default user password
	private static String MIFOS_PASSWORD = "testmifos";
	//MySQL JDBC driver class
	private static final String DB_DRIVER = "com.mysql.jdbc.Driver";

	/** Update encrypted password into the database
     **/
	private void updatePassword(final String applicationUser, final byte[] hashedPassword, final String dbHost,
									final String dbPort, final String dbUser, final String dbPassword,
									final String dbSchema) throws ClassNotFoundException, SQLException {

		Connection connection = this.getConnection(dbHost, dbPort, dbUser, dbPassword, dbSchema);
                connection.setAutoCommit(false);
		PreparedStatement statement =
			connection.prepareStatement("update " + dbSchema+ ".personnel set password = ? where login_name = '" + applicationUser + "'");

		statement.setBytes(1, hashedPassword);
		statement.executeUpdate();
                connection.commit();
		connection.close();
	}

	/**
	 * Get the database connection.
	 * @param dbHost
	 * @param dbPort
	 * @param dbUser
	 * @param dbPassword
	 * @param dbSchema
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private Connection getConnection(final String dbHost, final String dbPort,
										final String dbUser, final String dbPassword,
										final String dbSchema) throws ClassNotFoundException, SQLException {
		// Load the Database Driver.
		Class.forName(DB_DRIVER);

		// Create the connection string
		final String dbConnectionURL =  "jdbc:mysql://".concat(dbHost).concat(":").concat(dbPort).concat("/").concat(dbSchema);
		// Get Database Connection.
		Connection connection = DriverManager.getConnection(dbConnectionURL, dbUser, dbPassword);

		return connection;

	}

	/**
	 * This function will return the hashed password out of the passed string
	 * password
	 *
	 * @param password
	 *            password passed by the user
	 * @param randomBytes
	 *            random bytes
	 */
	private byte[] getHashedPassword(final String password, final byte[] randomBytes) throws Exception {
		byte[] hashedPassword = null;
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");

		messageDigest.update(randomBytes);
		messageDigest.update(password.getBytes("UTF-8"));
		hashedPassword = messageDigest.digest();
		return hashedPassword;

	}

	/**
	 * This function create the hashed password
	 *
	 */
	private byte[] hashPassword(final String password) throws Exception {

		byte[] hashedPassword = null;
		byte[] randomBytes = generateRandomBytes();
		byte[] tempEncPassword = getHashedPassword(password, randomBytes);

		hashedPassword = new byte[randomBytes.length + tempEncPassword.length];
		System.arraycopy(randomBytes, 0, hashedPassword, 0, randomBytes.length);
		System.arraycopy(tempEncPassword, 0, hashedPassword, randomBytes.length, tempEncPassword.length);
		return hashedPassword;
	}

	/**
	 * This function generate and returns the random no of bytes
	 * 
	 * @return 
	 */
	private byte[] generateRandomBytes() {

		byte[] randomBytes = new byte[12];
		
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(randomBytes);
		return randomBytes;
	}
	
	public static void main(String[] args) throws Exception {

		// The command line arguments.
		// 1. Database host
		// 2. Database Port
		// 3. Database User
		// 4. Database user password
		// 5. Database schema 
		// OPTIONAL
		// 6. Application user
		// 7. Application user password
		
		final String dbHost = args[0];
                final String dbPort = args[1];
                final String dbUser = args[2];
                final String dbPassword = args[3];
                final String dbSchema = args[4];

        if (args.length == 7) {
        	MIFOS_USER = args[5];
        	MIFOS_PASSWORD = args[6];
        }
        
		final PasswordHasher hasher = new PasswordHasher();
		byte[] hashedPassword = hasher.hashPassword(MIFOS_PASSWORD);
		hasher.updatePassword(MIFOS_USER, hashedPassword , dbHost, dbPort, dbUser , dbPassword, dbSchema);
	}


}
