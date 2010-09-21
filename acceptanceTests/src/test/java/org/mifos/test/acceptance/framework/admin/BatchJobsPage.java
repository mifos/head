package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.AbstractPage;

import com.thoughtworks.selenium.Selenium;

public class BatchJobsPage extends AbstractPage{

    public BatchJobsPage(Selenium selenium) {
        super(selenium);
    }

    public void selectBatchJob(String name){
     selenium.click("//input[@name='ONDEMAND' and @value='"+name+"']");
    }

    public void runSelectedBatchJobs(){
        selenium.click("RUN");
    }

}
