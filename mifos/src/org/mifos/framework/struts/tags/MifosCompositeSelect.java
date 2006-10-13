/**

 * MifosSelect.java    version: 1



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

/*
 * Created on Aug 3, 2005
 * Mifos Select is the class which will be used by the end user to create the
 * Mifos select Tag
 * usages: <mifos:MifosSelect [name][label] [property] [property1] [multiple] [selectStyle] >
 * [name] : name of the bean from which you want to populate the left listbox
 * [label] : label of the MifosSelect
 * [property] : name of the String array as declared in bean with first letter in caps
 * [property1] : name of the form bean property in which you want to store the selected list
 * [multiple] : whether list boxes are multiple or not
 * [selectStyle] : Style information of list boxes
 *  e.g <mifos:MifosSelect name="inputForm" label="abc" property="InList" property1="outList" multiple="true" selectStyle=" width:100 ">
 */
package org.mifos.framework.struts.tags;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;


/**
 * This class is mifos Select tag which renders the tag on the
 * screen
 *  User should call the javascript the function transferData(outSel) onclick event of
 *  submit button passing the refrence of the output list i.e. whatever he has given
 *  as property1 above e.g. <html:submit onclick="transferData(this.form.outList)"/>
 */

public class MifosCompositeSelect extends BodyTagSupport {

	/**
	 *
	 */
	private static final long serialVersionUID = 65587969876768761L;

	private String boxName;

	private String outputMethod;
	private String scope;
    /** label of the Tag */
    private String label;

    /** Name of the bean from which you want to pupulate the list */
    private String name;

    /** name of the Bean string array from which you want to populate the list */
    private String input;

    /** Form bean String array name where you want to store the list data */
    private String output;

    /** multiple property of lists */
    private String multiple;

    /** size property lists */
    private String size;

    /** For holding the select style */
    private String selectStyle="width:136px;";

    /** For holding the name of output */
    private String property;

    /** For holding the name of method for the text */
    private String property2;

    /** For holding the name of method for the value */
    private String property1;

   private String formName;
	/**
	 * @return Returns the input.
	 */
	public String getInput() {
		return input;
	}

	/**
	 * @param input The input to set.
	 */
	public void setInput(String input) {
		this.input = input;
	}

	/**
	 * @return Returns the output.
	 */
	public String getOutput() {
		return output;
	}

	/**
	 * @param output The output to set.
	 */
	public void setOutput(String output) {
		this.output = output;
	}

	/**
	 * @return Returns the property.
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * @param property The property to set.
	 */
	public void setProperty(String property) {
		this.property = property;
	}

    /**
     * Function get the multiple property of listboxes
     *
     * @return Returns the multiple.
     */
    public String getMultiple() {
        return multiple;
    }

    /**
     * Function set the multiple property of listboxes
     *
     * @param multiple
     *            The multiple to set.
     */
    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    /**
     * Function get the nmae of the tag
     *
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Function set the nmae of the tag
     *
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Function get the size of select boxes
     *
     * @return Returns the size.
     */
    public String getSize() {
        return size;
    }

    /**
     * Function Set the size of select boxes
     * @param size
     *            The size to set.
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * Function Set the Style of select boxes
      * @return Returns the selectStyle.
     */
    public String getSelectStyle() {
        return selectStyle;
    }

    /**
     * Function Set the Style of select boxes
     *
     * @param selectStyle
     *            The selectStyle to set.
     */
    public void setSelectStyle(String selectStyle) {
        this.selectStyle = selectStyle;
    }

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getProperty1() {
		return property1;
	}

	public void setProperty1(String property1) {
		this.property1 = property1;
	}

	public String getProperty2() {
		return property2;
	}

	public void setProperty2(String property2) {
		this.property2 = property2;
	}

	public String getBoxName() {
		return boxName;
	}

	public void setBoxName(String boxName) {
		this.boxName = boxName;
	}

	/**
	 * @return Returns the outputMethod.
	 */
	public String getOutputMethod() {
		return outputMethod;
	}

	/**
	 * @param outputMethod The outputMethod to set.
	 */
	public void setOutputMethod(String outputMethod) {
		this.outputMethod = outputMethod;
	}

    private RawButton[] rawbutton = new RawButton[2];

    private RawSelect[] rawselect = new RawSelect[2];

    /**
     * Default constructor creates two raw button and two rawselect object
     */
    public MifosCompositeSelect() {
        super();

        label = null;
        rawbutton[0] = new RawButton();
        rawbutton[1] = new RawButton();
        rawselect[0] = new RawSelect();
        rawselect[1] = new RawSelect();

    }

    /**
     * Constructor Initializes the label of the select tag
     *
     * @param label
     */
    public MifosCompositeSelect(String label) {
        super();
        this.label = label;
    }

    /**
     * Function to obtain the label of the select
     *
     * @return Returns the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Function to obtain the label of the select
     *
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

     //variable to hold the getlist method
    private Method getList = null;


    /**
     * Function to render the tag in jsp
     * @exception JspException throws  JspException
     */

    public int doEndTag() throws JspException {


        StringBuffer results = new StringBuffer();
        Collection inColl=null;
        Collection outColl=null;
        Collection midColl=null;
        if(this.input!=null) {
        	if(null !=pageContext.getRequest().getAttribute(this.input)) {
        		inColl= (Collection)pageContext.getRequest().getAttribute(this.input);
        	}
        }
        if(null==inColl || inColl.size()==0) {
        	//*********changes for bug id-29031**************
        	rawbutton[0] = new RawButton();
            rawbutton[1] = new RawButton();
            rawbutton[1].setValue("<< Remove");
            rawselect[0] = new RawSelect();
            rawselect[1] = new RawSelect();
        	addStyle(results);
            JavaScript(results);
            results.append("<table width=\"86%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
        	results.append("<tr> <td width=\"28%\">");
            results.append(rawselect[0].toString());
            
            results.append("</td><td width=\"31%\" align=\"center\">");
            results.append("<table " + "width=\"70%\" border=\"0\" "
                    + "cellspacing=\"0\" cellpadding=\"3\"> <tr>"
                    + "<td align=\"center\">" + rawbutton[0].toString());

            results.append("</td></tr><tr><td height=\"26\" align=\"center\">"
                    + rawbutton[1].toString());
            results.append("</td></tr></table></td><td width=\"41%\">");
            results.append(rawselect[1].toString());
            results.append("</td></tr></table>");
            results.append("<div id=\"tooltip\" style=\"position:absolute;visibility:hidden;border:1px solid black;font-size:12px;layer-background-color:lightyellow;background-color:lightyellow;z-index:1;padding:1px\"></div>" );

            TagUtils.getInstance().write(pageContext, results.toString());
        	//******************************
        	return super.doEndTag();
        }
        if(this.output!=null) {
        	Object obj=null;
        	if(scope==null) {
        		scope="session";
        	}
        	if(scope.equals("request")) {

        		obj= pageContext.getRequest().getAttribute(formName);

    		}
    		if(scope.equals("session"))
    		{
    			obj=pageContext.getSession().getAttribute(formName);
    		}


			try {
				Method getColltwo = obj.getClass().getDeclaredMethod("get" + this.output.substring(0, 1).toUpperCase()+ output.substring(1),
						(Class[]) null);//getTest in obj==actionform
				midColl = (Collection)getColltwo.invoke(obj, (Object[]) null);
				if(outputMethod !=null && midColl !=null) {
					outColl=new ArrayList();
					for(Object object:midColl) {
						Method objectcall = object.getClass().getDeclaredMethod("get" + this.outputMethod.substring(0, 1).toUpperCase()+ outputMethod.substring(1),
								(Class[]) null);//getTest in obj==actionform
						outColl.add(objectcall.invoke(object, (Object[]) null));
					}
				}
				else {
					outColl=midColl;
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
        }


        Map<Object,Object> inMap=null;
        Map<Object,Object> outMap=null;
        try {
			inMap=helper(inColl);
			// System.out.println("value of-------------------------"+outColl);
		if(outColl!=null) {
			outMap=helper(outColl);
		}
		}
        catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

        Map inputCopy = new HashMap(inMap);
        if(outMap != null) {
        	// System.out.println("inside outMap if"+outMap);
        	Set input = inMap.keySet();
        	Set output = outMap.keySet();
        	for(Iterator in= input.iterator();in.hasNext();) {
        		Object obj1 = in.next();
        		for(Iterator out= output.iterator();out.hasNext();) {
            		Object obj2 = out.next();
        			if(obj1.equals(obj2)) {
        				outMap.put(obj1,inMap.get(obj1));
        				// System.out.println("inside danger::::::::");
        				inputCopy.remove(obj1);
        			}
        		}
        	}
        }
        rawselect[0].setData(inputCopy);
        rawselect[1].setData(outMap);
        init();
        addStyle(results);
        JavaScript(results);
        results.append("<table width=\"86%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");

        if (null != getLabel()) {
            results.append("<tr> <td>" + getLabel() + "</td></tr>");
        }
        results.append("<tr> <td width=\"28%\">");
        results.append(rawselect[0].toString());
        results.append("</td><td width=\"31%\" align=\"center\">");
        results.append("<table " + "width=\"70%\" border=\"0\" "
                + "cellspacing=\"0\" cellpadding=\"3\"> <tr>"
                + "<td align=\"center\">" + rawbutton[0].toString());

        results.append("</td></tr><tr><td height=\"26\" align=\"center\">"
                + rawbutton[1].toString());
        results.append("</td></tr></table></td><td width=\"41%\">");
        results.append(rawselect[1].toString());
        results.append("</td></tr></table>");
        results.append("<div id=\"tooltip\" style=\"position:absolute;visibility:hidden;border:1px solid black;font-size:12px;layer-background-color:lightyellow;background-color:lightyellow;z-index:1;padding:1px\"></div>" );

        TagUtils.getInstance().write(pageContext, results.toString());
        return super.doEndTag();
    }

    /**
     * This function Add the javascript to the tag for moving
     * the data between the lists
     * @param results   StringBuffer object to hold the string representation of the
     *            tag
     */
    private void JavaScript(StringBuffer results) {
        results
                .append("<script language=\"javascript\" SRC=\"pages/framework/js/Logic.js\" >"
                        + "</script> <link rel=\"stylesheet\" type=\"text/css\" href=\"pages/framework/css/tooltip.css\" title=\"MyCSS\"/>");

    }
    /**
     * Function to add html style to mifos tag
     * @param results   StringBuffer object to hold the string representation of the tag
     */
    private void addStyle(StringBuffer results )
    {
        results
        .append(" <STYLE> .ttip {border:1px solid black;font-size:12px;layer-background-color:lightyellow;background-color:lightyellow}  </STYLE> " );
    }
    /**
     * Function to Initialize the members of the MifosSelect class
     */

    private void init() {
        rawbutton[0].setName("MoveRight");
        rawselect[0].setName(input);
        rawselect[0].setStyle(getSelectStyle());
        rawselect[1].setStyle(getSelectStyle());
        rawselect[1].setName(getProperty());

        if (null != getMultiple()) {
            rawselect[0].setMultiple("true");
            rawselect[1].setMultiple("true");
        }
        if (null != getSize()) {
            rawselect[0].setMultiple(getSize());
            rawselect[1].setMultiple(getSize());
        }
        rawbutton[1].setValue("<< Remove");
        rawbutton[0].setOnclick("moveOptions(this.form."+ rawselect[0].getName() + "," + "this.form."+ rawselect[1].getName() + ")");
        rawbutton[1].setOnclick("moveOptions(this.form."
                + rawselect[1].getName() + ","  + "this.form."
                + rawselect[0].getName()+ ")");
    }

    private Map helper(Collection coll) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    	Map<Object,Object> map=new HashMap<Object,Object>();
    	if(!coll.isEmpty()) {
    		for (Iterator it = coll.iterator(); it.hasNext();) {
    			Object object = it.next();
				Object string1=null;
				Object string2=null;
				String str1 = this.property2.substring(0, 1);
				getList = object.getClass().getDeclaredMethod(
						"get" + str1.toUpperCase() + this.property2.substring(1),
						(Class[]) null);
				string2 =  getList.invoke(object, (Object[]) null);//string2==text
				String str2 = this.property1.substring(0, 1);
				getList = object.getClass().getDeclaredMethod(
						"get" + str2.toUpperCase() + this.property1.substring(1),
						(Class[]) null);
				string1 =  getList.invoke(object, (Object[]) null);//string1==value
				map.put(string1,string2);
    		}
    		return map;
    	}
    	return null;
    }




}
