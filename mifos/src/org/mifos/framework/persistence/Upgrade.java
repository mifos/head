package org.mifos.framework.persistence;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class Upgrade {

	public URL sql() {
		throw new RuntimeException("only implemented for sql subclass");
	}

	abstract public void upgrade(Connection conn) 
	throws IOException, SQLException;

}
