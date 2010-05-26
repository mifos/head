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
import java.util.ArrayList;
import java.util.List;

import org.mifos.core.ClasspathResource;

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

    private List<Integer> getAvailableUpgrades() throws IOException {
        Reader reader = null;
        BufferedReader bufferedReader = null;
        List<Integer> upgrades = new ArrayList<Integer>();
        try {
            reader = ClasspathResource.getInstance("/sql").getAsReader("upgrades.txt");
            bufferedReader = new BufferedReader(reader);

            while (true) {
                upgrades.add(Integer.parseInt(bufferedReader.readLine()));
            }

        } catch (IOException e) {


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


    public void checkUnAppliedUpgradesAndUpgrade() throws IOException {
        List<Integer> availableUpgrades = getAvailableUpgrades();
        List<Integer> appliedUpgrades = getAppliedUpgrades();

        for (int i: availableUpgrades){
            if(appliedUpgrades.contains(i) == false){
                applyUpgrade(i);
            }
        }
    }

    private void applyUpgrade(int upgradeNumber){
        //TODO implement method
        // run sql script
        // for java upgrades, load class and run it.
    }

    private List<Integer> getAppliedUpgrades() {
        return null;

    }

}