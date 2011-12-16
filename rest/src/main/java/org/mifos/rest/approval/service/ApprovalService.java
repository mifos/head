package org.mifos.rest.approval.service;

import java.util.List;

import org.mifos.rest.approval.domain.ApprovalMethod;
import org.mifos.rest.approval.domain.RESTApprovalEntity;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ApprovalService {

    @PreAuthorize("isFullyAuthenticated()")
    RESTApprovalEntity getDetails(Long id);

    @PreAuthorize("isFullyAuthenticated()")
    void create(ApprovalMethod method) throws Exception;

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('CAN_APPROVE_REST_API')")
    List<RESTApprovalEntity> getAllWaiting();

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('CAN_APPROVE_REST_API')")
    List<RESTApprovalEntity> getAllNotWaiting();

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('CAN_APPROVE_REST_API')")
    Object approve(Long id) throws Exception;

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('CAN_APPROVE_REST_API')")
    void reject(Long id);

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('CAN_APPROVE_REST_API')")
    void updateMethodContent(Long id, ApprovalMethod approvalMethod) throws Exception;

}
