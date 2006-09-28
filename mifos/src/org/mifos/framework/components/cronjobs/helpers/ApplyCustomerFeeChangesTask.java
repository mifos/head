package org.mifos.framework.components.cronjobs.helpers;

import org.mifos.framework.components.cronjobs.MifosTask;
import org.mifos.framework.components.cronjobs.TaskHelper;

public class ApplyCustomerFeeChangesTask extends MifosTask {

	@Override
	public TaskHelper getTaskHelper() {
		return new ApplyCustomerFeeChangesHelper(this);
	}

}
