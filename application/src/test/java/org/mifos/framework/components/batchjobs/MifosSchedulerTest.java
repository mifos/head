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

package org.mifos.framework.components.batchjobs;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.ConfigurationLocator;
import org.springframework.core.io.ClassPathResource;

public class MifosSchedulerTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MifosLogManager.configureLogging();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testRegisterTasks() throws Exception {
        MifosScheduler mifosScheduler = getMifosScheduler("org/mifos/config/resources/task.xml");
        mifosScheduler.registerTasks();
        List<String> taskNames = mifosScheduler.getTaskNames();
       Assert.assertEquals(13, taskNames.size());
       Assert.assertTrue(taskNames.contains("ProductStatus"));
        //Assert.assertTrue(taskNames.contains("CollectionSheetTask"));
       Assert.assertTrue(taskNames.contains("LoanArrearsTask"));
       Assert.assertTrue(taskNames.contains("SavingsIntCalcTask"));
       Assert.assertTrue(taskNames.contains("SavingsIntPostingTask"));
       Assert.assertTrue(taskNames.contains("ApplyCustomerFeeTask"));
       Assert.assertTrue(taskNames.contains("RegenerateScheduleTask"));
       Assert.assertTrue(taskNames.contains("PortfolioAtRiskTask"));
       Assert.assertTrue(taskNames.contains("ApplyCustomerFeeChangesTask"));
       Assert.assertTrue(taskNames.contains("GenerateMeetingsForCustomerAndSavingsTask"));
       Assert.assertTrue(taskNames.contains("LoanArrearsAgingTask"));
       Assert.assertTrue(taskNames.contains("ApplyHolidayChangesTask"));
       Assert.assertTrue(taskNames.contains("CollectionSheetReportParameterCachingTask"));
       Assert.assertTrue(taskNames.contains("BranchReportTask"));
        // Temporarily commented out as requested by issue 1881
        //Assert.assertTrue(taskNames.contains("BranchCashConfirmationTask"));
    }

    public void testCallsConfigurationLocator() throws Exception {
        MifosScheduler mifosScheduler = getMifosScheduler("org/mifos/framework/components/batchjobs/mockTask.xml");
        mifosScheduler.registerTasks();
        List<String> taskNames = mifosScheduler.getTaskNames();
       Assert.assertEquals(1, taskNames.size());
       Assert.assertTrue(taskNames.contains("MockTask"));
    }

    private MifosScheduler getMifosScheduler(String taskConfigurationPath) throws IOException {
        ConfigurationLocator mockConfigurationLocator = createMock(ConfigurationLocator.class);
        expect(mockConfigurationLocator.getFile(SchedulerConstants.CONFIGURATION_FILE_NAME)).andReturn(
                new ClassPathResource(taskConfigurationPath).getFile());
        replay(mockConfigurationLocator);
        MifosScheduler mifosScheduler = new MifosScheduler();
        mifosScheduler.setConfigurationLocator(mockConfigurationLocator);
        return mifosScheduler;
    }

}
