package org.mifos.platform.rest.approval.service;

import java.lang.reflect.Method;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.platform.rest.approval.dao.ApprovalDao;
import org.mifos.platform.rest.approval.domain.ApprovalMethod;
import org.mifos.platform.rest.approval.domain.ApprovalState;
import org.mifos.platform.rest.approval.domain.RESTApprovalEntity;
import org.mifos.security.MifosUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class StandardApprovalService implements ApprovalService {

    @Autowired
    ApprovalDao approvalDao;

    private boolean skipCreate;

    @Override
    public void create(ApprovalMethod method) throws Exception {
        if(!skipCreate) {
            RESTApprovalEntity entity = new RESTApprovalEntity();
            entity.setApprovalMethod(method);
            entity.setState(ApprovalState.WAITING);
            MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            entity.setCreatedBy((short) user.getUserId());
            entity.setCreatedOn(new DateTime());
            approvalDao.create(entity);
            // Throw exception here
        }
    }

    @Override
    public void approve(Long id) throws Exception {
        RESTApprovalEntity entity = approvalDao.getDetails(id);
        ApprovalMethod am = entity.getApprovalMethod();
        entity.setState(ApprovalState.APPROVED);
        Method m = am.getType().getMethod(am.getName(), am.getArgsHolder().getTypes());
        skipCreate = true;
        // FIXME: What should be done with the returned object ?
        Object object = m.invoke(ApplicationContextProvider.getBean(am.getType()), am.getArgsHolder().getValues());
        skipCreate = false;
        approvalDao.update(entity);
    }

    @Override
    public void reject(Long id) {
        RESTApprovalEntity entity = approvalDao.getDetails(id);
        entity.setState(ApprovalState.REJECTED);
        approvalDao.update(entity);
    }

    @Override
    public List<RESTApprovalEntity> getWaitingForApproval() {
        return approvalDao.getDetailsAll();
    }

}
