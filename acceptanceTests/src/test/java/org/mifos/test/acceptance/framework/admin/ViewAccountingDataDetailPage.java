package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class ViewAccountingDataDetailPage extends MifosPage {
    
    public ViewAccountingDataDetailPage(Selenium selenium) {
        super(selenium);
    }
    
    public ViewAccountingDataDetailPage verifyPage() {
        verifyPage("view_accounting_data_detail");
        return this;
    }
    
    public boolean verifyIfListContains(String val){
        boolean contains = false;
        int noRow = selenium.getXpathCount("//table[@id='table.details']//tr").intValue();
        String tableElString;
        for (int i=2;i<noRow;i++){
             tableElString = selenium.getTable("table.details."+i+".4");
             if (tableElString.equals(val)){
                 contains = true;
                 break;
             }
        }
        return contains;
    }
}
