package org.mifos.rest.approval.service;

import java.util.List;

import org.mifos.rest.approval.domain.ApprovalMethod;
import org.mifos.rest.approval.domain.RESTApprovalEntity;

public interface ApprovalService {

    List<RESTApprovalEntity> getAllApprovals();

    List<RESTApprovalEntity> getWaitingForApproval();

    RESTApprovalEntity getDetails(Long id);

    void create(ApprovalMethod method) throws Exception;

    Object approve(Long id) throws Exception;

    void reject(Long id);

    List<RESTApprovalEntity> getApproved();

    List<RESTApprovalEntity> getRejected();

}
