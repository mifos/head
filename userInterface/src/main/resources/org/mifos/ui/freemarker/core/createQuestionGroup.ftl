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
[#import "macros.ftl" as mifos]
<html lang="EN">
<head>
    <title>Mifos</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <link href='pages/framework/css/cssstyle.css' rel="stylesheet" type="text/css">
<script type="text/javascript">
    function removeSection(sectionName){
        var sectionToDeleteBtn = document.getElementById('_eventId_deleteSection');
        sectionToDeleteBtn.value = sectionName;
        sectionToDeleteBtn.click();
    }
</script>
      <style type="text/css">
        .normalFontFixedDiv {
            color:#000000;
            font-family:Arial,Verdana,Helvetica,sans-serif;
            font-size:9pt;
            font-weight:normal;
            text-decoration:none;
            width:400px;
        }
        fieldset {
            float: left;
            clear: left;
            width: 100%;
            margin: 0 0 1.5em 0;
            padding: 0;
            border: none;
        }
        legend {
            margin-left: 1em;
            color: #000000;
            font-weight: bold;
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
        fieldset.submit {
            float: none;
            width: auto;
            border: 0 none #FFF;
            padding-left: 12em;
        }
        label {
            float: left;
            width: 10em;
            margin-right: 1em;
            text-align: right;
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
            <span id="page.id" title="createQuestionGroup"></span>
            <script src="pages/application/surveys/js/questions.js" type="text/javascript"></script>
            <table width="100%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td class="bluetablehead05">
                        <span class="fontnormal8pt"> <a href="AdminAction.do?method=load">Admin</a> / </span>
                        <span class="fontnormal8ptbold"> Add Question Group </span>
                    </td>
                </tr>
            </table>
            <form name="createquestiongroupform"
                  action="createQuestionGroup.ftl?execution=${flowExecutionKey}" method="POST">
                <div>
                    <span class="headingorange"> Add Question Group </span>
                </div>
                <div>
                    [@spring.formHiddenInput "questionGroupDefinition.id"/]
                    [@spring.showErrors "<br/>","fontnormalRedBold" /]
                </div>
                <div class="normalFontFixedDiv">
                    <fieldset>
                        <ol>
                            <li>
                                <span class="mandatorytext"><font color="#FF0000">* </font></span>
                                <label for="title">Question Group Title:</label>
                                [@spring.formInput "questionGroupDefinition.title",
                                'maxlength="50"
                                onkeypress="return FnCheckNumCharsOnPress(event,this);"
                                onblur="return FnCheckNumChars(event,this);return FnEscape(event,this)"'/]
                                [@spring.showErrors "<br/>","fontnormalRedBold" /]
                            </li>
                            <li>
                                <span class="mandatorytext"><font color="#FF0000">* </font></span>
                                <label for="eventSourceId">Applies To:</label>
                                [@mifos.formSingleSelectWithPrompt "questionGroupDefinition.eventSourceId", EventSources, "--select one--" /]
                                [@spring.showErrors "<br>","fontnormalRedBold" /]
                            </li>
                            <li>
                                <label for="sectionName">Section Heading:&nbsp;&nbsp;</label>
                                [@spring.formInput "questionGroupDefinition.sectionName",
                                'maxlength="50"
                                onkeypress="return FnCheckNumCharsOnPress(event,this);"
                                onblur="return FnCheckNumChars(event,this);return FnEscape(event,this)"'/]
                                [@spring.showErrors "<br/>","fontnormalRedBold" /]
                            </li>
                        </ol>
                    </fieldset>
                    <fieldset id="submitSection" class="submit">
                        <input type="submit" name="_eventId_addSection" id="_eventId_addSection"
                               value="Add Section"
                               class="buttn">
                    </fieldset>
                </div>

                <div id="divSections">
                    [#list questionGroupDefinition.sections as section]
                    <b>${section.name}:&nbsp;&nbsp;</b><a href="javascript:removeSection('${section.name}')">remove</a>
                    <br/>
                    <table width="100%" id="sections.table" name="sections.table" border="0"
                           cellpadding="3" cellspacing="0">
                        <tr>
                            <td class="drawtablehd">Question Name</td>
                            <td class="drawtablehd">Mandatory</td>
                            <td class="drawtablehd">Delete</td>
                        </tr>
                        <tr>
                            <td class="drawtablerow">&nbsp;</td>
                            <td class="drawtablerow">&nbsp;</td>
                            <td class="drawtablerow">&nbsp;</td>
                        </tr>
                    </table>
                    [/#list]
                    <input type="submit" id="_eventId_deleteSection" name="_eventId_deleteSection" value="" style="visibility:hidden">
                </div>

                <div id="divSumitQG">
                    <fieldset class="submit">
                        <input type="submit" name="_eventId_defineQuestionGroup"
                               id="_eventId_defineQuestionGroup" value="Submit" class="buttn">
                        &nbsp;
                        <input type="submit" name="_eventId_cancel" id="_eventId_cancel" value="Cancel"
                               class="cancelbuttn">
                    </fieldset>
                </div>
            </form>

            <br>
            <input type="hidden" name="h_user_locale" value="en_GB">
        </td>
    </tr>
</table>
</body>
</html>
