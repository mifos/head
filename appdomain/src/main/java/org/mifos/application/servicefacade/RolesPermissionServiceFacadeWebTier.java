package org.mifos.application.servicefacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.application.admin.servicefacade.RolesPermissionServiceFacade;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.config.Localization;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.personnel.persistence.LegacyPersonnelDao;
import org.mifos.dto.domain.ActivityRestrictionDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.MifosUser;
import org.mifos.security.activity.ActivityGeneratorException;
import org.mifos.security.rolesandpermission.business.ActivityEntity;
import org.mifos.security.rolesandpermission.business.ActivityRestrictionTypeEntity;
import org.mifos.security.rolesandpermission.business.RoleActivityRestrictionBO;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.security.rolesandpermission.exceptions.RolesPermissionException;
import org.mifos.security.rolesandpermission.persistence.LegacyRolesPermissionsDao;
import org.mifos.security.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class RolesPermissionServiceFacadeWebTier implements RolesPermissionServiceFacade {

    private static final Logger logger = LoggerFactory.getLogger(RoleBO.class);

    @Autowired
    LegacyRolesPermissionsDao legacyRolesPermissionsDao;

    @Autowired
    private LegacyPersonnelDao legacyPersonnelDao;

    @Override
    public List<ListElement> retrieveAllRoles() {
        try {
            List<RoleBO> roles = new RolesPermissionsBusinessService().getRoles();
            if(!roles.isEmpty()) {
                List<ListElement> roleList = new ArrayList<ListElement>();

                for (RoleBO role: roles) {
                    ListElement element = new ListElement(new Integer(role.getId()), role.getName());
                    roleList.add(element);
                }
                return roleList;
            }
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
        return null;
    }

    @Override
    public void createRole(Short userId, String name, List<Short> ActivityIds) throws RolesPermissionException {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);
        List<ActivityEntity> activityEntities = getActivityEntities(ActivityIds);

        RoleBO roleBO = new RoleBO(userContext, name, activityEntities);
        try {

            validateRole(name, activityEntities, roleBO);

            StaticHibernateUtil.startTransaction();
            legacyRolesPermissionsDao.save(roleBO);

            StaticHibernateUtil.flushSession();
            for(ActivityEntity ae : activityEntities) {
                StaticHibernateUtil.getSessionTL().refresh(ae);
            }

            StaticHibernateUtil.commitTransaction();
        } catch (PersistenceException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    @Override
    public void createRole(Short userId, String name, List<Short> ActivityIds,
            List<ActivityRestrictionDto> activityRestrictionDtoList) throws Exception {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);
        List<ActivityEntity> activityEntities = getActivityEntities(ActivityIds);
        List<RoleActivityRestrictionBO> activitiesRestrictions = getActivitiesRestrictionsForCreate(activityRestrictionDtoList, userContext);
        
        RoleBO roleBO = new RoleBO(userContext, name, activityEntities, activitiesRestrictions);
        try {

            validateRole(name, activityEntities, roleBO);

            StaticHibernateUtil.startTransaction();
            legacyRolesPermissionsDao.save(roleBO);

            StaticHibernateUtil.flushSession();
            for(ActivityEntity ae : activityEntities) {
                StaticHibernateUtil.getSessionTL().refresh(ae);
            }

            StaticHibernateUtil.commitTransaction();
        } catch (PersistenceException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    private void validateRole(String roleName, List<ActivityEntity> activityEntities, RoleBO roleBO) throws RolesPermissionException, PersistenceException {
        if (StringUtils.isBlank(roleName)) {
            throw new RolesPermissionException(RolesAndPermissionConstants.KEYROLENAMENOTSPECIFIED);
        }
        logger.info("Validating activities");
        if (null == activityEntities || activityEntities.size() == 0) {
            throw new RolesPermissionException(RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES);
        }
        logger.info("Activities validated");
        if (roleBO.getName() == null || !roleBO.getName().trim().equalsIgnoreCase(roleName.trim())) {
            if (legacyRolesPermissionsDao.getRole(roleName.trim()) != null) {
                throw new RolesPermissionException(RolesAndPermissionConstants.KEYROLEALREADYEXIST);
            }
        }
    }

    @Override
    public void updateRole(Short roleId, Short userId, String name, List<Short> ActivityIds) throws Exception {
        RolesPermissionsBusinessService rolesPermissionsBusinessService = new RolesPermissionsBusinessService();
        RoleBO role = rolesPermissionsBusinessService.getRole(roleId);
        List<ActivityEntity> activityList = getActivityEntities(ActivityIds);
        validateRole(name, activityList, role);

        try {
            StaticHibernateUtil.startTransaction();

            role.update(userId, name, activityList);
            legacyRolesPermissionsDao.save(role);

            StaticHibernateUtil.flushSession();
            for(ActivityEntity ae : legacyRolesPermissionsDao.getActivities()) {
                StaticHibernateUtil.getSessionTL().refresh(ae);
            }

            StaticHibernateUtil.commitTransaction();
        } catch (RolesPermissionException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    @Override
    public void updateRole(Short roleId, Short userId, String name, List<Short> ActivityIds,
            List<ActivityRestrictionDto> activityRestrictionDtoList) throws Exception {
        RolesPermissionsBusinessService rolesPermissionsBusinessService = new RolesPermissionsBusinessService();
        RoleBO role = rolesPermissionsBusinessService.getRole(roleId);
        List<ActivityEntity> activityList = getActivityEntities(ActivityIds);
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);
        List<RoleActivityRestrictionBO> activitiesRestrictions = getActivitiesRestrictionsForUpdate(userContext, activityRestrictionDtoList);
        validateRole(name, activityList, role);

        try {
            StaticHibernateUtil.startTransaction();
            
            role.updateWithActivitiesRestrictions(userId, name, activityList, activitiesRestrictions);
            legacyRolesPermissionsDao.save(role);

            StaticHibernateUtil.flushSession();
            for(ActivityEntity ae : legacyRolesPermissionsDao.getActivities()) {
                StaticHibernateUtil.getSessionTL().refresh(ae);
            }

            StaticHibernateUtil.commitTransaction();
        } catch (RolesPermissionException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        } 
        
    }
    
    @Override
    public List<ActivityRestrictionDto> getRoleActivitiesRestrictions(Short roleId) {
        try {
            List<ActivityRestrictionDto> activityRestrictionDtoList = new ArrayList<ActivityRestrictionDto>();
            List<RoleActivityRestrictionBO> activityRestrictionBOList = legacyRolesPermissionsDao
                    .getRoleActivitiesRestrictions(roleId);
            
            for (RoleActivityRestrictionBO activityRestrictionBO : activityRestrictionBOList) {
                
                Integer activityRestrictionId = activityRestrictionBO.getId();
                Short activityRestrictionTypeId = activityRestrictionBO.getActivityRestrictionType().getId();
                BigDecimal restrictionAmountValue = activityRestrictionBO.getRestrictionAmountValue();
                
                ActivityRestrictionDto activityRestrictionDto = new ActivityRestrictionDto(roleId,
                        activityRestrictionId, activityRestrictionTypeId, restrictionAmountValue);
                
                activityRestrictionDtoList.add(activityRestrictionDto);
            }
            
            return activityRestrictionDtoList;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private List<ActivityEntity> getActivityEntities(List<Short> ActivityIds) {
        List<ActivityEntity> activityEntities = new ArrayList<ActivityEntity>();
        for (Short id: ActivityIds) {
            try {
                ActivityEntity activityEntity = legacyRolesPermissionsDao.getActivityById(id);
                activityEntities.add(activityEntity);
            } catch (PersistenceException e) {
                throw new MifosRuntimeException(e);
            }
        }
        return activityEntities;
    }
    
    private List<RoleActivityRestrictionBO> getActivitiesRestrictionsForCreate(List<ActivityRestrictionDto> activityRestrictionDtoList, UserContext userContext) throws PersistenceException{
        List<RoleActivityRestrictionBO> activitiesRestrictions = new ArrayList<RoleActivityRestrictionBO>();
        for (ActivityRestrictionDto activityRestrictionDto : activityRestrictionDtoList){
            RoleActivityRestrictionBO roleActivityRestrictionBO = new RoleActivityRestrictionBO(userContext);
            
            ActivityRestrictionTypeEntity activityRestrictionTypeEntity = legacyRolesPermissionsDao.getActivityRestrictionTypeEntity((short)activityRestrictionDto.getActivityRestrictionTypeId());
            
            roleActivityRestrictionBO.setActivityRestrictionType(activityRestrictionTypeEntity);
            roleActivityRestrictionBO.setRestrictionAmountValue(activityRestrictionDto.getAmountValue());
            
            activitiesRestrictions.add(roleActivityRestrictionBO);
        }
        return activitiesRestrictions;
    }
    
    private List<RoleActivityRestrictionBO> getActivitiesRestrictionsForUpdate(UserContext userContext, List<ActivityRestrictionDto> activityRestrictionDtoList){
        try {
            List<RoleActivityRestrictionBO> activitiesRestrictions = new ArrayList<RoleActivityRestrictionBO>();
            
            for (ActivityRestrictionDto activityRestrictionDto : activityRestrictionDtoList){
                RoleActivityRestrictionBO roleActivityRestrictionBO;
                if ( activityRestrictionDto.getActivityRestrictionId() != null){
                    roleActivityRestrictionBO = legacyRolesPermissionsDao.getActivityRestrictionById(activityRestrictionDto.getActivityRestrictionId());
                    roleActivityRestrictionBO.update(userContext.getId(), activityRestrictionDto.getAmountValue());
                } else {
                    roleActivityRestrictionBO = new RoleActivityRestrictionBO(userContext);
                    
                    ActivityRestrictionTypeEntity activityRestrictionTypeEntity = legacyRolesPermissionsDao.getActivityRestrictionTypeEntity((short)activityRestrictionDto.getActivityRestrictionTypeId());
                    
                    roleActivityRestrictionBO.setActivityRestrictionType(activityRestrictionTypeEntity);
                    roleActivityRestrictionBO.setRestrictionAmountValue(activityRestrictionDto.getAmountValue());
                }
                activitiesRestrictions.add(roleActivityRestrictionBO);
            }
            
            return activitiesRestrictions;
        } catch (PersistenceException e){
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void deleteRole(Integer versionNo, Short roleId) throws Exception {
        RolesPermissionsBusinessService rolesPermissionsBusinessService = new RolesPermissionsBusinessService();

        RoleBO role = rolesPermissionsBusinessService.getRole(roleId);
        role.setVersionNo(versionNo);

        validateIfRoleAssignedToPersonnel(role);

        try {
            StaticHibernateUtil.startTransaction();
            legacyRolesPermissionsDao.delete(role);

            StaticHibernateUtil.flushSession();
            for(ActivityEntity ae : legacyRolesPermissionsDao.getActivities()) {
                StaticHibernateUtil.getSessionTL().refresh(ae);
            }

            StaticHibernateUtil.commitTransaction();
        } catch (PersistenceException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    private void validateIfRoleAssignedToPersonnel(RoleBO role) throws RolesPermissionException {
        logger.info("Validating if role is assigned to personnel");
        if (isRoleAssignedToPersonnel(role)) {
            throw new RolesPermissionException(RolesAndPermissionConstants.KEYROLEASSIGNEDTOPERSONNEL);
        }
        logger.info("Validation done for role assigned to personnel");
    }

    private boolean isRoleAssignedToPersonnel(RoleBO role) throws RolesPermissionException {
        Integer count;
        try {
            count = legacyPersonnelDao.getPersonnelRoleCount(role.getId());
        } catch (PersistenceException e) {
            throw new RolesPermissionException(e);
        }
        return (null != count && count > 0) ? true : false;
    }

    @Override
    public boolean hasUserAccessForActivity(Short activityID) throws Exception {
        boolean result = false;
        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            for (Short roleId : mifosUser.getRoleIds()) {
                RoleBO role = legacyRolesPermissionsDao.getRole(roleId);
                if (role.getActivityIds().contains(activityID) || null == activityID) {
                    result = true;
                    break;
                }
            }
        } catch (PersistenceException e) {
            throw new RolesPermissionException(e);
        }
        return result;
    }

    @Override
    public int calculateDynamicActivityId() throws ServiceException, ActivityGeneratorException {
        return legacyRolesPermissionsDao.calculateDynamicActivityId();
    }

    @Override
    public int createActivityForQuestionGroup(short parentActivity, String lookUpDescription) throws HibernateException, PersistenceException, ServiceException, ActivityGeneratorException {
        return legacyRolesPermissionsDao.createActivityForQuestionGroup(parentActivity, lookUpDescription);
    }

    @Override
    public void updateLookUpValue(int newActivityId, String activityNameHead, String title) throws PersistenceException {
        StaticHibernateUtil.startTransaction();
        legacyRolesPermissionsDao.updateLookUpValue((short)newActivityId, activityNameHead + title);
        legacyRolesPermissionsDao.reparentActivityUsingHibernate((short)newActivityId, SecurityConstants.QUESTION_MANAGMENT);
        legacyRolesPermissionsDao.changeActivityMessage((short)newActivityId, Localization.ENGLISH_LOCALE_ID,
                activityNameHead + title);
        StaticHibernateUtil.commitTransaction();
    }
}
