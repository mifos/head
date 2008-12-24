package org.mifos.framework.components.customTableTag;

import org.mifos.framework.exceptions.TableTagParseException;

public class LinkDetails {

  private String action=null;
  private ActionParam[] actionParam=null;

  public void setAction(String action){
    this.action=action;
  }

  public String getAction(){
    return action;
  }

  public void setActionParam(ActionParam[] actionParam){
    this.actionParam=actionParam;
  }

  public ActionParam[] getActionParam(){
    return actionParam;
  }
  
  public void generateLink(StringBuilder tableInfo,Object obj) throws TableTagParseException{
	  tableInfo.append(" href=\""+getAction()+"?");
	  ActionParam[] actionParam=getActionParam();
	  for(int i=0;i<actionParam.length;i++){
		  actionParam[i].generateParameter(tableInfo,obj);
		  if(i+1!=actionParam.length)
			  tableInfo.append("&");
	  }
	  tableInfo.append("\"");
  }

}
