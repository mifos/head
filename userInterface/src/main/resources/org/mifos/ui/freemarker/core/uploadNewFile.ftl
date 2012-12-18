[#ftl]
[#include "layout.ftl"]
[@layout.header "title" /]
[@clientLeftPane "ClientsAndAccounts"]
<span id="page.id" title="uploadNewFile"></span>
[@i18n.formattingInfo /]
<div class="content definePageMargin">
    <div class="borders margin20lefttop width90prc">
        <div class="borderbtm width100prc height25px">
            <p class="span-17 timelineboldorange arrowIMG  padding20left" style="width:50%">File information</p>
            <p class="span-3 timelineboldorange arrowIMG1 last padding20left10right width130px" style="float:right">[@spring.message "reviewAndSubmit"/]</p>
        </div>
        <div>
            <form method="POST" action="uploadNewFile.ftl?entityId=${entityId}&entityType=${entityType}&backPageUrl=${backPageUrl}" name="uploadNewFile" enctype="multipart/form-data">
                <div class="margin20lefttop">
                    <p class="font11pt">
                        <span class="fontBold">[@spring.message "upload.uploadNewFile" /]&nbsp;-</span>
                        <span class="orangeheading">[@spring.message "upload.chooseFileAndTypeDescription" /]</span>
                    </p>

                    <div class="font9pt">[@spring.message "upload.completethefieldsbelow.ThenclickPreview" /]</div>
                    <div class="font9pt"><span class="red">*</span>[@spring.message "fieldsmarkedwithanasteriskarerequired"/] </div>
                    [@form.showAllErrors "formBean.*"/]
                    <br/>

                    <div class="prepend-3  span-20 last">
                        <div class="span-20 ">
                            <span class="span-7 rightAlign">
                                <span class="red">* </span>[@spring.message "upload.file" /]
                            </span>
                            <span class="span-5">                                
                                [@spring.bind "formBean.file" /]
                                <input id="uploadNewFile.file" type="file" name="${spring.status.expression}" value="${spring.status.value?if_exists}"></input>
                            </span>
                        </div>
                        <div class="span-20 ">
                            <span class="span-7 rightAlign">
                                [@spring.message "Description"/]
                            </span>
                            <span class="span-5">
                            [@spring.bind "formBean.description" /]
                            <input name="${spring.status.expression}" value="${spring.status.value?if_exists}" size="40" maxlength="60" />
                            </span>
                        </div>
                    </div>
                    
                </div>
                <div class="clear">&nbsp;</div>
                <div class="buttonsSubmitCancel margin20leftright" >
                    <input class="buttn submit" type="submit" name="preview" value="[@spring.message "preview"/]"/>
                    <input class="buttn2" type="submit" name="CANCEL" value="[@spring.message "cancel"/]"/>
                </div>
                <div class="clear">&nbsp;</div>
            </form>
        </div>
    </div>
</div>
[@layout.footer/]
[/@clientLeftPane]