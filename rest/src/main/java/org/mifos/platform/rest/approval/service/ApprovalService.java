package org.mifos.platform.rest.approval.service;

import java.util.List;

import org.mifos.platform.rest.approval.domain.ApprovalMethod;
import org.mifos.platform.rest.approval.domain.RESTApprovalEntity;

public interface ApprovalService {

    void create(ApprovalMethod method) throws Exception;

    void approve(Long id) throws Exception;

    void reject(Long id);

    List<RESTApprovalEntity> getWaitingForApproval();

}
