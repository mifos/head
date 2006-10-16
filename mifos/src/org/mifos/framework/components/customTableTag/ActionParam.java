package org.mifos.framework.components.customTableTag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.mifos.framework.exceptions.TableTagParseException;

public class ActionParam {

  private String name=null;
  private String value=null;
  private String valueType=null;

  public void setName(String name){
    this.name=name;
  }

  public void setValue(String value){
    this.value=value;
  }

  public void setValueType(String valueType){
    this.valueType=valueType;
  }

  public String getName(){
    return name;
  }

  public String getValue(){
    return value;
  }

  public String getValueType(){
    return valueType;
  }

  public void generateParameter(StringBuilder tableInfo,Object obj) throws TableTagParseException{
	  tableInfo.append(getName()+"=");
      Method[] methods= obj.getClass().getMethods();
	  for(int i=0;i<methods.length;i++){
		  if(methods[i].getName().equalsIgnoreCase("get".concat(getValue()))){
			  try{
				  tableInfo.append(methods[i].invoke(obj,new Object[]{}));
			  }catch(IllegalAccessException e){
				  e.printStackTrace();
				  throw new TableTagParseException(e);
			  }catch(InvocationTargetException ex){
				  ex.printStackTrace();
				  throw new TableTagParseException(ex);
			  }
		  }
	  }
  }

}
