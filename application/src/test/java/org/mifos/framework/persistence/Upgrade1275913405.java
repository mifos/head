package org.mifos.framework.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

class Upgrade1275913405 extends Upgrade{

    public Upgrade1275913405(){
//        super();
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