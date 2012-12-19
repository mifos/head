package org.mifos.application.servicefacade;


import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.financial.business.COABO;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.ChartOfAccountsCache;
import org.mifos.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.application.admin.servicefacade.CoaDto;
import org.mifos.application.admin.servicefacade.CoaServiceFacade;
import org.mifos.application.admin.servicefacade.RolesPermissionServiceFacade;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.security.util.SecurityConstants;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class CoaServiceFacadeWebTier implements CoaServiceFacade {

    public static final String ASSETS_GL_CODE = "10000";
    public static final String LIABILITES_GL_CODE = "20000";
    public static final String INCOME_GL_CODE = "30000";
    public static final String EXPENDITURE_GL_CODE = "40000";

    public static final String CANNOT_MODIFY = "coa.cannotModify";
    public static final String PARENT_DOESNT_EXIST = "coa.parentDoesntExist";
    public static final String GLCODE_ALREADY_EXISTS = "coa.alreadyExists";
    public static final String EMPTY_GLCODE = "coa.empty";
    
    private LegacyAccountDao legacyAccountDao;
    
    private RolesPermissionServiceFacade rolesPermissionServiceFacade;
    
    @Override
    public List<CoaDto> getList(Short id) {
        
        List<COABO> coaBoList = null;
        
        if (id == null) {
            coaBoList = legacyAccountDao.getCOAlist();
        } else {
            coaBoList = legacyAccountDao.getCOAChildList(id);
        }
         
        List<CoaDto> coaDtoList = new ArrayList<CoaDto>();
        boolean userHasAccess = canModifyCOA();
        
        for (COABO coaBo : coaBoList) {
            CoaDto dto = coaBo.toDto();
            if (userHasAccess) {
                dto.setModifiable(isModifiable(coaBo));
            } else {
                dto.setModifiable(false);
            }
            coaDtoList.add(dto);
        }
        
        return coaDtoList;
    }

    private boolean isModifiable(COABO coaBo) {
        String glCode = coaBo.getGlCode();
        Short glCodeId = coaBo.getAssociatedGlcode().getGlcodeId();
        int count;
        
        try {
            count = legacyAccountDao.getCountForGlCode(glCodeId);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
        
        boolean modifiable = !(glCode.equals(ASSETS_GL_CODE)
                || glCode.equals(LIABILITES_GL_CODE)
                || glCode.equals(INCOME_GL_CODE)
                || glCode.equals(EXPENDITURE_GL_CODE)
                || count > 0
                || coaBo.getSubCategoryCOABOs().size() > 0);
        
        return modifiable;
    }

    @Override
    public void create(CoaDto coaDto) {
        try {
            legacyAccountDao.addGeneralLedgerAccount(coaDto.getAccountName(), coaDto.getGlCodeString(),
                    coaDto.getParentId(), null);
            reloadCache();
        } catch (MifosRuntimeException ex) {
            throw new BusinessRuleException(GLCODE_ALREADY_EXISTS);
        }
    }

    private void reloadCache() {
        ChartOfAccountsCache.clear();
        FinancialActionCache.clear();
        
        try {
            FinancialInitializer.initialize();
        } catch (FinancialException e) {
            throw new MifosRuntimeException(e);
        }
        
    }
    
    @Override
    public boolean canModifyCOA() {
        try {
            return rolesPermissionServiceFacade.hasUserAccessForActivity(SecurityConstants.CAN_MODIFY_CHART_OF_ACCOUNTS);
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }
    
    @Override
    public void delete(Short id) {
        try {
            COABO coaBo = legacyAccountDao.getPersistentObject(COABO.class, id);
            
            if (coaBo == null || !isModifiable(coaBo)) {
                throw new BusinessRuleException(CANNOT_MODIFY);
            }
            
            legacyAccountDao.deleteLedgerAccount(coaBo.getAccountId());
            reloadCache();
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
        
    }

    @Override
    public CoaDto getCoaDTO(Short id) {
        COABO coaBo;
        try {
            coaBo = legacyAccountDao.getPersistentObject(COABO.class, id);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
        return coaBo.toDto();
    }
    
    @Autowired
    public void setLegacyAccountDao(LegacyAccountDao legacyAccountDao) {
        this.legacyAccountDao = legacyAccountDao;
    }
    
    @Autowired
    public void setRolesPermissionServiceFacade(RolesPermissionServiceFacade rolesPermissionServiceFacade) {
        this.rolesPermissionServiceFacade = rolesPermissionServiceFacade;
    }

    @Override
    public void modify(CoaDto coaDto) {
        try {
            COABO coaBo = legacyAccountDao.getPersistentObject(COABO.class, coaDto.getAccountId());
            
            Short parentId = legacyAccountDao.getAccountIdFromGlCode(coaDto.getParentGlCode());
            
            Short accountId = legacyAccountDao.getAccountIdFromGlCode(coaDto.getGlCodeString());
            
            if (!StringUtils.hasText(coaDto.getGlCodeString())) {
                throw new BusinessRuleException(EMPTY_GLCODE);
            }
            
            if (accountId != null && !accountId.equals(coaBo.getAccountId())) {
                throw new BusinessRuleException(GLCODE_ALREADY_EXISTS);
            }
            
            if (coaBo == null || !isModifiable(coaBo)) {
                throw new BusinessRuleException(CANNOT_MODIFY);
            }
            
            if (parentId == null) {
                throw new BusinessRuleException(PARENT_DOESNT_EXIST);
            }
            
            legacyAccountDao.updateLedgerAccount(coaBo, coaDto.getAccountName(), coaDto.getGlCodeString(), coaDto.getParentGlCode());
            reloadCache();
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }
}
