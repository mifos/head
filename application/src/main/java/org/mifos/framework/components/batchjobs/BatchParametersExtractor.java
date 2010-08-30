package org.mifos.framework.components.batchjobs;

import java.util.Map.Entry;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.step.job.JobParametersExtractor;

public class BatchParametersExtractor implements JobParametersExtractor {

    @Override
    public JobParameters getJobParameters(Job job, StepExecution stepExecution) {
        JobParametersBuilder builder = new JobParametersBuilder();
        for(Entry<String, JobParameter> jobParameter : stepExecution.getJobParameters().getParameters().entrySet()) {
            builder.addParameter(jobParameter.getKey(), jobParameter.getValue());
        }
        return builder.toJobParameters();
    }

}
