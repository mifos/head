package org.mifos.framework.components.batchjobs;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.mifos.core.MifosResourceUtil;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.components.batchjobs.exceptions.TaskSystemException;
import org.mifos.framework.util.ConfigurationLocator;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = { "/integration-test-context.xml",
                                    "/org/mifos/config/resources/task.xml",
                                    "/org/mifos/config/resources/applicationContext.xml"})
public class TaskRunningIntegrationTest extends MifosIntegrationTestCase {

    MifosScheduler mifosScheduler;

    @Test
    public void testRunningSpecificTask() throws Exception {
        cleanuUpBatchTables();
        mifosScheduler = getMifosScheduler("org/mifos/framework/components/batchjobs/taskRunningTestTask.xml");
        String jobName = "ApplyHolidayChangesTaskJob";
        mifosScheduler.runIndividualTask(jobName);
        Thread.sleep(3000);
        JobExplorer explorer = mifosScheduler.getBatchJobExplorer();
        List<String> executedJobs = explorer.getJobNames();
        Assert.assertEquals(1, executedJobs.size());
        Assert.assertTrue(jobName.equals(executedJobs.get(0)));
    }

    @Test
    @Ignore
    public void testRunningAllTasks() throws Exception {
        mifosScheduler = getMifosScheduler("org/mifos/framework/components/batchjobs/taskRunningTestTask.xml");
        mifosScheduler.runAllTasks();
        Thread.sleep(5000);
        JobExplorer explorer = mifosScheduler.getBatchJobExplorer();
        List<String> executedJobs = explorer.getJobNames();
        Assert.assertEquals(8, executedJobs.size());
        List<String> jobNames = mifosScheduler.getTaskNames();
        for(String jobName : jobNames) {
            Assert.assertTrue(executedJobs.contains(jobName));
        }
    }

    @Test(expected=TaskSystemException.class)
    public void testRunningSpecificTaskOnOldConfigurationFile() throws Exception {
        mifosScheduler = getMifosSchedulerOldConfigurationFile("org/mifos/framework/components/batchjobs/old-task.xml",
                                                               "org/mifos/framework/components/batchjobs/quartz.properties");
    }

    private MifosScheduler getMifosScheduler(String taskConfiguration) throws TaskSystemException, IOException, FileNotFoundException {
        ConfigurationLocator mockConfigurationLocator = createMock(ConfigurationLocator.class);
        expect(mockConfigurationLocator.getResource(SchedulerConstants.CONFIGURATION_FILE_NAME)).andReturn(
                MifosResourceUtil.getClassPathResourceAsResource(taskConfiguration));
        expectLastCall().times(2);
        replay(mockConfigurationLocator);
        MifosScheduler mifosScheduler = new MifosScheduler();
        mifosScheduler.setConfigurationLocator(mockConfigurationLocator);
        mifosScheduler.initialize();
        return mifosScheduler;
    }

    private MifosScheduler getMifosSchedulerOldConfigurationFile(String taskResource, String quartzResource) throws IOException, TaskSystemException {
        ConfigurationLocator mockConfigurationLocator = createMock(ConfigurationLocator.class);
        expect(mockConfigurationLocator.getResource(SchedulerConstants.CONFIGURATION_FILE_NAME)).andReturn(
                MifosResourceUtil.getClassPathResourceAsResource(taskResource));
        expect(mockConfigurationLocator.getResource(SchedulerConstants.SCHEDULER_CONFIGURATION_FILE_NAME)).andReturn(
                MifosResourceUtil.getClassPathResourceAsResource(quartzResource));
        replay(mockConfigurationLocator);
        MifosScheduler mifosScheduler = new MifosScheduler();
        mifosScheduler.setConfigurationLocator(mockConfigurationLocator);
        mifosScheduler.initialize();
        return mifosScheduler;
    }

    private void cleanuUpBatchTables() throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement st = connection.createStatement();
        st.executeUpdate("truncate table BATCH_STEP_EXECUTION_CONTEXT");
        st.executeUpdate("truncate table BATCH_STEP_EXECUTION");
        st.executeUpdate("truncate table BATCH_STEP_EXECUTION_SEQ");
        st.executeUpdate("truncate table BATCH_JOB_EXECUTION_CONTEXT");
        st.executeUpdate("truncate table BATCH_JOB_EXECUTION");
        st.executeUpdate("truncate table BATCH_JOB_PARAMS");
        st.executeUpdate("truncate table BATCH_JOB_INSTANCE");
        st.executeUpdate("truncate table BATCH_JOB_SEQ");
        connection.close();
    }

}
