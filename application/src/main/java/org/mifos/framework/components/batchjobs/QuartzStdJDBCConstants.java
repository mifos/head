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

/*
 * Copyright 2001-2009 Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */

package org.mifos.framework.components.batchjobs;

/**
 * <p>
 * This interface extends <code>{@link
 * org.quartz.impl.jdbcjobstore.Constants}</code>
 * to include the query string constants in use by the <code>{@link
 * org.quartz.impl.jdbcjobstore.StdJDBCDelegate}</code>
 * class.
 * </p>
 *
 * @author <a href="mailto:jeff@binaryfeed.org">Jeffrey Wescott</a>
 */
public interface QuartzStdJDBCConstants extends QuartzConstants {

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Constants.
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    // table prefix substitution string
    String TABLE_PREFIX_SUBST = "{0}";

    // QUERIES
    String UPDATE_TRIGGER_STATES_FROM_OTHER_STATES = "UPDATE "
            + TABLE_PREFIX_SUBST
            + TABLE_TRIGGERS
            + " SET "
            + COL_TRIGGER_STATE
            + " = ?"
            + " WHERE "
            + COL_TRIGGER_STATE
            + " = ? OR "
            + COL_TRIGGER_STATE + " = ?";

    String UPDATE_TRIGGER_STATE_FROM_OTHER_STATES_BEFORE_TIME = "UPDATE "
            + TABLE_PREFIX_SUBST
            + TABLE_TRIGGERS
            + " SET "
            + COL_TRIGGER_STATE
            + " = ?"
            + " WHERE ("
            + COL_TRIGGER_STATE
            + " = ? OR "
            + COL_TRIGGER_STATE + " = ?) AND " + COL_NEXT_FIRE_TIME + " < ?";

    String SELECT_MISFIRED_TRIGGERS = "SELECT * FROM "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE "
            + COL_NEXT_FIRE_TIME + " < ? "
            + "ORDER BY " + COL_NEXT_FIRE_TIME + " ASC";

    String SELECT_TRIGGERS_IN_STATE = "SELECT "
            + COL_TRIGGER_NAME + ", " + COL_TRIGGER_GROUP + " FROM "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE "
            + COL_TRIGGER_STATE + " = ?";

    String SELECT_MISFIRED_TRIGGERS_IN_STATE = "SELECT "
            + COL_TRIGGER_NAME + ", " + COL_TRIGGER_GROUP + " FROM "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE "
            + COL_NEXT_FIRE_TIME + " < ? AND " + COL_TRIGGER_STATE + " = ? "
            + "ORDER BY " + COL_NEXT_FIRE_TIME + " ASC";

    String COUNT_MISFIRED_TRIGGERS_IN_STATES = "SELECT COUNT("
        + COL_TRIGGER_NAME + ") FROM "
        + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE "
        + COL_NEXT_FIRE_TIME + " < ? "
        + "AND ((" + COL_TRIGGER_STATE + " = ?) OR (" + COL_TRIGGER_STATE + " = ?))";

    String SELECT_MISFIRED_TRIGGERS_IN_STATES = "SELECT "
        + COL_TRIGGER_NAME + ", " + COL_TRIGGER_GROUP + " FROM "
        + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE "
        + COL_NEXT_FIRE_TIME + " < ? "
        + "AND ((" + COL_TRIGGER_STATE + " = ?) OR (" + COL_TRIGGER_STATE + " = ?)) "
        + "ORDER BY " + COL_NEXT_FIRE_TIME + " ASC";

    String SELECT_MISFIRED_TRIGGERS_IN_GROUP_IN_STATE = "SELECT "
            + COL_TRIGGER_NAME
            + " FROM "
            + TABLE_PREFIX_SUBST
            + TABLE_TRIGGERS
            + " WHERE "
            + COL_NEXT_FIRE_TIME
            + " < ? AND "
            + COL_TRIGGER_GROUP
            + " = ? AND " + COL_TRIGGER_STATE + " = ? "
            + "ORDER BY " + COL_NEXT_FIRE_TIME + " ASC";


    String SELECT_VOLATILE_TRIGGERS = "SELECT "
            + COL_TRIGGER_NAME + ", " + COL_TRIGGER_GROUP + " FROM "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE " + COL_IS_VOLATILE
            + " = ?";

    String DELETE_FIRED_TRIGGERS = "DELETE FROM "
            + TABLE_PREFIX_SUBST + TABLE_FIRED_TRIGGERS;

    String INSERT_JOB_DETAIL = "INSERT INTO "
            + TABLE_PREFIX_SUBST + TABLE_JOB_DETAILS + " (" + COL_JOB_NAME
            + ", " + COL_JOB_GROUP + ", " + COL_DESCRIPTION + ", "
            + COL_JOB_CLASS + ", " + COL_IS_DURABLE + ", " + COL_IS_VOLATILE
            + ", " + COL_IS_STATEFUL + ", " + COL_REQUESTS_RECOVERY + ", "
            + COL_JOB_DATAMAP + ") " + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

    String UPDATE_JOB_DETAIL = "UPDATE "
            + TABLE_PREFIX_SUBST + TABLE_JOB_DETAILS + " SET "
            + COL_DESCRIPTION + " = ?, " + COL_JOB_CLASS + " = ?, "
            + COL_IS_DURABLE + " = ?, " + COL_IS_VOLATILE + " = ?, "
            + COL_IS_STATEFUL + " = ?, " + COL_REQUESTS_RECOVERY + " = ?, "
            + COL_JOB_DATAMAP + " = ? " + " WHERE " + COL_JOB_NAME
            + " = ? AND " + COL_JOB_GROUP + " = ?";

    String SELECT_TRIGGERS_FOR_JOB = "SELECT "
            + COL_TRIGGER_NAME + ", " + COL_TRIGGER_GROUP + " FROM "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE " + COL_JOB_NAME
            + " = ? AND " + COL_JOB_GROUP + " = ?";

    String SELECT_TRIGGERS_FOR_CALENDAR = "SELECT "
        + COL_TRIGGER_NAME + ", " + COL_TRIGGER_GROUP + " FROM "
        + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE " + COL_CALENDAR_NAME
        + " = ?";

    String SELECT_STATEFUL_JOBS_OF_TRIGGER_GROUP = "SELECT DISTINCT J."
            + COL_JOB_NAME
            + ", J."
            + COL_JOB_GROUP
            + " FROM "
            + TABLE_PREFIX_SUBST
            + TABLE_TRIGGERS
            + " T, "
            + TABLE_PREFIX_SUBST
            + TABLE_JOB_DETAILS
            + " J WHERE T."
            + COL_TRIGGER_GROUP
            + " = ? AND T."
            + COL_JOB_NAME
            + " = J."
            + COL_JOB_NAME
            + " AND T."
            + COL_JOB_GROUP
            + " = J."
            + COL_JOB_GROUP
            + " AND J."
            + COL_IS_STATEFUL + " = ?";

    String DELETE_JOB_LISTENERS = "DELETE FROM "
            + TABLE_PREFIX_SUBST + TABLE_JOB_LISTENERS + " WHERE "
            + COL_JOB_NAME + " = ? AND " + COL_JOB_GROUP + " = ?";

    String DELETE_JOB_DETAIL = "DELETE FROM "
            + TABLE_PREFIX_SUBST + TABLE_JOB_DETAILS + " WHERE " + COL_JOB_NAME
            + " = ? AND " + COL_JOB_GROUP + " = ?";

    String SELECT_JOB_STATEFUL = "SELECT "
            + COL_IS_STATEFUL + " FROM " + TABLE_PREFIX_SUBST
            + TABLE_JOB_DETAILS + " WHERE " + COL_JOB_NAME + " = ? AND "
            + COL_JOB_GROUP + " = ?";

    String SELECT_JOB_EXISTENCE = "SELECT " + COL_JOB_NAME
            + " FROM " + TABLE_PREFIX_SUBST + TABLE_JOB_DETAILS + " WHERE "
            + COL_JOB_NAME + " = ? AND " + COL_JOB_GROUP + " = ?";

    String UPDATE_JOB_DATA = "UPDATE " + TABLE_PREFIX_SUBST
            + TABLE_JOB_DETAILS + " SET " + COL_JOB_DATAMAP + " = ? "
            + " WHERE " + COL_JOB_NAME + " = ? AND " + COL_JOB_GROUP + " = ?";

    String INSERT_JOB_LISTENER = "INSERT INTO "
            + TABLE_PREFIX_SUBST + TABLE_JOB_LISTENERS + " (" + COL_JOB_NAME
            + ", " + COL_JOB_GROUP + ", " + COL_JOB_LISTENER
            + ") VALUES(?, ?, ?)";

    String SELECT_JOB_LISTENERS = "SELECT "
            + COL_JOB_LISTENER + " FROM " + TABLE_PREFIX_SUBST
            + TABLE_JOB_LISTENERS + " WHERE " + COL_JOB_NAME + " = ? AND "
            + COL_JOB_GROUP + " = ?";

    String SELECT_JOB_DETAIL = "SELECT *" + " FROM "
            + TABLE_PREFIX_SUBST + TABLE_JOB_DETAILS + " WHERE " + COL_JOB_NAME
            + " = ? AND " + COL_JOB_GROUP + " = ?";

    String SELECT_NUM_JOBS = "SELECT COUNT(" + COL_JOB_NAME
            + ") " + " FROM " + TABLE_PREFIX_SUBST + TABLE_JOB_DETAILS;

    String SELECT_JOB_GROUPS = "SELECT DISTINCT("
            + COL_JOB_GROUP + ") FROM " + TABLE_PREFIX_SUBST
            + TABLE_JOB_DETAILS;

    String SELECT_JOBS_IN_GROUP = "SELECT " + COL_JOB_NAME
            + " FROM " + TABLE_PREFIX_SUBST + TABLE_JOB_DETAILS + " WHERE "
            + COL_JOB_GROUP + " = ?";

    String SELECT_VOLATILE_JOBS = "SELECT " + COL_JOB_NAME
            + ", " + COL_JOB_GROUP + " FROM " + TABLE_PREFIX_SUBST
            + TABLE_JOB_DETAILS + " WHERE " + COL_IS_VOLATILE + " = ?";

    String INSERT_TRIGGER = "INSERT INTO "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " (" + COL_TRIGGER_NAME
            + ", " + COL_TRIGGER_GROUP + ", " + COL_JOB_NAME + ", "
            + COL_JOB_GROUP + ", " + COL_IS_VOLATILE + ", " + COL_DESCRIPTION
            + ", " + COL_NEXT_FIRE_TIME + ", " + COL_PREV_FIRE_TIME + ", "
            + COL_TRIGGER_STATE + ", " + COL_TRIGGER_TYPE + ", "
            + COL_START_TIME + ", " + COL_END_TIME + ", " + COL_CALENDAR_NAME
            + ", " + COL_MISFIRE_INSTRUCTION + ", " + COL_JOB_DATAMAP + ", " + COL_PRIORITY + ") "
            + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    String INSERT_SIMPLE_TRIGGER = "INSERT INTO "
            + TABLE_PREFIX_SUBST + TABLE_SIMPLE_TRIGGERS + " ("
            + COL_TRIGGER_NAME + ", " + COL_TRIGGER_GROUP + ", "
            + COL_REPEAT_COUNT + ", " + COL_REPEAT_INTERVAL + ", "
            + COL_TIMES_TRIGGERED + ") " + " VALUES(?, ?, ?, ?, ?)";

    String INSERT_CRON_TRIGGER = "INSERT INTO "
            + TABLE_PREFIX_SUBST + TABLE_CRON_TRIGGERS + " ("
            + COL_TRIGGER_NAME + ", " + COL_TRIGGER_GROUP + ", "
            + COL_CRON_EXPRESSION + ", " + COL_TIME_ZONE_ID + ") "
            + " VALUES(?, ?, ?, ?)";

    String INSERT_BLOB_TRIGGER = "INSERT INTO "
            + TABLE_PREFIX_SUBST + TABLE_BLOB_TRIGGERS + " ("
            + COL_TRIGGER_NAME + ", " + COL_TRIGGER_GROUP + ", " + COL_BLOB
            + ") " + " VALUES(?, ?, ?)";

    String UPDATE_TRIGGER_SKIP_DATA = "UPDATE " + TABLE_PREFIX_SUBST
            + TABLE_TRIGGERS + " SET " + COL_JOB_NAME + " = ?, "
            + COL_JOB_GROUP + " = ?, " + COL_IS_VOLATILE + " = ?, "
            + COL_DESCRIPTION + " = ?, " + COL_NEXT_FIRE_TIME + " = ?, "
            + COL_PREV_FIRE_TIME + " = ?, " + COL_TRIGGER_STATE + " = ?, "
            + COL_TRIGGER_TYPE + " = ?, " + COL_START_TIME + " = ?, "
            + COL_END_TIME + " = ?, " + COL_CALENDAR_NAME + " = ?, "
            + COL_MISFIRE_INSTRUCTION + " = ?, " + COL_PRIORITY
            + " = ? WHERE " + COL_TRIGGER_NAME
            + " = ? AND " + COL_TRIGGER_GROUP + " = ?";

    String UPDATE_TRIGGER = "UPDATE " + TABLE_PREFIX_SUBST
        + TABLE_TRIGGERS + " SET " + COL_JOB_NAME + " = ?, "
        + COL_JOB_GROUP + " = ?, " + COL_IS_VOLATILE + " = ?, "
        + COL_DESCRIPTION + " = ?, " + COL_NEXT_FIRE_TIME + " = ?, "
        + COL_PREV_FIRE_TIME + " = ?, " + COL_TRIGGER_STATE + " = ?, "
        + COL_TRIGGER_TYPE + " = ?, " + COL_START_TIME + " = ?, "
        + COL_END_TIME + " = ?, " + COL_CALENDAR_NAME + " = ?, "
        + COL_MISFIRE_INSTRUCTION + " = ?, " + COL_PRIORITY + " = ?, "
        + COL_JOB_DATAMAP + " = ? WHERE "
        + COL_TRIGGER_NAME + " = ? AND " + COL_TRIGGER_GROUP + " = ?";

    String UPDATE_SIMPLE_TRIGGER = "UPDATE "
            + TABLE_PREFIX_SUBST + TABLE_SIMPLE_TRIGGERS + " SET "
            + COL_REPEAT_COUNT + " = ?, " + COL_REPEAT_INTERVAL + " = ?, "
            + COL_TIMES_TRIGGERED + " = ? WHERE " + COL_TRIGGER_NAME
            + " = ? AND " + COL_TRIGGER_GROUP + " = ?";

    String UPDATE_CRON_TRIGGER = "UPDATE "
            + TABLE_PREFIX_SUBST + TABLE_CRON_TRIGGERS + " SET "
            + COL_CRON_EXPRESSION + " = ? WHERE " + COL_TRIGGER_NAME
            + " = ? AND " + COL_TRIGGER_GROUP + " = ?";

    String UPDATE_BLOB_TRIGGER = "UPDATE "
            + TABLE_PREFIX_SUBST + TABLE_BLOB_TRIGGERS + " SET " + COL_BLOB
            + " = ? WHERE " + COL_TRIGGER_NAME + " = ? AND "
            + COL_TRIGGER_GROUP + " = ?";

    String SELECT_TRIGGER_EXISTENCE = "SELECT "
            + COL_TRIGGER_NAME + " FROM " + TABLE_PREFIX_SUBST + TABLE_TRIGGERS
            + " WHERE " + COL_TRIGGER_NAME + " = ? AND " + COL_TRIGGER_GROUP
            + " = ?";

    String UPDATE_TRIGGER_STATE = "UPDATE "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " SET " + COL_TRIGGER_STATE
            + " = ?" + " WHERE " + COL_TRIGGER_NAME + " = ? AND "
            + COL_TRIGGER_GROUP + " = ?";

    String UPDATE_TRIGGER_STATE_FROM_STATE = "UPDATE "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " SET " + COL_TRIGGER_STATE
            + " = ?" + " WHERE " + COL_TRIGGER_NAME + " = ? AND "
            + COL_TRIGGER_GROUP + " = ? AND " + COL_TRIGGER_STATE + " = ?";

    String UPDATE_TRIGGER_GROUP_STATE = "UPDATE "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " SET " + COL_TRIGGER_STATE
            + " = ?";

    String UPDATE_TRIGGER_GROUP_STATE_FROM_STATE = "UPDATE "
            + TABLE_PREFIX_SUBST
            + TABLE_TRIGGERS
            + " SET "
            + COL_TRIGGER_STATE
            + " = ?"
            + " WHERE "
            + COL_TRIGGER_GROUP
            + " = ? AND "
            + COL_TRIGGER_STATE + " = ?";

    String UPDATE_TRIGGER_STATE_FROM_STATES = "UPDATE "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " SET " + COL_TRIGGER_STATE
            + " = ?" + " WHERE " + COL_TRIGGER_NAME + " = ? AND "
            + COL_TRIGGER_GROUP + " = ? AND (" + COL_TRIGGER_STATE + " = ? OR "
            + COL_TRIGGER_STATE + " = ? OR " + COL_TRIGGER_STATE + " = ?)";

    String UPDATE_TRIGGER_GROUP_STATE_FROM_STATES = "UPDATE "
            + TABLE_PREFIX_SUBST
            + TABLE_TRIGGERS
            + " SET "
            + COL_TRIGGER_STATE
            + " = ?"
            + " WHERE "
            + COL_TRIGGER_GROUP
            + " = ? AND ("
            + COL_TRIGGER_STATE
            + " = ? OR "
            + COL_TRIGGER_STATE
            + " = ? OR "
            + COL_TRIGGER_STATE + " = ?)";

    String UPDATE_JOB_TRIGGER_STATES = "UPDATE "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " SET " + COL_TRIGGER_STATE
            + " = ? WHERE " + COL_JOB_NAME + " = ? AND " + COL_JOB_GROUP
            + " = ?";

    String UPDATE_JOB_TRIGGER_STATES_FROM_OTHER_STATE = "UPDATE "
            + TABLE_PREFIX_SUBST
            + TABLE_TRIGGERS
            + " SET "
            + COL_TRIGGER_STATE
            + " = ? WHERE "
            + COL_JOB_NAME
            + " = ? AND "
            + COL_JOB_GROUP
            + " = ? AND " + COL_TRIGGER_STATE + " = ?";

    String DELETE_TRIGGER_LISTENERS = "DELETE FROM "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGER_LISTENERS + " WHERE "
            + COL_TRIGGER_NAME + " = ? AND " + COL_TRIGGER_GROUP + " = ?";

    String INSERT_TRIGGER_LISTENER = "INSERT INTO "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGER_LISTENERS + " ("
            + COL_TRIGGER_NAME + ", " + COL_TRIGGER_GROUP + ", "
            + COL_TRIGGER_LISTENER + ") VALUES(?, ?, ?)";

    String SELECT_TRIGGER_LISTENERS = "SELECT "
            + COL_TRIGGER_LISTENER + " FROM " + TABLE_PREFIX_SUBST
            + TABLE_TRIGGER_LISTENERS + " WHERE " + COL_TRIGGER_NAME
            + " = ? AND " + COL_TRIGGER_GROUP + " = ?";

    String DELETE_SIMPLE_TRIGGER = "DELETE FROM "
            + TABLE_PREFIX_SUBST + TABLE_SIMPLE_TRIGGERS + " WHERE "
            + COL_TRIGGER_NAME + " = ? AND " + COL_TRIGGER_GROUP + " = ?";

    String DELETE_CRON_TRIGGER = "DELETE FROM "
            + TABLE_PREFIX_SUBST + TABLE_CRON_TRIGGERS + " WHERE "
            + COL_TRIGGER_NAME + " = ? AND " + COL_TRIGGER_GROUP + " = ?";

    String DELETE_BLOB_TRIGGER = "DELETE FROM "
            + TABLE_PREFIX_SUBST + TABLE_BLOB_TRIGGERS + " WHERE "
            + COL_TRIGGER_NAME + " = ? AND " + COL_TRIGGER_GROUP + " = ?";

    String DELETE_TRIGGER = "DELETE FROM "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE "
            + COL_TRIGGER_NAME + " = ? AND " + COL_TRIGGER_GROUP + " = ?";

    String SELECT_NUM_TRIGGERS_FOR_JOB = "SELECT COUNT("
            + COL_TRIGGER_NAME + ") FROM " + TABLE_PREFIX_SUBST
            + TABLE_TRIGGERS + " WHERE " + COL_JOB_NAME + " = ? AND "
            + COL_JOB_GROUP + " = ?";

    String SELECT_JOB_FOR_TRIGGER = "SELECT J."
            + COL_JOB_NAME + ", J." + COL_JOB_GROUP + ", J." + COL_IS_DURABLE
            + ", J." + COL_JOB_CLASS + ", J." + COL_REQUESTS_RECOVERY + " FROM " + TABLE_PREFIX_SUBST
            + TABLE_TRIGGERS + " T, " + TABLE_PREFIX_SUBST + TABLE_JOB_DETAILS
            + " J WHERE T." + COL_TRIGGER_NAME + " = ? AND T."
            + COL_TRIGGER_GROUP + " = ? AND T." + COL_JOB_NAME + " = J."
            + COL_JOB_NAME + " AND T." + COL_JOB_GROUP + " = J."
            + COL_JOB_GROUP;

    String SELECT_TRIGGER = "SELECT *" + " FROM "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE "
            + COL_TRIGGER_NAME + " = ? AND " + COL_TRIGGER_GROUP + " = ?";

    String SELECT_TRIGGER_DATA = "SELECT " +
            COL_JOB_DATAMAP + " FROM "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE "
            + COL_TRIGGER_NAME + " = ? AND " + COL_TRIGGER_GROUP + " = ?";

    String SELECT_TRIGGER_STATE = "SELECT "
            + COL_TRIGGER_STATE + " FROM " + TABLE_PREFIX_SUBST
            + TABLE_TRIGGERS + " WHERE " + COL_TRIGGER_NAME + " = ? AND "
            + COL_TRIGGER_GROUP + " = ?";

    String SELECT_TRIGGER_STATUS = "SELECT "
            + COL_TRIGGER_STATE + ", " + COL_NEXT_FIRE_TIME + ", "
            + COL_JOB_NAME + ", " + COL_JOB_GROUP + " FROM "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE "
            + COL_TRIGGER_NAME + " = ? AND " + COL_TRIGGER_GROUP + " = ?";

    String SELECT_SIMPLE_TRIGGER = "SELECT *" + " FROM "
            + TABLE_PREFIX_SUBST + TABLE_SIMPLE_TRIGGERS + " WHERE "
            + COL_TRIGGER_NAME + " = ? AND " + COL_TRIGGER_GROUP + " = ?";

    String SELECT_CRON_TRIGGER = "SELECT *" + " FROM "
            + TABLE_PREFIX_SUBST + TABLE_CRON_TRIGGERS + " WHERE "
            + COL_TRIGGER_NAME + " = ? AND " + COL_TRIGGER_GROUP + " = ?";

    String SELECT_BLOB_TRIGGER = "SELECT *" + " FROM "
            + TABLE_PREFIX_SUBST + TABLE_BLOB_TRIGGERS + " WHERE "
            + COL_TRIGGER_NAME + " = ? AND " + COL_TRIGGER_GROUP + " = ?";

    String SELECT_NUM_TRIGGERS = "SELECT COUNT("
            + COL_TRIGGER_NAME + ") " + " FROM " + TABLE_PREFIX_SUBST
            + TABLE_TRIGGERS;

    String SELECT_NUM_TRIGGERS_IN_GROUP = "SELECT COUNT("
            + COL_TRIGGER_NAME + ") " + " FROM " + TABLE_PREFIX_SUBST
            + TABLE_TRIGGERS + " WHERE " + COL_TRIGGER_GROUP + " = ?";

    String SELECT_TRIGGER_GROUPS = "SELECT DISTINCT("
            + COL_TRIGGER_GROUP + ") FROM " + TABLE_PREFIX_SUBST
            + TABLE_TRIGGERS;

    String SELECT_TRIGGERS_IN_GROUP = "SELECT "
            + COL_TRIGGER_NAME + " FROM " + TABLE_PREFIX_SUBST + TABLE_TRIGGERS
            + " WHERE " + COL_TRIGGER_GROUP + " = ?";

    String INSERT_CALENDAR = "INSERT INTO "
            + TABLE_PREFIX_SUBST + TABLE_CALENDARS + " (" + COL_CALENDAR_NAME
            + ", " + COL_CALENDAR + ") " + " VALUES(?, ?)";

    String UPDATE_CALENDAR = "UPDATE " + TABLE_PREFIX_SUBST
            + TABLE_CALENDARS + " SET " + COL_CALENDAR + " = ? " + " WHERE "
            + COL_CALENDAR_NAME + " = ?";

    String SELECT_CALENDAR_EXISTENCE = "SELECT "
            + COL_CALENDAR_NAME + " FROM " + TABLE_PREFIX_SUBST
            + TABLE_CALENDARS + " WHERE " + COL_CALENDAR_NAME + " = ?";

    String SELECT_CALENDAR = "SELECT *" + " FROM "
            + TABLE_PREFIX_SUBST + TABLE_CALENDARS + " WHERE "
            + COL_CALENDAR_NAME + " = ?";

    String SELECT_REFERENCED_CALENDAR = "SELECT "
            + COL_CALENDAR_NAME + " FROM " + TABLE_PREFIX_SUBST
            + TABLE_TRIGGERS + " WHERE " + COL_CALENDAR_NAME + " = ?";

    String DELETE_CALENDAR = "DELETE FROM "
            + TABLE_PREFIX_SUBST + TABLE_CALENDARS + " WHERE "
            + COL_CALENDAR_NAME + " = ?";

    String SELECT_NUM_CALENDARS = "SELECT COUNT("
            + COL_CALENDAR_NAME + ") " + " FROM " + TABLE_PREFIX_SUBST
            + TABLE_CALENDARS;

    String SELECT_CALENDARS = "SELECT " + COL_CALENDAR_NAME
            + " FROM " + TABLE_PREFIX_SUBST + TABLE_CALENDARS;

    String SELECT_NEXT_FIRE_TIME = "SELECT MIN("
            + COL_NEXT_FIRE_TIME + ") AS " + ALIAS_COL_NEXT_FIRE_TIME
            + " FROM " + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE "
            + COL_TRIGGER_STATE + " = ? AND " + COL_NEXT_FIRE_TIME + " >= 0";

    String SELECT_TRIGGER_FOR_FIRE_TIME = "SELECT "
            + COL_TRIGGER_NAME + ", " + COL_TRIGGER_GROUP + " FROM "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE "
            + COL_TRIGGER_STATE + " = ? AND " + COL_NEXT_FIRE_TIME + " = ?";

    String SELECT_NEXT_TRIGGER_TO_ACQUIRE = "SELECT "
        + COL_TRIGGER_NAME + ", " + COL_TRIGGER_GROUP + ", "
        + COL_NEXT_FIRE_TIME + ", " + COL_PRIORITY + " FROM "
        + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE "
        + COL_TRIGGER_STATE + " = ? AND " + COL_NEXT_FIRE_TIME + " < ? "
        + "AND (" + COL_NEXT_FIRE_TIME + " >= ?) "
        + "ORDER BY "+ COL_NEXT_FIRE_TIME + " ASC, " + COL_PRIORITY + " DESC";


    String INSERT_FIRED_TRIGGER = "INSERT INTO "
            + TABLE_PREFIX_SUBST + TABLE_FIRED_TRIGGERS + " (" + COL_ENTRY_ID
            + ", " + COL_TRIGGER_NAME + ", " + COL_TRIGGER_GROUP + ", "
            + COL_IS_VOLATILE + ", " + COL_INSTANCE_NAME + ", "
            + COL_FIRED_TIME + ", " + COL_ENTRY_STATE + ", " + COL_JOB_NAME
            + ", " + COL_JOB_GROUP + ", " + COL_IS_STATEFUL + ", "
            + COL_REQUESTS_RECOVERY + ", " + COL_PRIORITY
            + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    String UPDATE_INSTANCES_FIRED_TRIGGER_STATE = "UPDATE "
            + TABLE_PREFIX_SUBST + TABLE_FIRED_TRIGGERS + " SET "
            + COL_ENTRY_STATE + " = ? AND " + COL_FIRED_TIME + " = ? AND " + COL_PRIORITY+ " = ? WHERE "
            + COL_INSTANCE_NAME + " = ?";

    String SELECT_INSTANCES_FIRED_TRIGGERS = "SELECT * FROM "
            + TABLE_PREFIX_SUBST
            + TABLE_FIRED_TRIGGERS
            + " WHERE "
            + COL_INSTANCE_NAME + " = ?";

    String SELECT_INSTANCES_RECOVERABLE_FIRED_TRIGGERS = "SELECT * FROM "
            + TABLE_PREFIX_SUBST
            + TABLE_FIRED_TRIGGERS
            + " WHERE "
            + COL_INSTANCE_NAME + " = ? AND " + COL_REQUESTS_RECOVERY + " = ?";

    String SELECT_JOB_EXECUTION_COUNT = "SELECT COUNT("
            + COL_TRIGGER_NAME + ") FROM " + TABLE_PREFIX_SUBST
            + TABLE_FIRED_TRIGGERS + " WHERE " + COL_JOB_NAME + " = ? AND "
            + COL_JOB_GROUP + " = ?";

    String SELECT_FIRED_TRIGGERS = "SELECT * FROM "
            + TABLE_PREFIX_SUBST + TABLE_FIRED_TRIGGERS;

    String SELECT_FIRED_TRIGGER = "SELECT * FROM "
            + TABLE_PREFIX_SUBST + TABLE_FIRED_TRIGGERS + " WHERE "
            + COL_TRIGGER_NAME + " = ? AND " + COL_TRIGGER_GROUP + " = ?";

    String SELECT_FIRED_TRIGGER_GROUP = "SELECT * FROM "
            + TABLE_PREFIX_SUBST + TABLE_FIRED_TRIGGERS + " WHERE "
            + COL_TRIGGER_GROUP + " = ?";

    String SELECT_FIRED_TRIGGERS_OF_JOB = "SELECT * FROM "
            + TABLE_PREFIX_SUBST + TABLE_FIRED_TRIGGERS + " WHERE "
            + COL_JOB_NAME + " = ? AND " + COL_JOB_GROUP + " = ?";

    String SELECT_FIRED_TRIGGERS_OF_JOB_GROUP = "SELECT * FROM "
            + TABLE_PREFIX_SUBST
            + TABLE_FIRED_TRIGGERS
            + " WHERE "
            + COL_JOB_GROUP + " = ?";

    String DELETE_FIRED_TRIGGER = "DELETE FROM "
            + TABLE_PREFIX_SUBST + TABLE_FIRED_TRIGGERS + " WHERE "
            + COL_ENTRY_ID + " = ?";

    String DELETE_INSTANCES_FIRED_TRIGGERS = "DELETE FROM "
            + TABLE_PREFIX_SUBST + TABLE_FIRED_TRIGGERS + " WHERE "
            + COL_INSTANCE_NAME + " = ?";

    String DELETE_VOLATILE_FIRED_TRIGGERS = "DELETE FROM "
            + TABLE_PREFIX_SUBST + TABLE_FIRED_TRIGGERS + " WHERE "
            + COL_IS_VOLATILE + " = ?";

    String DELETE_NO_RECOVERY_FIRED_TRIGGERS = "DELETE FROM "
            + TABLE_PREFIX_SUBST
            + TABLE_FIRED_TRIGGERS
            + " WHERE "
            + COL_INSTANCE_NAME + " = ?" + COL_REQUESTS_RECOVERY + " = ?";

    String SELECT_FIRED_TRIGGER_INSTANCE_NAMES =
            "SELECT DISTINCT " + COL_INSTANCE_NAME + " FROM "
            + TABLE_PREFIX_SUBST
            + TABLE_FIRED_TRIGGERS;

    String INSERT_SCHEDULER_STATE = "INSERT INTO "
            + TABLE_PREFIX_SUBST + TABLE_SCHEDULER_STATE + " ("
            + COL_INSTANCE_NAME + ", " + COL_LAST_CHECKIN_TIME + ", "
            + COL_CHECKIN_INTERVAL + ") VALUES(?, ?, ?)";

    String SELECT_SCHEDULER_STATE = "SELECT * FROM "
            + TABLE_PREFIX_SUBST + TABLE_SCHEDULER_STATE + " WHERE "
            + COL_INSTANCE_NAME + " = ?";

    String SELECT_SCHEDULER_STATES = "SELECT * FROM "
            + TABLE_PREFIX_SUBST + TABLE_SCHEDULER_STATE;

    String DELETE_SCHEDULER_STATE = "DELETE FROM "
        + TABLE_PREFIX_SUBST + TABLE_SCHEDULER_STATE + " WHERE "
        + COL_INSTANCE_NAME + " = ?";

    String UPDATE_SCHEDULER_STATE = "UPDATE "
        + TABLE_PREFIX_SUBST + TABLE_SCHEDULER_STATE + " SET "
        + COL_LAST_CHECKIN_TIME + " = ? WHERE "
        + COL_INSTANCE_NAME + " = ?";

    String INSERT_PAUSED_TRIGGER_GROUP = "INSERT INTO "
            + TABLE_PREFIX_SUBST + TABLE_PAUSED_TRIGGERS + " ("
            + COL_TRIGGER_GROUP + ") VALUES(?)";

    String SELECT_PAUSED_TRIGGER_GROUP = "SELECT "
            + COL_TRIGGER_GROUP + " FROM " + TABLE_PREFIX_SUBST
            + TABLE_PAUSED_TRIGGERS + " WHERE " + COL_TRIGGER_GROUP + " = ?";

    String SELECT_PAUSED_TRIGGER_GROUPS = "SELECT "
        + COL_TRIGGER_GROUP + " FROM " + TABLE_PREFIX_SUBST
        + TABLE_PAUSED_TRIGGERS;

    String DELETE_PAUSED_TRIGGER_GROUP = "DELETE FROM "
            + TABLE_PREFIX_SUBST + TABLE_PAUSED_TRIGGERS + " WHERE "
            + COL_TRIGGER_GROUP + " = ?";

    String DELETE_PAUSED_TRIGGER_GROUPS = "DELETE FROM "
            + TABLE_PREFIX_SUBST + TABLE_PAUSED_TRIGGERS;

    //  CREATE TABLE qrtz_scheduler_state(INSTANCE_NAME VARCHAR2(80) NOT NULL,
    // LAST_CHECKIN_TIME NUMBER(13) NOT NULL, CHECKIN_INTERVAL NUMBER(13) NOT
    // NULL, PRIMARY KEY (INSTANCE_NAME));

}