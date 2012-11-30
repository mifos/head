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

[#-- 
Lookup recurrence name given an ID. 
Defined in org.mifos.application.meeting.util.helpers.RecurrencyType
--]
[#macro recurrenceType recurrencyTypeId]
    [#-- WEEKLY(Short.valueOf("1")), '
    	 MONTHLY(Short.valueOf("2")), 
    	 DAILY(Short.valueOf("3")); --]
	[#if recurrencyTypeId == 1]
		[@spring.message "ftl.macro.lookup.recurrenceType.weekly" /]
	[#elseif recurrencyTypeId == 2]
		[@spring.message "ftl.macro.lookup.recurrenceType.monthly" /]
	[#elseif recurrencyTypeId == 3]
		[@spring.message "ftl.macro.lookup.recurrenceType.daily" /]
	[#else]
		[@spring.message "ftl.macro.lookup.recurrenceType.undefined" /]
	[/#if]	
[/#macro]

[#-- 
Lookup recurrence frequency given an ID. 
Defined in org.mifos.application.meeting.util.helpers.RecurrencyType
--]
[#macro recurrencyFrequence recurrencyTypeId]
    [#-- WEEKLY(Short.valueOf("1")), '
    	 MONTHLY(Short.valueOf("2")), 
    	 DAILY(Short.valueOf("3")); --]
	[#if recurrencyTypeId == 1]
		[@spring.message "ftl.macro.lookup.recurrenceFrequency.week" /]
	[#elseif recurrencyTypeId == 2]
		[@spring.message "ftl.macro.lookup.recurrenceFrequency.month" /]
	[#elseif recurrencyTypeId == 3]
		[@spring.message "ftl.macro.lookup.recurrenceFrequency.day" /]
	[#else]
		[@spring.message "ftl.macro.lookup.recurrenceFrequency.undefined" /]
	[/#if]	
[/#macro]

[#-- Single purpose macro that returns the localized name where recurring period is MONTH --]
[#macro recurringFrequencyMonth]
	[@spring.message "ftl.macro.lookup.recurrenceFrequency.month" /]
[/#macro]

[#macro recurringFrequencyDay]
	[@spring.message "ftl.macro.lookup.recurrenceFrequency.day" /]
[/#macro]