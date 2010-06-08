package org.mifos.application.master.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.mifos.framework.persistence.Upgrade;

class JavaUpgrade1275913405 extends Upgrade{

    public JavaUpgrade1275913405(){
        super(1275913405);
    }

    protected JavaUpgrade1275913405(int higherVersion) {
        super(higherVersion);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {
        connection.createStatement().execute("drop table if exists baz");
        connection.createStatement().execute("create table baz ( "+
                "baz_id integer"+
                ") ENGINE=InnoDB CHARACTER SET utf8 ");


        connection.createStatement().execute("INSERT INTO baz VALUES(1202)");
        connection.commit();

    }

}