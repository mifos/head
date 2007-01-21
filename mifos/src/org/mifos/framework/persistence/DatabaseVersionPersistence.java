package org.mifos.framework.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseVersionPersistence extends Persistence {

	public static final int APPLICATION_VERSION = 104;

	public int read() throws SQLException {
		return read(getConnection());
	}

	public int read(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery("select DATABASE_VERSION from DATABASE_VERSION");
		if (results.next()) {
			int version = results.getInt("DATABASE_VERSION");
			if(results.next()) {
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

	public URL[] scripts(int applicationVersion, int databaseVersion) {
		if(applicationVersion < databaseVersion) {
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

	public void execute(URL url) throws IOException , SQLException {
		Connection conn = getConnection();
		execute(url, conn);
		conn.commit();
	}

	void execute(URL url, Connection conn) throws IOException , SQLException {
		InputStream in = url.openStream();
		execute(in, conn);
		in.close();
	}
	
	public void execute(InputStream stream) throws SQLException {
		Connection conn = getConnection();
		execute(stream, conn);
		conn.commit();
	}
	
	void execute(InputStream stream, Connection conn) throws SQLException {
		String[] sqls = readFile(stream);
		Statement st = conn.createStatement();
		for(String sql : sqls){
			st.executeUpdate(sql);
		}
		st.close();
	}
	
	void execute(URL[] scripts, Connection conn) throws IOException, SQLException {
		for(URL url : scripts){
			execute(url, conn);
		}
	}
	
	/**
	 * @return individual statements
	 * */
	public static String[] readFile(InputStream stream) {
		// mostly ripped from http://svn.apache.org/viewvc/ant/core/trunk/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
		try {
			ArrayList<String> statements = new ArrayList<String>();  
			Charset utf8 = Charset.forName("UTF-8");
			CharsetDecoder decoder = utf8.newDecoder();
			BufferedReader in = new BufferedReader(new InputStreamReader(stream, decoder));
			StringBuffer sql = new StringBuffer();
			String line;
			while ((line = in.readLine()) != null) {
                if (line.startsWith("//")) {
                    continue;
                }
                if (line.startsWith("--")) {
                    continue;
                }
                //StringTokenizer st = new StringTokenizer(line);
                //if (st.hasMoreTokens()) {
                //    String token = st.nextToken();
                //    if ("REM".equalsIgnoreCase(token)) {
                //        continue;
                //    }
                //}
            
                line = line.trim();
                
                sql.append("\n");
                sql.append(line);

                // SQL defines "--" as a comment to EOL
                // and in Oracle it may contain a hint
                // so we cannot just remove it, instead we must end it

                if (line.indexOf("--") >= 0) {
                    sql.append("\n");
                }

	            if (sql.length()>0 && sql.charAt(sql.length()-1)==';') {
	            	statements.add(sql.substring(0, sql.length() - 1));
	                sql.setLength(0);
	            }
	        }
	        // Catch any statements not followed by ;
	        if (sql.length() > 0) {
	        	statements.add(sql.toString());
	        }
	        
	        return statements.toArray(new String[statements.size()]);
	    }catch(IOException ioe){
	    	throw new RuntimeException(ioe);
	    }
	}
	
	public void upgradeDatabase() throws Exception {
		Connection conn = getConnection();
		try{
			upgradeDatabase(conn, APPLICATION_VERSION);
			conn.commit();
		}catch(Exception e){
			try{
				conn.rollback();
			}catch(SQLException sqle){
				sqle.printStackTrace();
			}
			throw e;
		}
	}
	
	void upgradeDatabase(Connection conn, int upgradeTo) throws Exception {
		if(!isVersioned(conn)){
			throw new RuntimeException("Database version is too old to be upgraded automatically");
		}
		int version = read(conn);
		URL[] sqlScripts = scripts(upgradeTo, version);
		for(URL url : sqlScripts){
			execute(url, conn);
			int upgradedVersion = read(conn);
			if(upgradedVersion != version + 1) {
				throw new RuntimeException("upgrade script from " + version +
					" did not end up at " + (version + 1));
			}
			version = upgradedVersion;
		}
	}

}
