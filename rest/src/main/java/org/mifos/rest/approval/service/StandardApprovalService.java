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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=Exception.class)
public class StandardApprovalService implements ApprovalService {

    public static final Logger LOG = LoggerFactory.getLogger(StandardApprovalService.class);

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
    public List<RESTApprovalEntity> getAllWaiting() {
        return approvalDao.findByState(ApprovalState.WAITING);
    }

    @Transactional(readOnly=true)
    @Override
    public List<RESTApprovalEntity> getAllApproved() {
        return approvalDao.findByState(ApprovalState.APPROVED);
    }

    @Transactional(readOnly=true)
    @Override
    public List<RESTApprovalEntity> getAllRejected() {
        return approvalDao.findByState(ApprovalState.REJECTED);
    }

    @Transactional(noRollbackFor=RESTCallInterruptException.class)
    @Override
    public void create(ApprovalMethod method) throws Exception {
        if(!skipCreate) {
            RESTApprovalEntity entity = new RESTApprovalEntity();
            entity.setApprovalMethod(method);
            entity.setState(ApprovalState.WAITING);
            entity.setCreatedBy(getCurrentUserId());
            entity.setCreatedOn(new DateTime());
            approvalDao.create(entity);
            throw new RESTCallInterruptException(entity.getId());
        }
    }

    @Transactional
    @Override
    public Object approve(Long id) throws Exception {
        RESTApprovalEntity entity = approvalDao.getDetails(id);
        ApprovalMethod am = entity.getApprovalMethod();

        Object result = null;
        try {
            result = excuteMethod(am);
            entity.setState(ApprovalState.APPROVED);
        } catch (Exception e) {
            skipCreate = false;
            result =  "Error : check parameters"+ ((e.getMessage() != null) ? " : " +e.getMessage() : "");
            LOG.warn("Invalid call", e);
        }

        entity.setApprovedBy(getCurrentUserId());
        entity.setApprovedOn(new DateTime());
        approvalDao.update(entity);

        return result;
    }

    @Transactional
    @Override
    public void updateMethodContent(Long id, ApprovalMethod approvalMethod) throws Exception {
        RESTApprovalEntity entity = approvalDao.getDetails(id);
        entity.setApprovalMethod(approvalMethod);
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

    private Object excuteMethod(ApprovalMethod am) throws Exception {
        Method m = am.getType().getMethod(am.getName(), am.getArgsHolder().getTypes());
        skipCreate = true;
        Object result = m.invoke(ApplicationContextProvider.getBean(am.getType()), am.getArgsHolder().getValues());
        skipCreate = false;
        return result;
    }

    private Short getCurrentUserId() {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (short) user.getUserId();
    }

}
