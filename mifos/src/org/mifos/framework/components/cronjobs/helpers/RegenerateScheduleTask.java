package org.mifos.framework.components.cronjobs.helpers;

import org.mifos.framework.components.cronjobs.MifosTask;

public class RegenerateScheduleTask extends MifosTask {

	
	public void run() {
		helper = new RegenerateScheduleHelper();
		helper.executeTask(this);
		super.run();		
	}
}
