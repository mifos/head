package org.mifos.framework.components.customTableTag;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;

import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.TableTagParseException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.valueobjects.Context;

import java.net.URISyntaxException;



public class TableTag extends BodyTagSupport {

  Table table=null;

  StringBuilder tableInfo=null;
  Locale locale=null;
  Locale mfiLocale=null;
  Locale prefferedLocale=null;

  private String source=null;
  private String scope=null;
  private String xmlFileName=null;
  private String moduleName=null;
  private String passLocale=null;
  
  
  public void setSource(String source){
	  this.source =source;
  }
  public String getSource(){
	  return source;
  }

  public String getPassLocale() {
	return passLocale;
  }

  public void setPassLocale(String passLocale) {
	this.passLocale = passLocale;
  }

  public void setScope(String scope){
	  this.scope=scope;
  }
  public String getScope(){
	  return scope;
  }

  public void setXmlFileName(String xmlFileName){
	  this.xmlFileName =xmlFileName;
  }

  public String getXmlFileName(){
	 return xmlFileName;
  }

  public void setModuleName(String moduleName){
	  this.moduleName=moduleName;
  }

  public String getModuleName(){
	  return  moduleName ;
  }

  public int doStartTag() throws JspException {
    try {

      table=TableTagParser.getInstance().parser(ResourceLoader.getURI("org/mifos/application/"+moduleName+"/util/resources/"+xmlFileName).toString());
     // PropertyResourceBundle bundle =	(PropertyResourceBundle)PropertyResourceBundle.getBundle("org/mifos/application/"+moduleName+"/util/resources/"+getResourcebundleName(moduleName));
      
		
      tableInfo=new StringBuilder();
      if(source==null || scope==null){
       	  throw new JspException();
        }
      List obj=null;
      if(scope.equalsIgnoreCase("session"))
    	  obj=(List)pageContext.getSession().getAttribute(source);
      else if(scope.equalsIgnoreCase("request"))
    	  obj=(List)pageContext.getRequest().getAttribute(source);
     

      if(obj==null || obj.isEmpty() ){
       	return super.doStartTag();
      }

      if(passLocale!=null && passLocale.equalsIgnoreCase("true")){
    	  if(obj!=null || !obj.isEmpty()){
    		Context context=(Context)pageContext.getRequest().getAttribute(Constants.CONTEXT);
    		UserContext userContext=null;
    		if(context!=null){
    			userContext=context.getUserContext();
    		}else{
    			userContext = (UserContext) pageContext.getSession().getAttribute(Constants.USER_CONTEXT_KEY);
    		}
    		populateLocale(userContext);
    	  }
      }
      
     table.getTable(tableInfo,obj,locale,prefferedLocale,mfiLocale, pageContext,getResourcebundleName(moduleName));


    }
    catch(URISyntaxException e){
    	e.printStackTrace() ;
    }
    catch (TableTagParseException ex) {
      ex.printStackTrace();
      throw new JspException(ex);
    }


    try {
		pageContext.getOut().print(tableInfo.toString());
	} catch (IOException e) {
		e.printStackTrace();
	}
    return super.doStartTag();
  }

  private void populateLocale(UserContext userContext){
	  	if(userContext.getPereferedLocale()!=null){
			locale=userContext.getPereferedLocale();
		}
		if(locale==null && userContext.getMfiLocale()!=null){
			locale=userContext.getMfiLocale();
		}
		//Setting prefferedLocale and mfiLocale
		if(userContext.getPereferedLocale()!=null){
			prefferedLocale=userContext.getPereferedLocale();
		}
		if(userContext.getMfiLocale()!=null){
			mfiLocale=userContext.getMfiLocale();
	    }
  }
  
  private String getResourcebundleName(String moduleName){
	  if(moduleName.lastIndexOf("\\")>-1)
	  moduleName=moduleName.substring(moduleName.lastIndexOf("\\")+1,moduleName.length());
	  else if(moduleName.lastIndexOf("/")>-1 )
			  moduleName=moduleName.substring(moduleName.lastIndexOf("/")+1,moduleName.length());
	  if (moduleName.contains( "UIResources") ) return moduleName;
	  else  return moduleName + "UIResources";
	  
  }

}
