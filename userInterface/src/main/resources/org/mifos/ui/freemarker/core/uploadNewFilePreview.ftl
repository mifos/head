[#ftl]
[#include "layout.ftl"]
[@layout.header "title" /]
[@clientLeftPane "ClientsAndAccounts"]
<span id="page.id" title="uploadNewFilePreview"></span>
[@i18n.formattingInfo /]
<div class="content definePageMargin">
    <div class="borders margin20lefttop width90prc">
        <div class="borderbtm width100prc height25px">
            <p class="span-17 completeIMG silverheading padding20left"
               style="width:50%">File information</p>

            <p class="span-3 timelineboldorange arrowIMG last padding20left10right width130px"
               style="float:right">[@spring.message "reviewAndSubmit"/]</p>
        </div>
        <div>
            <form method="post" action="uploadNewFilePreview.ftl?entityId=${entityId}&entityType=${entityType}&backPageUrl=${backPageUrl}" name="uploadNewFilePreview" enctype="multipart/form-data">
                <div class="margin30left20top">
                    <p class="font15"><span
                            class="fontBold">[@spring.message "upload.uploadNewFile" /]</span>&nbsp;-&nbsp;<span
                            class="orangeheading">[@spring.message "upload.chooseFileAndTypeDescription" /]</span></p>

                    <div>
                        [@spring.message "upload.reviewSelectedFile" /]
                    </div>
                    <br/>
                                    [@spring.bind "formBean"/]
                [@form.showAllErrors "formBean.*"/]
                <div class="marginLeft30">
                        <ul class="error">
                        [#if error?exists]
                            <li><b>[@spring.message error?default("")/] </b></li>
                        [/#if]
                        [#if fileExists][@spring.message "upload.fileExistsWillBeOverwritten" /][/#if]
                        </ul>
                    </div>
                    <div class="span-21 last">
                        <div class="span-20">
                            <span class="span-6 fontBold">[@spring.message "upload.name" /]</span>
                            <span class="span-4">${formBean.file.originalFilename}</span>
                        </div>
                        <div class="span-20 ">
                            <span class="span-6 fontBold">[@spring.message "Description" /]:</span>
                            <span class="span-4">${formBean.description}</span>
                        </div>
                    </div>
                </div>
                <div class="clear">&nbsp;</div>
                <div class="buttonsSubmitCancel margin20leftright">
                    <input class="buttn" type="submit" name="SUBMIT" value="[@spring.message "submit"/]"/>
                    <input class="buttn2" type="submit" name="CANCEL" value="[@spring.message "cancel"/]"/>
                </div>
                <div class="clear">&nbsp;</div>
            </form>
        </div>
    </div>
</div>
[@layout.footer/]
[/@clientLeftPane]