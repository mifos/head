/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.application.admin.system;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;

public class MockDatabaseMetaData implements DatabaseMetaData {

	public boolean allProceduresAreCallable() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean allTablesAreSelectable() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean deletesAreDetected(int type) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getAttributes(String catalog, String schemaPattern,
			String typeNamePattern, String attributeNamePattern)
			throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getBestRowIdentifier(String catalog, String schema,
			String table, int scope, boolean nullable) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public String getCatalogSeparator() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public String getCatalogTerm() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getCatalogs() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getColumnPrivileges(String catalog, String schema,
			String table, String columnNamePattern) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getColumns(String catalog, String schemaPattern,
			String tableNamePattern, String columnNamePattern)
			throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public Connection getConnection() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getCrossReference(String primaryCatalog,
			String primarySchema, String primaryTable, String foreignCatalog,
			String foreignSchema, String foreignTable) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getDatabaseMajorVersion() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getDatabaseMinorVersion() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public String getDatabaseProductName() throws SQLException {
		return "vendorName";
	}

	public String getDatabaseProductVersion() throws SQLException {
		return "1.0";
	}

	public int getDefaultTransactionIsolation() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getDriverMajorVersion() {
		throw new RuntimeException("not implemented");
	}

	public int getDriverMinorVersion() {
		throw new RuntimeException("not implemented");
	}

	public String getDriverName() throws SQLException {
		return "driverName";
	}

	public String getDriverVersion() throws SQLException {
		return "2.0";
	}

	public ResultSet getExportedKeys(String catalog, String schema, String table)
			throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public String getExtraNameCharacters() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public String getIdentifierQuoteString() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getImportedKeys(String catalog, String schema, String table)
			throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getIndexInfo(String catalog, String schema, String table,
			boolean unique, boolean approximate) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getJDBCMajorVersion() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getJDBCMinorVersion() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxBinaryLiteralLength() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxCatalogNameLength() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxCharLiteralLength() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxColumnNameLength() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxColumnsInGroupBy() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxColumnsInIndex() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxColumnsInOrderBy() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxColumnsInSelect() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxColumnsInTable() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxConnections() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxCursorNameLength() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxIndexLength() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxProcedureNameLength() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxRowSize() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxSchemaNameLength() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxStatementLength() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxStatements() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxTableNameLength() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxTablesInSelect() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getMaxUserNameLength() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public String getNumericFunctions() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getPrimaryKeys(String catalog, String schema, String table)
			throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getProcedureColumns(String catalog, String schemaPattern,
			String procedureNamePattern, String columnNamePattern)
			throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public String getProcedureTerm() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getProcedures(String catalog, String schemaPattern,
			String procedureNamePattern) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getResultSetHoldability() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public String getSQLKeywords() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public int getSQLStateType() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public String getSchemaTerm() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getSchemas() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public String getSearchStringEscape() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public String getStringFunctions() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getSuperTables(String catalog, String schemaPattern,
			String tableNamePattern) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getSuperTypes(String catalog, String schemaPattern,
			String typeNamePattern) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public String getSystemFunctions() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getTablePrivileges(String catalog, String schemaPattern,
			String tableNamePattern) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getTableTypes() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getTables(String catalog, String schemaPattern,
			String tableNamePattern, String[] types) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public String getTimeDateFunctions() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getTypeInfo() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getUDTs(String catalog, String schemaPattern,
			String typeNamePattern, int[] types) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public String getURL() throws SQLException {
		return "URL";
	}

	public String getUserName() throws SQLException {
		return "user";
	}

	public ResultSet getVersionColumns(String catalog, String schema,
			String table) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean insertsAreDetected(int type) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean isCatalogAtStart() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean isReadOnly() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean locatorsUpdateCopy() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean nullPlusNonNullIsNull() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean nullsAreSortedAtEnd() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean nullsAreSortedAtStart() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean nullsAreSortedHigh() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean nullsAreSortedLow() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean othersDeletesAreVisible(int type) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean othersInsertsAreVisible(int type) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean othersUpdatesAreVisible(int type) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean ownDeletesAreVisible(int type) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean ownInsertsAreVisible(int type) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean ownUpdatesAreVisible(int type) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean storesLowerCaseIdentifiers() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean storesMixedCaseIdentifiers() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean storesUpperCaseIdentifiers() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsANSI92EntryLevelSQL() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsANSI92FullSQL() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsANSI92IntermediateSQL() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsAlterTableWithAddColumn() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsAlterTableWithDropColumn() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsBatchUpdates() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsCatalogsInDataManipulation() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsCatalogsInProcedureCalls() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsCatalogsInTableDefinitions() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsColumnAliasing() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsConvert() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsConvert(int fromType, int toType)
			throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsCoreSQLGrammar() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsCorrelatedSubqueries() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsDataDefinitionAndDataManipulationTransactions()
			throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsDataManipulationTransactionsOnly()
			throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsDifferentTableCorrelationNames() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsExpressionsInOrderBy() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsExtendedSQLGrammar() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsFullOuterJoins() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsGetGeneratedKeys() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsGroupBy() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsGroupByBeyondSelect() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsGroupByUnrelated() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsIntegrityEnhancementFacility() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsLikeEscapeClause() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsLimitedOuterJoins() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsMinimumSQLGrammar() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsMixedCaseIdentifiers() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsMultipleOpenResults() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsMultipleResultSets() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsMultipleTransactions() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsNamedParameters() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsNonNullableColumns() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsOrderByUnrelated() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsOuterJoins() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsPositionedDelete() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsPositionedUpdate() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsResultSetConcurrency(int type, int concurrency)
			throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsResultSetHoldability(int holdability)
			throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsResultSetType(int type) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsSavepoints() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsSchemasInDataManipulation() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsSchemasInIndexDefinitions() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsSchemasInProcedureCalls() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsSchemasInTableDefinitions() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsSelectForUpdate() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsStatementPooling() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsStoredProcedures() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsSubqueriesInComparisons() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsSubqueriesInExists() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsSubqueriesInIns() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsSubqueriesInQuantifieds() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsTableCorrelationNames() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsTransactionIsolationLevel(int level)
			throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsTransactions() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsUnion() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsUnionAll() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean updatesAreDetected(int type) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean usesLocalFilePerTable() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean usesLocalFiles() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getClientInfoProperties() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getFunctionColumns(String catalog, String schemaPattern,
			String functionNamePattern, String columnNamePattern)
			throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getFunctions(String catalog, String schemaPattern,
			String functionNamePattern) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public RowIdLifetime getRowIdLifetime() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public ResultSet getSchemas(String catalog, String schemaPattern)
			throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new RuntimeException("not implemented");
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new RuntimeException("not implemented");
	}

}
