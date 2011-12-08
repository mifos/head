package org.mifos.rest.approval.dao;

import java.util.List;

import org.mifos.rest.approval.domain.ApprovalState;
import org.mifos.rest.approval.domain.RESTApprovalEntity;

public interface ApprovalDao {

    void create(RESTApprovalEntity entity);

    RESTApprovalEntity getDetails(Long id);

    void update(RESTApprovalEntity entity);

    List<RESTApprovalEntity> getDetailsAll();

    List<RESTApprovalEntity> findByState(ApprovalState state);
}
