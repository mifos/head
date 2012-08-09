package org.mifos.framework.components.batchjobs.helpers;


import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;

public class ETLReportDWHelper extends TaskHelper {

    private final String FILENAME = "DataWarehouseInitialLoad.kjb";   
    
    @Override
    public void execute(final long timeInMillis) throws BatchJobException {
        try {
            String path = this.getClass().getResource("/MifosDataWarehouseETL/"+FILENAME).toString();
            KettleEnvironment.init(false);
            JobMeta jobMeta = new JobMeta(path, null);
            Job job = new Job(null, jobMeta);
            job.setName(Thread.currentThread().getName());
            job.setLogLevel(LogLevel.BASIC);
            job.run();
            job.waitUntilFinished();
            if (job.getResult() != null && job.getResult().getNrErrors() != 0) {
                System.out.print(job.getErrors());
            }
            job.setFinished(true);
            jobMeta.eraseParameters();
            job.eraseParameters();
        } catch (KettleException e) {
            e.printStackTrace();
        }
    }
}
