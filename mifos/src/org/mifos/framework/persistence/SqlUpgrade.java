package org.mifos.framework.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SqlUpgrade extends Upgrade {

	private final URL script;

	public SqlUpgrade(URL sqlScript, int higherVersion) {
		super(higherVersion);
		this.script = sqlScript;
	}
	
	public URL sql() {
		return script;
	}

	public void runScript(Connection connection) throws IOException, SQLException {
		InputStream in = sql().openStream();
		execute(in, connection);
		in.close();		
	}
	@Override
	public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) throws IOException, SQLException {
		runScript(connection);
	}
	
	@Override
	public void downgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) throws IOException, SQLException {
	}
	
	public static void execute(InputStream stream, Connection conn) 
	throws SQLException {
		String[] sqls = readFile(stream);
		Statement statement = conn.createStatement();
		for (String sql : sqls) {
			statement.executeUpdate(sql);
		}
		statement.close();
	}

	/**
	 * Closes the stream when done.
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
	            if (line.startsWith("//") || line.startsWith("--")) {
	                continue;
	            }
	        
	            line = line.trim();
	            if ("".equals(line)) {
	            	continue;
	            }
	            
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
	    }
		catch (IOException e) {
	    	throw new RuntimeException(e);
	    }
		finally {
			try {
				stream.close();
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
}
