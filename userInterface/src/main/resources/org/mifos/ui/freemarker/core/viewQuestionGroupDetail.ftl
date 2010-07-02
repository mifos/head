[#ftl]
[#--
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
--]
[#import "spring.ftl" as spring]

<html lang="EN" xmlns:html-el="http://www.w3.org/1999/xhtml" xmlns:html-el="http://www.w3.org/1999/xhtml"
      xmlns:c="http://www.springframework.org/schema/webflow" xmlns:c="http://www.w3.org/1999/XSL/Transform"
      xmlns:fmt="http://jboss.org/xml/ns/javax/validation/mapping" xmlns:fmt="http://java.sun.com/JSP/Page">
<head>
    <title>Mifos</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <link href='pages/framework/css/cssstyle.css' rel="stylesheet" type="text/css">
    <style type="text/css">
        .clsHeaderBg {
            background-color:#D7DEEE;
            width:100%;
        }
        fieldset {
            float: left;
            clear: left;
            width: 100%;
            margin: 0 0 1.5em 0;
            padding: 0;
            border: none;
        }
        fieldset ol {
            padding: 1em 1em 0 1em;
            list-style: none;
        }
        fieldset li {
            float: left;
            clear: left;
            width: 100%;
            padding-bottom: 1em;
        }
    </style>
</head>

<body>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td width="188" rowspan="2"><img src="pages/framework/images/logo.gif" width="188" height="74"></td>
        <td align="right" bgcolor="#FFFFFF" class="fontnormal"><a id="header.link.yoursettings"
                                                                  href="yourSettings.do?method=get">Your settings</a>

            &nbsp;|&nbsp; <a id="header.link.logout" href="javascript:fnLogout()">Logout</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        </td>
    </tr>
    <tr>
        <td align="left" valign="bottom" bgcolor="#FFFFFF">
            <table border="0" cellspacing="1" cellpadding="0">
                <tr>
                    <td class="tablightorange"><a id="header.link.home" href="custSearchAction.do?method=getHomePage">Home</a>
                    </td>
                    <td class="tablightorange"><a id="header.link.clientsAndAccounts"
                                                  href="custSearchAction.do?method=loadMainSearch">Clients &
                        Accounts </a></td>

                    <td class="tablightorange"><a id="header.link.reports"
                                                  href="reportsAction.do?method=load">Reports</a></td>
                    <td class="taborange"><a id="header.link.admin" href="AdminAction.do?method=load"
                                             class="tabfontwhite">Admin</a></td>
                </tr>
            </table>

        </td>
    </tr>
    <tr>
        <td colspan="2" class="bgorange"><img src="pages/framework/images/trans.gif" width="6" height="6"></td>
    </tr>
    <tr>
        <td colspan="2" class="bgwhite"><img src="pages/framework/images/trans.gif" width="100" height="2"></td>
    </tr>
</table>




<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td width="174" height="500" align="left" valign="top" class="bgorangeleft">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td class="leftpanehead" colspan="2">Administrative tasks</td>
                </tr>
                <tr>
                    <td class="leftpanelinks">
                        <form name="custSearchActionForm" method="post"
                              action="/mifos/custSearchAction.do?method=loadAllBranches">
                            <table width="90%" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td class="paddingbottom03">
                                        <span class="fontnormal8ptbold">Search by client name, system ID or account number</span>
                                    </td>
                                </tr>
                            </table>
                            <table width="90%" border="0" cellpadding="0" cellspacing="0">
                                <tr>
                                    <td width="100%" colspan="2">
                                        <input type="text" name="searchString" maxlength="200" size="20" value="">
                                        <input type="hidden" name="searchNode(search_officeId)" value="0">
                                        <input type="hidden" name="officeName" value="">
                                    </td>
                                </tr>
                            </table>
                            <table width="143" border="0" cellspacing="0" cellpadding="10">
                                <tr>
                                    <td align="right">
                                        <input type="submit" name="searchButton" value="Search" class="buttn">
                                    </td>
                                </tr>
                            </table>
                        </form>
                    </td>
                </tr>
            </table>
        </td>
        <td align="left" valign="top" bgcolor="#FFFFFF" class="paddingleftmain" height="500">
            <span id="page.id" title="view_question_groups_details"></span>

            <div id="divBreadCrumb" class="clsHeaderBg">
              <span class="fontnormal8pt">
                  <a href="AdminAction.do?method=load">Admin</a> /
              </span>
              <span class="fontnormal8pt">
                  <a href="viewQuestionGroups.ftl">View Question Groups</a> /
              </span>
              [#if error_message_code??]
                [@spring.message error_message_code/]
              [#else]
                 <span class="fontnormal8ptbold">
                   ${Request.questionGroupDetail.title}
                 </span>
              [/#if]
            </div>
            <div id="divQuestionGroup">
                <div id="divQGHeader" style="padding:3px" class="headingorange">
                    [#if !error_message_code??]
                    ${Request.questionGroupDetail.title}
                    [/#if]
                    <br/>
                </div>
                <div id="divQGContents"  class="fontnormal">
                     <fieldset>
                        <ol>
                            <li id="question.group.applies.to">
                               Question group applies to: ${Request.eventSources[Request.questionGroupDetail.eventSourceId]}
                            </li>
                        </ol>
                    </fieldset>
                     <div id="divSections">
                         [#list Request.questionGroupDetail.sections as section]
                         ${section.name}
                         <br/>
                         <table width="100%" id="sections.table" name="sections.table" border="0"
                                cellpadding="3" cellspacing="0">
                             <tr>
                                 <td class="drawtablehd">Question Name</td>
                                 <td class="drawtablehd">Mandatory</td>
                             </tr>
                             <tr>
                                 <td class="drawtablerow">&nbsp;</td>
                                 <td class="drawtablerow">&nbsp;</td>
                             </tr>
                         </table>
                         [/#list]
                     </div>
                </div>
            </div>

        </td>
    </tr>
</table>
</body>
</html>