/**
 * 
 */
package org.mifos.application.productdefinition.struts.actionforms;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.valueobjects.ProductType;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.struts.actionforms.MifosSearchActionForm;
import org.mifos.framework.util.helpers.Constants;

/**
 * @author mohammedn
 *
 */
public class PrdConfigurationActionForm extends MifosSearchActionForm {
	/**
	 * default constructor
	 */
	public PrdConfigurationActionForm() {
		super();
		productTypeList=new ArrayList<ProductType>();
		productTypeList.add(new ProductType());
		productTypeList.add(new ProductType());
	}
	
	/**
	 * Serail Version UID for Serialization
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * latenessDays of loan product
	 */
	private String latenessDays;
	/**
	 * dormancyDays of savings product
	 */
	private String dormancyDays;
	/**
	 * List of product Types
	 */
	private List<ProductType> productTypeList;
	/**
	 * product type
	 */
	private ProductType productType;

	/**
	 * 
	 * @param index
	 * @return Returns the productType.
	 */
	public ProductType getProductType(int index) {
		return productTypeList.get(index);
	}
	/**
	 * The productTypeList to set.
	 * 
	 * @param index
	 * @param productType
	 */
	public void setProductType(int index,ProductType productType) {
		productTypeList.add(index,productType);
	}
	/**
	 * 
	 * @return Returns the productTypeList.
	 */
	public List<ProductType> getProductTypeList() {
		return productTypeList;
	}

	/**
	 * @return Returns the dormancyDays.
	 */
	public String getDormancyDays() {
		return dormancyDays;
	}
	/**
	 * @param dormancyDays The dormancyDays to set.
	 */
	public void setDormancyDays(String dormancyDays) {
		this.dormancyDays = dormancyDays;
	}
	/**
	 * @return Returns the latenessDays.
	 */
	public String getLatenessDays() {
		return latenessDays;
	}
	/**
	 * @param latenessDays The latenessDays to set.
	 */
	public void setLatenessDays(String latenessDays) {
		this.latenessDays = latenessDays;
	}
	
	/**
	 * This method is used to do any custom validations and also to skip vaildation for any 
	 * particular method
	 * 
	 * @param mapping
	 * @param request
	 * @return
	 */
	public ActionErrors customValidate(ActionMapping mapping,
			HttpServletRequest request) {
		String methodCalled= request.getParameter(ProductDefinitionConstants.METHOD);
		if(null !=methodCalled) {
			if(ProductDefinitionConstants.CANCELMETHOD.equals(methodCalled)  || 
					ProductDefinitionConstants.SEARCHMETHOD.equals(methodCalled)) {
				MifosLogManager.getLogger(LoggerConstants.LOGINLOGGER).info(
						"Skipping validation for "+methodCalled+ " method");
				request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}
		}
		return null;
	}
}
