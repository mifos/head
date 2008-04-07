package org.mifos.framework.util.helpers;

import java.sql.Types;

import org.hibernate.dialect.Dialect;

public class MayflyDialect extends Dialect {
	
	public MayflyDialect() {
		super();
//		registerColumnType( Types.BIT, "bit" );
		registerColumnType( Types.BIGINT, "bigint" );
		registerColumnType( Types.SMALLINT, "smallint" );
		registerColumnType( Types.TINYINT, "tinyint" );
		registerColumnType( Types.INTEGER, "integer" );
//		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.VARCHAR, "varchar($l)" );
//		registerColumnType( Types.FLOAT, "float" );
//		registerColumnType( Types.DOUBLE, "double" );
		registerColumnType( Types.DATE, "date" );
//		registerColumnType( Types.TIME, "time" );
		registerColumnType( Types.TIMESTAMP, "timestamp" );
//		registerColumnType( Types.VARBINARY, "varbinary($l)" );
//		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
		registerColumnType( Types.BLOB, "blob" );
		registerColumnType( Types.CLOB, "text" );
	}
	
	@Override
	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
	@Override
	public String getIdentityColumnString() {
		return "auto_increment not null";
	}

	@Override
	public String getIdentitySelectString() {
		return "call identity()";
	}

	@Override
	public String getIdentityInsertString() {
		return "default";
	}

	// I think we need to enable this, and the related
	// things like supportsSequences(), if we want to be
	// able to insert into a table like glcode, which
	// has an auto-increment column.
//	@Override
//	public Class getNativeIdentifierGeneratorClass() {
//		return SequenceGenerator.class;
//	}
	
	@Override
	public boolean supportsLimit() {
		return true;
	}
	
	@Override
	public boolean supportsLimitOffset() {
		return true;
	}
	
	@Override
	public boolean supportsVariableLimit() {
		return true;
	}

	@Override
	public String getLimitString(String sql, boolean hasOffset) {
		return new StringBuffer( sql.length()+20 )
			.append(sql)
			.append(hasOffset ? " limit ? offset ?" : " limit ?")
			.toString();
	}

}
