<!--
/**

* admin.jsp    version: 1.0



* Copyright © 2005-2006 Grameen Foundation USA

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
 -->
<%@ taglib uri="/tags/struts-html-el" prefix="html" %>


<html:html locale="true">
<head>
<title>Mifos </title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="css/cssstyle.css" rel="stylesheet" type="text/css">
</head>

<body>
<html:form method="post" action="centerAction.do">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="188" rowspan="2"><img src="images/logo.gif" width="188" height="74"></td>
    <td align="right" bgcolor="#FFFFFF" class="fontnormal"><html:link href="yourSettings.htm">Your Settings</html:link> &nbsp;|&nbsp;  <html:link href="login.htm">Logout</html:link> &nbsp;|&nbsp; <html:link href="#">Help</html:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  </tr>
  <tr>
    <td align="left" valign="bottom" bgcolor="#FFFFFF"><table border="0" cellspacing="1" cellpadding="0">
        <tr>
          <td class="tablightorange"><html:link href="index.htm">Home</html:link></td>
          <td class="taborange"><html:link styleClass="tabfontwhite" href="clientsaccounts.htm">Clients &amp; Accounts </html:link></td>
          <td class="tablightorange"><html:link href="reports.htm">Reports</html:link></td>
          <td class="tablightorange"><html:link href="admin.htm">Admin</html:link></td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td colspan="2" class="bgorange"><img src="images/trans.gif" width="6" height="6"></td>
  </tr>
  <tr>
    <td colspan="2" class="bgwhite"><img src="images/trans.gif" width="100" height="2"></td>
  </tr>
</table>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="174" height="350" align="left" valign="top" class="bgorangeleft"><table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td class="leftpanehead">Clients & Accounts Tasks</td>
        </tr>
        <tr>
          <td class="leftpanelinks"><span class="fontnormalbold">Manage collection
              sheets</span><br>
                          <html:link href="#">Print collection cheets</html:link><br>
                          <html:link href="#">Enter collection sheet data</html:link><br>
                          <br>
                          <span class="fontnormalbold">Create new clients</span><br>
                          
                          <html:link action="centerAction.do?method=load&office.officeId=20">Create new center</html:link><br> 
                          <html:link href="GroupAction.do?method=hierarchyCheck&office.officeId=5">Create new group</html:link><br>
                        
                          <html:link action="GroupAction.do?method=hierarchyCheck&office.officeId=5">Create new group w/o
                          center</html:link><br>
                          <html:link action="clientCreationAction.do?method=load&office.officeId=5&isClientUnderGrp=1">Create new client</html:link><br>
                          <br>
                          <span class="fontnormalbold">Create new accounts</span><br>
                          <html:link href="#">Create savings account</html:link><br>
                          <html:link href="#">Create insurance account</html:link><br>
                        <html:link href="CreateLoanAccountSearch.htm">Create loan account</html:link></td>
        </tr>
      </table>
    </td>
    <td height="400" align="left" valign="top" bgcolor="#FFFFFF" class="paddingleftmain">        <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL10" >
            <table width="90%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="left" valign="top"><span class="headingorange">Head
                    Office<br>
                  </span><span class="fontnormalbold">To review, edit a client,
                  center, group or account</span></td>
              </tr>
            </table>
            <br>
            <table width="90%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td width="313" valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="4">
                  <tr class="fontnormal">
                    <td width="100%" colspan="2" class="bglightblue"><span class="heading">Search</span></td>
                  </tr>
                </table>
                <table width="90%" border="0" cellspacing="0" cellpadding="4">
                  <tr>
                    <td class="paddingbottom03"><span class="fontnormal">Search
                        by name, system ID or account number </span> </td>
                  </tr>
                </table>
                  </td>
                <td width="101" align="center" valign="top" class="headingorange">or</td>
                <td width="287" valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="4">
                  <tr class="fontnormal">
                    <td width="100%" colspan="2" class="bglightblue"><span class="heading">Select
                        a branch</span></td>
                  </tr>
                </table>                  
                          <div id="Layer2" style="border-top: 1px solid #CECECE; border-bottom: 1px solid #CECECE; border-left: 1px solid #CECECE; height:100px; width:100%; overflow: auto; padding:6px; margin-top:5px;"> <span class="fontnormal">
                                  <html:link href="branchSearch.htm">Branch 01</html:link><br>
                                  <html:link href="branchSearch.htm">Branch 02</html:link><br>
                                  <html:link href="branchSearch.htm">Branch 03</html:link><br>
                                  <html:link href="branchSearch.htm">Branch 04</html:link><br>
                                  <html:link href="branchSearch.htm">Branch 05</html:link><br>
                              	  <html:link href="branchSearch.htm">Branch 06</html:link></span></div>
                </td>
              </tr>
            </table>
            <br>
            <br>
          </td>
        </tr>
      </table>
      <br></td>
  </tr>
</table>
</html:form>
</body>
</html:html>

