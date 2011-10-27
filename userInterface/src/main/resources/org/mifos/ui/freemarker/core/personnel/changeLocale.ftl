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
<br />
Current Language :
<select id="langId">
    [#list LOCALE_LIST as e]
    <option [#if CURRENT_LOCALE_ID == e.id] selected [/#if] value="${e.id}">${e.name}</option>
    [/#list]
</select>
<input id="langSubmit" type="button" value="Change" />
<br />
<br />
<b>NOTE:</b>
Some languages are not completely translated.
<a href="http://translatewiki.net/wiki/Translating:Mifos/stats"
    target="blank">stats</a>
<br />
<a
    href="http://mifosforge.jira.com/wiki/display/projects/Mifos+Localization"
    target="blank">Mifos Localization</a>
<br />
<a href="http://mifosforge.jira.com/wiki/display/MIFOS/i18n,+L10n"
    target="blank">Learn i18n/L10n</a>
