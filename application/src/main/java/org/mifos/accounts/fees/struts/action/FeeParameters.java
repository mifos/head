package org.mifos.accounts.fees.struts.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.application.master.business.MasterDataEntity;

public class FeeParameters {
    private Map<Short, String> categories;
    private Map<Short, String> timesOfCharging;
    private Map<Short, String> timesOfChargingCustomers;
    private Map<Short, String> formulas;
    private Map<Short, String> frequencies;
    private Map<Short, String> glCodes;

    public Map<Short, String> getCategories() {
        return this.categories;
    }

    public Map<Short, String> getTimesOfCharging() {
        return this.timesOfCharging;
    }

    public Map<Short, String> getTimesOfChargingCustomers() {
        return this.timesOfChargingCustomers;
    }

    public Map<Short, String> getFormulas() {
        return this.formulas;
    }

    public Map<Short, String> getFrequencies() {
        return this.frequencies;
    }

    public Map<Short, String> getGlCodes() {
        return this.glCodes;
    }

    public FeeParameters(List<CategoryTypeEntity> categories, List<FeePaymentEntity> timesOfCharging,
            List<MasterDataEntity> timesOfChargeingCustomer, List<FeeFormulaEntity> formulas,
            List<FeeFrequencyTypeEntity> frequencies, List<GLCodeEntity> glCodes) {
        this.categories = listToMap(categories);
        this.timesOfCharging = listToMap(timesOfCharging);
        this.timesOfChargingCustomers = listToMap(timesOfChargeingCustomer);
        this.formulas = listToMap(formulas);
        this.frequencies = listToMap(frequencies);
        this.glCodes = glCodesToMap(glCodes);
    }

    private Map<Short, String> glCodesToMap(List<GLCodeEntity> glCodes) {
        Map<Short, String> idCodeMap = new HashMap<Short, String>();
        for (GLCodeEntity glCode : glCodes) {
            idCodeMap.put(glCode.getGlcodeId(), glCode.getGlcode());
        }
        return idCodeMap;
    }

    private Map<Short, String> listToMap(List<? extends MasterDataEntity> masterDataEntityList) {
        Map<Short, String> idNameMap = new HashMap<Short, String>();
        for (MasterDataEntity masterDataEntity : masterDataEntityList) {
            idNameMap.put(masterDataEntity.getId(), masterDataEntity.getName());
        }
        return idNameMap;
    }
}
