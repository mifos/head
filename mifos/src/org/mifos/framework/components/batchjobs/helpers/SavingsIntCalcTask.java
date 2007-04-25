package org.mifos.framework.components.batchjobs.helpers;

import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.TaskHelper;

public class SavingsIntCalcTask extends MifosTask {

	@Override
	public TaskHelper getTaskHelper() {
		return new SavingsIntCalcHelper(this);
	}
}
