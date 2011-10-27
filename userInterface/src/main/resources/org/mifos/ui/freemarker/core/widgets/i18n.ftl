[#ftl]
[#--
* Copyright (c) 2005-2011 Grameen Foundation USA
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

[#-- Directive for filtering text with String.format() and MarkdownJ. --]
[#-- Useful (and very specific) for parsing i18n messages containing links. --]
[#-- usage [@i18n.mlink dest="viewQuestions.ftl"][@spring.message "view.questions.mlink"][/@i18n.mlink] --]
[#assign mlink="org.mifos.ui.ftl.MarkdownLinker"?new()]

[#-- Template method for localized formatting of Java or Joda dates --]
[#-- usage: ${i18n.date_formatter(monthlyCashFlow.dateTime, "MMMM", Application.LocaleSetting.locale)} --]
[#assign date_formatter="org.mifos.ui.ftl.DateFormatter"?new()]
