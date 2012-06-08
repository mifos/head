/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.security.rolesandpermission.business;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.security.util.UserContext;

/**
 * This class holds information about activities restriction. For example, activity Permissions-LoanProcessing-CanChangeStateToApproved'
 * for given Role could have a restriction for max amount of loan to approve. 
 */
@NamedQueries({
        @NamedQuery(name = "getActivityRestrictionById",
                    query = "FROM RoleActivityRestrictionBO roleActivityRestriction WHERE roleActivityRestriction.id=:id"),
        @NamedQuery(name = "getRoleActivityRestrictionForGivenTypeId",
                    query = "FROM RoleActivityRestrictionBO roleActivityRestriction WHERE roleActivityRestriction.roleId=:roleId"
                            + " AND roleActivityRestriction.activityRestrictionType.id=:activityRestrictionTypeId"),
        @NamedQuery(name = "getRoleActivitiesRestrictions",
                    query = "FROM RoleActivityRestrictionBO roleActivityRestriction WHERE roleActivityRestriction.roleId=:roleId") 
})
@Entity
@Table(name = "role_activity_restriction")
public class RoleActivityRestrictionBO extends AbstractBusinessObject {

    @Id
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "increment")
    @Column(name = "activity_restriction_id", nullable = false)
    private Integer id;

    @Column(name = "role_id", insertable = false, updatable = false)
    private Short roleId;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="role_id")
    private RoleBO role;
    
    @ManyToOne
    @JoinColumn(name = "activity_restriction_type_id", referencedColumnName = "activity_restriction_type_id")
    private ActivityRestrictionTypeEntity activityRestrictionType;

    @Column(name = "activity_restriction_amount_value")
    private BigDecimal restrictionAmountValue;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "created_by")
    private Short createdBy;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "updated_by")
    private Short updatedBy;

    @Version
    @Column(name = "version_no", nullable = false)
    private Integer versionNo;

    public RoleActivityRestrictionBO() {
    }

    public RoleActivityRestrictionBO(UserContext userContext) {
        super(userContext);
        setCreateDetails();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Short getRoleId() {
        return roleId;
    }

    public void setRoleId(Short roleId) {
        this.roleId = roleId;
    }

    public RoleBO getRole() {
        return role;
    }

    public void setRole(RoleBO role) {
        this.role = role;
    }

    public ActivityRestrictionTypeEntity getActivityRestrictionType() {
        return activityRestrictionType;
    }

    public void setActivityRestrictionType(ActivityRestrictionTypeEntity activityRestrictionType) {
        this.activityRestrictionType = activityRestrictionType;
    }

    public BigDecimal getRestrictionAmountValue() {
        return restrictionAmountValue;
    }

    public void setRestrictionAmountValue(BigDecimal restrictionAmountValue) {
        this.restrictionAmountValue = restrictionAmountValue;
    }

    public void update(Short personnelId, BigDecimal restrictionAmountValue){
        this.restrictionAmountValue = restrictionAmountValue;
        setUpdateDetails(personnelId);
    }
    
    @Override
    public Date getCreatedDate() {
        return createdDate;
    }

    @Override
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public Short getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(Short createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Date getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public Short getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(Short updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public Integer getVersionNo() {
        return versionNo;
    }

    @Override
    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }
    

}
