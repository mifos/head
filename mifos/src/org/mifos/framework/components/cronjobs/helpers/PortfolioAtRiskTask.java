package org.mifos.framework.components.cronjobs.helpers;

import org.mifos.framework.components.cronjobs.MifosTask;

public class PortfolioAtRiskTask extends MifosTask {

	@Override
	public void run() {
		helper = new PortfolioAtRiskHelper();
		helper.executeTask(this);
		super.run();		
	}
}
