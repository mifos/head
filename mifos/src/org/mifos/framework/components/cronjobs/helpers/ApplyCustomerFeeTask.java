package org.mifos.framework.components.cronjobs.helpers;

import org.mifos.framework.components.cronjobs.MifosTask;

public class ApplyCustomerFeeTask extends MifosTask {
	
	public void run() {
		helper = new ApplyCustomerFeeHelper();
		helper.executeTask(this);
		super.run();		
	}
	
}
