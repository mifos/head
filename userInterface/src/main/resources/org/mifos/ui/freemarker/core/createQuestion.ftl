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
<html lang="EN">
<head>
    <title>Mifos</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <link href='pages/framework/css/cssstyle.css' rel="stylesheet" type="text/css">
    <script src="pages/application/surveys/js/questions.js" type="text/javascript"></script>
</head>
<body onload="disableSubmitButtonOnEmptyQuestionList()">
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
            <span id="page.id" title="createQuestion"/>
            <script src="pages/application/surveys/js/questions.js" type="text/javascript"></script>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td class="bluetablehead05">
                        <span class="fontnormal8pt"> <a href="AdminAction.do?method=load">Admin</a> / </span>
                        <span class="fontnormal8ptbold"> Add Questions </span>
                    </td>
                </tr>
            </table>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td align="left" valign="top" class="paddingL15T15">
                        <table width="95%" border="0" cellpadding="3" cellspacing="0">
                            <tr>
                                <td class="headingorange"><span class="headingorange"> Add Questions </span></td>
                            </tr>
                            <tr class="fontnormal">
                                <td align="left" valign="top" class="paddingL15T15">
                                        <form name="createquestionform"
                                          action="createQuestion.ftl?execution=${flowExecutionKey}" method="POST"
                                          focus="questionText">
                                        <table width="98%" border="0" cellpadding="3" cellspacing="0">
                                            <tr class="fontnormal">
                                                <td width="24%" align="right"><span class="mandatorytext"><font
                                                        color="#FF0000">*</font></span>Question Title:
                                                </td>
                                                <td width="76%">
                                                    <script src="pages/framework/js/func.js"></script>
                                                    <script src="pages/framework/js/func_en_GB.js"></script>
                                                    [@spring.formInput "questionDefinition.title",
                                                    'maxlength="50"
                                                    onkeypress="return FnCheckNumCharsOnPress(event,this);"
                                                    onblur="return FnCheckNumChars(event,this);return
                                                    FnEscape(event,this)"' /]
                                                    <br/>
                                                    [@spring.showErrors "<br/>","fontnormalRedBold" /]
                                                </td>
                                            </tr>
                                            <tr class="fontnormal">
                                                <td width="24%" align="right"><span class="mandatorytext"><font
                                                        color="#FF0000">*</font></span>Answer type
                                                </td>
                                              <td>
                                                [@spring.formSingleSelect "questionDefinition.type", ["Free text", "Date"], 'styleId="answerType" onchange="setDisable();"' /]
                                                [@spring.showErrors "<br>","fontnormalRedBold" /]
                                              </td>
                                            </tr>

                                            <tr>
                                                <td>&nbsp;</td>
                                                <td>&nbsp;</td>
                                            </tr>
                                            <tr>
                                                <td>&nbsp;</td>
                                                <td>
                                                    <input type="submit" name="_eventId_addQuestion"
                                                           value="Add Question"
                                                           class="buttn" id="_eventId_addQuestion">
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="2" class="blueline" align="left">&nbsp;</td>
                                            </tr>
                                        </table>
                                        <table id="questions.table" name="questions.table" width="98%" border="0" cellpadding="3" cellspacing="0">
                                                      <tr>
                                                        <td class="drawtablehd">Question Title</td>
                                                        <td class="drawtablehd">Answer type</td>
                                                      </tr>
                                                      [#list questionDefinition.questions as question]
                                                      <tr>
                                                          <td class="drawtablerow">${question.title}</td>
                                                          <td class="drawtablerow">${question.type}</td>
                                                      </tr>
                                                      [/#list]
                                                      <tr>
                                                        <td class="drawtablerow">&nbsp;</td>
                                                        <td class="drawtablerow">&nbsp;</td>
                                                      </tr>
                                                    </table>
                                        <br>
                                        <table width="93%" border="0" cellpadding="0" cellspacing="0">
                                            <tr>
                                                <td align="center">
                                                    <input type="submit" id="_eventId_createQuestions" name="_eventId_createQuestions"
                                                           value="Submit"
                                                           class="buttn">
                                                </td>
                                                <td align="center">
                                                    <input type="submit" id="_eventId_cancel" name="_eventId_cancel"
                                                           value="Cancel"
                                                           class="cancelbuttn">
                                                </td>
                                            </tr>
                                        </table>
                                    </form>
                                    <br>
                                </td>
                        </table>
                        <br>
                    </td>
                </tr>
            </table>
            <br>
            <input type="hidden" name="h_user_locale" value="en_GB">
        </td>
    </tr>
</table>
</body>
</html>
