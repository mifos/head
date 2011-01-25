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

[@mifos.header "login" /]

    <div id="login-page-header">
    </div>

    <div class="page-content">

<h1>Welcome to Mifos</h1>

             <form method="POST" action="j_spring_security_check" id="login.form">

                    <div class="error-messages">
                      [#assign error=model.request.getParameter("error")!""]
                      [#if error =="true" ]
                        <span id="login.errorcaption">Login error:</span> <span id="login.errormessage">${model.request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION").message}</span><br/>
                        </font>
                      [/#if ]
                    </div>

    <fieldset class="login">
    <legend>Login</legend>
        <label for="login.form.username" accesskey="n">[@spring.message "UserName" /]:</label>
            <input type="text" name="j_username" id="login.form.username" tabindex="1" value="" title="User Name" /><br>
        <label for="login.form.password" accesskey="l">Password: </label>
            <input type="password" id="login.form.password" name="j_username" tabindex="2" title="Password" /><br>
        <label for="kludge"></label>
            <input type="submit" value="Login" class="buttn" id="login.form.submit" tabindex="3" />
    </fieldset>
                    
    </div>
[@mifos.footer /]

   