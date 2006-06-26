package org.mifos.framework.components.cronjobs.helpers;

import org.mifos.framework.components.cronjobs.MifosTask;

public class SavingsIntPostingTask extends MifosTask{
	public void run() {
		helper=new SavingsIntPostingHelper();
		helper.executeTask(this);
		super.run();
	}
}
