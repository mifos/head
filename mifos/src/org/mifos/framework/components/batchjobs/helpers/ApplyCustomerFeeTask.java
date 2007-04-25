package org.mifos.framework.components.batchjobs.helpers;

import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.TaskHelper;

public class ApplyCustomerFeeTask extends MifosTask {
	
	@Override
	public TaskHelper getTaskHelper() {
		return new ApplyCustomerFeeHelper(this);
	}
	
}
