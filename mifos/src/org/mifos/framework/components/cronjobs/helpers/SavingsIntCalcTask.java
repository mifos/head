package org.mifos.framework.components.cronjobs.helpers;

import org.mifos.framework.components.cronjobs.MifosTask;

public class SavingsIntCalcTask extends MifosTask{
	public void run() {
		helper=new SavingsIntCalcHelper();
		helper.executeTask(this);
		super.run();
	}
}
