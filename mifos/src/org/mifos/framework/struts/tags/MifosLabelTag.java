/**

 * MifosLabelTag.java   version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.framework.struts.tags;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigImplementer;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigItf;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.LabelTagUtils;
import org.mifos.framework.util.valueobjects.Context;

/**
 * Custom tag that represents type label. The tag adds a * if the corresponding
 * field assaociated is mandatory. The tag has a currency of the Locale
 * associated with it based on our attributes.
 * 
 * @author mohammedn
 * 
 */
public class MifosLabelTag extends BodyTagSupport {

	/**
	 * Serial Version UID for Serialization
	 */
	private static final long serialVersionUID = 1098346743243323316L;
	
	private FieldConfigItf fieldConfigItf=FieldConfigImplementer.getInstance();

	// ----------------------------------------------------- Instance Variables

	/**
	 * The name of the key based on which the Label is to be picked from
	 * Resource Bundle.
	 */
	private String name;
	
	private String keyhm;
	
	private String mandatory;
	
	private String bundle;
	
	/**
	 * The type of the Label is to check whether it is currency or not
	 */
	private String type;
	
	private String isColonRequired;
	
	private String isManadatoryIndicationNotRequired;
	
	public String getIsManadatoryIndicationNotRequired() {
		return isManadatoryIndicationNotRequired;
	}

	public void setIsManadatoryIndicationNotRequired(
			String isManadatoryIndicationNotRequired) {
		this.isManadatoryIndicationNotRequired = isManadatoryIndicationNotRequired;
	}

	public String getIsColonRequired() {
		return isColonRequired;
	}

	public void setIsColonRequired(String isColonRequired) {
		this.isColonRequired = isColonRequired;
	}

	public String getKeyhm() {
		return keyhm;
	}

	public void setKeyhm(String keyhm) {
		this.keyhm = keyhm;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMandatory() {
		return mandatory;
	}

	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return Returns the bundle.
	 */
	public String getBundle() {
		return bundle;
	}

	/**
	 * @param bundle The bundle to set.
	 */
	public void setBundle(String bundle) {
		this.bundle = bundle;
	}
	
	// --------------------------------------------------------- Constructors
	/**
	 * Construct a new instance of this tag.
	 */
	public MifosLabelTag() {
		super();
	}

	// --------------------------------------------------------- Public Methods

	/**
	 * Render the Label element
	 * 
	 * @return EVAL_PAGE
	 * @exception JspException
	 *                if a JSP exception has occurred
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		
		if(fieldConfigItf.isFieldHidden(getKeyhm())){
			StringBuilder label=new StringBuilder();
			hideLabelColumn(label);
			TagUtils.getInstance().write(pageContext,label.toString());
		}else{
			StringBuilder label=new StringBuilder();
			label.append(getLabel());
			if(getIsColonRequired()!=null && getIsColonRequired().equalsIgnoreCase("yes")){
				label.append(":");
			}
			TagUtils.getInstance().write(pageContext, label.toString());
		}
		return EVAL_PAGE;
	}

	/**
	 * Release any acquired resources.
	 */
	public void release() {

		super.release();
		name = null;
		type = null;
	}

	// ------------------------------------------------------ Protected Methods

	/**
	 * Create an appropriate Label element based on our parameters.
	 * 
	 * @return the label element
	 * @exception JspException
	 *                if a JSP exception has occurred
	 */
	protected String getLabel() throws JspException{

		StringBuilder result = new StringBuilder();
		// check the field for the confidential
		try {
			if (checkConfidentiality()) {
				// if the field is confidential for the user hide the tr
				hideLabelRow(result);
				return result.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// check if the field is hidden
		if (LabelTagUtils.getInstance().isHidden(name, pageContext)) {
			// if the field is hidden hide the tr associated with it.
			hideLabelRow(result);
			return result.toString();
		}

		// check if the field is mandatory by default
		if ((null != mandatory && "yes".equalsIgnoreCase(mandatory)) || fieldConfigItf.isFieldManadatory(getKeyhm())) {
			// if it is mandatory add a *.
			if(getIsManadatoryIndicationNotRequired()!=null && getIsManadatoryIndicationNotRequired().equalsIgnoreCase("yes"))
			{ }
			else
				result.append("<span class=\"mandatorytext\"><font color=\"#FF0000\">*</font></span>");

		} else {
			// if it is not mandatory check if it is configurable mandatory
			if (LabelTagUtils.getInstance().isConfigurableMandatory(name,
					pageContext)) {
				// if the field is configurable mandatory add a hidden variable
				// and *.
				result
						.append(
								"<input type=\"hidden\" name=\"hidden_" + name
										+ "\">")
						.append(
								"<span class=\"mandatorytext\"><font color=\"#FF0000\">*</font></span>");
			}
		}
		result.append(LabelTagUtils.getInstance().getLabel(pageContext,getLabelBundle(),LabelTagUtils.getInstance().getUserPreferredLocaleObject(pageContext),name,null));
		// check the label type.
		if (null != type && "currency".equalsIgnoreCase(type)) {
			// if label type is currency add the currency of the Locale.
			// TODO change the string locale to get the locale of the user from
			// UserContext
			// String locale =(UserContext)
			// (pageContext.getSession().getAttribute("UserContext")).getLocale();
			result.append(" ("
					+ LabelTagUtils.getInstance().getCurrency("locale") + ") ");
		}
		return result.toString();
	}
	
	/**
	 * The method checks for the confidentiality for the user.
	 * 
	 * @return true if the field is confidential to the user
	 * @throws Exception
	 */
	protected boolean checkConfidentiality() throws Exception {
		boolean confidential = false;
		// check if the key name starts with client
		if (!(name.startsWith("Client"))) {
			return confidential;
		}
		// get the object from the session
		Context context = (Context) pageContext.getSession().getAttribute(
				"Context");
		if (context == null) {
			return confidential;
		}
		Object obj = context.getValueObject();
		// get the class of the object
		Class objClass = obj.getClass();
		try {
			// fom the method getConfidentiality on the class
			Method confMethod = objClass.getDeclaredMethod(
					"getConfidentiality",new Class[]{});
			// invoke the method getConfidentiality on the class if the method
			confMethod.invoke(obj,new Object[]{});
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new Exception(e);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new Exception(e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new Exception(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new Exception(e);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new Exception(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		// if the method getConfidentiality does not throw Exception,get
		// UserContext from session
		UserContext userContext = (UserContext) pageContext.getSession()
				.getAttribute("UserContext");
		Method loanOfficerMethod;
		try {
			// get the loanOfficerId from the object
			loanOfficerMethod = objClass.getDeclaredMethod("getLoanOfficerId",
					new Class[]{});
			// check if the id from UserContext and loanOfficerId are equal
			if (userContext.getId() != (Short) loanOfficerMethod.invoke(obj,
					new Object[]{})) {
				// if the id from UserContext and loanOfficerId are not equal
				// check if the field is confidential
				confidential = LabelTagUtils.getInstance().isConfidential(name,
						pageContext);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new Exception(e);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new Exception(e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new Exception(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new Exception(e);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		return confidential;
	}

	/**
	 * This method is used to add the String which hides the tr to the
	 * StringBuilder
	 * 
	 * @param result--
	 *            The StringBuilder to which the String to hide the tr is to be
	 *            added
	 */
	protected void hideLabelRow(StringBuilder result) {
		result.append("<script language=\"javascript\">").append(
				"document.getElementById(\"" + name + "\")").append(
				".style.display=\"none\"").append("</script> ");
	}
	
	protected void hideLabelColumn(StringBuilder result) {
		result.append("<script language=\"javascript\">").append("if(document.getElementById(\"" + getKeyhm() + "\")!=null){").append(
				"document.getElementById(\"" + getKeyhm() + "\")").append(
				".style.display=\"none\";}").append("</script> ");
	}
	
	protected String getLabelBundle() throws JspException {
		if(bundle==null) {
			String[] labelNames=name.split("\\.");
			if(labelNames.length==2) {
				return labelNames[0]+"UIResources";
			}
			else {
				return "UIResources";
			}
		}
		return bundle;
	}
}
