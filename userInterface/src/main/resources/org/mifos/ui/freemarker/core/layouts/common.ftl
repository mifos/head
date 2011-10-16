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

[#-- This Freemarker template defines common macros use by all layout templates. --]

[#-- Renders the page header. --]
[#macro header pageTitle]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html dir="${Application.LocaleSetting.direction}">
    <head>
    <title id="${pageTitle}">[@spring.message "${pageTitle}" /]</title>
    <link href="pages/css/maincss.css" rel="stylesheet" type="text/css" />
    <link href="pages/css/gazelle.css" rel="stylesheet" type="text/css" />
    <link href="pages/css/screen.css" rel="stylesheet" type="text/css" />
    <link href="pages/css/main.css" rel="stylesheet" type="text/css" />
    <link href="pages/css/app.css" rel="stylesheet" type="text/css" />
    <link href="pages/css/screen-custom.css" rel="stylesheet" type="text/css" />
    <STYLE TYPE="text/css"><!-- @import url(pages/css/jquery/jquery-ui.css); --></STYLE>
    <STYLE TYPE="text/css"><!-- @import url(pages/css/datepicker/datepicker.css); --></STYLE>
    
    <link rel="shortcut icon" href="pages/framework/images/favicon.ico"/>
    
    <script type="text/javascript" src="pages/js/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="pages/js/jquery/jquery-ui.min.js"></script>
    <script type="text/javascript" src="pages/js/jquery/jquery.ui.datepicker.min.js"></script>
    <script type="text/javascript" src="pages/js/jquery/jquery-ui-i18n.js"></script>
    <script type="text/javascript" src="pages/js/jquery/jquery.datePicker.configuration.js"></script>
    <script type="text/javascript" src="pages/js/jquery/jquery.keyfilter-1.7.js"></script>
    <script type="text/javascript" src="pages/js/jquery/jquery.validate.min.js"></script>
    <script type="text/javascript" src="pages/framework/js/CommonUtilities.js"></script>
    </head>
    <body>
[/#macro]

[#-- Renders the page footer. --]
[#macro footer]
    </body>
    </html>
[/#macro]
