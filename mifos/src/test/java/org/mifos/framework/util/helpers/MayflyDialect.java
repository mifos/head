/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
