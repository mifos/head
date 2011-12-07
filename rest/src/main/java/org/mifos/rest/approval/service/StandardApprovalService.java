package org.mifos.rest.approval.service;

import java.lang.reflect.Method;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.rest.approval.dao.ApprovalDao;
import org.mifos.rest.approval.domain.ApprovalMethod;
import org.mifos.rest.approval.domain.ApprovalState;
import org.mifos.rest.approval.domain.RESTApprovalEntity;
import org.mifos.security.MifosUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StandardApprovalService implements ApprovalService {

    @Autowired
    ApprovalDao approvalDao;

    private boolean skipCreate;

    @Transactional(readOnly=true)
    @Override
    public RESTApprovalEntity getDetails(Long id) {
        return approvalDao.getDetails(id);
    }

    @Transactional(readOnly=true)
    @Override
    public List<RESTApprovalEntity> getWaitingForApproval() {
        return approvalDao.getDetailsAll();
    }

    @Transactional
    @Override
    public void create(ApprovalMethod method) throws Exception {
        if(!skipCreate) {
            RESTApprovalEntity entity = new RESTApprovalEntity();
            entity.setApprovalMethod(method);
            entity.setState(ApprovalState.WAITING);
            entity.setCreatedBy(getCurrentUserId());
            entity.setCreatedOn(new DateTime());
            approvalDao.create(entity);
            throw new RESTCallInterruptException();
        }
    }

    @Transactional
    @Override
    public void approve(Long id) throws Exception {
        RESTApprovalEntity entity = approvalDao.getDetails(id);
        ApprovalMethod am = entity.getApprovalMethod();
        entity.setState(ApprovalState.APPROVED);

        Method m = am.getType().getMethod(am.getName(), am.getArgsHolder().getTypes());
        skipCreate = true;
        // FIXME: What should be done with the returned object ?
        Object object = m.invoke(ApplicationContextProvider.getBean(am.getType()), am.getArgsHolder().getValues());

        entity.setApprovedBy(getCurrentUserId());
        entity.setApprovedOn(new DateTime());
        skipCreate = false;
        approvalDao.update(entity);
    }

    @Transactional
    @Override
    public void reject(Long id) {
        RESTApprovalEntity entity = approvalDao.getDetails(id);
        entity.setApprovedBy(getCurrentUserId());
        entity.setApprovedOn(new DateTime());
        entity.setState(ApprovalState.REJECTED);
        approvalDao.update(entity);
    }

    private Short getCurrentUserId() {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (short) user.getUserId();
    }

}
