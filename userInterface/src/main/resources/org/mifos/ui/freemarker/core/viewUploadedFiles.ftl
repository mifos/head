[#ftl]
[#include "layout.ftl"]
[@clientLeftPane "ClientsAndAccounts"]
    <span id="page.id" title="UploadedFiles"></span>
    <div class="content">
        <a href="uploadNewFile.ftl?entityId=${entityId}&entityType=${entityType}&backPageUrl=${backPageUrl}">
        [@spring.message "upload.selectNewFile" /]
        </a>
        <br/><br/>
        <table id="uploadedFiles" class="datatable">
            <thead>
                <tr>
                    <th>
                        <span class='fontnormalbold'>
                        [@spring.message "upload.name" /]
                        <span>
                    </th>
                    <th>
                        <span class='fontnormalbold'>
                        [@spring.message "upload.size" /]
                        <span>
                    </th>
                    <th>
                        <span class='fontnormalbold'>
                        [@spring.message "upload.date" /]
                        <span>
                    </th>
                    <th>
                        <span class='fontnormalbold'>
                        [@spring.message "Description" /]
                        <span>
                    </th>
                    <th>
                        <span class='fontnormalbold'>
                        [@spring.message "upload.actions" /]
                        <span>
                    </th>
                </tr>
            </thead>
            <tbody>
            [#list uploadedFiles as file]
                <tr>
                    <td>
                    [#if file.name?length > 30]${file.name?substring(0,30)}...
                    [#else]${file.name}
                    [/#if]
                    </td>
                    <td>${file.size}</td>
                    <td>${file.uploadDate}</td>
                    <td>${file.description}</td>
                    <td>
                        [#assign question][@spring.message "upload.deleteConfirmQuestion" /][/#assign]
                        <a href="viewUploadedFiles.ftl?entityId=${entityId}&entityType=${entityType}&backPageUrl=${backPageUrl}&deleteFileId=${file.uploadedFileId}" onclick="return confirm('${question}')" >Delete</a> / 
                        <a href="viewUploadedFiles.ftl?entityId=${entityId}&entityType=${entityType}&backPageUrl=${backPageUrl}&downloadFileId=${file.uploadedFileId}">Download</a>
                    </td>
                </tr>
            [/#list]
            </tbody>
        </table>
        [@widget.datatable "uploadedFiles" /]
        [@form.returnToPage "${backPageUrl}" "button.back" "loanaccountpayments.button.back"/]
    </div>
[/@clientLeftPane]