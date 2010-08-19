package org.mifos.application.admin.business.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.mifos.application.admin.servicefacade.BatchjobsDto;
import org.mifos.application.admin.servicefacade.BatchjobsServiceFacade;
import org.mifos.framework.components.batchjobs.MifosScheduler;
import org.mifos.framework.components.batchjobs.MifosTask;

public class BatchjobsServiceFacadeWebTier implements BatchjobsServiceFacade{

    @Override
    public List<BatchjobsDto> getBatchjobs(ServletContext context) {
        List<BatchjobsDto> batchjobs = new ArrayList<BatchjobsDto>();
        MifosScheduler mifosScheduler = (MifosScheduler) context.getAttribute(MifosScheduler.class.getName());
        for (MifosTask mifosTask : mifosScheduler.getTasks()) {
            batchjobs.add(new BatchjobsDto(mifosTask.name));
        }
        return batchjobs;
    }

}
