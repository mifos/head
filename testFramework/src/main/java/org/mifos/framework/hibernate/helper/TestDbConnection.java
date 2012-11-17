package org.mifos.framework.hibernate.helper;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SQL", justification="This is a wrapper class so the bugs found are wrong")
@SuppressWarnings("PMD")
public class TestDbConnection implements Connection {
    private Connection connection;

    public TestDbConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(final String s) throws SQLException {
        return connection.prepareStatement(s);
    }

    @Override
    public CallableStatement prepareCall(String s) throws SQLException {
        return connection.prepareCall(s);
    }

    @Override
    public String nativeSQL(String s) throws SQLException {
        return connection.nativeSQL(s);
    }

    @Override
    public void setAutoCommit(boolean b) throws SQLException {
        connection.setAutoCommit(b);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return connection.getMetaData();
    }

    @Override
    public void setReadOnly(boolean b) throws SQLException {
        connection.setReadOnly(b);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return connection.isReadOnly();
    }

    @Override
    public void setCatalog(String s) throws SQLException {
        connection.setCatalog(s);
    }

    @Override
    public String getCatalog() throws SQLException {
        return connection.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int i) throws SQLException {
        connection.setTransactionIsolation(i);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return connection.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return connection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        connection.clearWarnings();
    }

    @Override
    public Statement createStatement(int i, int i1) throws SQLException {
        return connection.createStatement(i, i1);
    }

    @Override
    public PreparedStatement prepareStatement(final String s, int i, int i1) throws SQLException {
        return connection.prepareStatement(s, i, i1);
    }

    @Override
    public CallableStatement prepareCall(String s, int i, int i1) throws SQLException {
        return connection.prepareCall(s, i, i1);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return connection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> stringClassMap) throws SQLException {
        connection.setTypeMap(stringClassMap);
    }

    @Override
    public void setHoldability(int i) throws SQLException {
        connection.setHoldability(i);
    }

    @Override
    public int getHoldability() throws SQLException {
        return connection.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return connection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String s) throws SQLException {
        return connection.setSavepoint(s);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        connection.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        connection.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int i, int i1, int i2) throws SQLException {
        return connection.createStatement(i, i1, i2);
    }

    @Override
    public PreparedStatement prepareStatement(final String s, int i, int i1, int i2) throws SQLException {
        return connection.prepareStatement(s, i, i1, i2);
    }

    @Override
    public CallableStatement prepareCall(String s, int i, int i1, int i2) throws SQLException {
        return connection.prepareCall(s, i, i1, i2);
    }

    @Override
    public PreparedStatement prepareStatement(final String s, int i) throws SQLException {
        return connection.prepareStatement(s, i);
    }

    @Override
    public PreparedStatement prepareStatement(final String s, int[] ints) throws SQLException {
        return connection.prepareStatement(s, ints);
    }

    @Override
    public PreparedStatement prepareStatement(final String s, String[] strings) throws SQLException {
        return connection.prepareStatement(s, strings);
    }

    @Override
    public Clob createClob() throws SQLException {
        return connection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return connection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return connection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return connection.createSQLXML();
    }

    @Override
    public boolean isValid(int i) throws SQLException {
        return connection.isValid(i);
    }

    @Override
    public void setClientInfo(String s, String s1) throws SQLClientInfoException {
        connection.setClientInfo(s, s1);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        connection.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String s) throws SQLException {
        return connection.getClientInfo(s);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return connection.getClientInfo();
    }

    @Override
    public Array createArrayOf(String s, Object[] objects) throws SQLException {
        return connection.createArrayOf(s, objects);
    }

    @Override
    public Struct createStruct(String s, Object[] objects) throws SQLException {
        return connection.createStruct(s, objects);
    }

    @Override
    public <T> T unwrap(Class<T> tClass) throws SQLException {
        return connection.unwrap(tClass);
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return connection.isWrapperFor(aClass);
    }

    // --------------------------------------------------------------------
    //   The following methods are here so that this compiles under 
    //   Java 7 (JDBC 4.0) as well as Java 6 (JDBC 3.0).  Note that
    //   we cannot make them use @Override nor delegate to the 
    //   respective connection method, as that would break 
    //   building this under Java 6 (JDBC 3.0).
    //
    //   TODO But is this class really needed?!
    
	// DON'T use @Override (see above)
	public void setSchema(String schema) throws SQLException {
		throw new UnsupportedOperationException();
	}

	// DON'T use @Override (see above)
	public String getSchema() throws SQLException {
		throw new UnsupportedOperationException();
	}

	// DON'T use @Override (see above)
	public void abort(Executor executor) throws SQLException {
		throw new UnsupportedOperationException();
	}

	// DON'T use @Override (see above)
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		throw new UnsupportedOperationException();
	}

	// DON'T use @Override (see above)
	public int getNetworkTimeout() throws SQLException {
		throw new UnsupportedOperationException();
	}
}
