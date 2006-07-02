<!--
/**

* checklistDetails.jsp    version: 1.0



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
* 
*/
 -->



<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%@ taglib uri="/tags/mifos-html" prefix="mifos" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>



<html:html>
<title>Mifos </title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../cssstyle.css" rel="stylesheet" type="text/css">

</head>

<body>
<html:form  method="post" action="/submitCheckListForm">
<html:hidden property="dispatch" /> 

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="188" rowspan="2"><img src="../images/logo.gif" width="188" height="74"></td>
    <td align="right" bgcolor="#FFFFFF" class="fontnormal"><a href="../yourSettings.htm">Your Settings</a> &nbsp;|&nbsp; <a href="../login.htm">Logout</a>&nbsp;|&nbsp; <a href="#">Help</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  </tr>
  <tr>
    <td align="left" valign="bottom" bgcolor="#FFFFFF"><table border="0" cellspacing="1" cellpadding="0">
        <tr>
          <td class="tablightorange"><a href="../index.htm">Home</a></td>
          <td class="tablightorange"><a href="../clientsaccounts.htm">Clients &amp; Accounts </a></td>
          <td class="tablightorange"><a href="../reports.htm">Reports</a></td>
          <td class="taborange"><a href="../admin.htm" class="tabfontwhite">Admin</a></td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td colspan="2" class="bgorange"><img src="../images/trans.gif" width="6" height="6"></td>
  </tr>
  <tr>
    <td colspan="2" class="bgwhite"><img src="../images/trans.gif" width="100" height="2"></td>
  </tr>
</table>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="350" align="left" valign="top" bgcolor="#FFFFFF">      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td align="center" class="heading">&nbsp;</td>
        </tr>
    </table>              
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
          <tr>
            <td class="bluetablehead">  <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td>
                    <table border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td><img src="../images/timeline/bigarrow.gif" width="17" height="17"></td>
                        <td class="timelineboldorange">Checklist information</td>
                      </tr>
                    </table>
                  </td>
                  <td align="right"><table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td><img src="../images/timeline/orangearrow.gif" width="17" height="17"></td>
                      <td class="timelineboldorangelight">Review &amp; submit</td>
                    </tr>
                  </table></td>
                </tr>
              </table></td>
          </tr>
        </table>
        <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
          <tr>
            <td align="left" valign="top" class="paddingleftCreates">              <table width="93%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td class="headingorange"><span class="heading">Add new
                      checklist - </span>Enter checklist information</td>
                </tr>
                <tr>
                  <td class="fontnormal">Complete the fields below. Then click
                    Preview. Click Cancel to return to Admin without submitting
                    information. <br>
                    <font color="#FF0000">*</font>Fields marked with an asterisk are required. </td>
                </tr>
              </table>
              <br>
              <table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td colspan="2" class="fontnormalbold">Checklist details<br>
                      <br>
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right" valign="top">
				  
				  <mifos:mifoslabel name="Name:" mandatory="yes"/>
				  
				  </td>
                  <td valign="top">
				  



				 <html:text  property="name"  />

                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right" valign="top">

				  <mifos:mifoslabel name="Type:" mandatory="yes"/>
				  
				  </td>
                  <td valign="top">
				  <mifos:select property="type" style="width:136px;"  onchange="update_item_list ();">

				  <html:option value="Client">Client</html:option>
   				  <html:option value="Group">Group</html:option>
				  <html:option value="Loans">Loans</html:option>
				  <html:option value="Savings">Savings</html:option>
				  </mifos:select>
				  
				  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right" valign="top">

				 <mifos:mifoslabel name="Displayed when moving into Status:"  mandatory="yes"/>

				  </td>
                   <td valign="top">
				   <mifos:select property="status" style="width:136px;" >
					
				  <html:option value="Save for later">Save for later</html:option>
				   <html:option value="Submit for approval">Submit for approval</html:option>
				   <html:option value="Approved">Approved</html:option>
				   <html:option value="On hold">On hold</html:option>
				   <html:option value="Closed">Closed</html:option>
				   <html:option value="Cancelled">Cancelled
				   
				   
				   
				   </html:option>
				   
				   
				  
				   </mifos:select></td>
                </tr>
                <tr class="fontnormal">
                  <td align="right" valign="top">
				  <mifos:mifoslabel name="Status of checklist:" mandatory="yes"/>
				  
				  
				  </td>
                  <td valign="top">


				<mifos:select property="statuschecklist" style="width:136px;">
				   <html:option value="Active">Active</html:option>
				   <html:option value="Inactive">Inactive</html:option>
				   
				   </mifos:select>

				  
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td width="29%" align="right" valign="top">

				   <mifos:mifoslabel name="Items:" mandatory="yes"/>
				  
				  </td>
                  <td width="71%" valign="top"><table width="80%" border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td class="fontnormal">Enter text for each item and click
                          Add to save the item in the list below. </td>
                      </tr>
                      <tr>
                        <td><img src="../images/trans.gif" width="1" height="1"></td>
                      </tr>
                    </table>
                      <table width="86%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                          <td width="34%" valign="top">
						  
						  
						 <html:textarea  property="textarea" cols="50" rows="5">
						
							</html:textarea>


                          </td>
                          <td width="66%" valign="top" class="paddingleft05notop">
						  
						  
						  
						  
						  
						<html:button  property="method" styleClass="insidebuttn"  style="width:65px" onclick="valid(this.form)">

						<bean:message key="button.add"/>
					
						</html:button>



                          </td>

                        </tr>
                      </table>
                      <br>
                      <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr class="fontnormal">

                          <div id="myDiv"></div>
 
						  
						  
						  <br>
						  
						  
						  </td>


                        </tr>

						<tr valign="top" class="fontnormal">


                          <td colspan="2"><input name="Button33222" type="button" class="insidebuttn" value="Remove Selected" style="width:120px" onClick="RemoveSelected()">


                          </td>
                        </tr>
                        
                    </table>   
                  </td>
                </tr>
              </table>
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center" class="blueline">&nbsp; </td>
                </tr>
              </table>
              <br>
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">
				  
				   <html:submit property="method" style="width:70px" styleClass="buttn" onclick="validateFields(this.form);">

					<bean:message key="button.preview"/>
					
					</html:submit>


&nbsp;
     <html:cancel property="method" style="width:70px" styleClass="cancelbuttn" >

					<bean:message key="button.cancel"/>
					
					</html:cancel>
                  </td>
                </tr>
              </table>
              <br></td>
          </tr>
        </table>
      <br></td>
  </tr>
</table>
</html:form>
</body>
</html:html>

