/**
 * 
 */
package org.mifos.framework.components.cronjobs.helpers;

import org.mifos.framework.components.cronjobs.MifosTask;

public class ApplyCustomerFeeChangesTask extends MifosTask {

	@Override
	public void run() {
		helper = new ApplyCustomerFeeChangesHelper();
		helper.executeTask(this);
		super.run();
	}

}
