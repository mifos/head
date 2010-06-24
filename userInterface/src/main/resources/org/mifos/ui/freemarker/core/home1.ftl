[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Reports" /]
    <!--  Left Sidebar Begins-->
  <div class="sidebar ht750" >
    <p class="orangetab">[@spring.message "quickStart"/]</p>
    <p class="paddingLeft"><span class="fontBold">[@spring.message "manageClients"/]</span><br />
      <a href="newGroup.html">[@spring.message "createnewGroup"/]</a><br />
      <a href="newClient.html">[@spring.message "createNewClient"/]</a> </p>
    <p class="paddingLeft"><span class="fontBold">[@spring.message "manageAccounts"/]</span><br />
      <a href="newLoan.html">[@spring.message "openNewLoanAccount"/]</a><br />
      <a href="newSavings.html">[@spring.message "opennewSavingsAccount"/]</a><br />
      <a href="collectionData.html">[@spring.message "enterCollectionSheetData"/]</a> </p>
  </div>
  <!--  Left Sidebar Ends-->
  
  <!--  Main Content Begins-->
  <div class=" content leftMargin180">
    <div class="span-22">
    	<div class=" orangeheading font14">[@spring.message "welcomeToMifos"/]</div>
    	<p ><span>[@spring.message "thelasttimeyouloggedonwas08/02/10"/] </span></p>
    	<div class="fontBold"> [@spring.message "youcannavigateyourwaythroughMifosusing"/]</div>
    	<p class="span-5 paddingLeft leftIMG">[@spring.message "thelinkstotheleft"/]</p>
    	<p class="span-5 paddingLeft downIMG">[@spring.message "thesearchtoolbelow"/]</p>
    	<p class="span-5 paddingLeft topIMG last">[@spring.message "thetabsatthetop"/]</p>
    	<div class="clear" >&nbsp;</div>
    	<div>
      		<div class="fontBold">[@spring.message "toquicklyfindAClientCenterGroupaccountyoucan"/] </div>
      		<div>[@spring.message "searchbynamesystemIDoraccountnumber"/]</div>
      		<div><input type="text" id="txt"  /></div>
      		<div><input class="buttn" type="button" name="search" value="[@spring.message "search"/]" onclick="" /></div>
    	</div>
    </div>
  </div>
  <!--Main Content Ends-->
  
  
  
  [@mifos.footer/]