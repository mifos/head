package org.mifos.framework.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseVersionPersistence extends Persistence {

	public static final int APPLICATION_VERSION = 101;

	public int read() throws SQLException {
		return read(getConnection());
	}

	int read(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery("select DATABASE_VERSION from DATABASE_VERSION");
		if (results.next()) {
			int version = results.getInt("DATABASE_VERSION");
			if(results.next())
				throw new RuntimeException("too many rows in DATABASE_VERSION");
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

	public URL[] scripts(int applicationVersion, int databaseVersion) {
		if(applicationVersion < databaseVersion)
			throw new UnsupportedOperationException("downgrades not yet supported (from "+
					databaseVersion+" to "+applicationVersion+")");
		
		if(applicationVersion == databaseVersion) {
			return new URL[0];
		}
		
		ArrayList<URL> urls = new ArrayList<URL>(applicationVersion-databaseVersion);
		for(int i=databaseVersion; i<applicationVersion; i++){
			String name = "upgrade_to_" + (i + 1 ) + ".sql";
			URL url = lookup(name);
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
			urls.add(url);
		}
		return urls.toArray(new URL[urls.size()]);
		
	}

	URL lookup(String name) {
		return getClass().getResource(name);
	}

	public void execute(InputStream stream) {
		// TODO Auto-generated method stub
		
	}

	public static String readFile(InputStream stream) {
		try {
			Charset utf8 = Charset.forName("UTF-8");
			CharsetDecoder decoder = utf8.newDecoder();
			Reader input = new InputStreamReader(stream, decoder);
			StringWriter output = new StringWriter();
			
			char[] buffer = new char[32768];
			int n;
			while ((n = input.read(buffer)) != -1) {
				output.write(buffer, 0, n);
			}
			input.close();
			return output.toString();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
