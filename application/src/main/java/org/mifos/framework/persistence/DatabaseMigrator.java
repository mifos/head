/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.framework.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.mifos.core.ClasspathResource;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.security.AddActivity;
import org.mifos.security.util.SecurityConstants;

/**
 * This class handles automated database schema and data changes.
 *
 * <ul>
 * <li>read (for example) application/src/main/resources/sql/upgrades.txt (a file containing names of available upgrades
 * from checkpoint 2 releases back)</li>
 * <li>read which upgrades from upgrades.txt have been applied to a database</li>
 * <li>determine if the database needs upgrading</li>
 * <li>apply any upgrades not currently applied to the database</li>
 * </ul>
 *
 * This class will eventually replace {@link DatabaseVersionPersistence}.
 */
public class DatabaseMigrator {

    private Connection connection;

    public SortedMap<Integer, String> availableUpgrades;

    public static final short ENGLISH_LOCALE = 1;

    public static final String CLASS_UPGRADE_TYPE = "class";
    public static final String METHOD_UPGRADE_TYPE = "method";
    public static final String SCRIPT_UPGRADE_TYPE = "sql";

    public DatabaseMigrator() {
        this(StaticHibernateUtil.getSessionTL().connection(), getAvailableUpgrades());

    }

    public DatabaseMigrator(Connection connection, SortedMap<Integer, String> availableUpgrades){
        this.connection = connection;
        this.availableUpgrades = availableUpgrades;

    }

    static SortedMap<Integer, String> getAvailableUpgrades(){
        Reader reader = null;
        BufferedReader bufferedReader = null;
        SortedMap<Integer, String> upgrades = new TreeMap<Integer, String>();
        try {
            reader = ClasspathResource.getInstance("/sql/").getAsReader("upgrades-checkpoint.txt");
            bufferedReader = new BufferedReader(reader);

            while (true) {
                String line = bufferedReader.readLine();
                String upgradeType = line.substring(0, line.indexOf(':'));
                Integer upgradeId = Integer.parseInt(line.substring(line.indexOf(':') + 1));
                upgrades.put(upgradeId, upgradeType);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

         return upgrades;
    }

    public void upgrade() throws Exception {
        List<Integer> appliedUpgrades = getAppliedUpgrades();

        for (int i : availableUpgrades.keySet()) {
            if (!appliedUpgrades.contains(i)) {
                applyUpgrade(i, availableUpgrades.get(i));
            }
        }
    }

    public boolean checkForUnAppliedUpgrades() throws Exception{
        if (false == this.isNSDU()){
            createAppliedUpgradesTable();
        }

        boolean unAppliedUpgrades = false;

        List<Integer> appliedUpgrades = getAppliedUpgrades();

        for (int i : availableUpgrades.keySet()) {
            if (!appliedUpgrades.contains(i)) {
                return true;
            }
        }

        return unAppliedUpgrades;

    }

    public void firstRun(Map<Integer, Integer> legacyUpgrades) throws Exception {
        createAppliedUpgradesTable();

        //check version number
        int databaseVersion = readDatabaseVersion();

        //insert all upgrades below version number in applied upgrades table
        Set<Integer> appliedUpgrades = legacyUpgrades.keySet();
        for(Integer i:appliedUpgrades){
            if (i <= databaseVersion){
                connection.createStatement().execute("insert into APPLIED_UPGRADES values("+legacyUpgrades.get(i)+")");
            }
        }

        //remove database version table
        connection.createStatement().execute("drop table database_version");

        if (!isNSDU()){
            throw new RuntimeException("Failed to migrate schema to NSDU");
        }
    }

    public Map<Integer, Integer> getLegacyUpgradesMap(){
        // TODO
        return null;
    }

    public int readDatabaseVersion() throws SQLException {
        return readDatabaseVersion(connection);
    }

    public int readDatabaseVersion(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet results = statement.executeQuery("select DATABASE_VERSION from DATABASE_VERSION");
        if (results.next()) {
            int version = results.getInt("DATABASE_VERSION");
            if (results.next()) {
                throw new RuntimeException("too many rows in DATABASE_VERSION");
            }
            statement.close();
            return version;
        }
        throw new RuntimeException("No row in DATABASE_VERSION");
    }

    private void createAppliedUpgradesTable() {
        try {
            connection.createStatement().execute("CREATE TABLE  APPLIED_UPGRADES" +
                    "( UPGRADE_ID INTEGER NOT NULL,"+
                    "PRIMARY KEY(UPGRADE_ID)"+
                    ")ENGINE=InnoDB CHARACTER SET utf8;");
            connection.commit();
        } catch (SQLException e) {
            System.err.print("Unable to create APPLIED_UPGRADES table for NSDU");
        }
    }

    private void applyUpgrade(int upgradeNumber, String type) throws Exception {

        if (SCRIPT_UPGRADE_TYPE.equals(type)) {

            DatabaseSetup.executeScript(upgradeNumber + ".sql", connection);

        }

        if (CLASS_UPGRADE_TYPE.equals(type)) {
            String className = "org.mifos.application.master.persistence.JavaUpgrade" + upgradeNumber;

            Upgrade upgradeClass = getInstanceOfUpgradeClass(className);
            upgradeClass.upgrade(connection);

        }

        if (METHOD_UPGRADE_TYPE.equals(type)){

            Method method = DatabaseMigrator.class.getDeclaredMethod("upgrade"+upgradeNumber);

            try {
                method.invoke(this);
            } catch (InvocationTargetException e) {
                e.getCause().printStackTrace();
                e.printStackTrace();
            }


        }
    }

    private Upgrade getInstanceOfUpgradeClass(String className) {
        Upgrade upgrade = null;
        Class<?> c = null;
        Constructor<?> cs = null;
        try {
            c = Class.forName(className);
            cs = c.getDeclaredConstructor();
            cs.setAccessible(true);
            upgrade = (Upgrade) cs.newInstance();
//            upgrade = new JavaUpgrade1275913405();

        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return upgrade;
    }

    private List<Integer> getAppliedUpgrades() {

        // TODO convert query to HQL
        StaticHibernateUtil.initialize();

        Connection connection = null;
        try {
            connection = TestDatabase.getJDBCConnection();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        List<Integer> appliedUpgrades = new ArrayList<Integer>();

        Statement stmt = null;
        try {
            stmt = connection.createStatement(ResultSet.FETCH_FORWARD, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery("SELECT UPGRADE_ID FROM APPLIED_UPGRADES");

            if (rs.next()) {
                appliedUpgrades.add(rs.getInt(0));
            }

            rs.close();

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appliedUpgrades;

    }

    public boolean isNSDU() throws SQLException{
        return isNSDU(connection);
    }

    public boolean isNSDU(Connection conn) throws SQLException {
        ResultSet results = conn.getMetaData().getColumns(null, null, "APPLIED_UPGRADES", "UPGRADE_ID");
        boolean foundColumns = results.next();
        results.close();
        return foundColumns;
    }


    @SuppressWarnings("unused")
    private static void upgrade236() {
        new AddActivity("Permissions-CanShutdownMifos",
                SecurityConstants.CAN_SHUTDOWN_MIFOS, SecurityConstants.SYSTEM_INFORMATION);
    }

    @SuppressWarnings("unused")
    private static void upgrade248() {
        new AddActivity("Permissions-CanDefineHoliday",
                SecurityConstants.CAN_DEFINE_HOLIDAY, SecurityConstants.ORGANIZATION_MANAGEMENT);
//        register(register, new CompositeUpgrade(
//                new AddActivity(248, SecurityConstants.PRODUCT_MIX, null, ENGLISH_LOCALE, "Product mix"),
//                new AddActivity(248, SecurityConstants.CAN_DEFINE_PRODUCT_MIX, SecurityConstants.PRODUCT_MIX, ENGLISH_LOCALE, "Can Define product mix"),
//                new AddActivity(248, SecurityConstants.CAN_EDIT_PRODUCT_MIX, SecurityConstants.PRODUCT_MIX, ENGLISH_LOCALE, "Can Edit product mix")));
    }

    public void upgrade1277124044() throws IOException, SQLException{
        Upgrade upgrade = new DummyUpgrade();
        upgrade.upgrade(connection);
    }

}