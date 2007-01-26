package org.mifos.framework.struts.tags;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;
import org.apache.strutsel.taglib.html.ELTextareaTag;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;

public class MifosTextareaTag extends ELTextareaTag {
	
	private FieldConfig fieldConfig = FieldConfig.getInstance();
	
	private String keyhm=null;

	public String getKeyhm() {
		return keyhm;
	}

	public void setKeyhm(String keyhm) {
		this.keyhm = keyhm;
	}

	@Override
	public int doStartTag() throws JspException {
		if(fieldConfig.isFieldHidden(getKeyhm()))
			return EVAL_PAGE;
		else if (!fieldConfig.isFieldHidden(getKeyhm()) && fieldConfig.isFieldManadatory(getKeyhm()) ){
			StringBuffer inputsForhidden=new StringBuffer();
			inputsForhidden.append("<input type=\"hidden\"  name=\""+getKeyhm()+"\" value=\""+getPropertyExpr()+"\"/>");
		    TagUtils.getInstance().write(this.pageContext,inputsForhidden.toString());
		}
		return super.doStartTag();
	}
	
}
