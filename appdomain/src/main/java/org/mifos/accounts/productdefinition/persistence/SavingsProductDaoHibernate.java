package org.mifos.accounts.productdefinition.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.mifos.accounts.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.meeting.business.RecurrenceTypeEntity;
import org.mifos.customers.business.CustomerLevelEntity;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;

public class SavingsProductDaoHibernate implements SavingsProductDao {

    private final GenericDao genericDao;

    @Autowired
    public SavingsProductDaoHibernate(final GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PrdOfferingDto> findSavingsProductByCustomerLevel(CustomerLevelEntity customerLevel) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(AccountConstants.PRDTYPEID, ProductType.SAVINGS.getValue());
        queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.SAVINGS_ACTIVE.getValue());
        queryParameters.put(AccountConstants.PRODUCT_APPLICABLE_TO, customerLevel.getProductApplicableType());
        return (List<PrdOfferingDto>) genericDao.executeNamedQueryWithResultTransformer(
                "accounts.getApplicableSavingsProductOfferings", queryParameters, PrdOfferingDto.class);
    }

    @Override
    public ProductTypeEntity findSavingsProductConfiguration() {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("productTypeId", AccountTypes.SAVINGS_ACCOUNT.getValue());

        return (ProductTypeEntity) this.genericDao.executeUniqueResultNamedQuery("findProductTypeConfigurationById", queryParameters);
    }

    @Override
    public void save(ProductTypeEntity productType) {
        this.genericDao.createOrUpdate(productType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> findAllSavingsProducts() {
        return (List<Object[]>) genericDao.executeNamedQuery("findAllSavingsProducts", null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SavingsBO> retrieveSavingsAccountsForPrd(Short prdOfferingId) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(ProductDefinitionConstants.PRDOFFERINGID, prdOfferingId);
        return (List<SavingsBO>) genericDao.executeNamedQuery(NamedQueryConstants.RETRIEVE_SAVINGS_ACCCOUNT, queryParameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RecurrenceTypeEntity> getSavingsApplicableRecurrenceTypes() {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        return (List<RecurrenceTypeEntity>) genericDao.executeNamedQuery(NamedQueryConstants.SAVINGS_APPL_RECURRENCETYPES, queryParameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SavingsOfferingBO> getAllActiveSavingsProducts() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.SAVINGS_ACTIVE.getValue());
        return (List<SavingsOfferingBO>) genericDao.executeNamedQuery(NamedQueryConstants.GET_ALL_ACTIVE_SAVINGS_PRODUCTS, queryParameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SavingsOfferingBO> getSavingsOfferingsNotMixed(Short localeId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.SAVINGS_ACTIVE.getValue());

        List<SavingsOfferingBO> savingsOfferings = (List<SavingsOfferingBO>) genericDao.executeNamedQuery(
                NamedQueryConstants.PRODUCT_NOTMIXED_SAVING_PRODUCTS, queryParameters);
        return savingsOfferings;
    }

    @Override
    public void save(SavingsOfferingBO product) {
        this.genericDao.createOrUpdate(product);
    }

    @Override
    public SavingsOfferingBO findById(Integer productId) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("prdOfferingId", productId.shortValue());

        return (SavingsOfferingBO) this.genericDao.executeUniqueResultNamedQuery("savingsProduct.byid", queryParameters);
    }

    @Override
    public SavingsOfferingBO findBySystemId(String globalPrdOfferingNum) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("globalPrdOfferingNum", globalPrdOfferingNum);

        return (SavingsOfferingBO) this.genericDao.executeUniqueResultNamedQuery("savingsProduct.byglobalid", queryParameters);
    }

    @Override
    public void validateProductWithSameNameDoesNotExist(final String name) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(ProductDefinitionConstants.PRDOFFERINGNAME, name);
        Number matchingProducts = (Number) this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.PRODUCTOFFERING_CREATEOFFERINGNAMECOUNT, queryParameters);
        if (matchingProducts.intValue() > 0) {
            throw new BusinessRuleException("Duplicate.generalDetails.name");
        }
    }

    @Override
    public void validateProductWithSameShortNameDoesNotExist(final String shortName) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(ProductDefinitionConstants.PRDOFFERINGSHORTNAME, shortName);
        Number matchingProducts = (Number) this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.PRODUCTOFFERING_CREATEOFFERINGSHORTNAMECOUNT, queryParameters);
        if (matchingProducts.intValue() > 0) {
            throw new BusinessRuleException("Duplicate.generalDetails.shortName");
        }
    }

    @Override
    public boolean activeOrInactiveSavingsAccountsExistForProduct(final Integer productId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("prdOfferingId", productId.shortValue());
        Number activeOrInactiveAccounts = (Number) this.genericDao.executeUniqueResultNamedQuery("savingsProduct.openSavingsAccounts", queryParameters);
        return activeOrInactiveAccounts.intValue() > 0;
    }

    @Override
    public InterestCalcTypeEntity retrieveInterestCalcType(InterestCalcType interestCalcType) {
        InterestCalcTypeEntity result = null;
        List<InterestCalcTypeEntity> allSavingsTypes = retrieveInterestCalculationTypes();
        for (InterestCalcTypeEntity entity : allSavingsTypes) {
            if (entity.getId().equals(interestCalcType.getValue())) {
                result = entity;
            }
        }
        return result;
    }

    @Override
    public List<InterestCalcTypeEntity> retrieveInterestCalculationTypes() {
        return doFetchListOfMasterDataFor(InterestCalcTypeEntity.class);
    }

    @SuppressWarnings("unchecked")
    private <T extends MasterDataEntity> List<T> doFetchListOfMasterDataFor(Class<T> type) {
        Session session = StaticHibernateUtil.getSessionTL();
        List<T> masterEntities = session.createQuery("from " + type.getName()).list();
        for (MasterDataEntity masterData : masterEntities) {
            Hibernate.initialize(masterData.getNames());
        }
        return masterEntities;
    }
}