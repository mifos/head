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

	public SqlUpgrade(URL sqlScript) {
		this.script = sqlScript;
	}
	
	@Override
	public URL sql() {
		return script;
	}

	@Override
	public void upgrade(Connection conn) throws IOException, SQLException {
		InputStream in = sql().openStream();
		execute(in, conn);
		in.close();
	}
	
	void execute(InputStream stream, Connection conn) throws SQLException {
		String[] sqls = readFile(stream);
		Statement statement = conn.createStatement();
		for (String sql : sqls) {
			statement.executeUpdate(sql);
		}
		statement.close();
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
	    }
		catch (IOException e) {
	    	throw new RuntimeException(e);
	    }
	}

}
