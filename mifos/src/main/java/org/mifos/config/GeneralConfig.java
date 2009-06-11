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

package org.mifos.config;

public class GeneralConfig {

    public static final String MaxPointsPerPPISurvey = "GeneralConfig.MaxPointsPerPPISurvey";
    public static final String BatchSizeForBatchJobs = "GeneralConfig.BatchSizeForBatchJobs";
    public static final String RecordCommittingSizeForBatchJobs = "GeneralConfig.RecordCommittingSizeForBatchJobs";
    public static final String OutputIntervalForBatchJobs = "GeneralConfig.OutputIntervalForBatchJobs";

    public static int getMaxPointsPerPPISurvey() {
        int maxPointsPerPPISurvey = 101; // default value is 101
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        if (configMgr.containsKey(MaxPointsPerPPISurvey))
            maxPointsPerPPISurvey = configMgr.getInt(MaxPointsPerPPISurvey);
        return maxPointsPerPPISurvey;
    }

    public static int getBatchSizeForBatchJobs() {
        int batchSizeForBatchJobs = 40; // default value is 40
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        if (configMgr.containsKey(BatchSizeForBatchJobs))
            batchSizeForBatchJobs = configMgr.getInt(BatchSizeForBatchJobs);
        return batchSizeForBatchJobs;
    }

    public static int getRecordCommittingSizeForBatchJobs() {
        int committingRecordSizeForBatchJobs = 1000; // default value is 1000
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        if (configMgr.containsKey(RecordCommittingSizeForBatchJobs))
            committingRecordSizeForBatchJobs = configMgr.getInt(RecordCommittingSizeForBatchJobs);
        return committingRecordSizeForBatchJobs;
    }

    public static int getOutputIntervalForBatchJobs() {
        int outputRecordIntervalForBatchJobs = 1000; // default value is 1000
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        if (configMgr.containsKey(OutputIntervalForBatchJobs))
            outputRecordIntervalForBatchJobs = configMgr.getInt(OutputIntervalForBatchJobs);
        return outputRecordIntervalForBatchJobs;
    }

}
