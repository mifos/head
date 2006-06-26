package org.mifos.framework.struts.tags;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;
import org.apache.strutsel.taglib.html.ELFileTag;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigImplementer;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigItf;

public class MifosFileTag extends ELFileTag {
	
	private FieldConfigItf fieldConfigItf=FieldConfigImplementer.getInstance();
	private String keyhm;

	public String getKeyhm() {
		return keyhm;
	}

	public void setKeyhm(String keyhm) {
		this.keyhm = keyhm;
	}

	@Override
	public int doStartTag() throws JspException {
		if(fieldConfigItf.isFieldHidden(getKeyhm()))
			return EVAL_PAGE;
		else if (!fieldConfigItf.isFieldHidden(getKeyhm()) && fieldConfigItf.isFieldManadatory(getKeyhm()) ){
			StringBuffer inputsForhidden=new StringBuffer();
			inputsForhidden.append("<input type=\"hidden\"  name=\""+getKeyhm()+"\" value=\""+getPropertyExpr()+"\"/>");
		    TagUtils.getInstance().write(this.pageContext,inputsForhidden.toString());
		}
		return super.doStartTag();
	}
	
	

}
