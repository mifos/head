[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
    <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-17 completeIMG silverheading">[@spring.message "offices.previewNewOffice.officeinformation"/]</p>
        <p class="span-3 arrowIMG orangeheading last">[@spring.message "review&Submit"/]</p>
      </div>
      <div class="subcontent ">
      <form method="" action="" name="formname">
        <p class="font15"><span class="fontBold">[@spring.message "offices.previewNewOffice.addanewoffice"/]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "review&Submit"/]</span></p>
        <p>&nbsp;&nbsp;</p>
        <div class="orangeheading">[@spring.message "offices.previewNewOffice.officeinformation"/]</div>
        <div class="prepend-1  span-21 last">
        	<div class="span-20 "><span class="span-3 fontBold">[@spring.message "offices.previewNewOffice.officeName"/]</span>
    				<span>&nbsp;</span>
  			</div>
            <div class="span-20 "><span class="span-3 fontBold">[@spring.message "offices.previewNewOffice.officeshortname"/]</span>
    				<span>&nbsp;</span>
  			</div>
            <div class="span-20 "><span class="span-3 fontBold">[@spring.message "offices.previewNewOffice.officetype"/]</span>
    				<span></span>
  			</div>
            <div class="span-20 "><span class="span-3 fontBold">[@spring.message "offices.previewNewOffice.parentOffice"/]</span>
    				<span>&nbsp;</span>
  			</div>
        </div>
        <p>&nbsp;</p>
        <div class="prepend-1  span-21 last">
        	<div class="span-20 "><span class="span-3 fontBold">[@spring.message "offices.previewNewOffice.officeaddress"/]</span><br />
    				<span></span>
  			</div>

            <div class="span-20 "><span class="span-3 fontBold">[@spring.message "offices.previewNewOffice.cityorDistrict"/]</span>
    				<span>&nbsp;</span>
  			</div>
            <div class="span-20 "><span class="span-3 fontBold">[@spring.message "offices.previewNewOffice.state"/]</span>
    				<span>&nbsp;</span>
  			</div>
            <div class="span-20 "><span class="span-3 fontBold">[@spring.message "offices.previewNewOffice.country"/]</span>
    				<span>&nbsp;</span>
  			</div>
            <div class="span-20 "><span class="span-3 fontBold">[@spring.message "offices.previewNewOffice.postalCode"/]</span>
    				<span>&nbsp;</span>
  			</div>
            <div class="span-20 "><span class="span-3 fontBold">[@spring.message "offices.previewNewOffice.telephone"/]</span>
    				<span>&nbsp;</span>
  			</div>
            <div class="clear">&nbsp;</div>
            <div> <input class="buttn2" type="button" name="edit" value="[@spring.message "offices.editOfficeInformation.editofficeinformation"/]" onclick="location.href='editOfficeInformation.ftl'"/>
            </div>
        </div>
        
        <div class="clear">&nbsp;</div>
        <hr />
        <div class="prepend-8">
          <input class="buttn" type="button" name="submit" value="[@spring.message "submit"/]" onclick="#"/>
          <input class="buttn2" type="button" name="cancel" value="[@spring.message "cancel"/]" onclick="window.location='admin.ftl'"/>
        </div>
        <div class="clear">&nbsp;</div>
        </form>
      </div><!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
   [@mifos.footer/]