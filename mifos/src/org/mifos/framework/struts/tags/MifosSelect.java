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

package org.mifos.framework.struts.tags;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;

/**
 * This class is mifos Select tag which renders the tag on the
 * screen
 *  User should call the javascript the function transferData(outSel) onclick event of
 *  submit button passing the refrence of the output list i.e. whatever he has given
 *  as property1 above e.g. <html:submit onclick="transferData(this.form.outList)"/>
 * 
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

public class MifosSelect extends BodyTagSupport {
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
    private String selectStyle;

    /** For holding the name of output */
    private String property;

    /** For holding the name of method for the text */
    private String Property2;

    /** For holding the name of method for the value */
    private String Property1;
    
    private String spacedOut;
    private String leftListName;
    private String addButtonName;
    private String removeButtonName;


	
	public String getAddButtonName() {
		return addButtonName;
	}

	public void setAddButtonName(String addButtonName) {
		this.addButtonName = addButtonName;
	}

	public String getLeftListName() {
		return leftListName;
	}

	public void setLeftListName(String leftListName) {
		this.leftListName = leftListName;
	}

	public String getRemoveButtonName() {
		return removeButtonName;
	}

	public void setRemoveButtonName(String removeButtonName) {
		this.removeButtonName = removeButtonName;
	}

	public String getSpacedOut() {
		return spacedOut;
	}

	public void setSpacedOut(String spacedOut) {
		this.spacedOut = spacedOut;
	}

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
	 * @return Returns the property1.
	 */
	public String getProperty1() {
		return Property1;
	}

	/**
	 * @param property1 The property1 to set.
	 */
	public void setProperty1(String property1) {
		Property1 = property1;
	}

	/**
	 * @return Returns the property2.
	 */
	public String getProperty2() {
		return Property2;
	}

	/**
	 * @param property2 The property2 to set.
	 */
	public void setProperty2(String property2) {
		Property2 = property2;
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

    private RawButton[] rawbutton = new RawButton[2];

    private RawSelect[] rawselect = new RawSelect[2];

    /**
     * Default constructor creates two raw button and two rawselect object
     */
    public MifosSelect() {
        super();

        label = null;
        rawbutton[0] = new RawButton();
        rawbutton[1] = new RawButton();
        rawselect[0] = new RawSelect();
        rawselect[1] = new RawSelect();

    }

    /**
     * Constructor Initializes the label of the select tag
     */
    public MifosSelect(String label) {
        super();
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

     //variable to hold the getlist method
    private Method getList = null;

    /**
     * Function to render the tag in jsp
     */
    @Override
	public int doEndTag() throws JspException {
        Collection inColl= (Collection)pageContext.getRequest().getAttribute(
        	this.input);

        Collection outColl= null;
        if(this.output!=null) {
        	outColl=(Collection)pageContext.getRequest().getAttribute(
        		this.output);
        }

        String html = render(inColl, outColl);
		TagUtils.getInstance().write(pageContext, html);
        return super.doEndTag();
    }

	String render(Collection inColl, Collection outColl) {
		StringBuffer html = new StringBuffer();
        Map<Object,Object> inMap;
        Map<Object,Object> outMap;
        try {
			inMap = helper(inColl);
			if (outColl != null) {
				outMap = helper(outColl);
			}
			else {
				outMap = null;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
		
		Map inputCopy = new LinkedHashMap();
		if (inMap != null) {
			inputCopy = new LinkedHashMap(inMap);
			if (outMap != null) {
				Set input = inMap.keySet();
				Set output = outMap.keySet();
				for (Iterator iter = input.iterator(); iter.hasNext();) {
					Object obj1 = iter.next();
					for (Iterator out = output.iterator(); out.hasNext();) {
						Object obj2 = out.next();
						if (obj1.equals(obj2)) {
							inputCopy.remove(obj1);
						}
					}
				}
			}
		}
        rawselect[0].setData(inputCopy);
        rawselect[1].setData(outMap);
        init();
        addStyle(html);
        addJavaScript(html);
        html.append("<table >");

        if (null != getLabel()) {
            html.append("<tr> <td>" + getLabel() + "</td></tr>");
        }
        String spacedOut = getSpacedOut();
        if ((spacedOut != null) && (spacedOut.equalsIgnoreCase("true")))
        	html.append("<tr> <td width=\"28%\">");
        else
        	html.append("<tr> <td>");
        html.append(rawselect[0].toString());
        if ((spacedOut != null) && (spacedOut.equalsIgnoreCase("true")))
        	html.append("</td><td width=\"31%\" align=\"center\">");
        else
        	html.append("</td><td>");
        if ((spacedOut != null) && (spacedOut.equalsIgnoreCase("true")))
        	html.append("<table " + "width=\"70%\" border=\"0\" "
                    + "cellspacing=\"0\" cellpadding=\"3\"> <tr>"
                    + "<td align=\"center\">" + rawbutton[0].toString());
        else
        	html.append("<table " + "width=\"50%\" border=\"0\" "
                + "cellspacing=\"0\" cellpadding=\"3\"> <tr>"
                + "<td align=\"center\">" + rawbutton[0].toString());

        html.append("</td></tr><tr><td height=\"26\" align=\"center\">"
                + rawbutton[1].toString());
        html.append("</td></tr></table></td><td>");
        html.append(rawselect[1].toString());
        html.append("</td></tr></table>");
        html.append("<div id=\"tooltip\" style=\"position:absolute;visibility:hidden;border:1px solid black;font-size:12px;layer-background-color:lightyellow;background-color:lightyellow;z-index:1;padding:1px\"></div>" );

        return html.toString();
	}

    /**
     * This function Add the javascript to the tag for moving
     * the data between the lists
     */
    private void addJavaScript(StringBuffer results) {
        results.append("<script language=\"javascript\" SRC=\"pages/framework/js/Logic.js\" >"
                        + "</script> <link rel=\"stylesheet\" type=\"text/css\" href=\"pages/framework/css/tooltip.css\" title=\"MyCSS\"/>");
    }
    /**
     * Function to add html style to mifos tag
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
    	if (getAddButtonName() == null)
    		rawbutton[0].setName("MoveRight");
    	else
    		rawbutton[0].setName(getAddButtonName());
    	if (getRemoveButtonName() != null)
    		rawbutton[1].setName(getRemoveButtonName());
    	if (getLeftListName() == null)
    	{
	        if(getProperty1() != null)
	        	rawselect[0].setName(getProperty1());
	        else {
	        	rawselect[0].setName("LeftSelect");
	        }
    	}
    	else
    		rawselect[0].setName(getLeftListName());
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
                + rawselect[1].getName() + "," + "this.form."
                + rawselect[0].getName() + ")");
    }

    Map helper(Collection coll) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Map<Object, Object> map = new LinkedHashMap<Object, Object>();
		if (coll != null && !coll.isEmpty()) {
			for (Iterator it = coll.iterator(); it.hasNext();) {
				Object object = it.next();
				Object string1 = null;
				Object string2 = null;
				String str1 = this.Property2.substring(0, 1);
				getList = object.getClass().getMethod(
						"get" + str1.toUpperCase() +
								this.Property2.substring(1), (Class[]) null);
				string2 = getList.invoke(object, (Object[]) null);
				String str2 = this.Property1.substring(0, 1);
				getList = object.getClass().getMethod(
						"get" + str2.toUpperCase() +
								this.Property1.substring(1), (Class[]) null);
				string1 = getList.invoke(object, (Object[]) null);
				map.put(string1, string2);
			}
			return map;
		}
		return null;
	}

}
