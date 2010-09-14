[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-8 completeIMG silverheading ">[@spring.message "systemUsers.preview.chooseOffice"/]</p>
        <p class="span-8 completeIMG silverheading ">[@spring.message "systemUsers.preview.userInformation"/]</p>
        <p class="span-3 arrowIMG orangeheading last">[@spring.message "review&Submit"/]</p>
      </div>
      <div class="subcontent ">
      <form method="post" name="formname" action="user.ftl?execution=${flowExecutionKey}">
        <p class="font15"><span class="fontBold">[@spring.message "systemUsers.preview.addanewuser"/]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "review&Submit"/]</span></p>
        <div>[@spring.message "systemUsers.preview.reviewtheinformationbelow"/]</div>
        <p>&nbsp;</p>
        <p><span class="fontBold">[@spring.message "systemUsers.preview.office"/] </span><span>&nbsp;</span></p>
        <div><span class="orangeheading">[@spring.message "systemUsers.preview.userInformation"/] </span><span>&nbsp;</span></div>
        <div class="clear">&nbsp;</div>
        <div class="prepend-1  span-21 last">
        	<div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.firstName"/]&nbsp;</span><span></span>
  			</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.middleName"/]&nbsp;</span><span></span>
  			</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.secondLastName"/]&nbsp;</span><span></span>
			</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.lastName"/]&nbsp;</span><span></span>
  			</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.governmentID"/]&nbsp;</span><span></span> 
  			</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.email"/]&nbsp;</span><span></span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.dateofBirth"/]&nbsp;</span><span> </span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.age"/]&nbsp;</span><span></span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.maritalStatus"/]&nbsp;</span><span></span>
  			</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.gender"/]&nbsp;</span><span></span>
  			</div>
            <div class="clear">&nbsp;</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.languagePreferred"/]&nbsp;</span><span></span>
  			</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.mFIJoiningDate"/]:&nbsp;</span><span> </span>
  			</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.status"/]&nbsp;</span><span></span>
            </div>
            <div class="clear">&nbsp;</div>
        	<div class="clear">&nbsp;</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.address"/]&nbsp;</span><span></span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.city/District"/]&nbsp;</span><span></span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.state"/]&nbsp;</span><span>  </span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.country"/]&nbsp;</span><span> </span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.postalCode"/]&nbsp;</span><span></span>
            </div>
            <div class="clear">&nbsp;</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.telephone"/]&nbsp;</span><span></span>
            </div>
            <div class="clear">&nbsp;</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.office&Permissions"/]</span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.office"/]&nbsp;</span><span></span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.userTitle"/]&nbsp;</span><span></span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.userHierarchy"/]&nbsp;</span><span>	</span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.roles"/]&nbsp;</span><span></span>
            </div>
            <div class="clear">&nbsp;</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.loginInformation"/]</span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.userName"/]&nbsp;</span><span></span>
            </div>
         	<div class="clear">&nbsp;</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.additionalInformation"/]</span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.externalId"/]&nbsp;</span><span></span>
            </div>
            
            <div class="clear">&nbsp;</div>
            	<input class="buttn2" type="submit" name="_eventId_reedit" value="Edit personal information" />
        	</div>
            
            <div class="clear">&nbsp;</div>
            <hr />
            <div class="clear">&nbsp;</div>
            <div class="prepend-9">
          		<input class="buttn" type="submit" name="_eventId_submit" value="Submit" />
          		<input class="buttn2" type="submit" name="_eventId_cancel" value="Cancel" />
        	</div>
        </form>
      </div>
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]