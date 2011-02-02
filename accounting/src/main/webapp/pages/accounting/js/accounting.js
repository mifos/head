    function addDatePicker() {
        var format = 'yy-mm-dd';
        $(function() {
            $("#fromDate").datepicker({
                dateFormat: format,
                showOn: "button",
                buttonImage: "pages/framework/images/mainbox/calendaricon.gif",
                buttonImageOnly: true
            });
            $("#fromDate").val(todayDate())
        });
        $(function() {
            $("#toDate").datepicker({
                dateFormat: format,
                showOn: "button",
                buttonImage: "pages/framework/images/mainbox/calendaricon.gif",
                buttonImageOnly: true
            });
            $("#toDate").val(todayDate())
        });
    }

    function todayDate() {
        return new Date().getFullYear() + "-" + (new Date().getMonth() + 1) + "-" + new Date().getDate();
    }

    function goToAdmin() {
         window.location.href="AdminAction.do?method=load";
    }

    function gotToConfirmExportDeletePage() {
         window.location.href='confirmExportsDelete.ftl';
     }

    function goToViewExports() {
         window.location.href='renderAccountingDataCacheInfo.ftl';
    }
    
    function deleteCacheDir() {
        $.ajax({
            url   : 'deleteCacheDir.ftl',
            cache : false,
            error : function (xhr, ajaxOptions, thrownError){
                    alert(xhr.responseText + thrownError);
                    },
            success : function(data) {
                         goToViewExports();
                    }
              })
    }
    
   function loadExportsList(size, type) {
        $.ajax({
            url   : 'generateExportsList.ftl?listSize='+size+'&listType='+type,
            cache : false,
            error : function (xhr, ajaxOptions, thrownError){
                    alert(xhr.responseText + thrownError);
                    },
            success : function(data) {
                         $("#export_list").html(data);
                         addExportListLink(size, type);
                    }
              })
    }

    function addExportListLink(size, type) {
        var options = "<a href='#' onclick=\"javascript:loadExportsList("+(size+10)+",'"+type+"');\" title='Add 10 previous exports on screen'>More 10</a> &nbsp;";

        if(size != 10 ) {
           options += "<a href='#' onclick=\"javascript:loadExportsList("+(size-10)+",'"+type+"');\" title='Remove 10 previous exports on screen'>Less 10</a>&nbsp;";
           options += "<a href='#' onclick=\"javascript:loadExportsList(10,'"+type+"');\" title='Go back to list of 10 exports'>Back</a>";
        }

        if(type != "all") {
           options += "<br/><a href='#' onclick=\"javascript:loadExportsList(10,'all');\" title='Get all exports generated/not generated'>Show all exports</a>";
        } else {
           options += "<br/>Show all exports";
        }

        if(type != "generated") {
           options += "<br/><a href='#' onclick=\"javascript:loadExportsList(10,'generated');\" title='Get only generated exports'>Show only generated exports</a>";
        } else {
           options += "<br/>Show only generated exports";
        }

       if(type != "notgenerated") {
           options += "<br/><a href='#' onclick=\"javascript:loadExportsList(10,'notgenerated');\" title='Get only not generated exports'>Show only not generated exports</a>";
        } else {
           options += "<br/>Show only not generated exports";
        }

           $("#export_list_options").html(options);
    }

var gAutoPrint = true;

function processPrint(table){

if (document.getElementById != null){
var html = '<HTML>\n<HEAD>\n';
if (document.getElementsByTagName != null){
var headTags = document.getElementsByTagName("head");
if (headTags.length > 0) html += headTags[0].innerHTML;

}

html += '\n</HE' + 'AD>\n<BODY>\n';
var printReadyElem = document.getElementById(table);

if (printReadyElem != null)
html += printReadyElem.innerHTML;
else{
alert("Error, no contents.");
return;
}

html += '\n</BO' + 'DY>\n</HT' + 'ML>';

var printWin = window.open("","processPrint");
printWin.document.open();
printWin.document.write(html);
printWin.document.close();

if (gAutoPrint) printWin.print();
} else alert("Browser not supported.");
}