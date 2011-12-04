package org.mifos.platform.rest.approval.dao;

import java.util.List;

import org.mifos.platform.rest.approval.domain.RESTApprovalEntity;

public interface ApprovalDao {

    void create(RESTApprovalEntity entity);

    RESTApprovalEntity getDetails(Long id);

    void update(RESTApprovalEntity entity);

    List<RESTApprovalEntity> getDetailsAll();
}
