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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.core.ClasspathResource;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DatabaseSetup;

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



    private Map<Integer, String> getAvailableUpgrades() throws IOException {
        Reader reader = null;
        BufferedReader bufferedReader = null;
        Map<Integer, String> upgrades = new HashMap<Integer, String>();
        try {
            reader = ClasspathResource.getInstance("/sql/").getAsReader("upgrades-non-sequential-checkpoint.txt");
            bufferedReader = new BufferedReader(reader);

            while (true) {
                String line = bufferedReader.readLine();
                String upgradeType = line.substring(0, line.indexOf(':'));
                Integer upgradeId = Integer.parseInt(line.substring(line.indexOf(':')+1));
                upgrades.put(upgradeId, upgradeType);
            }

        } catch (Exception e) {

        } finally {
            if (reader != null) {
                reader.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        return upgrades;
    }

    public void checkUnAppliedUpgradesAndUpgrade() throws IOException, SQLException  {
        Map<Integer, String> availableUpgrades = getAvailableUpgrades();
        List<Integer> appliedUpgrades = getAppliedUpgrades();

        for (int i : availableUpgrades.keySet()) {
            if (!appliedUpgrades.contains(i)) {
                applyUpgrade(i, availableUpgrades.get(i));
            }
        }
    }

    private void applyUpgrade(int upgradeNumber, String type) throws IOException, SQLException {

        Connection  connection = null;

        if ("sql".equals(type)) {
            try {
                connection = TestDatabase.getJDBCConnection();
                DatabaseSetup.executeScript(upgradeNumber + ".sql", connection);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e){

            }

        }

        if ("java".equals(type)) {
            String className = "org.mifos.application.master.persistence.Upgrade" + upgradeNumber;

                Upgrade upgrade = getInstanceOfUpgradeClass(className);
                upgrade.upgrade(connection);

        }
    }

    private Upgrade getInstanceOfUpgradeClass(String className){
        Upgrade upgrade = null;
        try {
            Class c = Class.forName(className);
            Constructor<Upgrade> constructor = c.getConstructor();
            upgrade= constructor.newInstance();

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

    private List<Integer> getAppliedUpgrades()   {

        // TODO convert query to HQL
        StaticHibernateUtil.initialize();


        Connection connection = null ;
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

}