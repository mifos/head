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

}
