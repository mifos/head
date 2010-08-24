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
 * This interface can be implemented by any <code>{@link
 * org.quartz.impl.jdbcjobstore.DriverDelegate}</code>
 * class that needs to use the constants contained herein.
 * </p>
 *
 * @author <a href="mailto:jeff@binaryfeed.org">Jeffrey Wescott</a>
 * @author James House
 */
public interface QuartzConstants {

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Constants.
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    // Table names
    String TABLE_JOB_DETAILS = "job_details";

    String TABLE_TRIGGERS = "triggers";

    String TABLE_SIMPLE_TRIGGERS = "simple_triggers";

    String TABLE_CRON_TRIGGERS = "cron_triggers";

    String TABLE_BLOB_TRIGGERS = "blob_triggers";

    String TABLE_FIRED_TRIGGERS = "fired_triggers";

    String TABLE_JOB_LISTENERS = "job_listeners";

    String TABLE_TRIGGER_LISTENERS = "trigger_listeners";

    String TABLE_CALENDARS = "calendars";

    String TABLE_PAUSED_TRIGGERS = "paused_trigger_grps";

    String TABLE_LOCKS = "locks";

    String TABLE_SCHEDULER_STATE = "scheduler_state";

    // TABLE_JOB_DETAILS columns names
    String COL_JOB_NAME = "JOB_NAME";

    String COL_JOB_GROUP = "JOB_GROUP";

    String COL_IS_DURABLE = "IS_DURABLE";

    String COL_IS_VOLATILE = "IS_VOLATILE";

    String COL_IS_STATEFUL = "IS_STATEFUL";

    String COL_REQUESTS_RECOVERY = "REQUESTS_RECOVERY";

    String COL_JOB_DATAMAP = "JOB_DATA";

    String COL_JOB_CLASS = "JOB_CLASS_NAME";

    String COL_DESCRIPTION = "DESCRIPTION";

    // TABLE_JOB_LISTENERS columns names
    String COL_JOB_LISTENER = "JOB_LISTENER";

    // TABLE_TRIGGERS columns names
    String COL_TRIGGER_NAME = "TRIGGER_NAME";

    String COL_TRIGGER_GROUP = "TRIGGER_GROUP";

    String COL_NEXT_FIRE_TIME = "NEXT_FIRE_TIME";

    String COL_PREV_FIRE_TIME = "PREV_FIRE_TIME";

    String COL_TRIGGER_STATE = "TRIGGER_STATE";

    String COL_TRIGGER_TYPE = "TRIGGER_TYPE";

    String COL_START_TIME = "START_TIME";

    String COL_END_TIME = "END_TIME";

    String COL_PRIORITY = "PRIORITY";

    String COL_MISFIRE_INSTRUCTION = "MISFIRE_INSTR";

    String ALIAS_COL_NEXT_FIRE_TIME = "ALIAS_NXT_FR_TM";

    // TABLE_SIMPLE_TRIGGERS columns names
    String COL_REPEAT_COUNT = "REPEAT_COUNT";

    String COL_REPEAT_INTERVAL = "REPEAT_INTERVAL";

    String COL_TIMES_TRIGGERED = "TIMES_TRIGGERED";

    // TABLE_CRON_TRIGGERS columns names
    String COL_CRON_EXPRESSION = "CRON_EXPRESSION";

    // TABLE_BLOB_TRIGGERS columns names
    String COL_BLOB = "BLOB_DATA";

    String COL_TIME_ZONE_ID = "TIME_ZONE_ID";

    // TABLE_TRIGGER_LISTENERS
    String COL_TRIGGER_LISTENER = "TRIGGER_LISTENER";

    // TABLE_FIRED_TRIGGERS columns names
    String COL_INSTANCE_NAME = "INSTANCE_NAME";

    String COL_FIRED_TIME = "FIRED_TIME";

    String COL_ENTRY_ID = "ENTRY_ID";

    String COL_ENTRY_STATE = "STATE";

    // TABLE_CALENDARS columns names
    String COL_CALENDAR_NAME = "CALENDAR_NAME";

    String COL_CALENDAR = "CALENDAR";

    // TABLE_LOCKS columns names
    String COL_LOCK_NAME = "LOCK_NAME";

    // TABLE_LOCKS columns names
    String COL_LAST_CHECKIN_TIME = "LAST_CHECKIN_TIME";

    String COL_CHECKIN_INTERVAL = "CHECKIN_INTERVAL";

    // MISC CONSTANTS
    String DEFAULT_TABLE_PREFIX = "QRTZ_";

    // STATES
    String STATE_WAITING = "WAITING";

    String STATE_ACQUIRED = "ACQUIRED";

    String STATE_EXECUTING = "EXECUTING";

    String STATE_COMPLETE = "COMPLETE";

    String STATE_BLOCKED = "BLOCKED";

    String STATE_ERROR = "ERROR";

    String STATE_PAUSED = "PAUSED";

    String STATE_PAUSED_BLOCKED = "PAUSED_BLOCKED";

    String STATE_DELETED = "DELETED";

    /**
     * @deprecated Whether a trigger has misfired is no longer a state, but
     * rather now identified dynamically by whether the trigger's next fire
     * time is more than the misfire threshold time in the past.
     */
    String STATE_MISFIRED = "MISFIRED";

    String ALL_GROUPS_PAUSED = "_$_ALL_GROUPS_PAUSED_$_";

    // TRIGGER TYPES
    String TTYPE_SIMPLE = "SIMPLE";

    String TTYPE_CRON = "CRON";

    String TTYPE_BLOB = "BLOB";
}

// EOF
