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

/**
 * The default implementation of BatchJobConfigurationService delegates to {@link GeneralConfig}. That class
 * should be removed and responsiblity moved back to this class.
 */
package org.mifos.framework.components.batchjobs.configuration;

import org.mifos.config.GeneralConfig;

public class StandardBatchJobConfigurationService implements BatchJobConfigurationService {

    public int getBatchSizeForBatchJobs() {
        return GeneralConfig.getBatchSizeForBatchJobs();
    }

    public int getRecordCommittingSizeForBatchJobs() {
        return GeneralConfig.getRecordCommittingSizeForBatchJobs();
    }

    public int getOutputIntervalForBatchJobs() {
        return GeneralConfig.getOutputIntervalForBatchJobs();
    }

}
