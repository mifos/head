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

    public String getPreviousRunStart(String name) {
        return selenium.getText( "//form/div/div/div/div/span[2]/strong[text()='" + name + "']/" +
                "parent::span/parent::div/following-sibling::div/following-sibling::div/span[2]");
    }

    public String getPreviousRunStatus(String name) {
        return selenium.getText( "//form/div/div/div/div/span[2]/strong[text()='" + name + "']/" +
                "parent::span/parent::div/following-sibling::div/following-sibling::div/span[4]");
    }
}
