package org.mifos.framework.components.cronjobs.helpers;

import org.mifos.framework.components.cronjobs.MifosTask;

public class GenMeetingsForCustomerNSavingsTask extends MifosTask {

	@Override
	public void run() {
		helper = new GenerateMeetingsForCustomerAndSavingsHelper();
		helper.executeTask(this);
		super.run();
	}

}
